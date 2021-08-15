package me.maiky.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;
import me.maiky.packets.impl.PacketId;
import me.maiky.packets.impl.PacketOut;
import me.maiky.packets.in.PacketHandshakingIn;
import me.maiky.packets.in.PacketLoginStartIn;
import me.maiky.packets.in.PacketPingIn;
import me.maiky.packets.out.PacketLoginSuccessOut;
import me.maiky.packets.out.PacketPongOut;
import me.maiky.packets.out.PacketResponseOut;
import me.maiky.server.Server;
import me.maiky.util.DataTypeIO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

@ToString
@Getter
public class Connection extends Thread {

    private final Server server;

    private final Socket socket;

    private boolean running;

    private State connectionState;

    private Player player;

    private AtomicLong lastKeepAlive;

    private DataOutputStream output;
    private DataInputStream input;

    private InetAddress address;

    public Connection(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        this.address = socket.getInetAddress();
        this.lastKeepAlive = new AtomicLong();
        this.running = false;
    }

    @Override
    public void run() {
        // Update variables
        running = true;
        connectionState = State.HANDSHAKE;

        try {
            // Keep socket alive until closed
            socket.setKeepAlive(true);

            // Input & Output stream
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            int handshakeSize = DataTypeIO.readVarInt(input);
            if (handshakeSize == 0xFE) {
                // Send legacy
                connectionState = State.LEGACY;
                output.writeByte(255);

                String ip = getAddress().getHostName() + ":" + socket.getPort();

                System.out.println("[/" + ip + "] <-> Legacy Status has pinged (Possible API call)");
                // TODO make supported
            }

            int handShakeId = DataTypeIO.readVarInt(input);
            PacketHandshakingIn handshake = new PacketHandshakingIn(input);

            // TODO: Add bungeecord support later
            String bungeeForwarding = handshake.getServerAddress();
            boolean isBungeecord = false;
            UUID bungeeUUID = null;
            //SkinResponse bungeeSkin = null;

            if (handshake.getHandshakeType() == null) return;

            try {
                if (handshake.getHandshakeType() == PacketHandshakingIn.HandshakeType.STATUS) {
                    connectionState = State.STATUS;
                    while (socket.isConnected()) {
                        DataTypeIO.readVarInt(input);
                        int packetId = DataTypeIO.readVarInt(input);

                        System.out.println(packetId);
                        System.out.println(packetId == 0x01);

                        if (packetId == 0x00) {
                            String ip = getAddress().getHostName() + ":" + socket.getPort();
                            System.out.println("[" + ip + "] <-> Handshake Status has pinged");
                            PacketResponseOut out = buildResponse(handshake);
                            sendPacket(out);
                        } else if (packetId == 0x01) {
                            PacketPingIn in = new PacketPingIn(input);
                            PacketPongOut out = new PacketPongOut(in.getPayload());
                            System.out.println(out);
                            sendPacket(out);
                            break;
                        }
                    }
                } else if (handshake.getHandshakeType() == PacketHandshakingIn.HandshakeType.LOGIN) {
                    connectionState = State.LOGIN;

                    if (isBungeecord) {
                        try {
                            String[] data = bungeeForwarding.split("\\x00");
                            //String host = data[0];
                            String ip = data[1];

                            bungeeUUID = UUID.fromString(data[2].replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5"));
                            address = InetAddress.getByName(ip);

                            if (data.length > 3) {
                                String skinJson = data[3];

                                String skin = skinJson.split("\"value\":\"")[1].split("\"")[0];
                                String signature = skinJson.split("\"signature\":\"")[1].split("\"")[0];
                                //bungeeSkin = new SkinResponse(skin, signature);
                            }
                        } catch (Exception e) {
                            //Limbo.getInstance().getConsole().sendMessage("If you wish to use bungeecord's IP forwarding, please enable that in your bungeecord config.yml as well!");
                            //disconnectDuringLogin(new BaseComponent[]{new TextComponent(ChatColor.RED + "Please connect from the proxy!")});
                        }
                    }

                    while (socket.isConnected()) {
                        int size = DataTypeIO.readVarInt(input);
                        int packetId = DataTypeIO.readVarInt(input);

                        if (packetId == 0x00) {
                            PacketLoginStartIn start = new PacketLoginStartIn(input);
                            String username = start.getUsername();
                            UUID uuid = isBungeecord ? bungeeUUID : UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));

                            PacketLoginSuccessOut success = new PacketLoginSuccessOut(uuid, username);
                            sendPacket(success);

                            connectionState = State.PLAY;

                           /*
                            player = new Player(this, username, uuid, Limbo.getInstance().getNextEntityId(), Limbo.getInstance().getServerProperties().getWorldSpawn(), new PlayerInteractManager());
                            player.setSkinLayers((byte) (0x01 | 0x02 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40));
                            Limbo.getInstance().addPlayer(player);
                            */

                            break;
                        } else {
                            input.skipBytes(size - DataTypeIO.getVarIntLength(packetId));
                        }
                    }
                } else if (connectionState == State.PLAY) {
                    while(socket.isConnected()) {
                        int size = DataTypeIO.readVarInt(input);
                        int packetId = DataTypeIO.readVarInt(input);

                        if (packetId == )
                    }
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } catch (IOException ignored) {}

        try {
            connectionState = State.DISCONNECTED;
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.getClients().remove(this);
    }

    public PacketResponseOut buildResponse(PacketHandshakingIn handshake) throws IOException {
        Gson gson = new Gson();

        JsonObject response = new JsonObject();

        JsonObject version = new JsonObject();
        version.addProperty("name", "MCQueueApp");
        version.addProperty("protocol", handshake.getProtocolVersion());

        JsonObject players = new JsonObject();
        players.addProperty("max", 1000);
        players.addProperty("online", 0);

        JsonObject user = new JsonObject();
        user.addProperty("name", "hoi");
        user.addProperty("id", UUID.randomUUID().toString());

        JsonArray array = new JsonArray();
        players.add("sample", array);

        JsonObject description = new JsonObject();
        description.addProperty("text", "MC Queue werkt!");

        response.add("version", version);
        response.add("players", players);
        response.add("description", description);
        //response.addProperty("favicon", "data:image/png;base64,idk");

        return new PacketResponseOut(gson.toJson(response));
    }

    public void sendPacket(PacketOut out) throws IOException {
        byte[] packetByte = out.serializePacket();
        DataTypeIO.writeVarInt(output, packetByte.length);
        output.write(packetByte);
        output.flush();;
    }

    public enum State {
        LEGACY,
        HANDSHAKE,
        STATUS,
        LOGIN,
        PLAY,
        DISCONNECTED;
    }

}

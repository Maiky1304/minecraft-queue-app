package me.maiky.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.ToString;
import me.maiky.packets.impl.PacketId;
import me.maiky.packets.impl.PacketOut;
import me.maiky.packets.in.PacketHandshakingIn;
import me.maiky.packets.in.PacketPingIn;
import me.maiky.packets.out.PacketPongOut;
import me.maiky.packets.out.PacketResponseOut;
import me.maiky.util.DataTypeIO;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

@ToString
@Getter
public class Connection extends Thread {

    private final Socket socket;

    private boolean running;

    private State connectionState;

    private Player player;

    private AtomicLong lastKeepAlive;

    private DataOutputStream output;
    private DataInputStream input;

    private InetAddress address;

    public Connection(Socket socket) {
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

            try {
                System.out.println(handshake.getHandshakeType());

                switch(handshake.getHandshakeType()) {
                    case STATUS:
                        connectionState = State.STATUS;
                        while(socket.isConnected()) {
                            DataTypeIO.readVarInt(input);
                            int packetId = DataTypeIO.readVarInt(input);
                            System.out.println(packetId);
                            if (packetId == 0x00) {
                                String ip = getAddress().getHostName() + ":" + socket.getPort();

                                System.out.println("[" + ip + "] <-> Handshake Status has pinged");

                                PacketResponseOut out = buildResponse();
                                sendPacket(out);
                            } else if (packetId == 0x01) {

                                System.out.println("Ping!");

                                PacketPongOut out = new PacketPongOut(DataTypeIO.readVarLong(input));
                                sendPacket(out);
                            }
                        }
                        break;
                }
            } catch (IOException exception) {
                /*
                socket.close();
                connectionState = State.DISCONNECTED;
                 */
            }
        } catch (IOException ignored) {}

        /*
        try {
            connectionState = State.DISCONNECTED;
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
         */
    }

    public PacketResponseOut buildResponse() throws IOException {
        PacketHandshakingIn handshake = new PacketHandshakingIn(input);

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
        System.out.println(out);
        byte[] bytes = out.serializePacket();
        DataTypeIO.writeVarInt(this.output, bytes.length);
        this.output.write(bytes);
        this.output.flush();
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

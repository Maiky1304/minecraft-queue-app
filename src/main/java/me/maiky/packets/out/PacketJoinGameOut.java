package me.maiky.packets.out;

import me.maiky.client.Gamemode;
import me.maiky.client.NamespacedKey;
import me.maiky.packets.impl.PacketId;
import me.maiky.packets.impl.PacketOut;
import me.maiky.util.DataTypeIO;
import net.querz.nbt.tag.CompoundTag;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

@PacketId(0X26)
public class PacketJoinGameOut extends PacketOut {

    @Override
    public byte[] serializePacket() throws IOException {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(0X26);
        output.writeInt(random.nextInt(999));
        output.writeBoolean(false);
        output.writeByte((byte) Gamemode.SURVIVAL.getId());

        // Fake worlds
        String[] worlds = new String[]{"queue-world"};
        DataTypeIO.writeVarInt(output, worlds.length);
        Arrays.stream(worlds).forEach(w -> {
            try {
                DataTypeIO.writeString(output, w, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Write fake empty NBT Tags
        DataTypeIO.writeCompoundTag(output, new CompoundTag());
        DataTypeIO.writeCompoundTag(output, new CompoundTag());

        // Write the rest
        DataTypeIO.writeString(output, new NamespacedKey(worlds[0]).toString(), StandardCharsets.UTF_8);
        output.writeLong(692989839477472L);

        // Max players for queue
        DataTypeIO.writeVarInt(output, Integer.MAX_VALUE);
        // View distance (can be 1 since we're only using the void)
        DataTypeIO.writeVarInt(output, 1);
        // debug info in F3
        output.writeBoolean(false);
        // respawn screen on/off
        output.writeBoolean(false);
        // is debug enabled?
        output.writeBoolean(false);
        // world = flat?
        output.writeBoolean(true);

        return buffer.toByteArray();
    }

}

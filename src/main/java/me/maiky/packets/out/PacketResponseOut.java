package me.maiky.packets.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.maiky.packets.impl.Packet;
import me.maiky.packets.impl.PacketId;
import me.maiky.packets.impl.PacketOut;
import me.maiky.util.DataTypeIO;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

@PacketId(0x00)
@AllArgsConstructor @Getter
public class PacketResponseOut extends PacketOut {

    private final String json;

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(0x00);
        DataTypeIO.writeString(output, json, StandardCharsets.UTF_8);

        return buffer.toByteArray();
    }

}

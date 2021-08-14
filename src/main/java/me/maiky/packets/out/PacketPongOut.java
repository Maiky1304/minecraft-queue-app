package me.maiky.packets.out;

import lombok.AllArgsConstructor;
import me.maiky.packets.impl.PacketOut;
import me.maiky.util.DataTypeIO;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

@AllArgsConstructor
public class PacketPongOut extends PacketOut {

    private final long payload;

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(0x01);
        DataTypeIO.writeVarLong(output, payload);

        return buffer.toByteArray();
    }

}

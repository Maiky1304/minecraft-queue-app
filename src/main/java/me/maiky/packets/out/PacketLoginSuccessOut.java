package me.maiky.packets.out;

import lombok.AllArgsConstructor;
import me.maiky.packets.impl.PacketId;
import me.maiky.packets.impl.PacketOut;
import me.maiky.util.DataTypeIO;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

@PacketId(0x01)
@AllArgsConstructor
public class PacketLoginSuccessOut extends PacketOut {

    private final UUID uuid;
    private final String username;

    @Override
    public byte[] serializePacket() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        DataOutputStream output = new DataOutputStream(buffer);
        output.writeByte(0x02);
        DataTypeIO.writeUUID(output, this.uuid);
        DataTypeIO.writeString(output, this.username, StandardCharsets.UTF_8);

        return buffer.toByteArray();
    }

}

package me.maiky.packets.in;

import lombok.Getter;
import lombok.ToString;
import me.maiky.packets.impl.PacketId;
import me.maiky.packets.impl.PacketIn;
import me.maiky.util.DataTypeIO;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

@ToString
@Getter
@PacketId(0x0A)
public class PacketPluginMessageIn extends PacketIn {

    private final int messageId;
    private final String channel;
    private final byte[] data;

    public PacketPluginMessageIn(DataInputStream input, int packetLength, int packetId) throws IOException {
        this.messageId = DataTypeIO.readVarInt(input);
        this.channel = DataTypeIO.readString(input, StandardCharsets.UTF_8);
        int dataLength = packetLength - DataTypeIO.getVarIntLength(packetId)
                - DataTypeIO.getVarIntLength(messageId) - DataTypeIO.getStringLength(channel, StandardCharsets.UTF_8);
        this.data = new byte[dataLength];
        input.read(this.data);
    }
}

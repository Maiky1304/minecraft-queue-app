package me.maiky.packets.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.maiky.packets.impl.PacketId;
import me.maiky.packets.impl.PacketIn;
import me.maiky.util.DataTypeIO;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Getter
@PacketId(0x00)
public class PacketLoginStartIn extends PacketIn {

    private final String username;

    public PacketLoginStartIn(DataInputStream inputStream) throws IOException {
        this.username = DataTypeIO.readString(inputStream, StandardCharsets.UTF_8);
    }

}

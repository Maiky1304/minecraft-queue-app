package me.maiky.packets.in;

import lombok.Getter;
import lombok.ToString;
import me.maiky.packets.impl.PacketId;
import me.maiky.packets.impl.PacketIn;
import me.maiky.util.DataTypeIO;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Getter
@ToString
@PacketId(0x01)
public class PacketPingIn extends PacketIn {

	private final long payload;

	public PacketPingIn(long payload) {
		this.payload = payload;
	}

	public PacketPingIn(DataInputStream in) throws IOException {
		this(DataTypeIO.readVarLong(in));
	}

}
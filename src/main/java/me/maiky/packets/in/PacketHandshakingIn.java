package me.maiky.packets.in;

import lombok.ToString;
import me.maiky.packets.impl.PacketId;
import me.maiky.packets.impl.PacketIn;
import me.maiky.util.DataTypeIO;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@ToString
@PacketId(0x00)
public class PacketHandshakingIn extends PacketIn {

	public enum HandshakeType {
		STATUS(1),
		LOGIN(2);
		
		int networkId;
		
		HandshakeType(int networkId) {
			this.networkId = networkId;
		}
		
		public int getNetworkId() {
			return networkId;
		}
		
		public static HandshakeType fromNetworkId(int networkId) {
			for (HandshakeType type : HandshakeType.values()) {
				if (type.getNetworkId() == networkId) {
					return type;
				}
			}
			return null;
		}
	}
	
	private final int protocolVersion;
	private final String serverAddress;
	private final int serverPort;
	private final HandshakeType handshakeType;

	public PacketHandshakingIn(int protocolVersion, String serverAddress, int serverPort, HandshakeType handshakeType) {
		this.protocolVersion = protocolVersion;
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.handshakeType = handshakeType;
	}
	
	public PacketHandshakingIn(DataInputStream in) throws IOException {
		this(DataTypeIO.readVarInt(in), DataTypeIO.readString(in, StandardCharsets.UTF_8), in.readShort() & 0xFFFF, HandshakeType.fromNetworkId(DataTypeIO.readVarInt(in)));
	}

	public int getProtocolVersion() {
		return protocolVersion;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public int getServerPort() {
		return serverPort;
	}

	public HandshakeType getHandshakeType() {
		return handshakeType;
	}

}
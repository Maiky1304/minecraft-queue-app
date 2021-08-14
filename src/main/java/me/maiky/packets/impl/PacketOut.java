package me.maiky.packets.impl;

import lombok.ToString;

import java.io.IOException;

@ToString
public abstract class PacketOut extends Packet {
	
	public abstract byte[] serializePacket() throws IOException;

}
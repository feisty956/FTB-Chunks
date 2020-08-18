package com.feed_the_beast.mods.ftbchunks.net;

import com.feed_the_beast.mods.ftbchunks.FTBChunks;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class SendAllChunksPacket
{
	public String dimension;
	public UUID owner;
	public List<SendChunkPacket.SingleChunk> chunks;

	public SendAllChunksPacket()
	{
	}

	SendAllChunksPacket(PacketBuffer buf)
	{
		dimension = buf.readString(Short.MAX_VALUE);
		owner = new UUID(buf.readLong(), buf.readLong());

		int s = buf.readVarInt();
		chunks = new ArrayList<>(s);

		for (int i = 0; i < s; i++)
		{
			SendChunkPacket.SingleChunk chunk = new SendChunkPacket.SingleChunk(buf);
			chunk.ownerId = owner;
			chunks.add(chunk);
		}
	}

	void write(PacketBuffer buf)
	{
		buf.writeString(dimension, 100);
		buf.writeLong(owner.getMostSignificantBits());
		buf.writeLong(owner.getLeastSignificantBits());
		buf.writeVarInt(chunks.size());

		for (SendChunkPacket.SingleChunk chunk : chunks)
		{
			chunk.write(buf);
		}
	}

	void handle(Supplier<NetworkEvent.Context> context)
	{
		context.get().enqueueWork(() -> FTBChunks.instance.proxy.updateAllChunks(this));
		context.get().setPacketHandled(true);
	}
}
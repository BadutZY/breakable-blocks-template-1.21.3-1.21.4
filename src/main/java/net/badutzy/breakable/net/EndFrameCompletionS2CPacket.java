package net.badutzy.breakable.net;

import net.badutzy.breakable.ObtainableEnd;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public record EndFrameCompletionS2CPacket(UUID profile) implements CustomPayload {
    public static final CustomPayload.Id<EndFrameCompletionS2CPacket> ID = new Id<>(Identifier.of(ObtainableEnd.MOD_ID, "end_frame_complete"));
    public static final PacketCodec<RegistryByteBuf, EndFrameCompletionS2CPacket> CODEC = CustomPayload.codecOf(EndFrameCompletionS2CPacket::write, EndFrameCompletionS2CPacket::new);

    public EndFrameCompletionS2CPacket(RegistryByteBuf buf) {
        this(buf.readUuid());
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(profile);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

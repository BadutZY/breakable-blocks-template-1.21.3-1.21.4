package net.badutzy.breakable.net;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class ObtainableEndServerNetworking {
    public static void init() {
        PayloadTypeRegistry.playS2C().register(EndFrameCompletionS2CPacket.ID, EndFrameCompletionS2CPacket.CODEC);
    }

    public static void sendEndFrameCompleteEvent(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (ServerPlayNetworking.canSend(player, EndFrameCompletionS2CPacket.ID)) {
                ServerPlayNetworking.send(player, new EndFrameCompletionS2CPacket(player.getUuid()));
            }
        }
    }
}

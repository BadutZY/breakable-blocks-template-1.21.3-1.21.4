package net.badutzy.breakable.net.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.badutzy.breakable.net.EndFrameCompletionS2CPacket;
import net.minecraft.client.network.ClientPlayerEntity;

public class ObtainableEndClientNetworking {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(EndFrameCompletionS2CPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientPlayerEntity player = context.client().player;
            });
        });
    }
}

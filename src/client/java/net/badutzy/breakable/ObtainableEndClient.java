package net.badutzy.breakable;

import net.fabricmc.api.ClientModInitializer;
import net.badutzy.breakable.net.client.ObtainableEndClientNetworking;

public class ObtainableEndClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ObtainableEndClientNetworking.register();
	}
}
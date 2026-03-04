package net.badutzy.breakable.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ObtainableEndBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected ObtainableEndBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(dataOutput, registriesFuture);
    }

    @Override
    public void generate() {
        // Tidak perlu generate loot table untuk End Portal Frame dan Spawner
        // karena kita handle drop secara manual melalui PlayerBlockBreakEvents.AFTER
        // Ini mencegah double drop
    }
}
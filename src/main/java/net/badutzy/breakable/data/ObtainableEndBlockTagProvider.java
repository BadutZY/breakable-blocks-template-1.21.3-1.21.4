package net.badutzy.breakable.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ObtainableEndBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    protected ObtainableEndBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void configure(RegistryWrapper.WrapperLookup registries) {
        // basic block tags
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(Blocks.END_PORTAL_FRAME)
                .add(Blocks.SPAWNER);
    }
}

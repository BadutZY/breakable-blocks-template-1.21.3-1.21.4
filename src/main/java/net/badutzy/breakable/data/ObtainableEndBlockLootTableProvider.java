package net.badutzy.breakable.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ObtainableEndBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected ObtainableEndBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(dataOutput, registriesFuture);
    }

    @Override
    public void generate() {
        // Always drop one End Portal Frame.
        // Drop an Ender Eye if there was one in the End Portal Frame piece.
        addDrop(Blocks.END_PORTAL_FRAME, LootTable.builder()
                .pools(List.of(
                                LootPool.builder()
                                        .with(ItemEntry.builder(Items.END_PORTAL_FRAME))
                                        .build(),
                                LootPool.builder()
                                        .with(ItemEntry.builder(Items.ENDER_EYE)
                                                .conditionally(BlockStatePropertyLootCondition.builder(Blocks.END_PORTAL_FRAME)
                                                        .properties(StatePredicate.Builder.create()
                                                                .exactMatch(EndPortalFrameBlock.EYE, true)
                                                        )
                                                )
                                        )
                                        .build()
                        )
                )
        );

        // Add loot table for Spawner - always drop spawner
        // Spawn eggs are handled separately in ObtainableEnd.java
        addDrop(Blocks.SPAWNER, LootTable.builder()
                .pools(List.of(
                                LootPool.builder()
                                        .with(ItemEntry.builder(Items.SPAWNER))
                                        .build()
                        )
                )
        );
    }
}
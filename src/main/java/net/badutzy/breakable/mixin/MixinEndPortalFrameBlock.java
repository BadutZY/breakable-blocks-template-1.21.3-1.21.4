package net.badutzy.breakable.mixin;

import net.badutzy.breakable.net.ObtainableEndServerNetworking;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;
import java.util.Optional;

@Mixin(EndPortalFrameBlock.class)
public abstract class MixinEndPortalFrameBlock extends Block {
    @Unique
    private static final Map<Vec3i, Direction> PORTAL_POSITIONS = Map.ofEntries(
            Map.entry(new Vec3i(-1, 0, -2), Direction.SOUTH),
            Map.entry(new Vec3i( 0, 0, -2), Direction.SOUTH),
            Map.entry(new Vec3i( 1, 0, -2), Direction.SOUTH),
            Map.entry(new Vec3i(-2, 0, -1), Direction.EAST),
            Map.entry(new Vec3i(-2, 0,  0), Direction.EAST),
            Map.entry(new Vec3i(-2, 0,  1), Direction.EAST),
            Map.entry(new Vec3i( 2, 0, -1), Direction.WEST),
            Map.entry(new Vec3i( 2, 0,  0), Direction.WEST),
            Map.entry(new Vec3i( 2, 0,  1), Direction.WEST),
            Map.entry(new Vec3i(-1, 0,  2), Direction.NORTH),
            Map.entry(new Vec3i( 0, 0,  2), Direction.NORTH),
            Map.entry(new Vec3i( 1, 0,  2), Direction.NORTH)
    );

    public MixinEndPortalFrameBlock(Settings settings) {
        super(settings);
    }

    @ModifyArg(method="<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"))
    private static AbstractBlock.Settings obtainableend$breakableFrames(AbstractBlock.Settings settings) {
        // Set loot table untuk End Portal Frame agar bisa drop items
        settings.lootTable(Optional.of(RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.ofVanilla("blocks/end_portal_frame"))));

        // Set hardness 25.0f (sama dengan Obsidian) dan blast resistance 1200.0f
        // requiresTool() WAJIB agar hanya pickaxe yang bisa harvest
        return settings
                .hardness(25.0f)
                .resistance(1200.0f)
                .requiresTool();
    }

    /**
     * CRITICAL FIX: Override afterBreak untuk memastikan Eye of Ender drop
     * Method ini dipanggil setelah block dihancurkan oleh player
     * Kita drop Eye of Ender secara manual jika ada di block state
     */
    @Override
    public void afterBreak(World world, net.minecraft.entity.player.PlayerEntity player, BlockPos pos, BlockState state, @Nullable net.minecraft.block.entity.BlockEntity blockEntity, ItemStack tool) {
        super.afterBreak(world, player, pos, state, blockEntity, tool);

        // Drop Eye of Ender jika block memiliki eye dan tidak dalam creative mode
        if (!world.isClient() && state.get(EndPortalFrameBlock.EYE) && !player.isCreative()) {
            Block.dropStack(world, pos, new ItemStack(Items.ENDER_EYE));
        }
    }

    /*
     * Whenever an end portal frame piece is placed, see if it is part of a complete frame.
     * If it is part of a complete frame, reorient all frame pieces to make the frame valid.
     */
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        outer: for (Vec3i centerOffset : PORTAL_POSITIONS.keySet()) {
            BlockPos center = pos.add(centerOffset);

            for (Vec3i frameOffset : PORTAL_POSITIONS.keySet()) {
                BlockPos frame = center.add(frameOffset);

                if (!world.getBlockState(frame).isOf(Blocks.END_PORTAL_FRAME)) {
                    continue outer;
                }
            }

            for (Vec3i frameOffset : PORTAL_POSITIONS.keySet()) {
                BlockPos frame = center.add(frameOffset);

                world.setBlockState(frame, world.getBlockState(frame).with(EndPortalFrameBlock.FACING, PORTAL_POSITIONS.get(frameOffset)), 2);
            }

            MinecraftServer server = world.getServer();
            if (server != null) {
                ObtainableEndServerNetworking.sendEndFrameCompleteEvent(server);
            }

            break;
        }
    }

    /*
     * When an end portal frame piece is broken, try to break any associated end portal blocks.
     */
    @Override
    public void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        Direction primary = state.get(EndPortalFrameBlock.FACING);
        Direction secondary = primary.rotateClockwise(Direction.Axis.Y);

        super.onStateReplaced(state, world, pos, moved);
        BlockState newState = world.getBlockState(pos);

        if (    newState != null &&
                newState.isOf(Blocks.END_PORTAL_FRAME) &&
                newState.get(EndPortalFrameBlock.EYE) &&
                primary.equals(newState.get(EndPortalFrameBlock.FACING))) {

            // Replaced with functionally equivalent frame piece.
            return;
        }

        BlockPos target;
        for (int movePrimary = 1; movePrimary <= 3; ++movePrimary) {
            for (int moveSecondary = -2; moveSecondary <= 2; ++moveSecondary) {
                target = pos.offset(primary, movePrimary).offset(secondary, moveSecondary);

                if (world.getBlockState(target).isOf(Blocks.END_PORTAL)) {
                    world.breakBlock(target, false);
                }
            }
        }
    }
}
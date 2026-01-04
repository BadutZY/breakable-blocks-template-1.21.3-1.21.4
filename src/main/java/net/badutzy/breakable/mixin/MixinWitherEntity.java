package net.badutzy.breakable.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/*
 * When End Portal Frame blocks are configured not to be immune to destruction by the Wither,
 * this mixin effectively removes End Portal Frame blocks from the Wither Immune block tag.
 */
@Mixin(WitherEntity.class)
public class MixinWitherEntity {
    @WrapOperation(method = "canDestroy",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"
            )
    )
    @SuppressWarnings("unused")
    private static boolean obtainableend$frameImmunity(BlockState instance, TagKey<Block> tag, Operation<Boolean> original) {

        return original.call(instance, tag);
    }
}

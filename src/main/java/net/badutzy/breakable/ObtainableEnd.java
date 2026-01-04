package net.badutzy.breakable;

import net.fabricmc.api.ModInitializer;
import net.badutzy.breakable.net.ObtainableEndServerNetworking;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ObtainableEnd implements ModInitializer {
	public static final String MOD_ID = "obtainable-end";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Map<EntityType<?>, ItemStack> ANIMAL_SPAWN_EGGS = new HashMap<>();

	public static final Identifier END_FRAME_COMPLETE_SOUND_ID = Identifier.of(MOD_ID, "end_frame_complete_sound");
	public static final SoundEvent END_FRAME_COMPLETE_SOUND_EVENT = SoundEvent.of(END_FRAME_COMPLETE_SOUND_ID);

	@Override
	public void onInitialize() {

		ObtainableEndServerNetworking.init();
		LOGGER.info("Now you can break End Portal Frame");
		LOGGER.info("Now you can break Spawner");

		// Initialize spawn egg mappings untuk hewan
		initializeAnimalSpawnEggs();

		// Intercept block attack attempts
		AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
			if (world.getBlockState(pos).isOf(Blocks.SPAWNER)) {
				// Allow creative mode
				if (player.isCreative()) {
					return ActionResult.PASS;
				}

				ItemStack heldItem = player.getMainHandStack();
				if (!isValidPickaxe(heldItem)) {
					LOGGER.info("Blocking spawner attack with invalid tool: {}", heldItem.getItem().toString());
					return ActionResult.FAIL;
				}
			}
			return ActionResult.PASS;
		});

		// Register event untuk menghandle block break - BEFORE event
		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
			// Check if block is Spawner
			if (state.isOf(Blocks.SPAWNER)) {
				// Always allow breaking in creative mode
				if (player.isCreative()) {
					return true;
				}

				// Check if player is using any type of pickaxe
				ItemStack heldItem = player.getMainHandStack();
				boolean hasValidPickaxe = isValidPickaxe(heldItem);

				LOGGER.info("Player {} trying to break spawner with {}, valid: {}",
						player.getName().getString(),
						heldItem.getItem().toString(),
						hasValidPickaxe);

				// Prevent breaking with invalid tools
				if (!hasValidPickaxe) {
					LOGGER.info("Blocking spawner break - invalid tool");
					return false;
				}

				return true;
			}

			// Check if block is End Portal Frame
			if (state.isOf(Blocks.END_PORTAL_FRAME)) {
				// Always allow breaking in creative mode
				if (player.isCreative()) {
					return true;
				}

				// Check if player is using any type of pickaxe
				ItemStack heldItem = player.getMainHandStack();
				boolean hasValidPickaxe = isValidPickaxe(heldItem);

				LOGGER.info("Player {} trying to break end portal frame with {}, valid: {}",
						player.getName().getString(),
						heldItem.getItem().toString(),
						hasValidPickaxe);

				// Prevent breaking with invalid tools
				if (!hasValidPickaxe) {
					LOGGER.info("Blocking end portal frame break - invalid tool");
					return false;
				}

				return true;
			}

			return true;
		});

		// Register event untuk drop items setelah block berhasil dihancurkan
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			// Skip dropping in creative mode (creative mode already handles items)
			if (player.isCreative()) {
				return;
			}

			// Handle Spawner drops
			if (state.isOf(Blocks.SPAWNER)) {
				ItemStack heldItem = player.getMainHandStack();
				if (isValidPickaxe(heldItem)) {
					// Drop spawner block
					dropItem(world, pos, new ItemStack(Items.SPAWNER));
					LOGGER.info("Spawner broken and dropped with {}", heldItem.getItem().toString());

					// Check for spawn egg dan drop jika ada
					if (blockEntity instanceof MobSpawnerBlockEntity spawnerEntity) {
						ItemStack spawnEgg = getSpawnEgg(spawnerEntity);
						if (!spawnEgg.isEmpty()) {
							dropItem(world, pos, spawnEgg);
							LOGGER.info("Spawn egg dropped: {}", spawnEgg.getItem().toString());
						}
					}
				}
			}

			// Handle End Portal Frame drops
			if (state.isOf(Blocks.END_PORTAL_FRAME)) {
				ItemStack heldItem = player.getMainHandStack();
				if (isValidPickaxe(heldItem)) {
					// Always drop one End Portal Frame
					dropItem(world, pos, new ItemStack(Items.END_PORTAL_FRAME));
					LOGGER.info("End Portal Frame broken and dropped with {}", heldItem.getItem().toString());

					// Drop Ender Eye if the frame had one
					if (state.get(EndPortalFrameBlock.EYE)) {
						dropItem(world, pos, new ItemStack(Items.ENDER_EYE));
						LOGGER.info("Ender Eye dropped from End Portal Frame");
					}
				}
			}
		});
	}

	private void initializeAnimalSpawnEggs() {
		ANIMAL_SPAWN_EGGS.put(EntityType.ALLAY, new ItemStack(Items.ALLAY_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.ARMADILLO, new ItemStack(Items.ARMADILLO_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.AXOLOTL, new ItemStack(Items.AXOLOTL_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.BAT, new ItemStack(Items.BAT_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.BEE, new ItemStack(Items.BEE_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.CAMEL, new ItemStack(Items.CAMEL_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.CAT, new ItemStack(Items.CAT_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.CHICKEN, new ItemStack(Items.CHICKEN_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.COD, new ItemStack(Items.COD_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.COW, new ItemStack(Items.COW_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.DONKEY, new ItemStack(Items.DONKEY_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.FOX, new ItemStack(Items.FOX_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.FROG, new ItemStack(Items.FROG_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.GLOW_SQUID, new ItemStack(Items.GLOW_SQUID_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.GOAT, new ItemStack(Items.GOAT_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.HORSE, new ItemStack(Items.HORSE_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.LLAMA, new ItemStack(Items.LLAMA_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.MOOSHROOM, new ItemStack(Items.MOOSHROOM_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.MULE, new ItemStack(Items.MULE_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.OCELOT, new ItemStack(Items.OCELOT_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.PANDA, new ItemStack(Items.PANDA_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.PARROT, new ItemStack(Items.PARROT_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.PIG, new ItemStack(Items.PIG_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.POLAR_BEAR, new ItemStack(Items.POLAR_BEAR_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.PUFFERFISH, new ItemStack(Items.PUFFERFISH_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.RABBIT, new ItemStack(Items.RABBIT_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.SALMON, new ItemStack(Items.SALMON_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.SHEEP, new ItemStack(Items.SHEEP_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.SNIFFER, new ItemStack(Items.SNIFFER_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.SQUID, new ItemStack(Items.SQUID_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.STRIDER, new ItemStack(Items.STRIDER_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.TADPOLE, new ItemStack(Items.TADPOLE_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.TROPICAL_FISH, new ItemStack(Items.TROPICAL_FISH_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.TURTLE, new ItemStack(Items.TURTLE_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.WOLF, new ItemStack(Items.WOLF_SPAWN_EGG));

		// Neutral mobs
		ANIMAL_SPAWN_EGGS.put(EntityType.DOLPHIN, new ItemStack(Items.DOLPHIN_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.HOGLIN, new ItemStack(Items.HOGLIN_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.IRON_GOLEM, new ItemStack(Items.IRON_GOLEM_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.PIGLIN, new ItemStack(Items.PIGLIN_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.SNOW_GOLEM, new ItemStack(Items.SNOW_GOLEM_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.TRADER_LLAMA, new ItemStack(Items.TRADER_LLAMA_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.WANDERING_TRADER, new ItemStack(Items.WANDERING_TRADER_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.ZOGLIN, new ItemStack(Items.ZOGLIN_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.VILLAGER, new ItemStack(Items.VILLAGER_SPAWN_EGG));

		// Hostile mobs
		ANIMAL_SPAWN_EGGS.put(EntityType.BLAZE, new ItemStack(Items.BLAZE_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.BOGGED, new ItemStack(Items.BOGGED_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.BREEZE, new ItemStack(Items.BREEZE_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.CREEPER, new ItemStack(Items.CREEPER_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.DROWNED, new ItemStack(Items.DROWNED_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.ELDER_GUARDIAN, new ItemStack(Items.ELDER_GUARDIAN_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.ENDER_DRAGON, new ItemStack(Items.ENDER_DRAGON_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.ENDERMITE, new ItemStack(Items.ENDERMITE_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.EVOKER, new ItemStack(Items.EVOKER_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.GHAST, new ItemStack(Items.GHAST_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.GUARDIAN, new ItemStack(Items.GUARDIAN_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.HUSK, new ItemStack(Items.HUSK_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.MAGMA_CUBE, new ItemStack(Items.MAGMA_CUBE_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.PHANTOM, new ItemStack(Items.PHANTOM_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.PIGLIN_BRUTE, new ItemStack(Items.PIGLIN_BRUTE_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.PILLAGER, new ItemStack(Items.PILLAGER_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.RAVAGER, new ItemStack(Items.RAVAGER_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.SHULKER, new ItemStack(Items.SHULKER_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.SILVERFISH, new ItemStack(Items.SILVERFISH_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.SKELETON, new ItemStack(Items.SKELETON_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.SKELETON_HORSE, new ItemStack(Items.SKELETON_HORSE_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.SLIME, new ItemStack(Items.SLIME_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.SPIDER, new ItemStack(Items.SPIDER_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.STRAY, new ItemStack(Items.STRAY_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.VEX, new ItemStack(Items.VEX_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.VINDICATOR, new ItemStack(Items.VINDICATOR_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.WARDEN, new ItemStack(Items.WARDEN_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.WITCH, new ItemStack(Items.WITCH_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.WITHER, new ItemStack(Items.WITHER_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.WITHER_SKELETON, new ItemStack(Items.WITHER_SKELETON_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.ZOMBIE, new ItemStack(Items.ZOMBIE_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.ZOMBIE_HORSE, new ItemStack(Items.ZOMBIE_HORSE_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.ZOMBIE_VILLAGER, new ItemStack(Items.ZOMBIE_VILLAGER_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.ZOMBIFIED_PIGLIN, new ItemStack(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.ENDERMAN, new ItemStack(Items.ENDERMAN_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.CAVE_SPIDER, new ItemStack(Items.CAVE_SPIDER_SPAWN_EGG));
		ANIMAL_SPAWN_EGGS.put(EntityType.CREAKING, new ItemStack(Items.CREAKING_SPAWN_EGG));
	}

	private ItemStack getSpawnEgg(MobSpawnerBlockEntity spawnerEntity) {
		try {
			// Dapatkan EntityType dari spawner menggunakan getRenderedEntity()
			EntityType<?> entityType = spawnerEntity.getLogic().getRenderedEntity(spawnerEntity.getWorld(), spawnerEntity.getPos()).getType();

			// Check apakah entity type ada spawn egg-nya
			if (ANIMAL_SPAWN_EGGS.containsKey(entityType)) {
				return ANIMAL_SPAWN_EGGS.get(entityType).copy();
			}

			LOGGER.debug("No spawn egg found for entity type: {}", entityType.toString());
			return ItemStack.EMPTY;

		} catch (Exception e) {
			LOGGER.error("Error getting spawn egg from spawner: {}", e.getMessage());
			return ItemStack.EMPTY;
		}
	}

	private void dropItem(World world, BlockPos pos, ItemStack itemStack) {
		if (!world.isClient) {
			// Drop item di posisi block yang dihancurkan
			ItemEntity itemEntity = new ItemEntity(world,
					pos.getX() + 0.5,
					pos.getY() + 0.5,
					pos.getZ() + 0.5,
					itemStack);

			// Set pickup delay sedikit agar tidak langsung terpickup
			itemEntity.setPickupDelay(10);

			world.spawnEntity(itemEntity);
		}
	}

	// Helper method untuk check valid pickaxe tools
	public static boolean isValidPickaxe(ItemStack itemStack) {
		// Menggunakan ItemTags.PICKAXES untuk kompatibilitas dengan mod lain
		return itemStack.isIn(ItemTags.PICKAXES);
	}
}
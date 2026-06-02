package com.goldskinmc.livingsponge.content;

import com.goldskinmc.livingsponge.LivingSpongeMod;
import com.goldskinmc.livingsponge.world.level.block.entity.LivingSpongeBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class LivingSpongeBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, LivingSpongeMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<LivingSpongeBlockEntity>> LIVING_SPONGE =
            BLOCK_ENTITY_TYPES.register(
                    "living_sponge",
                    () -> BlockEntityType.Builder.of(
                            LivingSpongeBlockEntity::new,
                            LivingSpongeBlocks.LIVING_SPONGE.get(),
                            LivingSpongeBlocks.CREATIVE_LIVING_SPONGE.get()
                    ).build(null)
            );

    private LivingSpongeBlockEntities() {
    }

    public static void register(final IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}

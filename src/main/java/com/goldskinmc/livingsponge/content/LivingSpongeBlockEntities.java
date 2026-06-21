package com.goldskinmc.livingsponge.content;

import com.goldskinmc.livingsponge.LivingSpongeMod;
import com.goldskinmc.livingsponge.world.level.block.entity.LivingSpongeBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Supplier;

public final class LivingSpongeBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, LivingSpongeMod.MOD_ID);

    public static final Supplier<BlockEntityType<LivingSpongeBlockEntity>> LIVING_SPONGE =
            BLOCK_ENTITY_TYPES.register(
                    "living_sponge",
                    () -> new BlockEntityType<>(
                            LivingSpongeBlockEntity::new,
                            Set.of()
                    )
            );

    private LivingSpongeBlockEntities() {
    }

    public static void register(final IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
        eventBus.addListener(LivingSpongeBlockEntities::onAddValidBlocks);
    }

    private static void onAddValidBlocks(final BlockEntityTypeAddBlocksEvent event) {
        event.modify(
                LIVING_SPONGE.get(),
                LivingSpongeBlocks.LIVING_SPONGE.get(),
                LivingSpongeBlocks.MATURE_LIVING_SPONGE.get(),
                LivingSpongeBlocks.OLD_LIVING_SPONGE.get(),
                LivingSpongeBlocks.CREATIVE_LIVING_SPONGE.get(),
                LivingSpongeBlocks.CREATIVE_MATURE_LIVING_SPONGE.get(),
                LivingSpongeBlocks.CREATIVE_OLD_LIVING_SPONGE.get()
        );
    }
}

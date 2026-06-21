package com.goldskinmc.livingsponge.content;

import com.goldskinmc.livingsponge.LivingSpongeMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class LivingSpongeCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LivingSpongeMod.MOD_ID);

    public static final Supplier<CreativeModeTab> LIVING_SPONGE_TAB = CREATIVE_MODE_TABS.register(
            "living_sponge",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.livingsponge"))
                    .icon(() -> new ItemStack(LivingSpongeItems.LIVING_SPONGE.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(LivingSpongeItems.LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.EXPANDED_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.HUGE_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.FLAT_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.EXPANDED_FLAT_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.HUGE_FLAT_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.WALL_FORMING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.EXPANDED_WALL_FORMING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.HUGE_WALL_FORMING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.FLAT_WALL_FORMING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.EXPANDED_FLAT_WALL_FORMING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.HUGE_FLAT_WALL_FORMING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.SOLIDIFYING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.EXPANDED_SOLIDIFYING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.HUGE_SOLIDIFYING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.FLAT_SOLIDIFYING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.EXPANDED_FLAT_SOLIDIFYING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.HUGE_FLAT_SOLIDIFYING_LIVING_SPONGE.get());

                        output.accept(LivingSpongeItems.MAGMA_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.EXPANDED_MAGMA_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.HUGE_MAGMA_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.MAGMA_FLAT_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.EXPANDED_MAGMA_FLAT_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.HUGE_MAGMA_FLAT_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.MAGMA_WALL_FORMING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.EXPANDED_MAGMA_WALL_FORMING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.HUGE_MAGMA_WALL_FORMING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.MAGMA_FLAT_WALL_FORMING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.EXPANDED_MAGMA_FLAT_WALL_FORMING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.HUGE_MAGMA_FLAT_WALL_FORMING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.MAGMA_VOLUME_SOLIDIFYING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.EXPANDED_MAGMA_VOLUME_SOLIDIFYING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.HUGE_MAGMA_VOLUME_SOLIDIFYING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.MAGMA_FLAT_SOLIDIFYING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.EXPANDED_MAGMA_FLAT_SOLIDIFYING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.HUGE_MAGMA_FLAT_SOLIDIFYING_LIVING_SPONGE.get());

                        output.accept(LivingSpongeBlocks.SPONGE_REMAINS.get());
                        output.accept(LivingSpongeItems.CREATIVE_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.CREATIVE_FLAT_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.CREATIVE_SOLIDIFYING_LIVING_SPONGE.get());
                        output.accept(LivingSpongeItems.CREATIVE_FLAT_SOLIDIFYING_LIVING_SPONGE.get());
                    })
                    .build()
    );

    private LivingSpongeCreativeTabs() {
    }

    public static void register(final IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}

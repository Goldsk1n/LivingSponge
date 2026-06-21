package com.goldskinmc.livingsponge.recipe.conditions;

import com.goldskinmc.livingsponge.LivingSpongeMod;
import com.goldskinmc.livingsponge.config.LivingSpongeConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public record ConfigFlagCondition(String flag) implements ICondition {
    public static final MapCodec<ConfigFlagCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("flag").forGetter(ConfigFlagCondition::flag)
    ).apply(instance, ConfigFlagCondition::new));

    private static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, LivingSpongeMod.MOD_ID);

    static {
        CONDITION_CODECS.register("config_flag", () -> CODEC);
    }

    public static void register(final IEventBus modEventBus) {
        CONDITION_CODECS.register(modEventBus);
    }

    @Override
    public boolean test(final IContext context) {
        return LivingSpongeConfig.configFlag(flag);
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}

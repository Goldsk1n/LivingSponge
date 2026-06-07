package com.goldskinmc.livingsponge.recipe.conditions;

import com.google.gson.JsonObject;
import com.goldskinmc.livingsponge.LivingSpongeMod;
import com.goldskinmc.livingsponge.config.LivingSpongeConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public record ConfigFlagCondition(String flag) implements ICondition {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(LivingSpongeMod.MOD_ID, "config_flag");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test(final IContext context) {
        return LivingSpongeConfig.configFlag(flag);
    }

    public static final class Serializer implements IConditionSerializer<ConfigFlagCondition> {
        public static final Serializer INSTANCE = new Serializer();

        private Serializer() {
        }

        @Override
        public void write(final JsonObject json, final ConfigFlagCondition value) {
            json.addProperty("flag", value.flag());
        }

        @Override
        public ConfigFlagCondition read(final JsonObject json) {
            return new ConfigFlagCondition(GsonHelper.getAsString(json, "flag"));
        }

        @Override
        public ResourceLocation getID() {
            return ID;
        }
    }
}

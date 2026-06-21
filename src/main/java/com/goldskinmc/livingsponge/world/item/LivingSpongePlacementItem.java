package com.goldskinmc.livingsponge.world.item;

import com.goldskinmc.livingsponge.content.LivingSpongeBlocks;
import com.goldskinmc.livingsponge.config.LivingSpongeConfig;
import com.goldskinmc.livingsponge.simulation.LivingSpongeLifecycleStage;
import com.goldskinmc.livingsponge.simulation.profile.MediumTrait;
import com.goldskinmc.livingsponge.simulation.profile.OutputTrait;
import com.goldskinmc.livingsponge.simulation.profile.RadiusTrait;
import com.goldskinmc.livingsponge.simulation.profile.SpreadTrait;
import com.goldskinmc.livingsponge.simulation.profile.SpongeTraits;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Consumer;

public final class LivingSpongePlacementItem extends BlockItem {
    private final SpongeTraits traits;
    private final boolean creativeOverrides;

    public LivingSpongePlacementItem(
            final Block block,
            final Properties properties,
            final SpongeTraits traits,
            final boolean creativeOverrides
    ) {
        super(block, properties);
        this.traits = traits;
        this.creativeOverrides = creativeOverrides;
    }

    public SpongeTraits traits() {
        return traits;
    }

    public boolean creativeOverrides() {
        return creativeOverrides;
    }

    @Override
    protected BlockState getPlacementState(final BlockPlaceContext context) {
        final LivingSpongeLifecycleStage placedStage = LivingSpongeConfig.values().lifecycle().placedSpongesStartMature()
                ? LivingSpongeLifecycleStage.MATURE
                : LivingSpongeLifecycleStage.YOUNG;
        return LivingSpongeBlocks.spongeStateFor(placedStage, traits, creativeOverrides);
    }

    @Override
    public void appendHoverText(
            final ItemStack stack,
            final Item.TooltipContext tooltipContext,
            final TooltipDisplay tooltipDisplay,
            final Consumer<Component> tooltipAdder,
            final TooltipFlag isAdvanced
    ) {
        super.appendHoverText(stack, tooltipContext, tooltipDisplay, tooltipAdder, isAdvanced);

        tooltipAdder.accept(traitLine("tooltip.livingsponge.traits.medium", mediumLabel(traits.medium(), creativeOverrides)));
        tooltipAdder.accept(traitLine("tooltip.livingsponge.traits.spread", spreadLabel(traits.spread())));
        tooltipAdder.accept(traitLine("tooltip.livingsponge.traits.output", outputLabel(traits.output())));
        tooltipAdder.accept(traitLine("tooltip.livingsponge.traits.radius", radiusLabel(creativeRadiusTrait(), LivingSpongeConfig.values())));
        tooltipAdder.accept(Component.empty());
        tooltipAdder.accept(mediumSummaryLine(traits.medium(), creativeOverrides));
        tooltipAdder.accept(descriptionLine(spreadSummaryKey(traits.spread())));
        tooltipAdder.accept(descriptionLine(outputSummaryKey(traits)));

        if (creativeOverrides) {
            tooltipAdder.accept(creativeSummaryLine());
        }
    }

    private static Component traitLine(final String labelKey, final Component value) {
        return Component.translatable(labelKey, value).withStyle(ChatFormatting.GRAY);
    }

    private static Component descriptionLine(final String key) {
        return Component.translatable(key).withStyle(ChatFormatting.GRAY);
    }

    private static Component mediumLabel(final MediumTrait medium, final boolean creativeOverrides) {
        if (creativeOverrides) {
            final LivingSpongeConfig.Creative creative = LivingSpongeConfig.values().creative();
            if (creative.supportsWater() && creative.supportsLava()) {
                return Component.translatable("tooltip.livingsponge.medium.creative").withStyle(ChatFormatting.AQUA);
            }
            if (creative.supportsWater()) {
                return Component.translatable("tooltip.livingsponge.medium.water").withStyle(ChatFormatting.AQUA);
            }
            if (creative.supportsLava()) {
                return Component.translatable("tooltip.livingsponge.medium.magma").withStyle(ChatFormatting.AQUA);
            }
            return Component.literal("Disabled").withStyle(ChatFormatting.AQUA);
        }
        return Component.translatable("tooltip.livingsponge.medium." + key(medium)).withStyle(ChatFormatting.AQUA);
    }

    private static Component spreadLabel(final SpreadTrait spread) {
        return Component.translatable("tooltip.livingsponge.spread." + key(spread)).withStyle(ChatFormatting.AQUA);
    }

    private static Component outputLabel(final OutputTrait output) {
        return Component.translatable("tooltip.livingsponge.output." + key(output)).withStyle(ChatFormatting.AQUA);
    }

    private static Component creativeSummaryLine() {
        final LivingSpongeConfig.Creative creative = LivingSpongeConfig.values().creative();
        return Component.literal(creative.speedMultiplier() + "x faster.").withStyle(ChatFormatting.DARK_GRAY);
    }

    private RadiusTrait creativeRadiusTrait() {
        if (creativeOverrides && LivingSpongeConfig.values().creative().forceHugeRadius()) {
            return RadiusTrait.HUGE;
        }
        return traits.radius();
    }

    private static Component radiusLabel(final RadiusTrait radius, final LivingSpongeConfig.BalanceValues values) {
        return Component.translatable(
                "tooltip.livingsponge.radius." + key(radius),
                Component.literal(Integer.toString(radius.radiusCap(values))).withStyle(ChatFormatting.AQUA)
        ).withStyle(ChatFormatting.AQUA);
    }

    private static Component mediumSummaryLine(final MediumTrait medium, final boolean creativeOverrides) {
        if (!creativeOverrides) {
            return descriptionLine(switch (medium) {
                case WATER -> "tooltip.livingsponge.summary.medium.water";
                case MAGMA -> "tooltip.livingsponge.summary.medium.magma";
            });
        }

        final LivingSpongeConfig.Creative creative = LivingSpongeConfig.values().creative();
        final String summary;
        if (creative.supportsWater() && creative.supportsLava()) {
            summary = "Water and lava.";
        } else if (creative.supportsWater()) {
            summary = "Water only.";
        } else if (creative.supportsLava()) {
            summary = "Lava only.";
        } else {
            summary = "Disabled.";
        }
        return Component.literal(summary).withStyle(ChatFormatting.GRAY);
    }

    private static String spreadSummaryKey(final SpreadTrait spread) {
        return switch (spread) {
            case VOLUME -> "tooltip.livingsponge.summary.spread.volume";
            case FLAT -> "tooltip.livingsponge.summary.spread.flat";
        };
    }

    private static String outputSummaryKey(final SpongeTraits spongeTraits) {
        return switch (spongeTraits.output()) {
            case NEUTRAL -> "tooltip.livingsponge.summary.output.neutral";
            case WALL_FORMING -> "tooltip.livingsponge.summary.output.wall_forming";
            case SOLIDIFYING -> "tooltip.livingsponge.summary.output.solidifying";
        };
    }

    private static String key(final Enum<?> value) {
        return value.name().toLowerCase();
    }
}

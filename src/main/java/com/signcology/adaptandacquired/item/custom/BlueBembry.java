package com.signcology.adaptandacquired.item.custom;

import com.signcology.adaptandacquired.skill.PlayerSkillsProvider;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class BlueBembry extends Item {

    public BlueBembry(Properties pProperties) {
        super(pProperties.food(new FoodProperties.Builder().nutrition(5).saturationMod(0.6F).build()));
    }


    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pLivingEntity) {
        pLivingEntity.getCapability(PlayerSkillsProvider.PLAYER_SKILLS).ifPresent( skills -> {
            if(!pLevel.isClientSide()) {
                skills.setSupSkill("None");
                pLevel.playSound(null, BlockPos.containing(pLivingEntity.position()), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.PLAYERS);
            }
        });

        return this.isEdible() ? pLivingEntity.eat(pLevel, pStack) : pStack;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.adaptandacquired.blue_bembry.tooltip"));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}

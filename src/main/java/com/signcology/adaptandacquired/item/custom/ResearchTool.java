package com.signcology.adaptandacquired.item.custom;

import com.signcology.adaptandacquired.skill.PlayerSkillsProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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

public class ResearchTool extends Item {

    private void PlaySkillUnlocked(Level level, UseOnContext pContext) {
        level.playSound(null, pContext.getClickedPos(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS);
    }
    private void PlaySkillUnlocked(LivingEntity entity) {
        entity.playSound(SoundEvents.AMETHYST_BLOCK_BREAK, 1, 1);
    }

    public ResearchTool(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if(!pLevel.isClientSide() && Screen.hasShiftDown()) {
            pPlayer.getCapability(PlayerSkillsProvider.PLAYER_SKILLS).ifPresent(skills -> {
                pPlayer.sendSystemMessage(Component.literal("Defense Skill: " + skills.getDefSkill()).withStyle(ChatFormatting.AQUA));
                pPlayer.sendSystemMessage(Component.literal("Offense Skill: " + skills.getOffSkill()).withStyle(ChatFormatting.AQUA));
                pPlayer.sendSystemMessage(Component.literal("Support Skill: " + skills.getSupSkill()).withStyle(ChatFormatting.AQUA));
            });
        }
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        return InteractionResultHolder.fail(itemstack);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        pPlayer.getCapability(PlayerSkillsProvider.PLAYER_SKILLS).ifPresent( skills -> {
            if (skills.getDefSkill().equals("None")) {
                if(pInteractionTarget.getType() == EntityType.TURTLE) {
                    skills.setDefSkill("Natural Armor");
                    pPlayer.sendSystemMessage(Component.literal("Acquired \"Natural Armor\" skill").withStyle(ChatFormatting.GREEN));
                    PlaySkillUnlocked(pInteractionTarget);
                }
                else if(pInteractionTarget.getType() == EntityType.IRON_GOLEM) {
                    skills.setDefSkill("Iron Armor Expert");
                    pPlayer.sendSystemMessage(Component.literal("Acquired \"Iron Armor Expert\" skill").withStyle(ChatFormatting.GREEN));
                    PlaySkillUnlocked(pInteractionTarget);
                }
            }
        });
        return InteractionResult.PASS;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        Block clickedBlock = level.getBlockState(pContext.getClickedPos()).getBlock();

        assert pContext.getPlayer() != null;

        pContext.getPlayer().getCapability(PlayerSkillsProvider.PLAYER_SKILLS).ifPresent( skills -> {
            if(!level.isClientSide()) {
                if (skills.getDefSkill().equals("None")) {
                    if(clickedBlock == Blocks.GOLD_BLOCK) {
                        skills.setDefSkill("Gold Armor Expert");
                        pContext.getPlayer().sendSystemMessage(Component.literal("Acquired \"Gold Armor Expert\" skill").withStyle(ChatFormatting.GREEN));
                        PlaySkillUnlocked(level, pContext);
                    }
                    else if(clickedBlock == Blocks.WHITE_WOOL) {
                        skills.setDefSkill("Fall Damage Immunity");
                        pContext.getPlayer().sendSystemMessage(Component.literal("Acquired \"Fall Damage Immunity\" skill").withStyle(ChatFormatting.GREEN));
                        PlaySkillUnlocked(level, pContext);
                    }
                }

                if (skills.getOffSkill().equals("None")) {
                    if(clickedBlock == Blocks.MAGMA_BLOCK) {
                        skills.setOffSkill("Burning Hands");
                        pContext.getPlayer().sendSystemMessage(Component.literal("Acquired \"Burning Hands\" skill").withStyle(ChatFormatting.GREEN));
                        PlaySkillUnlocked(level, pContext);
                    }
                }
                if (skills.getSupSkill().equals("None")) {
                    if(clickedBlock == Blocks.DIORITE) {
                        skills.setSupSkill("DIORITE");
                        pContext.getPlayer().sendSystemMessage(Component.literal("Acquired \"DIORITE (useless)\" skill").withStyle(ChatFormatting.GREEN));
                        PlaySkillUnlocked(level, pContext);
                    }
                    else if(clickedBlock == Blocks.HAY_BLOCK) {
                        skills.setSupSkill("Traveler");
                        pContext.getPlayer().sendSystemMessage(Component.literal("Acquired \"Traveler\" skill").withStyle(ChatFormatting.GREEN));
                        PlaySkillUnlocked(level, pContext);
                    }
                }

            }
        });


        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        if(Screen.hasShiftDown()) {
            pTooltipComponents.add(Component.translatable("tooltip.adaptandacquired.research_tool.shift_down"));
        } else {

            Entity entity = pStack.getEntityRepresentation();
            assert entity != null;

            //pTooltipComponents.add(Component.translatable("tooltip.adaptandacquired.research_tool.tooltip"));

            // Im lazy as fuck bruh
            pTooltipComponents.add(Component.literal("Right-Click blocks/mobs to obtain a skill").withStyle(ChatFormatting.YELLOW));
            pTooltipComponents.add(Component.literal("Not all blocks/mobs will give a skill and some may require some conditions").withStyle(ChatFormatting.YELLOW));
            pTooltipComponents.add(Component.literal("Shift + Right-Click with this item to see your skills").withStyle(ChatFormatting.LIGHT_PURPLE));


            // Crashes the game for some reason
            /*
            entity.getCapability(PlayerSkillsProvider.PLAYER_SKILLS).ifPresent(skills -> {
                pTooltipComponents.add(Component.translatable("tooltip.adaptandacquired.research_tool.tooltip"));
                //pTooltipComponents.add(Component.literal("Your skills").withStyle(ChatFormatting.LIGHT_PURPLE));
                //pTooltipComponents.add(Component.literal("Defense Skill: " + skills.getDefSkill() ).withStyle(ChatFormatting.LIGHT_PURPLE));
                //pTooltipComponents.add(Component.literal("Offense Skill: " + skills.getOffSkill() ).withStyle(ChatFormatting.LIGHT_PURPLE));
                //pTooltipComponents.add(Component.literal("Support Skill: " + skills.getSupSkill() ).withStyle(ChatFormatting.LIGHT_PURPLE));
            });
            */
        }

        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

}

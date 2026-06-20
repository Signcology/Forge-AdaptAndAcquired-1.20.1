package com.signcology.adaptandacquired.item.custom;

import com.signcology.adaptandacquired.skill.PlayerSkills;
import com.signcology.adaptandacquired.skill.PlayerSkillsProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
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

    private void PlaySkillUnlocked(Level level, Player player, BlockPos position) {
        level.playSound(null, position, SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS);
    }
    private void PlaySkillUnlocked(LivingEntity entity) {
        entity.playSound(SoundEvents.AMETHYST_BLOCK_BREAK, 1, 1);
    }

    private void CheckSkills(Level level, Player player, PlayerSkills skills, Block block) {
        if (skills.getDefSkill().equals("None")) {
            if(block == Blocks.GOLD_BLOCK) {
                skills.setDefSkill("Gold Armor Expert");
                player.sendSystemMessage(Component.literal("Acquired \"Gold Armor Expert\" skill").withStyle(ChatFormatting.GREEN));
                PlaySkillUnlocked(level, player, player.blockPosition());
            }
            else if(block == Blocks.WHITE_WOOL) {
                skills.setDefSkill("Fall Damage Immunity");
                player.sendSystemMessage(Component.literal("Acquired \"Fall Damage Immunity\" skill").withStyle(ChatFormatting.GREEN));
                PlaySkillUnlocked(level, player, player.blockPosition());
            }
        }
        else if (skills.getDefSkill().equals("Fall Damage Immunity")) {
            if(block == Blocks.END_STONE) {
                skills.setDefSkill("Gravity Shifter");
                player.sendSystemMessage(Component.literal("Acquired \"Gravity Shifter\" skill").withStyle(ChatFormatting.GREEN));
                PlaySkillUnlocked(level, player, player.blockPosition());
            }
        }

        if (skills.getOffSkill().equals("None")) {
            if(block == Blocks.MAGMA_BLOCK) {
                skills.setOffSkill("Burning Hands");
                player.sendSystemMessage(Component.literal("Acquired \"Burning Hands\" skill").withStyle(ChatFormatting.RED));
                PlaySkillUnlocked(level, player, player.blockPosition());
            }
            else if(block == Blocks.BLUE_ICE) {
                skills.setOffSkill("Freezing Hands");
                player.sendSystemMessage(Component.literal("Acquired \"Freezing Hands\" skill").withStyle(ChatFormatting.RED));
                PlaySkillUnlocked(level, player, player.blockPosition());
            }
        }

        if (skills.getSupSkill().equals("None")) {
            if(block == Blocks.HAY_BLOCK) {
                skills.setSupSkill("Traveler");
                player.sendSystemMessage(Component.literal("Acquired \"Traveler\" skill").withStyle(ChatFormatting.BLUE));
                PlaySkillUnlocked(level, player, player.blockPosition());
            }
            else if(block == Blocks.NETHER_PORTAL) {
                skills.setSupSkill("Nether Shifter");
                player.sendSystemMessage(Component.literal("Acquired \"Nether Shifter\" skill").withStyle(ChatFormatting.BLUE));
                PlaySkillUnlocked(level, player, player.blockPosition());
            }
            else if(block == Blocks.END_PORTAL) {
                skills.setSupSkill("Nether Shifter");
                player.sendSystemMessage(Component.literal("Acquired \"End Shifter\" skill").withStyle(ChatFormatting.BLUE));
                PlaySkillUnlocked(level, player, player.blockPosition());
            }
        }
    }

    private void CheckSkills(Player player, PlayerSkills skills, LivingEntity pInteractionTarget) {
        if (skills.getDefSkill().equals("None")) {
            if(pInteractionTarget.getType() == EntityType.TURTLE) {
                skills.setDefSkill("Natural Armor");
                player.sendSystemMessage(Component.literal("Acquired \"Natural Armor\" skill").withStyle(ChatFormatting.GREEN));
                PlaySkillUnlocked(player);
            }
            else if(pInteractionTarget.getType() == EntityType.IRON_GOLEM) {
                skills.setDefSkill("Iron Armor Expert");
                player.sendSystemMessage(Component.literal("Acquired \"Iron Armor Expert\" skill").withStyle(ChatFormatting.GREEN));
                PlaySkillUnlocked(player);
            }
            else if(pInteractionTarget.getType() == EntityType.COW) {
                skills.setDefSkill("Leather Armor Expert");
                player.sendSystemMessage(Component.literal("Acquired \"Leather Armor Expert\" skill").withStyle(ChatFormatting.GREEN));
                PlaySkillUnlocked(player);
            }
        }
        if (skills.getOffSkill().equals("Burning Hands")) {
            if(pInteractionTarget.getType() == EntityType.BLAZE) {
                skills.setOffSkill("Burning Hands+");
                player.sendSystemMessage(Component.literal("Acquired \"Burning Hands+\" skill").withStyle(ChatFormatting.RED));
                PlaySkillUnlocked(player);
            }
        }
        if (skills.getSupSkill().equals("None")) {
            if(pInteractionTarget.getType() == EntityType.ENDERMAN) {
                skills.setSupSkill("Teleporter");
                player.sendSystemMessage(Component.literal("Acquired \"Teleporter\" skill").withStyle(ChatFormatting.BLUE));
                PlaySkillUnlocked(player);
            }
        }
    }

    public ResearchTool(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if(!pLevel.isClientSide() && Screen.hasShiftDown()) {
            pPlayer.getCapability(PlayerSkillsProvider.PLAYER_SKILLS).ifPresent(skills -> {
                pPlayer.sendSystemMessage(Component.literal("Defense Skill: " + skills.getDefSkill()).withStyle(ChatFormatting.AQUA));
                switch (skills.getDefSkill()) {
                    case "Fall Damage Immunity" -> pPlayer.sendSystemMessage(Component.literal("Immune to fall damage"));
                    case "Gold Armor Expert" -> pPlayer.sendSystemMessage(Component.literal("Gold armor regenerates and gain Resistance II when wearing full gold set"));
                    case "Iron Armor Expert" -> pPlayer.sendSystemMessage(Component.literal("Gain Strength when wearing full iron set"));
                    case "Leather Armor Expert" -> pPlayer.sendSystemMessage(Component.literal("Gain buffs when wearing leather armor"));
                }
                pPlayer.sendSystemMessage(Component.literal("Offense Skill: " + skills.getOffSkill()).withStyle(ChatFormatting.AQUA));
                switch (skills.getOffSkill()) {
                    case "Burning Hands" -> pPlayer.sendSystemMessage(Component.literal("Alt + Right-Click burns and knockback enemies (cost 1 level)"));
                    case "Burning Hands+" -> pPlayer.sendSystemMessage(Component.literal("Alt + Right-Click burns and knockback enemies (cost 1 level), also gain fire immunity"));
                    case "Freezing Hands" -> pPlayer.sendSystemMessage(Component.literal("Alt + Right-Click to incase the target in ice (cost 1 level)"));
                }
                pPlayer.sendSystemMessage(Component.literal("Support Skill: " + skills.getSupSkill()).withStyle(ChatFormatting.AQUA));
                switch (skills.getSupSkill()) {
                    case "Traveler" -> pPlayer.sendSystemMessage(Component.literal("Slowly regenerate your hunger almost to full"));
                    case "Teleporter" -> pPlayer.sendSystemMessage(Component.literal("Alt + Right-Click to teleport 20 blocks in front of you (cost 1 level)"));
                    case "Nether Shifter" -> pPlayer.sendSystemMessage(Component.literal("Alt + Right-Click to swap between the nether and the overworld (cost 1 level)"));
                    case "End Shifter" -> pPlayer.sendSystemMessage(Component.literal("Alt + Right-Click to swap between the end and the overworld (cost 1 level)"));
                }
            });
        }
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        return InteractionResultHolder.fail(itemstack);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack pStack, Player pPlayer, LivingEntity pInteractionTarget, InteractionHand pUsedHand) {
        if(!pPlayer.level().isClientSide()) {
            pPlayer.getCapability(PlayerSkillsProvider.PLAYER_SKILLS).ifPresent(skills -> {
                CheckSkills(pPlayer, skills, pInteractionTarget);
            });
        }
        return InteractionResult.PASS;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        Block clickedBlock = level.getBlockState(pContext.getClickedPos()).getBlock();

        assert pContext.getPlayer() != null;

        pContext.getPlayer().getCapability(PlayerSkillsProvider.PLAYER_SKILLS).ifPresent( skills -> {
            if(!level.isClientSide()) {
                CheckSkills(level, pContext.getPlayer(), skills, clickedBlock);
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

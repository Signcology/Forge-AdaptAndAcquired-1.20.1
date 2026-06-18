package com.signcology.adaptandacquired.event;

import com.signcology.adaptandacquired.adaptandacquired;
import com.signcology.adaptandacquired.skill.PlayerSkills;
import com.signcology.adaptandacquired.skill.PlayerSkillsProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = adaptandacquired.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvent {

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player) {
            if(!event.getObject().getCapability(PlayerSkillsProvider.PLAYER_SKILLS).isPresent()) {
                event.addCapability(ResourceLocation.fromNamespaceAndPath(adaptandacquired.MODID, "perks"), new PlayerSkillsProvider());
                //System.out.println("ADDING CAP");
            }
            //if(!event.getObject().getCapability(PlayerSkillsProvider.PLAYER_SKILLS).isPresent()) {
            //    System.out.println("YOU DID IT WRONG");
            //}
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if(event.isWasDeath()) {
            System.out.println("Death");
            event.getOriginal().getCapability(PlayerSkillsProvider.PLAYER_SKILLS).ifPresent((oldStore) -> event.getEntity().getCapability(PlayerSkillsProvider.PLAYER_SKILLS).ifPresent((newStore) -> newStore.copyFrom(oldStore)));
        }
    }


    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerSkills.class);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        var player = event.player;
        if(event.side == LogicalSide.SERVER) {
            event.player.getCapability(PlayerSkillsProvider.PLAYER_SKILLS).ifPresent(skills -> {
                if(skills.getDefSkill().equals("Gold Armor Expert") && event.player.getRandom().nextFloat() < 0.005f) { // Once Every 10 Seconds on Avg
                    var strength = 2;
                    if (player.getInventory().getArmor(0).is(Items.GOLDEN_BOOTS)) {
                        var oldDamage = player.getInventory().getArmor(0).getDamageValue();
                        player.getInventory().getArmor(0).setDamageValue(oldDamage-strength);
                    }
                    if (player.getInventory().getArmor(1).is(Items.GOLDEN_LEGGINGS)) {
                        var oldDamage = player.getInventory().getArmor(1).getDamageValue();
                        player.getInventory().getArmor(1).setDamageValue(oldDamage-strength);
                    }
                    if (player.getInventory().getArmor(2).is(Items.GOLDEN_CHESTPLATE)) {
                        var oldDamage = player.getInventory().getArmor(2).getDamageValue();
                        player.getInventory().getArmor(2).setDamageValue(oldDamage-strength);
                    }
                    if (player.getInventory().getArmor(3).is(Items.GOLDEN_HELMET)) {
                        var oldDamage = player.getInventory().getArmor(3).getDamageValue();
                        player.getInventory().getArmor(3).setDamageValue(oldDamage-strength);
                    }
                    //event.player.sendSystemMessage(Component.literal("Subtracted Thirst"));
                }
                if (skills.getDefSkill().equals("Gold Armor Expert") &&
                        player.getInventory().getArmor(3).is(Items.GOLDEN_HELMET) &&
                        player.getInventory().getArmor(2).is(Items.GOLDEN_CHESTPLATE) &&
                        player.getInventory().getArmor(1).is(Items.GOLDEN_LEGGINGS) &&
                        player.getInventory().getArmor(0).is(Items.GOLDEN_BOOTS)
                ) {
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,1, 1, false, false));
                }
                if(skills.getSupSkill().equals("Traveler") && player.getFoodData().getFoodLevel() < 10 && event.player.getRandom().nextFloat() < 0.005f) {
                    player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() + 1);
                }
            });
        }
    }


    @SubscribeEvent
    public static void onLivingDamageEvent(LivingDamageEvent event) {
        //System.out.println("DAMAGE DETECTED");
        event.getEntity().getCapability(PlayerSkillsProvider.PLAYER_SKILLS).ifPresent(perks -> {
            if(perks.getDefSkill().equals("Fall Damage Immunity")) {
                if (event.getSource().is(DamageTypes.FALL)) {
                    //event.getEntity().sendSystemMessage(Component.literal("Fall Detected").withStyle(ChatFormatting.RED));
                    event.setCanceled(true);
                }
            }

        });
    }

    @SubscribeEvent
    public static void onPlayerInteraction(PlayerInteractEvent.EntityInteractSpecific event) {
        event.getEntity().getCapability(PlayerSkillsProvider.PLAYER_SKILLS).ifPresent(perks -> {
            if(perks.getOffSkill().equals("Burning Hands")) {
                event.getTarget().setSecondsOnFire(10);
            }

        });
    }

}

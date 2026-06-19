package com.signcology.adaptandacquired.event;

import com.mojang.brigadier.Command;
import com.signcology.adaptandacquired.AdaptAndAcquired;
import com.signcology.adaptandacquired.skill.PlayerSkills;
import com.signcology.adaptandacquired.skill.PlayerSkillsProvider;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.lang.ref.Reference;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = AdaptAndAcquired.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvent {

    private static void playEffectFire(Level level, Vec3 position) {
        // NOT WORKING ??????
        level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, position.x, position.y, position.z, 1d, 1d, 1d);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player) {
            if(!event.getObject().getCapability(PlayerSkillsProvider.PLAYER_SKILLS).isPresent()) {
                event.addCapability(ResourceLocation.fromNamespaceAndPath(AdaptAndAcquired.MODID, "perks"), new PlayerSkillsProvider());
                //System.out.println("ADDING CAP");
            }
            //if(!event.getObject().getCapability(PlayerSkillsProvider.PLAYER_SKILLS).isPresent()) {
            //    System.out.println("YOU DID IT WRONG");
            //}
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        //System.out.println("Player Cloned");
        if(event.isWasDeath()) {
            System.out.println("Death");
            event.getOriginal().getCapability(PlayerSkillsProvider.PLAYER_SKILLS).ifPresent((oldStore) ->
                    event.getEntity().getCapability(PlayerSkillsProvider.PLAYER_SKILLS).ifPresent((newStore) ->
                            newStore.copyFrom(oldStore)));
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
                // GOLD ARMOR EXPERT SKILL
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
                ) {player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,2, 1, false, false));}
                // IRON ARMOR EXPERT SKILL
                if (skills.getDefSkill().equals("Iron Armor Expert") &&
                        player.getInventory().getArmor(3).is(Items.IRON_HELMET) &&
                        player.getInventory().getArmor(2).is(Items.IRON_CHESTPLATE) &&
                        player.getInventory().getArmor(1).is(Items.IRON_LEGGINGS) &&
                        player.getInventory().getArmor(0).is(Items.IRON_BOOTS)
                ) {player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,2, 0, false, false));}
                // TRAVELER SKILL
                if(skills.getSupSkill().equals("Traveler") && player.getFoodData().getFoodLevel() < 18 && event.player.getRandom().nextFloat() < 0.005f) {
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
                    event.setCanceled(true);
                }
            }

        });
    }

    @SubscribeEvent
    public static void onPlayerInteraction(PlayerInteractEvent.EntityInteractSpecific event) {
        event.getEntity().getCapability(PlayerSkillsProvider.PLAYER_SKILLS).ifPresent(perks -> {
            if(perks.getOffSkill().equals("Burning Hands") && !event.getTarget().isOnFire() && Screen.hasAltDown()) {
                event.getTarget().setSecondsOnFire(10);
                playEffectFire(event.getLevel(), event.getTarget().position());

                if (event.getEntity().experienceLevel > 0) {
                    event.getEntity().experienceLevel -= 1;
                    var xp = EntityType.EXPERIENCE_ORB.spawn(event.getLevel().getServer().getLevel(event.getLevel().dimension()), event.getEntity().blockPosition(), MobSpawnType.COMMAND);
                    assert xp != null;
                    xp.value = 1;

                    var dir = new Vec3(
                            event.getTarget().position().x - event.getEntity().position().x,
                            1,
                            event.getTarget().position().z - event.getEntity().position().z
                    ).normalize();
                    var power = 5000;
                    dir.multiply(new Vec3(power,power,power));

                    event.getTarget().addDeltaMovement(dir);
                }
            }

        });
    }

}

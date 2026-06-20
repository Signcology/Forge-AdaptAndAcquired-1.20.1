package com.signcology.adaptandacquired.event;

import com.signcology.adaptandacquired.AdaptAndAcquired;
import com.signcology.adaptandacquired.skill.PlayerSkills;
import com.signcology.adaptandacquired.skill.PlayerSkillsProvider;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
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

@Mod.EventBusSubscriber(modid = AdaptAndAcquired.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvent {

    private static void updatePlayerLevel(Level level, Player player, int subtract) {
        player.experienceLevel -= subtract;
        var xp = EntityType.EXPERIENCE_ORB.spawn(level.getServer().getLevel(level.dimension()), player.blockPosition(), MobSpawnType.COMMAND);
        assert xp != null;
        xp.value = 1;
    }

    private static void playEffectFire(Level level, Vec3 position) {
        // NOT WORKING ??????
        level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, position.x, position.y, position.z, 1d, 1d, 1d);
    }

    private static void activateAbility(Level level, Player player, PlayerSkills skills, Entity target) {
        if(skills.getOffSkill().equals("Burning Hands") && target != null && !target.isOnFire() && Screen.hasAltDown()) {
            target.setSecondsOnFire(10);
            playEffectFire(level, target.position());

            if (player.experienceLevel > 0) {
                updatePlayerLevel(level, player, 1);

                var dir = new Vec3(
                        target.position().x - player.position().x,
                        1,
                        target.position().z - player.position().z
                ).normalize();
                var power = 5000;
                dir.multiply(new Vec3(power,power,power));

                target.addDeltaMovement(dir);
            }
        }

        if(skills.getSupSkill().equals("Teleporter") && player.experienceLevel > 0 && Screen.hasAltDown()) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,20, 0, false, false));
            var dis = 20;
            var newPos = player.position().add(player.getLookAngle().multiply(dis,dis,dis));
            player.teleportTo(newPos.x, newPos.y, newPos.z);
            updatePlayerLevel(level, player, 1);
            level.playSound(null, player.blockPosition(), SoundEvents.TRIDENT_RETURN, SoundSource.PLAYERS);
        }
        else if (skills.getSupSkill().equals("Nether Shifter") && player.experienceLevel > 0 && Screen.hasAltDown()) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,100, 0, false, false));
            level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS);
            if (level.dimension() == Level.OVERWORLD) {
                //player.changeDimension(level.getServer().getLevel(Level.NETHER));
                player.teleportTo(level.getServer().getLevel(Level.NETHER), player.position().x/8, player.position().y, player.position().z/8, null, player.yRotO, player.xRotO);
                level.getServer().getLevel(Level.NETHER).playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS);
                updatePlayerLevel(level.getServer().getLevel(Level.NETHER), player, 1);
            }
            else if (level.dimension() == Level.NETHER) {
                player.teleportTo(level.getServer().getLevel(Level.OVERWORLD), player.position().x*8, player.position().y, player.position().z*8, null, player.yRotO, player.xRotO);
                level.getServer().getLevel(Level.OVERWORLD).playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS);
                updatePlayerLevel(level.getServer().getLevel(Level.OVERWORLD), player, 1);
            }
        }
        else if (skills.getSupSkill().equals("End Shifter") && player.experienceLevel > 0 && Screen.hasAltDown()) {
            updatePlayerLevel(level, player, 1);
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,100, 0, false, false));
            level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS);
            if (level.dimension() == Level.OVERWORLD) {
                player.teleportTo(level.getServer().getLevel(Level.END), player.position().x, player.position().y, player.position().z, null, player.yRotO, player.xRotO);
                level.getServer().getLevel(Level.END).playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS);
                updatePlayerLevel(level.getServer().getLevel(Level.END), player, 1);
            }
            else if (level.dimension() == Level.END) {
                player.teleportTo(level.getServer().getLevel(Level.OVERWORLD), player.position().x, player.position().y, player.position().z, null, player.yRotO, player.xRotO);
                level.getServer().getLevel(Level.OVERWORLD).playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS);
                updatePlayerLevel(level.getServer().getLevel(Level.OVERWORLD), player, 1);
            }
        }

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
                else if (skills.getDefSkill().equals("Gold Armor Expert") &&
                        player.getInventory().getArmor(3).is(Items.GOLDEN_HELMET) &&
                        player.getInventory().getArmor(2).is(Items.GOLDEN_CHESTPLATE) &&
                        player.getInventory().getArmor(1).is(Items.GOLDEN_LEGGINGS) &&
                        player.getInventory().getArmor(0).is(Items.GOLDEN_BOOTS)
                ) {player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,2, 1, false, false));}
                // IRON ARMOR EXPERT SKILL
                else if (skills.getDefSkill().equals("Iron Armor Expert") &&
                        player.getInventory().getArmor(3).is(Items.IRON_HELMET) &&
                        player.getInventory().getArmor(2).is(Items.IRON_CHESTPLATE) &&
                        player.getInventory().getArmor(1).is(Items.IRON_LEGGINGS) &&
                        player.getInventory().getArmor(0).is(Items.IRON_BOOTS)
                ) {player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,2, 0, false, false));}
                else if(skills.getDefSkill().equals("Leather Armor Expert") && event.player.getRandom().nextFloat() < 0.005f) { // Once Every 10 Seconds on Avg
                    var strength = 2;
                    if (player.getInventory().getArmor(0).is(Items.LEATHER_BOOTS)) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,2, 0, false, false));
                    }
                    if (player.getInventory().getArmor(1).is(Items.LEATHER_LEGGINGS)) {
                        player.addEffect(new MobEffectInstance(MobEffects.JUMP,2, 0, false, false));
                    }
                    /*
                    if (player.getInventory().getArmor(2).is(Items.LEATHER_CHESTPLATE)) {
                        //player.addEffect(new MobEffectInstance(MobEffects.,2, 0, false, false));
                    }
                    */
                    if (player.getInventory().getArmor(3).is(Items.LEATHER_HELMET)) {
                        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,220, 0, false, false));
                    }
                }

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
    public static void RightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getLevel().isClientSide) {
            event.getEntity().getCapability(PlayerSkillsProvider.PLAYER_SKILLS).ifPresent(skills -> {
                var level = event.getLevel();
                var player = event.getEntity();
                activateAbility(level, player, skills, null);
            });
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (!event.getLevel().isClientSide) {
            event.getEntity().getCapability(PlayerSkillsProvider.PLAYER_SKILLS).ifPresent(skills -> {
                var level = event.getLevel();
                var player = event.getEntity();
                activateAbility(level, player, skills, null);
            });
        }
    }


    @SubscribeEvent
    public static void onPlayerInteraction(PlayerInteractEvent.EntityInteractSpecific event) {
        event.getEntity().getCapability(PlayerSkillsProvider.PLAYER_SKILLS).ifPresent(skills -> {
            activateAbility(event.getLevel(), event.getEntity(), skills, event.getTarget());
        });
    }

}

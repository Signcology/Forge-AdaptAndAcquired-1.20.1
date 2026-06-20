package com.signcology.adaptandacquired.event;

import com.signcology.adaptandacquired.AdaptAndAcquired;
import com.signcology.adaptandacquired.skill.PlayerSkills;
import com.signcology.adaptandacquired.skill.PlayerSkillsProvider;
import com.signcology.adaptandacquired.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
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

import java.util.Objects;

@Mod.EventBusSubscriber(modid = AdaptAndAcquired.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvent {

    private static void updatePlayerLevel(Level level, Player player, int subtract) {
        player.experienceLevel -= subtract;
        var xp = EntityType.EXPERIENCE_ORB.spawn(level.getServer().getLevel(level.dimension()), player.blockPosition(), MobSpawnType.COMMAND);
        assert xp != null;
        xp.value = 1;
    }

    private static void activateAbility(Level level, Player player, PlayerSkills skills, Entity target) {
        if(skills.getOffSkill().equals("Burning Hands") && target != null && !target.isOnFire() && Screen.hasAltDown()) {
            target.setSecondsOnFire(10);

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
        else if(skills.getOffSkill().equals("Freezing Hands") && target != null && !target.isOnFire() && Screen.hasAltDown()) {
            if (player.experienceLevel > 0) {
                updatePlayerLevel(level, player, 1);
                target.setTicksFrozen(200);


                var radius = 2;
                var start = new BlockPos(target.blockPosition().getX()-radius, target.blockPosition().getY()-radius, target.blockPosition().getZ()-radius);
                for (var i = 0; i<1+radius*2 ; i++)
                {
                    for (var j = 0; j<1+radius*2 ; j++)
                    {
                        for (var k = 0; k<1+radius*2 ; k++)
                        {
                            if (level.getBlockState(start.offset(i,j,k)).isAir() &&
                                    Math.abs(i) != radius &&
                                    Math.abs(j) != radius &&
                                    Math.abs(k) != radius
                            )
                                level.setBlock(start.offset(i,j,k), Blocks.ICE.defaultBlockState(), 2);
                        }
                    }
                }
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
            var nether = Objects.requireNonNull(level.getServer()).getLevel(Level.NETHER);
            var overworld = Objects.requireNonNull(level.getServer()).getLevel(Level.OVERWORLD);
            level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS);
            if (level.dimension() == Level.OVERWORLD) {
                var destination_pos = new BlockPos((int) player.position().x/8, (int) player.position().y, (int) player.position().z/8);
                while (nether.getBlockState(destination_pos).canOcclude()) {
                    destination_pos = destination_pos.offset(0,1,0);
                }
                player.teleportTo(nether, destination_pos.getX(), destination_pos.getY(), destination_pos.getZ(), null, player.yRotO, player.xRotO);
                nether.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS);
                updatePlayerLevel(nether, player, 1);
            }
            else if (level.dimension() == Level.NETHER) {
                var destination_pos = new BlockPos((int) player.position().x*8, (int) player.position().y, (int) player.position().z*8);
                while (overworld.getBlockState(destination_pos).canOcclude()) {
                    destination_pos = destination_pos.offset(0,1,0);
                }
                player.teleportTo(overworld, destination_pos.getX(), destination_pos.getY(), destination_pos.getZ(), null, player.yRotO, player.xRotO);
                overworld.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS);
                updatePlayerLevel(overworld, player, 1);
            }
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,100, 0, false, false));
        }
        else if (skills.getSupSkill().equals("End Shifter") && player.experienceLevel > 0 && Screen.hasAltDown()) {
            var end = Objects.requireNonNull(level.getServer()).getLevel(Level.END);
            var overworld = Objects.requireNonNull(level.getServer()).getLevel(Level.OVERWORLD);
            level.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS);
            if (level.dimension() == Level.OVERWORLD) {
                var destination_pos = new BlockPos((int) player.position().x, (int) player.position().y, (int) player.position().z);
                while (end.getBlockState(destination_pos).canOcclude()) {
                    destination_pos = destination_pos.offset(0,1,0);
                }
                player.teleportTo(end, destination_pos.getX(), destination_pos.getY(), destination_pos.getZ(), null, player.yRotO, player.xRotO);
                end.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS);
                updatePlayerLevel(end, player, 1);
            }
            else if (level.dimension() == Level.END) {
                var destination_pos = new BlockPos((int) player.position().x, (int) player.position().y, (int) player.position().z);
                while (overworld.getBlockState(destination_pos).canOcclude()) {
                    destination_pos = destination_pos.offset(0,1,0);
                }
                player.teleportTo(overworld, destination_pos.getX(), destination_pos.getY(), destination_pos.getZ(), null, player.yRotO, player.xRotO);
                overworld.playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS);
                updatePlayerLevel(overworld, player, 1);
            }
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,100, 0, false, false));
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

        event.player.getCapability(PlayerSkillsProvider.PLAYER_SKILLS).ifPresent(skills -> {
            // GOLD ARMOR EXPERT SKILL
            ServerLevel serverLevel = null;
            if(!skills.getDefSkill().equals("None") || !skills.getOffSkill().equals("None") || !skills.getSupSkill().equals("None")) {
                serverLevel = Objects.requireNonNull(player.level().getServer()).getLevel(player.level().dimension());
                if (event.player.getRandom().nextFloat() < 0.1f) {
                    assert serverLevel != null;
                    switch (skills.getDefSkill()) {
                        case "Gold Armor Expert" -> serverLevel.sendParticles(ParticleTypes.WAX_ON, player.position().x, player.position().y+1, player.position().z, 1, .15, .5, .15, 1);
                        case "Iron Armor Expert" -> serverLevel.sendParticles(ParticleTypes.WAX_OFF, player.position().x, player.position().y+1, player.position().z, 1, .15, .5, .15, 1);
                        case "Leather Armor Expert" -> serverLevel.sendParticles(ParticleTypes.COMPOSTER, player.position().x, player.position().y+1, player.position().z, 1, .15, .5, .15, 1);
                    }
                    switch (skills.getSupSkill()) {
                        case "Teleporter" -> serverLevel.sendParticles(ParticleTypes.PORTAL, player.position().x, player.position().y+1, player.position().z, 1, .15, .5, .15, 1);
                        case "Nether Shifter" -> serverLevel.sendParticles(ParticleTypes.CRIMSON_SPORE, player.position().x, player.position().y+1, player.position().z, 1, .15, .5, .15, 0.01);
                        case "End Shifter" -> serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, player.position().x, player.position().y+1, player.position().z, 1, .15, .5, .15, 0.01);
                    }

                }
            }

            //serverLevel.sendParticles(ParticleTypes.WAX_OFF, player.position().x, player.position().y+1, player.position().z, 1, .5, .5, .5, 1);

            if(event.side == LogicalSide.SERVER && skills.getDefSkill().equals("Gold Armor Expert") && event.player.getRandom().nextFloat() < 0.005f) { // Once Every 10 Seconds on Avg
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
            else if (event.side == LogicalSide.SERVER && skills.getDefSkill().equals("Gold Armor Expert") &&
                    player.getInventory().getArmor(3).is(Items.GOLDEN_HELMET) &&
                    player.getInventory().getArmor(2).is(Items.GOLDEN_CHESTPLATE) &&
                    player.getInventory().getArmor(1).is(Items.GOLDEN_LEGGINGS) &&
                    player.getInventory().getArmor(0).is(Items.GOLDEN_BOOTS)
            ) {player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,2, 1, false, false));}
            // IRON ARMOR EXPERT SKILL
            else if (event.side == LogicalSide.SERVER && skills.getDefSkill().equals("Iron Armor Expert") &&
                    player.getInventory().getArmor(3).is(Items.IRON_HELMET) &&
                    player.getInventory().getArmor(2).is(Items.IRON_CHESTPLATE) &&
                    player.getInventory().getArmor(1).is(Items.IRON_LEGGINGS) &&
                    player.getInventory().getArmor(0).is(Items.IRON_BOOTS)
            ) {player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,2, 0, false, false));}
            else if(event.side == LogicalSide.SERVER && skills.getDefSkill().equals("Leather Armor Expert")) {
                if (player.getInventory().getArmor(0).is(Items.LEATHER_BOOTS)) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,2, 0, false, false));
                }
                if (player.getInventory().getArmor(1).is(Items.LEATHER_LEGGINGS)) {
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP,2, 0, false, false));
                }
                if (player.getInventory().getArmor(3).is(Items.LEATHER_HELMET)) {
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,220, 0, false, false));
                }
            }
            else if(skills.getDefSkill().equals("Gravity Shifter") && Screen.hasAltDown()) {
                if (event.side == LogicalSide.SERVER) {
                    player.addEffect(new MobEffectInstance(MobEffects.LEVITATION,2, 9, false, false));
                    player.level().playSound(null, player.blockPosition(), SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS);
                }
                //player.sendSystemMessage(Component.literal("Gravity Shifter TEST").withStyle(ChatFormatting.LIGHT_PURPLE));
                assert serverLevel != null;
                serverLevel.sendParticles(ParticleTypes.WAX_OFF, player.position().x, player.position().y, player.position().z, 1, 0, 0, 0, 1);
                //ParticleUtils.spawnParticlesOnBlockFaces(player.level(), player.blockPosition(), ParticleTypes.WAX_OFF, UniformInt.of(3, 5));
                //player.playSound(SoundEvents.SOUL_ESCAPE);
            }
            else if(event.side == LogicalSide.SERVER && skills.getDefSkill().equals("Repel") && Screen.hasAltDown()) {
                var list = player.level().getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, player, AABB.ofSize(player.position(), 10,10,10));
                for (var i = 0; i<list.toArray().length; i++) {
                    player.sendSystemMessage(Component.literal("Repel TEST").withStyle(ChatFormatting.LIGHT_PURPLE));
                }
                //player.addEffect(new MobEffectInstance(MobEffects.LEVITATION,2, 9, false, false));

                //player.playSound(SoundEvents.SOUL_ESCAPE);
            }

            // TRAVELER SKILL
            if(event.side == LogicalSide.SERVER && skills.getSupSkill().equals("Traveler") && player.getFoodData().getFoodLevel() < 18 && event.player.getRandom().nextFloat() < 0.005f) {
                player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() + 1);
            }
        });

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

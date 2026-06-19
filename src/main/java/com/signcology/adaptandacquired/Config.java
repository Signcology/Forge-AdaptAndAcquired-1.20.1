package com.signcology.adaptandacquired;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = AdaptAndAcquired.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue ALLOW_CHISEL = BUILDER
            .comment("Is the chisel allowed to be used")
            .define("allowChisel", true);

    private static final ForgeConfigSpec.IntValue AIR_SEARCH_DISTANCE = BUILDER
            .comment("How far the mod will look for transparent block")
            .defineInRange("airSearchDistance", 1, 1, 255);

    private static final ForgeConfigSpec.IntValue UPDATE_DISTANCE = BUILDER
            .comment("How far the mod check and update blocks")
            .defineInRange("updateDistance", 2, 1, 255);

    private static final ForgeConfigSpec.IntValue UPDATE_EXPLOSION_DISTANCE = BUILDER
            .comment("How far the mod check and update blocks from a explosion")
            .defineInRange("updateExplosionDistance", 5, 1, 255);

    // a list of strings that are treated as resource locations for items
    /*
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);
    */

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean allowChisel;
    public static int airSearchDistance;
    public static int updateDistance;
    public static int updateExplosionDistance;
    public static Set<Item> items;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(ResourceLocation.tryParse(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        allowChisel = ALLOW_CHISEL.get();
        airSearchDistance = AIR_SEARCH_DISTANCE.get();
        updateDistance = UPDATE_DISTANCE.get();
        updateExplosionDistance = UPDATE_EXPLOSION_DISTANCE.get();

        // convert the list of strings into a set of items
        /*
        items = ITEM_STRINGS.get().stream()
                .map(itemName -> ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(itemName)))
                .collect(Collectors.toSet());

         */
    }
}

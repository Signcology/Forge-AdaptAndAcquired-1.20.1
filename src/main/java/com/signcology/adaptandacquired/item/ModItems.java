package com.signcology.adaptandacquired.item;

import com.signcology.adaptandacquired.adaptandacquired;
import com.signcology.adaptandacquired.item.custom.BlueBembry;
import com.signcology.adaptandacquired.item.custom.GreenGrum;
import com.signcology.adaptandacquired.item.custom.RedRawchew;
import com.signcology.adaptandacquired.item.custom.ResearchTool;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, adaptandacquired.MODID);


    public static final RegistryObject<Item> TESTYTIMMY = ITEMS.register("testytimmy",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RESEARCH_TOOL = ITEMS.register("research_tool",
            () -> new ResearchTool(new Item.Properties()
                    .durability(64)));

    public static final RegistryObject<Item> GREEN_GRUM = ITEMS.register("green_grum",
            () -> new GreenGrum(new Item.Properties()));
    public static final RegistryObject<Item> RED_RAWCHEW = ITEMS.register("red_rawchew",
            () -> new RedRawchew(new Item.Properties()));
    public static final RegistryObject<Item> BLUE_BEMBRY = ITEMS.register("blue_bembry",
            () -> new BlueBembry(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

package com.signcology.adaptandacquired.item;

import com.signcology.adaptandacquired.AdaptAndAcquired;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AdaptAndAcquired.MODID);

    public static final RegistryObject<CreativeModeTab> ADAPTANDACQUIRED = CREATIVE_MODE_TABS.register("adaptandacquired_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.RESEARCH_TOOL.get()))
                    .title(Component.translatable("creativetab.adaptandacquired.all"))
                    .displayItems((itemDisplayParameters, output) -> {
                        //output.accept(ModBlocks.HARDSTONE.get());
                        output.accept(ModItems.RESEARCH_TOOL.get());
                        output.accept(ModItems.GREEN_GRUM.get());
                        output.accept(ModItems.RED_RAWCHEW.get());
                        output.accept(ModItems.BLUE_BEMBRY.get());
                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}

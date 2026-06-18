package com.signcology.adaptandacquired.util;

import com.signcology.adaptandacquired.adaptandacquired;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> HARD_BLOCK = createTag("hard_block");
        public static final TagKey<Block> STONE_BLOCK = createTag("stone_block");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(adaptandacquired.MODID, name));
        }
    }

    public static class Items {

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(adaptandacquired.MODID, name));
        }
    }
}

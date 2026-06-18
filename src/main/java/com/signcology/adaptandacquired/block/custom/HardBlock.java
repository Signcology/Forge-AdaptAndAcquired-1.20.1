package com.signcology.adaptandacquired.block.custom;

import net.minecraft.world.level.block.Block;

public class HardBlock extends Block {
    public HardBlock(Properties properties) {
        super(properties);
    }
    /*
    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        //pLevel.playSound(pPlayer, pPos, SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS, 1f, 1f);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        pTooltipComponents.add(Component.translatable("tooltip.surfacelevel.hardblock.tooltip"));
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
    }

     */
}

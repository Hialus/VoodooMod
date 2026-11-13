package de.morrien.voodoo.block;

import com.mojang.serialization.MapCodec;
import de.morrien.voodoo.blockentity.BlockEntityTypeRegistry;
import de.morrien.voodoo.blockentity.PoppetShelfBlockEntity;
import de.morrien.voodoo.container.PoppetShelfContainer;
import de.morrien.voodoo.util.PoppetShelfChunkLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

import static de.morrien.voodoo.VoodooConfig.COMMON;

/**
 * Created by Timor Morrien
 */
public class PoppetShelfBlock extends BaseEntityBlock {
    public static final MapCodec<PoppetShelfBlock> CODEC = simpleCodec(PoppetShelfBlock::new);
    protected static final VoxelShape voxelShape = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

    public PoppetShelfBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<PoppetShelfBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!world.isClientSide) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof PoppetShelfBlockEntity) {
                MenuProvider containerProvider = new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        final PoppetShelfBlockEntity poppetShelf = (PoppetShelfBlockEntity) blockEntity;
                        Component component;
                        if (poppetShelf.getOwnerName() == null) {
                            component = Component.translatable("text.voodoo.poppet.not_bound");
                        } else {
                            final Player player = world.getPlayerByUUID(poppetShelf.getOwnerUuid());
                            if (player != null && !poppetShelf.getOwnerName().equals(player.getName().getString()))
                                poppetShelf.setOwnerName(player.getName().getString());
                            component = Component.literal(poppetShelf.getOwnerName());
                        }
                        return Component.translatable("screen.voodoo.poppet_shelf", component);
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int i, Inventory playerventory, Player playerEntity) {
                        return new PoppetShelfContainer(i, playerventory, (PoppetShelfBlockEntity) blockEntity);
                    }
                };
                player.openMenu(containerProvider);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof PoppetShelfBlockEntity) {
                Containers.dropContents(world, pos, (PoppetShelfBlockEntity) blockEntity);
                world.updateNeighbourForOutputSignal(pos, this);
            }
            if (world instanceof ServerLevel serverLevel)
                PoppetShelfChunkLoader.setShelfLoaded(serverLevel, pos, false);
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        final BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof PoppetShelfBlockEntity && placer != null) {
            ((PoppetShelfBlockEntity) blockEntity).setOwnerName(placer.getName().getString());
            ((PoppetShelfBlockEntity) blockEntity).setOwnerUuid(placer.getUUID());
            if (world instanceof ServerLevel serverLevel && COMMON.poppetShelf.chunkLoading.get())
                PoppetShelfChunkLoader.setShelfLoaded(serverLevel, pos, true);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return voxelShape;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BlockEntityTypeRegistry.poppetShelfBlockEntity.get(), PoppetShelfBlockEntity::tick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new PoppetShelfBlockEntity(blockPos, blockState);
    }
}

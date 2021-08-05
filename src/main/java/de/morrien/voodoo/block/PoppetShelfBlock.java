package de.morrien.voodoo.block;

import de.morrien.voodoo.blockentity.PoppetShelfBlockEntity;
import de.morrien.voodoo.container.PoppetShelfContainer;
import de.morrien.voodoo.blockentity.BlockEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nullable;

/**
 * Created by Timor Morrien
 */
public class PoppetShelfBlock extends BaseEntityBlock {
    protected static final VoxelShape voxelShape = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

    public PoppetShelfBlock() {
        super(Properties
                .of(Material.STONE, MaterialColor.NETHER)
                .strength(6, 6)
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(2)
                .sound(SoundType.NETHER_BRICKS)
                .noOcclusion()
        );
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof PoppetShelfBlockEntity) {
                MenuProvider containerProvider = new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        final PoppetShelfBlockEntity poppetShelf = (PoppetShelfBlockEntity) blockEntity;
                        Component component;
                        if (poppetShelf.getOwnerName() == null) {
                            component = new TranslatableComponent("text.voodoo.poppet.not_bound");
                        } else {
                            final Player player = world.getPlayerByUUID(poppetShelf.getOwnerUuid());
                            if (player != null && !poppetShelf.getOwnerName().equals(player.getName().getString()))
                                poppetShelf.setOwnerName(player.getName().getString());
                            component = new TextComponent(poppetShelf.getOwnerName());
                        }
                        return new TranslatableComponent("screen.voodoo.poppet_shelf", component);
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int i, Inventory playerventory, Player playerEntity) {
                        return new PoppetShelfContainer(i, world, pos, playerventory, playerEntity);
                    }
                };
                NetworkHooks.openGui((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Container) {
                Containers.dropContents(world, pos, (Container) blockEntity);
                world.updateNeighbourForOutputSignal(pos, this);
            }

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

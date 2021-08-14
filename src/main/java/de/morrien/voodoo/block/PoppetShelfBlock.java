package de.morrien.voodoo.block;

import de.morrien.voodoo.container.PoppetShelfContainer;
import de.morrien.voodoo.tileentity.PoppetShelfTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

/**
 * Created by Timor Morrien
 */
public class PoppetShelfBlock extends Block {
    protected static final VoxelShape voxelShape = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

    public PoppetShelfBlock() {
        super(Properties
                .of(Material.STONE, MaterialColor.NETHER)
                .strength(6, 6)
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(2)
                .sound(SoundType.STONE)
                .noOcclusion()
        );
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof PoppetShelfTileEntity) {
                INamedContainerProvider containerProvider = new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        final PoppetShelfTileEntity poppetShelf = (PoppetShelfTileEntity) tileEntity;
                        ITextComponent component;
                        if (poppetShelf.getOwnerName() == null) {
                            component = new TranslationTextComponent("text.voodoo.poppet.not_bound");
                        } else {
                            final PlayerEntity player = world.getPlayerByUUID(poppetShelf.getOwnerUuid());
                            if (player != null && !poppetShelf.getOwnerName().equals(player.getName().getString()))
                                poppetShelf.setOwnerName(player.getName().getString());
                            component = new StringTextComponent(poppetShelf.getOwnerName());
                        }
                        return new TranslationTextComponent("screen.voodoo.poppet_shelf", component);
                    }

                    @Override
                    public Container createMenu(int i, PlayerInventory playerventory, PlayerEntity playerEntity) {
                        return new PoppetShelfContainer(i, world, pos, playerventory, playerEntity);
                    }
                };
                NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getBlockPos());
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = world.getBlockEntity(pos);
            if (tileentity instanceof PoppetShelfTileEntity) {
                InventoryHelper.dropContents(world, pos, ((PoppetShelfTileEntity) tileentity).getInventory());
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        final TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof PoppetShelfTileEntity && placer != null) {
            ((PoppetShelfTileEntity) tileEntity).setOwnerUuid(placer.getUUID());
            ((PoppetShelfTileEntity) tileEntity).setOwnerName(placer.getName().getString());
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return voxelShape;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new PoppetShelfTileEntity();
    }
}

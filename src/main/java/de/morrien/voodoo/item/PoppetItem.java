package de.morrien.voodoo.item;

import de.morrien.voodoo.*;
import de.morrien.voodoo.entity.EntityPoppetItem;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import net.minecraft.item.Item.Properties;

import static de.morrien.voodoo.VoodooUtil.*;

/**
 * Created by Timor Morrien
 */
public class PoppetItem extends Item {
    private Poppet.PoppetType poppetType;

    public PoppetItem(Poppet.PoppetType poppetType) {
        super(new Properties()
                .tab(VoodooGroup.INSTANCE)
                .durability(poppetType.getDurability())
                .setNoRepair()
        );
        this.poppetType = poppetType;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return poppetType.getDurability();
    }

    public Poppet.PoppetType getPoppetType() {
        return poppetType;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return stack.getItem() == ItemRegistry.poppetMap.get(Poppet.PoppetType.VOODOO).get();
    }

    @Nullable
    @Override
    public Entity createEntity(World world, Entity itemEntity, ItemStack itemstack) {
        return new EntityPoppetItem(world, (ItemEntity) itemEntity, itemstack);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        if (state.getBlock() instanceof BedBlock) {
            if (!world.isClientSide && player != null && player.isShiftKeyDown()) {
                MinecraftServer server = world.getServer();
                if (server != null) {
                    if (state.getValue(BedBlock.PART) != BedPart.HEAD) {
                        pos = pos.relative(state.getValue(BedBlock.FACING));
                    }
                    PlayerEntity sleeper = null;
                    for (ServerPlayerEntity playerEntity : server.getPlayerList().getPlayers()) {
                        BlockPos bedPos = playerEntity.getRespawnPosition();
                        if (bedPos != null && bedPos.equals(pos) && (sleeper == null || playerEntity.getSleepTimer() < sleeper.getSleepTimer())) {
                            sleeper = playerEntity;
                        }
                    }
                    if (sleeper != null) {
                        bind(context.getItemInHand(), sleeper);
                    }
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public void releaseUsing(ItemStack stack, World world, LivingEntity livingEntity, int timeLeft) {
        if (!world.isClientSide &&
                timeLeft <= 72000 - VoodooConfig.COMMON.voodoo.pullDuration.get() &&
                livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            PlayerEntity boundPlayer = getBoundPlayer(stack, world);
            if (boundPlayer != null) {
                ItemStack offhand = livingEntity.getOffhandItem();
                if (VoodooConfig.COMMON.voodoo.enableNeedle.get() && !offhand.isEmpty() && offhand.getItem() == ItemRegistry.needle.get()) {
                    boundPlayer.hurt(new VoodooDamageSource(VoodooDamageSource.VoodooDamageType.NEEDLE), 1f);
                    offhand.shrink(1);
                    stack.hurtAndBreak(1, livingEntity, (e) -> {
                        player.broadcastBreakEvent(player.getUsedItemHand());
                    });
                } else if (VoodooConfig.COMMON.voodoo.enablePush.get()) {
                    Poppet voodooProtectionPoppet = Poppet.getPlayerPoppet(boundPlayer, Poppet.PoppetType.VOODOO_PROTECTION);
                    stack.hurtAndBreak(1, livingEntity, (e) -> {
                        player.broadcastBreakEvent(player.getUsedItemHand());
                    });
                    if (voodooProtectionPoppet != null) {
                        voodooProtectionPoppet.use();
                        player.displayClientMessage(new TranslationTextComponent("text.voodoo.voodoo_protection.had", boundPlayer.getName()), true);
                    } else {
                        Vector3d vec = player.getLookAngle();
                        boundPlayer.push(vec.x * 1.5, vec.y * 1.2, vec.z * 1.5);
                        boundPlayer.hurtMarked = true;
                    }
                }
            }
        }
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        UUID uuid = getBoundUUID(stack);
        if (uuid == null) return;
        if (world != null) {
            PlayerEntity player = world.getPlayerByUUID(uuid);
            if (player != null) {
                final String playerName = player.getName().getString();
                if (!playerName.equals(getBoundName(stack))) {
                    stack.getTag().putString(BOUND_NAME, playerName);
                }
                tooltip.add(new StringTextComponent(I18n.get("text.voodoo.taglock_kit.bound").replace("&p", playerName)));
                return;
            }
        }
        if (isBound(stack)) {
            tooltip.add(new StringTextComponent(I18n.get("text.voodoo.taglock_kit.bound").replace("&p", getBoundName(stack))));
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return (poppetType == Poppet.PoppetType.VOODOO) ? 72000 : 0;
    }

    @Override
    public boolean canBeDepleted() {
        return poppetType.hasDurability();
    }
}

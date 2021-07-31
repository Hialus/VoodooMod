package de.morrien.voodoo.item;

import de.morrien.voodoo.Poppet;
import de.morrien.voodoo.VoodooConfig;
import de.morrien.voodoo.VoodooDamageSource;
import de.morrien.voodoo.VoodooGroup;
import de.morrien.voodoo.entity.EntityPoppetItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Created by Timor Morrien
 */
public class PoppetItem extends Item {
    public static final String BOUND_UUID = "BoundUUID";
    public static final String BOUND_NAME = "BoundName";
    private Poppet.PoppetType poppetType;

    public PoppetItem(Poppet.PoppetType poppetType) {
        super(new Properties()
                .group(VoodooGroup.INSTANCE)
                .maxDamage(poppetType.getDurability())
                .setNoRepair()
        );
        this.poppetType = poppetType;
    }

    public static PlayerEntity getBoundPlayer(ItemStack stack, World world) {
        if (isBound(stack)) {
            return world.getPlayerByUuid(getBoundUUID(stack));
        }
        return null;
    }

    public static boolean isBound(ItemStack stack) {
        return stack.hasTag() && stack.getTag().hasUniqueId(BOUND_UUID);
    }

    public static UUID getBoundUUID(ItemStack stack) {
        if (isBound(stack))
            return stack.getTag().getUniqueId(BOUND_UUID);
        else
            return null;
    }

    public static String getBoundName(ItemStack stack) {
        if (isBound(stack))
            return stack.getTag().getString(BOUND_NAME);
        else
            return null;
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
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        playerIn.setActiveHand(handIn);
        return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity livingEntity, int timeLeft) {
        if (!worldIn.isRemote &&
                timeLeft <= 72000 - VoodooConfig.COMMON.voodoo.pullDuration.get() &&
                livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            PlayerEntity boundPlayer = getBoundPlayer(stack, worldIn);
            if (boundPlayer != null) {
                ItemStack offhand = livingEntity.getHeldItemOffhand();
                if (VoodooConfig.COMMON.voodoo.enableNeedle.get() && !offhand.isEmpty() && offhand.getItem() == ItemRegistry.needle.get()) {
                    boundPlayer.attackEntityFrom(new VoodooDamageSource(VoodooDamageSource.VoodooDamageType.NEEDLE), 1f);
                    offhand.shrink(1);
                    stack.damageItem(1, livingEntity, (e) -> {
                        player.sendBreakAnimation(player.getActiveHand());
                    });
                } else if (VoodooConfig.COMMON.voodoo.enablePush.get()) {
                    Poppet voodooProtectionPoppet = Poppet.getPlayerPoppet(boundPlayer, Poppet.PoppetType.VOODOO_PROTECTION);
                    stack.damageItem(1, livingEntity, (e) -> {
                        player.sendBreakAnimation(player.getActiveHand());
                    });
                    if (voodooProtectionPoppet != null) {
                        voodooProtectionPoppet.use();
                        player.sendStatusMessage(new TranslationTextComponent("text.voodoo.voodoo_protection.had", boundPlayer.getName()), true);
                    } else {
                        Vector3d vec = player.getLookVec();
                        boundPlayer.addVelocity(vec.x * 1.5, vec.y * 1.2, vec.z * 1.5);
                        boundPlayer.velocityChanged = true;
                    }
                }
            }
        }
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        UUID uuid = getBoundUUID(stack);
        if (uuid == null) return;
        if (worldIn != null) {
            PlayerEntity player = worldIn.getPlayerByUuid(uuid);
            if (player != null) {
                final String playerName = player.getName().getString();
                if (!playerName.equals(getBoundName(stack))) {
                    stack.getTag().putString(BOUND_NAME, playerName);
                }
                tooltip.add(new StringTextComponent(I18n.format("text.voodoo.taglock_kit.bound").replace("&p", playerName)));
                return;
            }
        }
        if (isBound(stack)) {
            tooltip.add(new StringTextComponent(I18n.format("text.voodoo.taglock_kit.bound").replace("&p", getBoundName(stack))));
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return (poppetType == Poppet.PoppetType.VOODOO) ? 72000 : 0;
    }

    @Override
    public boolean isDamageable() {
        return poppetType.hasDurability();
    }
}

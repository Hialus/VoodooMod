package de.morrien.voodoo;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import static net.minecraftforge.common.ForgeConfigSpec.*;

public class VoodooConfig {
    public static final Client CLIENT;
    public static final Common COMMON;
    static final ForgeConfigSpec clientSpec;
    static final ForgeConfigSpec commonSpec;

    static {
        final Pair<Client, ForgeConfigSpec> clientSpecPair = new Builder().configure(VoodooConfig.Client::new);
        clientSpec = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();

        final Pair<Common, ForgeConfigSpec> commonSpecPair = new Builder().configure(Common::new);
        commonSpec = commonSpecPair.getRight();
        COMMON = commonSpecPair.getLeft();
    }

    public static class Client {

        Client(Builder builder) {
        }
    }

    public static class Common {
        public VoodooPoppet voodoo;
        public VoodooProtectionPoppet voodooProtection;
        public DeathProtectionPoppet deathProtection;
        public FireProtectionPoppet fireProtection;
        public WaterProtectionPoppet waterProtection;
        public FallProtectionPoppet fallProtection;
        public ExplosionProtectionPoppet explosionProtection;
        public ProjectileProtectionPoppet projectileProtection;
        public WitherProtectionPoppet witherProtection;
        public HungerProtectionPoppet hungerProtection;
        public PotionProtectionPoppet potionProtection;
        public VoidProtectionPoppet voidProtection;

        Common(Builder builder) {
            builder.comment("Poppet configuration settings").push("poppets");
            this.voodoo = new VoodooPoppet(builder);
            this.voodooProtection = new VoodooProtectionPoppet(builder);
            this.deathProtection = new DeathProtectionPoppet(builder);
            this.fireProtection = new FireProtectionPoppet(builder);
            this.waterProtection = new WaterProtectionPoppet(builder);
            this.fallProtection = new FallProtectionPoppet(builder);
            this.explosionProtection = new ExplosionProtectionPoppet(builder);
            this.projectileProtection = new ProjectileProtectionPoppet(builder);
            this.witherProtection = new WitherProtectionPoppet(builder);
            this.hungerProtection = new HungerProtectionPoppet(builder);
            this.potionProtection = new PotionProtectionPoppet(builder);
            this.voidProtection = new VoidProtectionPoppet(builder);
            builder.pop();
        }

        public static class VoodooPoppet {
            public IntValue durability;
            public IntValue pullDuration;
            public BooleanValue enableFire;
            public BooleanValue enableNeedle;
            public BooleanValue enablePush;

            public VoodooPoppet(Builder builder) {
                builder.comment("Voodoo Protection Poppet").push("voodoo_protection_poppet");
                this.durability = builder
                        .comment("Durability of the poppet")
                        .defineInRange("durability", 20, 0, Integer.MAX_VALUE);
                this.pullDuration = builder
                        .comment("How many ticks the poppet has to be pulled before an action can be executed")
                        .defineInRange("pull_duration", 20, 0, 200);
                this.enableFire = builder
                        .comment("Allow damaging bound player by throwing poppet into fire")
                        .define("enableFire", true);
                this.enableNeedle = builder
                        .comment("Allow damaging bound player with needles")
                        .define("enableFire", true);
                this.enablePush = builder
                        .comment("Allow pushing bound player around")
                        .define("enableFire", true);
                builder.pop();
            }
        }

        public static class VoodooProtectionPoppet {
            public final IntValue durability;

            public VoodooProtectionPoppet(Builder builder) {
                builder.comment("Voodoo Protection Poppet").push("voodoo_protection_poppet");
                this.durability = builder
                        .comment("Durability of the poppet")
                        .defineInRange("durability", 20, 0, Integer.MAX_VALUE);
                builder.pop();
            }
        }

        public static class DeathProtectionPoppet {
            public final IntValue durability;

            public DeathProtectionPoppet(Builder builder) {
                builder.comment("Death Protection Poppet").push("death_protection_poppet");
                this.durability = builder
                        .comment("Durability of the poppet")
                        .defineInRange("durability", 0, 0, Integer.MAX_VALUE);
                builder.pop();
            }
        }

        public static class FireProtectionPoppet {
            public final IntValue durability;

            public FireProtectionPoppet(Builder builder) {
                builder.comment("Fire Protection Poppet").push("fire_protection_poppet");
                this.durability = builder
                        .comment("Durability of the poppet")
                        .defineInRange("durability", 40, 0, Integer.MAX_VALUE);
                builder.pop();
            }
        }

        public static class WaterProtectionPoppet {
            public final IntValue durability;

            public WaterProtectionPoppet(Builder builder) {
                builder.comment("Water Protection Poppet").push("water_protection_poppet");
                this.durability = builder
                        .comment("Durability of the poppet")
                        .defineInRange("durability", 20, 0, Integer.MAX_VALUE);
                builder.pop();
            }
        }

        public static class FallProtectionPoppet {
            public final IntValue durability;

            public FallProtectionPoppet(Builder builder) {
                builder.comment("Fire Protection Poppet").push("fall_protection_poppet");
                this.durability = builder
                        .comment("Durability of the poppet")
                        .defineInRange("durability", 50, 0, Integer.MAX_VALUE);
                builder.pop();
            }
        }

        public static class ExplosionProtectionPoppet {
            public final IntValue durability;

            public ExplosionProtectionPoppet(Builder builder) {
                builder.comment("Explosion Protection Poppet").push("explosion_protection_poppet");
                this.durability = builder
                        .comment("Durability of the poppet")
                        .defineInRange("durability", 10, 0, Integer.MAX_VALUE);
                builder.pop();
            }
        }

        public static class ProjectileProtectionPoppet {
            public final IntValue durability;

            public ProjectileProtectionPoppet(Builder builder) {
                builder.comment("Projectile Protection Poppet").push("projectile_protection_poppet");
                this.durability = builder
                        .comment("Durability of the poppet")
                        .defineInRange("durability", 20, 0, Integer.MAX_VALUE);
                builder.pop();
            }
        }

        public static class WitherProtectionPoppet {
            public final IntValue durability;

            public WitherProtectionPoppet(Builder builder) {
                builder.comment("Wither Protection Poppet").push("wither_protection_poppet");
                this.durability = builder
                        .comment("Durability of the poppet")
                        .defineInRange("durability", 5, 0, Integer.MAX_VALUE);
                builder.pop();
            }
        }

        public static class HungerProtectionPoppet {
            public final IntValue durability;

            public HungerProtectionPoppet(Builder builder) {
                builder.comment("Hunger Protection Poppet").push("hunger_protection_poppet");
                this.durability = builder
                        .comment("Durability of the poppet")
                        .defineInRange("durability", 5, 0, Integer.MAX_VALUE);
                builder.pop();
            }
        }

        public static class PotionProtectionPoppet {
            public final IntValue durability;

            public PotionProtectionPoppet(Builder builder) {
                builder.comment("Potion Protection Poppet").push("potion_protection_poppet");
                this.durability = builder
                        .comment("Durability of the poppet")
                        .defineInRange("durability", 3, 0, Integer.MAX_VALUE);
                builder.pop();
            }
        }

        public static class VoidProtectionPoppet {
            public final IntValue durability;

            public VoidProtectionPoppet(Builder builder) {
                builder.comment("Void Protection Poppet").push("void_protection_poppet");
                this.durability = builder
                        .comment("Durability of the poppet")
                        .defineInRange("durability", 0, 0, Integer.MAX_VALUE);
                builder.pop();
            }
        }
    }
}
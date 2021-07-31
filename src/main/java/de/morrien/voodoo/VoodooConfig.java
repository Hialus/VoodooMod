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

        public static class PoppetBase {
            public final BooleanValue enabled;
            public final IntValue durability;

            public PoppetBase(Builder builder, String comment, String path, int defaultDurability) {
                builder.comment(comment).push(path);
                this.enabled = builder
                        .comment("Set to false to disable this poppet")
                        .define("enabled", true);
                this.durability = builder
                        .comment("Durability of the poppet")
                        .defineInRange("durability", defaultDurability, 0, Integer.MAX_VALUE);
                builder.pop();
            }
        }

        public static class VoodooProtectionPoppet extends PoppetBase {
            public VoodooProtectionPoppet(Builder builder) {
                super(builder,
                        "Voodoo Protection Poppet",
                        "voodoo_protection_poppet",
                        20
                );
            }
        }

        public static class DeathProtectionPoppet extends PoppetBase {
            public DeathProtectionPoppet(Builder builder) {
                super(builder,
                        "Death Protection Poppet",
                        "death_protection_poppet",
                        2
                );
            }
        }

        public static class FireProtectionPoppet extends PoppetBase {
            public FireProtectionPoppet(Builder builder) {
                super(builder,
                        "Fire Protection Poppet",
                        "fire_protection_poppet",
                        40
                );
            }
        }

        public static class WaterProtectionPoppet extends PoppetBase {
            public WaterProtectionPoppet(Builder builder) {
                super(builder,
                        "Water Protection Poppet",
                        "water_protection_poppet",
                        20
                );
            }
        }

        public static class FallProtectionPoppet extends PoppetBase {
            public FallProtectionPoppet(Builder builder) {
                super(builder,
                        "Fall Protection Poppet",
                        "fall_protection_poppet",
                        50
                );
            }
        }

        public static class ExplosionProtectionPoppet extends PoppetBase {
            public ExplosionProtectionPoppet(Builder builder) {
                super(builder,
                        "Explosion Protection Poppet",
                        "explosion_protection_poppet",
                        10
                );
            }
        }

        public static class ProjectileProtectionPoppet extends PoppetBase {
            public ProjectileProtectionPoppet(Builder builder) {
                super(builder,
                        "Projectile Protection Poppet",
                        "projectile_protection_poppet",
                        20
                );
            }
        }

        public static class WitherProtectionPoppet extends PoppetBase {
            public WitherProtectionPoppet(Builder builder) {
                super(builder,
                        "Wither Protection Poppet",
                        "wither_protection_poppet",
                        5
                );
            }
        }

        public static class HungerProtectionPoppet extends PoppetBase {
            public HungerProtectionPoppet(Builder builder) {
                super(builder,
                        "Hunger Protection Poppet",
                        "hunger_protection_poppet",
                        5
                );
            }
        }

        public static class PotionProtectionPoppet extends PoppetBase {
            public PotionProtectionPoppet(Builder builder) {
                super(builder,
                        "Potion Protection Poppet",
                        "potion_protection_poppet",
                        3
                );
            }
        }

        public static class VoidProtectionPoppet extends PoppetBase {
            public VoidProtectionPoppet(Builder builder) {
                super(builder,
                        "Void Protection Poppet",
                        "void_protection_poppet",
                        0
                );
            }
        }
    }
}
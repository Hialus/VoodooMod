package de.morrien.voodoo;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import org.apache.commons.lang3.tuple.Pair;

public class VoodooConfig {
    public static final Client CLIENT;
    public static final Common COMMON;
    public static final Durability DURABILITY;
    public static final ModConfigSpec clientSpec;
    public static final ModConfigSpec commonSpec;
    public static final ModConfigSpec durabilitySpec;

    static {
        final Pair<Client, ModConfigSpec> clientSpecPair = new Builder().configure(VoodooConfig.Client::new);
        clientSpec = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();

        final Pair<Common, ModConfigSpec> commonSpecPair = new Builder().configure(Common::new);
        commonSpec = commonSpecPair.getRight();
        COMMON = commonSpecPair.getLeft();

        final Pair<Durability, ModConfigSpec> durabilitySpecPair = new Builder().configure(Durability::new);
        durabilitySpec = durabilitySpecPair.getRight();
        DURABILITY = durabilitySpecPair.getLeft();
    }

    public static class Client {

        Client(Builder builder) {
        }
    }

    public static class Common {
        public VoodooPoppet voodoo;
        public VampiricPoppet vampiric;
        public ReflectorPoppet reflector;
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
        public PoppetShelf poppetShelf;

        Common(Builder builder) {
            builder.comment("Poppet configuration settings").push("poppets");
            this.voodoo = new VoodooPoppet(builder);
            this.vampiric = new VampiricPoppet(builder);
            this.reflector = new ReflectorPoppet(builder);
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
            this.poppetShelf = new PoppetShelf(builder);
        }

        public static class VoodooPoppet {
            public IntValue pullDuration;
            public BooleanValue enableFire;
            public IntValue fireDurabilityCost;
            public BooleanValue enableDrowning;
            public IntValue drownDurabilityCost;
            public BooleanValue enableNeedle;
            public IntValue needleDurabilityCost;
            public IntValue needleDamage;
            public BooleanValue enablePush;
            public IntValue pushDurabilityCost;

            public VoodooPoppet(Builder builder) {
                builder.comment("Voodoo Poppet").push("voodoo_poppet");

                this.pullDuration = builder
                        .comment("How many ticks the poppet has to be pulled before an action can be executed")
                        .defineInRange("pull_duration", 20, 0, 72000);

                builder.comment("Needle options").push("needle");
                {
                    this.enableNeedle = builder
                            .comment("Allow damaging bound player with needles")
                            .define("enable_needle", true);
                    this.needleDurabilityCost = builder
                            .comment("The durability that should be removed each time the poppet does damage with a needle")
                            .defineInRange("needle_durability_cost", 1, 0, Integer.MAX_VALUE);
                    this.needleDamage = builder
                            .comment("The amount of hit points that should be removed from the target player. Given in half hearts")
                            .defineInRange("needle_damage", 2, 0, Integer.MAX_VALUE);
                }
                builder.pop();

                builder.comment("Pushing options").push("push");
                {
                    this.enablePush = builder
                            .comment("Allow pushing bound player around")
                            .define("enable_push", true);
                    this.pushDurabilityCost = builder
                            .comment("The durability that should be removed each time the poppet pushes the bound player around")
                            .defineInRange("push_durability_cost", 2, 0, Integer.MAX_VALUE);
                }
                builder.pop();

                builder.comment("Fire options").push("fire");
                {
                    this.enableFire = builder
                            .comment("Allow damaging bound player by throwing poppet into fire")
                            .define("enable_fire", true);
                    this.fireDurabilityCost = builder
                            .comment("The durability that should be removed each time the poppet does damage by fire")
                            .defineInRange("fire_durability_cost", 2, 0, Integer.MAX_VALUE);
                }
                builder.pop();

                builder.comment("Drowning options").push("drowning");
                {
                    this.enableDrowning = builder
                            .comment("Allow drowning bound player by throwing poppet into water")
                            .define("enable_drowning", true);
                    this.drownDurabilityCost = builder
                            .comment("The durability that should be removed each time the poppet does damage by drowning")
                            .defineInRange("drown_durability_cost", 2, 0, Integer.MAX_VALUE);
                }
                builder.pop();
                builder.pop();
            }
        }

        public static class PoppetBase {
            public final BooleanValue enabled;

            public PoppetBase(Builder builder, String comment, String path) {
                this(builder, comment, path, true);
            }

            public PoppetBase(Builder builder, String comment, String path, boolean pop) {
                builder.comment(comment).push(path);
                this.enabled = builder
                        .comment("Set to false to disable this poppet")
                        .define("enabled", true);
                if (pop)
                    builder.pop();
            }
        }

        public static class VampiricPoppet extends PoppetBase {
            public final IntValue healthLimit;
            public final IntValue drainageInterval;
            public final IntValue healthPerDrain;

            public VampiricPoppet(Builder builder) {
                super(builder,
                        "Vampiric Poppet",
                        "vampiric_poppet",
                        false
                );
                this.healthLimit = builder
                        .comment(
                                "The hit point limit for draining health",
                                "If the target players health is below this number no health will be drained"
                        )
                        .defineInRange("health_limit", 6, 0, Integer.MAX_VALUE);
                this.drainageInterval = builder
                        .comment("The interval in which health should be drained in ticks.")
                        .defineInRange("drainage_interval", 20, 1, Integer.MAX_VALUE);
                this.healthPerDrain = builder
                        .comment("The hit points that should be stolen per drain.")
                        .defineInRange("health_per_drain", 3, 1, Integer.MAX_VALUE);
                builder.pop();
            }
        }

        public static class ReflectorPoppet extends PoppetBase {
            public ReflectorPoppet(Builder builder) {
                super(builder,
                        "Reflector Poppet",
                        "reflector_poppet"
                );
            }
        }

        public static class VoodooProtectionPoppet extends PoppetBase {
            public VoodooProtectionPoppet(Builder builder) {
                super(builder,
                        "Voodoo Protection Poppet",
                        "voodoo_protection_poppet"
                );
            }
        }

        public static class DeathProtectionPoppet extends PoppetBase {
            public DeathProtectionPoppet(Builder builder) {
                super(builder,
                        "Death Protection Poppet",
                        "death_protection_poppet"
                );
            }
        }

        public static class FireProtectionPoppet extends PoppetBase {
            public FireProtectionPoppet(Builder builder) {
                super(builder,
                        "Fire Protection Poppet",
                        "fire_protection_poppet"
                );
            }
        }

        public static class WaterProtectionPoppet extends PoppetBase {
            public WaterProtectionPoppet(Builder builder) {
                super(builder,
                        "Water Protection Poppet",
                        "water_protection_poppet"
                );
            }
        }

        public static class FallProtectionPoppet extends PoppetBase {
            public FallProtectionPoppet(Builder builder) {
                super(builder,
                        "Fall Protection Poppet",
                        "fall_protection_poppet"
                );
            }
        }

        public static class ExplosionProtectionPoppet extends PoppetBase {
            public ExplosionProtectionPoppet(Builder builder) {
                super(builder,
                        "Explosion Protection Poppet",
                        "explosion_protection_poppet"
                );
            }
        }

        public static class ProjectileProtectionPoppet extends PoppetBase {
            public ProjectileProtectionPoppet(Builder builder) {
                super(builder,
                        "Projectile Protection Poppet",
                        "projectile_protection_poppet"
                );
            }
        }

        public static class WitherProtectionPoppet extends PoppetBase {
            public WitherProtectionPoppet(Builder builder) {
                super(builder,
                        "Wither Protection Poppet",
                        "wither_protection_poppet"
                );
            }
        }

        public static class HungerProtectionPoppet extends PoppetBase {
            public HungerProtectionPoppet(Builder builder) {
                super(builder,
                        "Hunger Protection Poppet",
                        "hunger_protection_poppet"
                );
            }
        }

        public static class PotionProtectionPoppet extends PoppetBase {
            public PotionProtectionPoppet(Builder builder) {
                super(builder,
                        "Potion Protection Poppet",
                        "potion_protection_poppet"
                );
            }
        }

        public static class VoidProtectionPoppet extends PoppetBase {
            public VoidProtectionPoppet(Builder builder) {
                super(builder,
                        "Void Protection Poppet",
                        "void_protection_poppet"
                );
            }
        }

        public static class PoppetShelf {
            public final BooleanValue chunkLoading;

            public PoppetShelf(Builder builder) {
                builder.comment("Poppet shelf configuration settings").push("poppet_shelf");
                this.chunkLoading = builder
                        .comment("Keep the chunk of every poppet shelf that has an owner loaded")
                        .define("chunk_loading", true);
                builder.pop();
            }
        }
    }
    public static class Durability {
        public final IntValue voodoo;
        public final IntValue vampiric;
        public final IntValue reflector;
        public final IntValue voodooProtection;
        public final IntValue deathProtection;
        public final IntValue fireProtection;
        public final IntValue waterProtection;
        public final IntValue fallProtection;
        public final IntValue explosionProtection;
        public final IntValue projectileProtection;
        public final IntValue witherProtection;
        public final IntValue hungerProtection;
        public final IntValue potionProtection;
        public final IntValue voidProtection;

        Durability(Builder builder) {
            builder.comment("Poppet configuration settings").push("poppets");
            this.voodoo = define(builder, "Voodoo Poppet", "voodoo_poppet", 20);
            this.vampiric = define(builder, "Vampiric Poppet", "vampiric_poppet", 20);
            this.reflector = define(builder, "Reflector Poppet", "reflector_poppet", 10);
            this.voodooProtection = define(builder, "Voodoo Protection Poppet", "voodoo_protection_poppet", 1);
            this.deathProtection = define(builder, "Death Protection Poppet", "death_protection_poppet", 2);
            this.fireProtection = define(builder, "Fire Protection Poppet", "fire_protection_poppet", 10);
            this.waterProtection = define(builder, "Water Protection Poppet", "water_protection_poppet", 5);
            this.fallProtection = define(builder, "Fall Protection Poppet", "fall_protection_poppet", 30);
            this.explosionProtection = define(builder, "Explosion Protection Poppet", "explosion_protection_poppet", 4);
            this.projectileProtection = define(builder, "Projectile Protection Poppet", "projectile_protection_poppet", 10);
            this.witherProtection = define(builder, "Wither Protection Poppet", "wither_protection_poppet", 10);
            this.hungerProtection = define(builder, "Hunger Protection Poppet", "hunger_protection_poppet", 5);
            this.potionProtection = define(builder, "Potion Protection Poppet", "potion_protection_poppet", 6);
            this.voidProtection = define(builder, "Void Protection Poppet", "void_protection_poppet", 0);
            builder.pop();
        }

        private static IntValue define(Builder builder, String comment, String path, int defaultDurability) {
            builder.comment(comment).push(path);
            final IntValue durability = builder
                    .comment("Durability of the poppet")
                    .defineInRange("durability", defaultDurability, 0, Integer.MAX_VALUE);
            builder.pop();
            return durability;
        }
    }
}
package de.morrien.voodoo.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record Binding(UUID uuid, String name) {
    public static final Codec<Binding> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("uuid").forGetter(Binding::uuid),
            Codec.STRING.fieldOf("name").forGetter(Binding::name)
    ).apply(instance, Binding::new));
    public static final StreamCodec<ByteBuf, Binding> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, Binding::uuid,
            ByteBufCodecs.STRING_UTF8, Binding::name,
            Binding::new
    );
}

package com.terraformersmc.vistas.control;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;

import java.util.Optional;

public record RotationControl(
        boolean frozen,
        boolean woozy,
        double addedPitch,
        double addedYaw,
        double addedRoll,
        double speedMultiplier
) {
    public static final RotationControl DEFAULT = new RotationControl();

    public static final Codec<RotationControl> CODEC = RecordCodecBuilder.create(
            (instance) -> instance
                    .group(
                            Codec.BOOL.optionalFieldOf("frozen")
                                    .forGetter((rotationCtrl) -> Optional.of(rotationCtrl.frozen)),
                            Codec.BOOL.optionalFieldOf("woozy")
                                    .forGetter((rotationCtrl) -> Optional.of(rotationCtrl.woozy)),
                            Codec.DOUBLE.optionalFieldOf("addedPitch")
                                    .forGetter((rotationCtrl) -> Optional.of(rotationCtrl.addedPitch)),
                            Codec.DOUBLE.optionalFieldOf("addedYaw")
                                    .forGetter((rotationCtrl) -> Optional.of(rotationCtrl.addedYaw)),
                            Codec.DOUBLE.optionalFieldOf("addedRoll")
                                    .forGetter((rotationCtrl) -> Optional.of(rotationCtrl.addedRoll)),
                            Codec.DOUBLE.optionalFieldOf("speedMultiplier")
                                    .forGetter((rotationCtrl) -> Optional.of(rotationCtrl.speedMultiplier))
                    )
                    .apply(instance, RotationControl::new));

    public RotationControl() {
        this(false, false, 0.0D, 0.0D, 0.0D, 1.0D);
    }

    @SuppressWarnings("unused")
    public RotationControl {
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public RotationControl(
            Optional<Boolean> frozen,
            Optional<Boolean> woozy,
            Optional<Double> addedPitch,
            Optional<Double> addedYaw,
            Optional<Double> addedRoll,
            Optional<Double> speedMultiplier
    ) {
        this(
                frozen.orElse(false),
                woozy.orElse(false),
                addedPitch.orElse(0.0D),
                addedYaw.orElse(0.0D),
                addedRoll.orElse(0.0D),
                speedMultiplier.orElse(1.0D)
        );
    }

    public double getSpeed() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return this.speedMultiplier();
        }

        return mc.options.panoramaSpeed().get() * this.speedMultiplier();
    }

    public double getPitch(double time) {
        return (
                (this.woozy()
                        ? -time * 0.1D
                        : Math.sin(time * 0.001D) * 5.0D + 25.0D
                ) + this.addedPitch()
        ) * this.getSpeed();
    }

    public double getYaw(double time) {
        return ((-time * 0.1D) + this.addedYaw()) * this.getSpeed();
    }

    public double getRoll(double time) {
        return (this.addedRoll()) * this.getSpeed();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RotationControl(
                boolean frozen1,
                boolean woozy1,
                double addedPitch1,
                double addedYaw1,
                double addedRoll1,
                double speedMultiplier1
        )) {
            return this.frozen == frozen1
                    && this.woozy == woozy1
                    && this.addedPitch == addedPitch1
                    && this.addedYaw == addedYaw1
                    && this.addedRoll == addedRoll1
                    && this.speedMultiplier == speedMultiplier1;
        }
        return false;
    }
}

package com.terraformersmc.vistas.control;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record VisualControl(
        double fov,
        double width,
        double height,
        double depth,
        double addedX,
        double addedY,
        double addedZ,
        double colorR,
        double colorG,
        double colorB,
        double colorA
) {
    public static final VisualControl DEFAULT = new VisualControl();

    public static final Codec<VisualControl> CODEC = RecordCodecBuilder.create(
            (instance) -> instance
                    .group(
                            Codec.DOUBLE.optionalFieldOf("fov")
                                    .forGetter((visualCtrl) -> Optional.of(visualCtrl.fov)),
                            Codec.DOUBLE.optionalFieldOf("width")
                                    .forGetter((visualCtrl) -> Optional.of(visualCtrl.width)),
                            Codec.DOUBLE.optionalFieldOf("height")
                                    .forGetter((visualCtrl) -> Optional.of(visualCtrl.height)),
                            Codec.DOUBLE.optionalFieldOf("depth")
                                    .forGetter((visualCtrl) -> Optional.of(visualCtrl.depth)),
                            Codec.DOUBLE.optionalFieldOf("addedX")
                                    .forGetter((visualCtrl) -> Optional.of(visualCtrl.addedX)),
                            Codec.DOUBLE.optionalFieldOf("addedY")
                                    .forGetter((visualCtrl) -> Optional.of(visualCtrl.addedY)),
                            Codec.DOUBLE.optionalFieldOf("addedZ")
                                    .forGetter((visualCtrl) -> Optional.of(visualCtrl.addedZ)),
                            Codec.DOUBLE.optionalFieldOf("colorR")
                                    .forGetter((visualCtrl) -> Optional.of(visualCtrl.colorR)),
                            Codec.DOUBLE.optionalFieldOf("colorG")
                                    .forGetter((visualCtrl) -> Optional.of(visualCtrl.colorG)),
                            Codec.DOUBLE.optionalFieldOf("colorB")
                                    .forGetter((visualCtrl) -> Optional.of(visualCtrl.colorB)),
                            Codec.DOUBLE.optionalFieldOf("colorA")
                                    .forGetter((visualCtrl) -> Optional.of(visualCtrl.colorA))
                    )
                    .apply(instance, VisualControl::new));

    public VisualControl() {
        this(85.0D, 2.0D, 2.0D, 2.0D, 0.0D, 0.0D, 0.0D, 255.0D, 255.0D, 255.0D, 255.0D);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public VisualControl(
            Optional<Double> fov,
            Optional<Double> width,
            Optional<Double> height,
            Optional<Double> depth,
            Optional<Double> addedX,
            Optional<Double> addedY,
            Optional<Double> addedZ,
            Optional<Double> colorR,
            Optional<Double> colorG,
            Optional<Double> colorB,
            Optional<Double> colorA
    ) {
        this(
                fov.orElse(85.0D),
                width.orElse(2.0D),
                height.orElse(2.0D),
                depth.orElse(2.0D),
                addedX.orElse(0.0D),
                addedY.orElse(0.0D),
                addedZ.orElse(0.0D),
                colorR.orElse(255.0D),
                colorG.orElse(255.0D),
                colorB.orElse(255.0D),
                colorA.orElse(255.0D)
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VisualControl(
                double fov1,
                double width1,
                double height1,
                double depth1,
                double x1,
                double y1,
                double z1,
                double r1,
                double g1,
                double b1,
                double a1
        )) {
            return this.fov == fov1
                    && this.width == width1
                    && this.height == height1
                    && this.depth == depth1
                    && this.addedX == x1
                    && this.addedY == y1
                    && this.addedZ == z1
                    && this.colorR == r1
                    && this.colorG == g1
                    && this.colorB == b1
                    && this.colorA == a1;
        }
        return false;
    }
}

package com.terraformersmc.vistas.control;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record LogoControl(
        Identifier logoId,
        double logoX,
        double logoY,
        double logoRot,
        boolean outlined,
        double splashX,
        double splashY,
        double splashRot,
        boolean showEdition
) {
    public static final LogoControl DEFAULT = new LogoControl();

    public static final Codec<LogoControl> CODEC = RecordCodecBuilder.create(
            (instance) -> instance
                    .group(
                            Identifier.CODEC.optionalFieldOf("logoId")
                                    .forGetter((logoCtrl) -> Optional.of(logoCtrl.logoId)),
                            Codec.DOUBLE.optionalFieldOf("logoX")
                                    .forGetter((logoCtrl) -> Optional.of(logoCtrl.logoX)),
                            Codec.DOUBLE.optionalFieldOf("logoY")
                                    .forGetter((logoCtrl) -> Optional.of(logoCtrl.logoY)),
                            Codec.DOUBLE.optionalFieldOf("logoRot")
                                    .forGetter((logoCtrl) -> Optional.of(logoCtrl.logoRot)),
                            Codec.BOOL.optionalFieldOf("outlined")
                                    .forGetter((logoCtrl) -> Optional.of(logoCtrl.outlined)),
                            Codec.DOUBLE.optionalFieldOf("splashX")
                                    .forGetter((logoCtrl) -> Optional.of(logoCtrl.splashX)),
                            Codec.DOUBLE.optionalFieldOf("splashY")
                                    .forGetter((logoCtrl) -> Optional.of(logoCtrl.splashY)),
                            Codec.DOUBLE.optionalFieldOf("splashRot")
                                    .forGetter((logoCtrl) -> Optional.of(logoCtrl.splashRot)),
                            Codec.BOOL.optionalFieldOf("showEdition")
                                    .forGetter((logoCtrl) -> Optional.of(logoCtrl.showEdition))
                    )
                    .apply(instance, LogoControl::new));

    public LogoControl() {
        this(
                LogoRenderer.MINECRAFT_LOGO,
                0.0D,
                0.0D,
                0.0D,
                true,
                0.0D,
                0.0D,
                -20.0D,
                true
        );
    }


    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public LogoControl(
            Optional<Identifier> logoId,
            Optional<Double> logoX,
            Optional<Double> logoY,
            Optional<Double> logoRot,
            Optional<Boolean> outlined,
            Optional<Double> splashX,
            Optional<Double> splashY,
            Optional<Double> splashRot,
            Optional<Boolean> showEdition) {
        this(
                logoId.orElse(LogoRenderer.MINECRAFT_LOGO),
                logoX.orElse(0.0D),
                logoY.orElse(0.0D),
                logoRot.orElse(0.0D),
                outlined.orElse(true),
                splashX.orElse(0.0D),
                splashY.orElse(0.0D),
                splashRot.orElse(-20.0D),
                showEdition.orElse(true)
        );
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LogoControl(
                Identifier logoId1,
                double logoX1,
                double logoY1,
                double logoRot1,
                boolean outlined1,
                double splashX1,
                double splashY1,
                double splashRot1,
                boolean showEdition1
        )) {
            return this.logoId == logoId1
                    && this.logoX == logoX1
                    && this.logoY == logoY1
                    && this.logoRot == logoRot1
                    && this.outlined == outlined1
                    && this.splashX == splashX1
                    && this.splashY == splashY1
                    && this.splashRot == splashRot1
                    && this.showEdition == showEdition1;
        }
        return false;
    }
}

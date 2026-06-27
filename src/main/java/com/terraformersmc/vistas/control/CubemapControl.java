package com.terraformersmc.vistas.control;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record CubemapControl(
        Identifier cubemapId,
        RotationControl rotationControl,
        VisualControl visualControl
) {
    /**
     * @see net.minecraft.client.gui.render.GuiRenderer#cubeMap
     */
    public static final Identifier DEFAULT_ID = Identifier.withDefaultNamespace("textures/gui/title/background/panorama");

    public static final CubemapControl DEFAULT = new CubemapControl();

    public static final Codec<CubemapControl> CODEC = RecordCodecBuilder.create(
            (instance) -> instance
                    .group(
                            Identifier.CODEC.optionalFieldOf("cubemapId")
                                    .forGetter((cubemapCtrl) -> Optional.of(cubemapCtrl.cubemapId)),
                            RotationControl.CODEC.optionalFieldOf("rotationControl")
                                    .forGetter((cubemapCtrl) -> Optional.of(cubemapCtrl.rotationControl)),
                            VisualControl.CODEC.optionalFieldOf("visualControl")
                                    .forGetter((cubemapCtrl) -> Optional.of(cubemapCtrl.visualControl))
                    )
                    .apply(instance, CubemapControl::new));

    public CubemapControl() {
        this(
                DEFAULT_ID,
                RotationControl.DEFAULT,
                VisualControl.DEFAULT
        );
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public CubemapControl(
            Optional<Identifier> cubemapId,
            Optional<RotationControl> rotationControl,
            Optional<VisualControl> visualControl
    ) {
        this(
                cubemapId.orElse(DEFAULT_ID),
                rotationControl.orElse(RotationControl.DEFAULT),
                visualControl.orElse(VisualControl.DEFAULT)
        );
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CubemapControl(
                Identifier cubemapId1,
                RotationControl rotationControl1,
                VisualControl visualControl1
        )) {
            return this.cubemapId == cubemapId1
                    && this.rotationControl == rotationControl1
                    && this.visualControl == visualControl1;
        }
        return false;
    }
}

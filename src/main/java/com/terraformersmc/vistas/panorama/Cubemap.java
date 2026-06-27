package com.terraformersmc.vistas.panorama;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public class Cubemap {
	public static final Cubemap DEFAULT = new Cubemap();

	public static final Codec<Cubemap> CODEC = RecordCodecBuilder.create(
			(instance) -> 
			instance.group(
					ResourceLocation.CODEC.optionalFieldOf("cubemapId")
						.forGetter((cubemap) -> Optional.of(cubemap.cubemapId)),
					RotationControl.CODEC.optionalFieldOf("rotationControl")
						.forGetter((cubemap) -> Optional.of(cubemap.rotationControl)),
					VisualControl.CODEC.optionalFieldOf("visualControl")
						.forGetter((cubemap) -> Optional.of(cubemap.visualControl))
					)
			.apply(instance, Cubemap::new));

	private final ResourceLocation cubemapId;
	private final RotationControl rotationControl;
	private final VisualControl visualControl;

	public Cubemap() {
		this.cubemapId = ResourceLocation.withDefaultNamespace("textures/gui/title/background/panorama");
		this.rotationControl = RotationControl.DEFAULT;
		this.visualControl = VisualControl.DEFAULT;
	}

	@SuppressWarnings("unused")
	public Cubemap(ResourceLocation cubemapId, RotationControl rotationControl, VisualControl visualControl) {
		this.cubemapId = cubemapId;
		this.rotationControl = rotationControl;
		this.visualControl = visualControl;
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public Cubemap(Optional<ResourceLocation> cubemapId, Optional<RotationControl> rotationControl, Optional<VisualControl> visualControl) {
		this.cubemapId = cubemapId.orElse(ResourceLocation.withDefaultNamespace("textures/gui/title/background/panorama"));
		this.rotationControl = rotationControl.orElse(RotationControl.DEFAULT);
		this.visualControl = visualControl.orElse(VisualControl.DEFAULT);
	}

	public ResourceLocation getCubemapId() {
		return cubemapId;
	}

	public RotationControl getRotationControl() {
		return rotationControl;
	}

	public VisualControl getVisualControl() {
		return visualControl;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Cubemap cub) {
			return this.cubemapId == cub.cubemapId && this.rotationControl == cub.rotationControl && this.visualControl == cub.visualControl;
		}
		return super.equals(obj);
	}
}

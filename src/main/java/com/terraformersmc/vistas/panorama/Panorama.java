package com.terraformersmc.vistas.panorama;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvent;

public class Panorama {
	public static final Panorama DEFAULT = new Panorama();

	public static final Codec<Music> OPTIONAL_MUSIC_SOUND_CODEC = RecordCodecBuilder.create(
			(instance) -> 
			instance.group(
					ResourceLocation.CODEC.fieldOf("sound")
						.forGetter((sound) -> sound.event().unwrapKey().orElseThrow().location()),
					Codec.INT.optionalFieldOf("min_delay")
						.forGetter((sound) -> Optional.of(sound.minDelay())),
					Codec.INT.optionalFieldOf("max_delay")
						.forGetter((sound) -> Optional.of(sound.maxDelay())),
					Codec.BOOL.optionalFieldOf("replace_current_music")
						.forGetter((sound) -> Optional.of(sound.replaceCurrentMusic()))
					)
			.apply(instance, (sound, min, max, replace) -> new Music(Holder.direct(SoundEvent.createVariableRangeEvent(sound)), min.orElse(20), max.orElse(600), replace.orElse(true))));

	public static final Codec<Panorama> CODEC = RecordCodecBuilder.create(
			(instance) -> 
			instance.group(
					Codec.INT.optionalFieldOf("weight")
						.forGetter((panorama) -> Optional.of(panorama.weight)),
					OPTIONAL_MUSIC_SOUND_CODEC.optionalFieldOf("musicSound")
						.forGetter((panorama) -> Optional.of(panorama.musicSound)),
					ResourceLocation.CODEC.optionalFieldOf("splashText")
						.forGetter((panorama) -> Optional.of(panorama.splashText)),
					LogoControl.CODEC.optionalFieldOf("logoControl")
						.forGetter((panorama) -> Optional.of(panorama.logoControl)),
					Codec.list(Cubemap.CODEC).optionalFieldOf("cubemaps")
						.forGetter((panorama) -> Optional.of(panorama.cubemaps))
					)
			.apply(instance, Panorama::new));

	private final int weight;
	private final Music musicSound;
	private final ResourceLocation splashText;
	private final LogoControl logoControl;
	private final List<Cubemap> cubemaps;

	public Panorama() {
		this.weight = 1;
		this.musicSound = Musics.MENU;
		this.splashText = ResourceLocation.withDefaultNamespace("texts/splashes.txt");
		this.logoControl = LogoControl.DEFAULT;
		this.cubemaps = Lists.newArrayList(Cubemap.DEFAULT);
	}

	@SuppressWarnings("unused")
	public Panorama(int weight, Music musicSound, ResourceLocation splashText, LogoControl logoControl, List<Cubemap> cubemaps) {
		this.weight = weight;
		this.musicSound = musicSound;
		this.splashText = splashText;
		this.logoControl = logoControl;
		this.cubemaps = cubemaps;
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public Panorama(Optional<Integer> weight, Optional<Music> musicSound, Optional<ResourceLocation> splashText, Optional<LogoControl> logoControl, Optional<List<Cubemap>> cubemaps) {
		this.weight = weight.orElse(1);
		this.musicSound = musicSound.orElse(Musics.MENU);
		this.splashText = splashText.orElse(ResourceLocation.withDefaultNamespace("texts/splashes.txt"));
		this.logoControl = logoControl.orElse(LogoControl.DEFAULT);
		this.cubemaps = cubemaps.orElse(Lists.newArrayList(Cubemap.DEFAULT));
	}

	public int getWeight() {
		return weight;
	}

	public Music getMusicSound() {
		return musicSound;
	}

	public ResourceLocation getSplashText() {
		return splashText;
	}

	public LogoControl getLogoControl() {
		return logoControl;
	}

	public List<Cubemap> getCubemaps() {
		return cubemaps;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Panorama pan) {
			return this.weight == pan.weight && this.musicSound == pan.musicSound && this.splashText == pan.splashText && this.logoControl == pan.logoControl && this.cubemaps == pan.cubemaps;
		}
		return super.equals(obj);
	}
}

package com.terraformersmc.vistas.control;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvent;

import java.util.List;
import java.util.Optional;

public record PanoramaControl(
        int weight,
        Music musicSound,
        Identifier splashText,
        LogoControl logoControl,
        List<CubemapControl> cubemapCtrls
) {
    /**
     * @see net.minecraft.client.resources.SplashManager#SPLASHES_LOCATION
     */
    public static final Identifier DEFAULT_TEXT = Identifier.withDefaultNamespace("texts/splashes.txt");

    public static final PanoramaControl DEFAULT = new PanoramaControl();

    public static final Codec<Music> OPTIONAL_MUSIC_SOUND_CODEC = RecordCodecBuilder.create(
            (instance) -> instance
                    .group(
                            Identifier.CODEC.fieldOf("sound")
                                    .forGetter((sound) -> sound.sound().unwrapKey().orElseThrow().identifier()),
                            Codec.INT.optionalFieldOf("min_delay")
                                    .forGetter((sound) -> Optional.of(sound.minDelay())),
                            Codec.INT.optionalFieldOf("max_delay")
                                    .forGetter((sound) -> Optional.of(sound.maxDelay())),
                            Codec.BOOL.optionalFieldOf("replace_current_music")
                                    .forGetter((sound) -> Optional.of(sound.replaceCurrentMusic()))
                    )
                    .apply(instance, (sound, min, max, replace) -> new Music(
                            Holder.direct(SoundEvent.createVariableRangeEvent(sound)),
                            min.orElse(20),
                            max.orElse(600),
                            replace.orElse(true))
                    ));

    public static final Codec<PanoramaControl> CODEC = RecordCodecBuilder.create(
            (instance) ->
                    instance.group(
                                    Codec.INT.optionalFieldOf("weight")
                                            .forGetter((panoramaCtrl) -> Optional.of(panoramaCtrl.weight)),
                                    OPTIONAL_MUSIC_SOUND_CODEC.optionalFieldOf("musicSound")
                                            .forGetter((panoramaCtrl) -> Optional.of(panoramaCtrl.musicSound)),
                                    Identifier.CODEC.optionalFieldOf("splashText")
                                            .forGetter((panoramaCtrl) -> Optional.of(panoramaCtrl.splashText)),
                                    LogoControl.CODEC.optionalFieldOf("logoControl")
                                            .forGetter((panoramaCtrl) -> Optional.of(panoramaCtrl.logoControl)),
                                    Codec.list(CubemapControl.CODEC).optionalFieldOf("cubemaps")
                                            .forGetter((panoramaCtrl) -> Optional.of(panoramaCtrl.cubemapCtrls))
                            )
                            .apply(instance, PanoramaControl::new));

    public PanoramaControl() {
        this(
                1,
                Musics.MENU,
                DEFAULT_TEXT,
                LogoControl.DEFAULT,
                Lists.newArrayList(CubemapControl.DEFAULT)
        );
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public PanoramaControl(Optional<Integer> weight, Optional<Music> musicSound, Optional<Identifier> splashText, Optional<LogoControl> logoControl, Optional<List<CubemapControl>> cubemaps) {
        this(
                weight.orElse(1),
                musicSound.orElse(Musics.MENU),
                splashText.orElse(DEFAULT_TEXT),
                logoControl.orElse(LogoControl.DEFAULT),
                cubemaps.orElse(Lists.newArrayList(CubemapControl.DEFAULT))
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PanoramaControl(
                int weight1,
                Music musicSound1,
                Identifier splashText1,
                LogoControl logoControl1,
                List<CubemapControl> cubemaps1
        )) {
            return this.weight == weight1
                    && this.musicSound == musicSound1
                    && this.splashText == splashText1
                    && this.logoControl == logoControl1
                    && this.cubemapCtrls == cubemaps1;
        }
        return false;
    }
}

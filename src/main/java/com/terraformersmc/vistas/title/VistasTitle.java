package com.terraformersmc.vistas.title;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.config.VistasConfig;
import com.terraformersmc.vistas.control.PanoramaControl;
import net.minecraft.IdentifierException;
import net.minecraft.resources.Identifier;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class VistasTitle {
    public static final Map<Identifier, PanoramaControl> MOD_PANORAMAS = Maps.newConcurrentMap();
    public static final Map<Identifier, PanoramaControl> ALL_PANORAMAS = Maps.newConcurrentMap();
    public static final Map<PanoramaControl, Identifier> ALL_PANORAMA_IDS = Maps.newConcurrentMap();
    public static final MutableObject<PanoramaControl> CURRENT_PANORAMA = new MutableObject<>(PanoramaControl.DEFAULT);
    public static final Random RANDOM = new Random();

    public static void selectPanorama() {
        selectPanorama(Profiler.get());
    }

    public static void selectPanorama(ProfilerFiller profiler) {
        profiler.startTick();
        profiler.push("set");
        if (VistasConfig.getInstance().forcePanorama) {
            profiler.push("force");
            try {
                PanoramaControl panoramaCtrl = VistasTitle.ALL_PANORAMAS.get(
                        Identifier.parse(VistasConfig.getInstance().panorama)
                );
                if (panoramaCtrl == null) {
                    throw new NullPointerException();
                }
                VistasTitle.CURRENT_PANORAMA.setValue(panoramaCtrl);
            } catch (IdentifierException badId) {
                Vistas.LOGGER.warn(
                        "String: '{}' is an invalid Identifier in config; resetting...",
                        VistasConfig.getInstance().panorama
                );
                VistasConfig.getInstance().panorama = Vistas.DEFAULT.toString();
                VistasTitle.CURRENT_PANORAMA.setValue(PanoramaControl.DEFAULT);
            } catch (NullPointerException nullPanorama) {
                Vistas.LOGGER.warn(
                        "String: '{}' is an unregistered Panorama in config; resetting...",
                        VistasConfig.getInstance().panorama
                );
                VistasConfig.getInstance().panorama = Vistas.DEFAULT.toString();
                VistasTitle.CURRENT_PANORAMA.setValue(PanoramaControl.DEFAULT);
            }
            profiler.pop();
        } else if (VistasConfig.getInstance().randomPerScreen) {
            profiler.push("random");
            VistasTitle.CURRENT_PANORAMA.setValue(VistasTitle.getRandomPanorama());
            profiler.pop();
        }
        profiler.pop();
        profiler.endTick();
    }

    public static PanoramaControl getRandomPanorama() {
        if (!ALL_PANORAMAS.isEmpty()) {

            int total = 0;
            for (PanoramaControl panoramaCtrl : ALL_PANORAMAS.values()) {
                total += panoramaCtrl.weight();
            }

            List<PanoramaControl> panoramaCtrls = Lists.newArrayList(ALL_PANORAMAS.values());
            Collections.shuffle(panoramaCtrls, RANDOM);

            for (PanoramaControl panoramaCtrl : panoramaCtrls) {
                if (RANDOM.nextInt(total) < panoramaCtrl.weight()) {
                    return panoramaCtrl;
                } else {
                    total -= panoramaCtrl.weight();
                }
            }
            return panoramaCtrls.get(RANDOM.nextInt(panoramaCtrls.size()));
        }
        return PanoramaControl.DEFAULT;
    }

    public static void registerPanorama(Identifier id, PanoramaControl panoramaCtrl) {
        ALL_PANORAMAS.put(id, panoramaCtrl);
        ALL_PANORAMA_IDS.put(panoramaCtrl, id);
    }

    @SuppressWarnings("unused")
    public static void deregisterPanorama(Identifier id) {
        PanoramaControl panoramaCtrl = ALL_PANORAMAS.get(id);
        ALL_PANORAMAS.remove(id);
        ALL_PANORAMA_IDS.remove(panoramaCtrl);
    }

    public static void clearPanoramas() {
        ALL_PANORAMAS.clear();
        ALL_PANORAMA_IDS.clear();
    }
}

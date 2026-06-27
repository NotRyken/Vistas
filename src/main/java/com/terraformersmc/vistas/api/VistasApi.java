package com.terraformersmc.vistas.api;

import com.terraformersmc.vistas.panorama.Panorama;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public interface VistasApi {
	void appendPanoramas(Map<ResourceLocation, Panorama> set);
}

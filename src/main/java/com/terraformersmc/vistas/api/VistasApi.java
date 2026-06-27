package com.terraformersmc.vistas.api;

import com.terraformersmc.vistas.panorama.Panorama;
import java.util.Map;
import net.minecraft.resources.Identifier;

public interface VistasApi {
	void appendPanoramas(Map<Identifier, Panorama> set);
}

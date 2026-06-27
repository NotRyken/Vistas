package com.terraformersmc.vistas;

import com.terraformersmc.vistas.api.VistasApi;
import com.terraformersmc.vistas.config.VistasConfig;
import com.terraformersmc.vistas.resource.PanoramicScreenshots;
import com.terraformersmc.vistas.title.VistasTitle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Vistas implements ClientModInitializer {
	public static final String NAME = "Vistas";
	public static final String NAMESPACE = "vistas";
	public static final ResourceLocation DEFAULT = id("default");
	public static final Logger LOGGER = LogManager.getLogger(NAME);

	@Override
	public void onInitializeClient() {
		VistasConfig.init();
		PanoramicScreenshots.registerKeyBinding();
		FabricLoader.getInstance().getEntrypointContainers("vistas", VistasApi.class)
				.forEach((api) -> api.getEntrypoint().appendPanoramas(VistasTitle.BUILTIN_PANORAMAS));
	}

	public static ResourceLocation id(String id) {
		return ResourceLocation.fromNamespaceAndPath(NAMESPACE, id);
	}
}

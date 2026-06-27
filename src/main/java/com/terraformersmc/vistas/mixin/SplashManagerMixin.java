package com.terraformersmc.vistas.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.terraformersmc.vistas.access.MinecraftAccess;
import com.terraformersmc.vistas.resource.PanoramaResourceReloader;
import com.terraformersmc.vistas.title.VistasTitle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(SplashManager.class)
public abstract class SplashManagerMixin {
	@ModifyReturnValue(
			method = "getSplash",
			at = @At(value = "RETURN"),
			slice = @Slice(
					from = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"),
					to = @At(value = "TAIL")
			)
	)
	@SuppressWarnings("unused")
	private SplashRenderer vistas$getRenderer(SplashRenderer original) {
		Minecraft client = Minecraft.getInstance();
		PanoramaResourceReloader resourceReloader = ((MinecraftAccess) client).getPanoramaResourceReloader();
		ResourceLocation panoramaId = VistasTitle.PANORAMAS_INVERT.get(VistasTitle.CURRENT.getValue());

		if (resourceReloader != null && panoramaId != null) {
			return new SplashRenderer(resourceReloader.get());
		}

		return original;
	}
}

package com.terraformersmc.vistas.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.terraformersmc.vistas.access.MinecraftAccess;
import com.terraformersmc.vistas.resource.PanoramaResourceReloader;
import com.terraformersmc.vistas.title.VistasTitle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(SplashManager.class)
public abstract class SplashManagerMixin {

    @Shadow
    private static Component literalSplash(String text) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    /**
     * Replaces the Minecraft splash text with the Vistas text.
     */
    @ModifyReturnValue(
            method = "getSplash",
            at = @At(value = "RETURN"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"),
                    to = @At(value = "TAIL")
            )
    )
    @SuppressWarnings("unused")
    private SplashRenderer vistas$replaceSplashRenderer(SplashRenderer original) {
        PanoramaResourceReloader resourceReloader =
                ((MinecraftAccess) Minecraft.getInstance()).vistas$getPanoramaResourceReloader();
        Identifier panoramaId = VistasTitle.ALL_PANORAMA_IDS.get(VistasTitle.CURRENT_PANORAMA.get());

        if (resourceReloader != null && panoramaId != null) {
            return new SplashRenderer(literalSplash(resourceReloader.get()));
        }

        return original;
    }
}

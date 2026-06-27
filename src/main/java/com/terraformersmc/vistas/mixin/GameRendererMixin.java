package com.terraformersmc.vistas.mixin;

import com.terraformersmc.vistas.title.VistasPanorama;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Panorama;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Mutable
    @Shadow
    @Final
    protected Panorama panorama;

    /**
     * Replaces the Minecraft panorama with the Vistas one.
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void vistas$replacePanorama(CallbackInfo ci) {
        this.panorama = new VistasPanorama();
    }

    /**
     * Closes the Vistas panorama.
     */
    @Inject(method = "close", at = @At("TAIL"))
    private void vistas$closePanorama(CallbackInfo ci) {
        if (this.panorama instanceof VistasPanorama vp) {
            vp.close();
        }
    }
}

package com.terraformersmc.vistas.mixin;

import com.terraformersmc.vistas.access.MinecraftAccess;
import com.terraformersmc.vistas.resource.PanoramaResourceReloader;
import com.terraformersmc.vistas.title.VistasPanorama;
import com.terraformersmc.vistas.title.VistasTitle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.sounds.Music;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements MinecraftAccess {

    @Unique
    private PanoramaResourceReloader panoramaResourceReloader;

    @Shadow
    @Final
    private ReloadableResourceManager resourceManager;

    @Shadow
    @Nullable
    public LocalPlayer player;

    @Shadow
    @Final
    private TextureManager textureManager;

    /**
     * Creates and registers the Vistas {@link PanoramaResourceReloader} as a resource reload
     * listener.
     */
    @Inject(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/packs/resources/ReloadableResourceManager;registerReloadListener(Lnet/minecraft/server/packs/resources/PreparableReloadListener;)V",
                    ordinal = 2,
                    shift = Shift.AFTER
            )
    )
    private void vistas$registerPanoramaReloader(GameConfig gameConfig, CallbackInfo ci) {
        this.panoramaResourceReloader = new PanoramaResourceReloader();
        this.resourceManager.registerReloadListener(this.panoramaResourceReloader);
    }

    /**
     * If not in-game, replaces the Minecraft music with the Vistas music.
     */
    @Inject(method = "getSituationalMusic", at = @At("HEAD"), cancellable = true)
    private void vistas$replaceMenuMusic(CallbackInfoReturnable<Music> ci) {
        if (this.player == null) {
            ci.setReturnValue(VistasTitle.CURRENT_PANORAMA.get().musicSound());
        }
    }

    /**
     * On game load completion (i.e., after resource loading has finished), registers the
     * panorama textures.
     */
    @Inject(method = "onGameLoadFinished", at = @At("HEAD"))
    private void vistas$registerTextures(@Nullable Minecraft.GameLoadCookie cookie, CallbackInfo ci) {
        if (Minecraft.getInstance().gameRenderer.getPanorama() instanceof VistasPanorama vp) {
            vp.registerTextures(this.textureManager);
        }
    }

    @Override
    public PanoramaResourceReloader vistas$getPanoramaResourceReloader() {
        return this.panoramaResourceReloader;
    }
}

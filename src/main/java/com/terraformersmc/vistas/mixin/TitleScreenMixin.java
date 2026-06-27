package com.terraformersmc.vistas.mixin;

import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.access.LogoDrawerAccess;
import com.terraformersmc.vistas.config.VistasConfig;
import com.terraformersmc.vistas.resource.PanoramaResourceReloader;
import com.terraformersmc.vistas.title.VistasTitle;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    @Shadow
    @Final
    private LogoRenderer logoRenderer;

    @Shadow
    @Nullable
    private SplashRenderer splash;

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    /**
     * On screen creation, if the Vistas default panorama is in use, randomly replaces the
     * Minecraft logo with the Vistas one.
     */
    @Inject(method = "<init>(Z)V", at = @At("TAIL"))
    private void vistas$replaceLogo(boolean fading, CallbackInfo ci) {
        boolean isVistas = new Random().nextDouble() < 0.1E-4D // 0.01%
                && VistasTitle.CURRENT_PANORAMA.get().equals(VistasTitle.ALL_PANORAMAS.get(Vistas.DEFAULT));

        ((LogoDrawerAccess) this.logoRenderer).vistas$setIsVistas(isVistas);
    }

    /**
     * On screen init, selects a Vistas panorama and, if the Vistas default panorama is in use,
     * randomly replaces the Minecraft logo with the Vistas one.
     */
    @Inject(method = "init", at = @At("HEAD"))
    private void vistas$init(CallbackInfo ci) {
        if (PanoramaResourceReloader.isReady()) {
            VistasTitle.selectPanorama();
        }

        if (VistasConfig.getInstance().forcePanorama)
            return;

        if (!VistasConfig.getInstance().randomPerScreen)
            return;

        this.splash = null;

        boolean isVistas = new Random().nextDouble() < 0.1E-4D // 0.01%
                && VistasTitle.CURRENT_PANORAMA.get().equals(VistasTitle.ALL_PANORAMAS.get(Vistas.DEFAULT));
        ((LogoDrawerAccess) this.logoRenderer).vistas$setIsVistas(isVistas);
    }
}

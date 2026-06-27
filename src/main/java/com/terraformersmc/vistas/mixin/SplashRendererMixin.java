package com.terraformersmc.vistas.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.terraformersmc.vistas.control.LogoControl;
import com.terraformersmc.vistas.control.PanoramaControl;
import com.terraformersmc.vistas.title.VistasTitle;
import net.minecraft.client.gui.components.SplashRenderer;
import org.joml.Matrix3x2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SplashRenderer.class)
public abstract class SplashRendererMixin {

    /**
     * Adjusts the splash text position and rotation.
     */
    @WrapOperation(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/joml/Matrix3x2f;rotate(F)Lorg/joml/Matrix3x2f;"
            )
    )
    @SuppressWarnings("ParameterCanBeLocal")
    private Matrix3x2f vistas$adjustSplash(
            Matrix3x2f instance,
            float ang,
            Operation<Matrix3x2f> operation
    ) {
        PanoramaControl panoramaCtrl = VistasTitle.CURRENT_PANORAMA.get();
        LogoControl logoCtrl = panoramaCtrl.logoControl();

        instance.translate((float) logoCtrl.splashX(), (float) logoCtrl.splashY());
        ang = (float) VistasTitle.CURRENT_PANORAMA.get().logoControl().splashRot();

        return operation.call(instance, (float) Math.toRadians(ang));
    }
}

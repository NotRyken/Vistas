package com.terraformersmc.vistas.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.terraformersmc.vistas.resource.PanoramicScreenshots;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.File;
import net.minecraft.client.Screenshot;

@Mixin(Screenshot.class)
public abstract class ScreenshotMixin {
    @WrapOperation(method = "method_68157",
            at = @At(value = "NEW", target = "(Ljava/io/File;Ljava/lang/String;)Ljava/io/File;")
    )
    @SuppressWarnings("unused")
    private static File vistas$panoramaPathOverride(File path, String file, Operation<File> original) {
        if (path.toString().contains(PanoramicScreenshots.PANORAMAS_PATH)) {
            return path;
        }

        return original.call(path, file);
    }
}

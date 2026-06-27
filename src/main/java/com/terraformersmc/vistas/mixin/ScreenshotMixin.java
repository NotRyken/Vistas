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

    /**
     * If the path references a Vistas file, returns it instead of the child.
     */
    @WrapOperation(
            method = "lambda$grab$0",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/io/File;Ljava/lang/String;)Ljava/io/File;"
            )
    )
    private static File vistas$overridePanoramaPath(
            File parent,
            String child,
            Operation<File> original
    ) {
        if (parent.toString().contains(PanoramicScreenshots.PANORAMAS_PATH)) {
            return parent;
        }

        return original.call(parent, child);
    }
}

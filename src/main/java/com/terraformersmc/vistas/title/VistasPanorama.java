package com.terraformersmc.vistas.title;

import com.terraformersmc.vistas.Vistas;
import com.terraformersmc.vistas.control.CubemapControl;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.Panorama;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.PanoramaRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.ReloadableTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.Map;

public class VistasPanorama extends Panorama implements AutoCloseable {

    private final Map<CubemapControl, VistasCubemapRenderer> renderers = new Object2ObjectOpenHashMap<>();
    private final Minecraft mc;

    public VistasPanorama() {
        super();
        this.mc = Minecraft.getInstance();
    }

    /**
     * Renders panorama cubemaps.
     *
     * @see #extractRenderState
     */
    public void renderCubemaps() {
        VistasTitle.CURRENT_PANORAMA.get().cubemapCtrls().forEach(cubemapCtrl -> {
            VistasCubemapRenderer renderer = renderers.get(cubemapCtrl);
            if (renderer != null) {
                renderer.draw(this.mc, 1.0F);
            }
        });
    }

    /**
     * Renders overlay cubemaps.
     *
     * @see #renderCubemaps
     */
    @Override
    public void extractRenderState(
            @NonNull GuiGraphicsExtractor graphics,
            int width,
            int height,
            boolean shouldSpin
    ) {
        mc.gameRenderer.getGameRenderState().guiRenderState.panoramaRenderState =
                new PanoramaRenderState(0.0F);

        VistasCubemapRenderer.time += this.mc.getDeltaTracker().getRealtimeDeltaTicks();
        // render overlays
        VistasTitle.CURRENT_PANORAMA.get().cubemapCtrls().forEach(cubemapCtrl -> {
            VistasCubemapRenderer renderer = renderers.get(cubemapCtrl);
            if (renderer != null) {
                Identifier overlayId = renderer.getcubemapCtrl()
                        .cubemapId()
                        .withSuffix("_overlay.png");
                if (this.mc.getResourceManager().getResource(overlayId).isPresent()) {
                    graphics.blit(
                            RenderPipelines.GUI_TEXTURED,
                            overlayId,
                            0,
                            0,
                            0.0F,
                            0.0F,
                            width,
                            height,
                            16,
                            128,
                            16,
                            128
                    );
                }
            }
        });
    }

    /**
     * Registers the textures for all panoramas, overlays and logos.
     */
    public void registerTextures(TextureManager textureManager) {
        VistasTitle.ALL_PANORAMAS.values().forEach(panoramaCtrl -> {
            // panorama and overlay cubemap textures
            panoramaCtrl.cubemapCtrls().forEach(cubemapCtrl -> {
                VistasCubemapRenderer renderer = new VistasCubemapRenderer(cubemapCtrl);
                renderer.registerTextures(textureManager);
                renderers.put(cubemapCtrl, renderer);
            });

            // logo texture
            Identifier logoId = panoramaCtrl.logoControl().logoId();
            textureManager.registerForNextReload(logoId);
            AbstractTexture logoTexture = textureManager.getTexture(logoId);
            if (logoTexture instanceof ReloadableTexture reloadableTexture) {
                try {
                    reloadableTexture.apply(
                            reloadableTexture.loadContents(this.mc.getResourceManager())
                    );
                } catch (IOException e) {
                    Vistas.LOGGER.warn("Failed to load texture: {}", logoId);
                }
            }
        });
    }

    @Override
    public void close() {
        renderers.values().forEach(VistasCubemapRenderer::close);
        renderers.clear();
    }
}

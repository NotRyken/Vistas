package com.terraformersmc.vistas.title;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.terraformersmc.vistas.control.CubemapControl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.WindowRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.CubeMapTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * @see net.minecraft.client.renderer.CubeMap
 */
public class VistasCubemapRenderer implements AutoCloseable {

    protected static double time = 0.0D;

    private final Projection projection;
    private final ProjectionMatrixBuffer projectionMatrix;
    private final CubemapControl cubemapCtrl;

    @Nullable
    private GpuBuffer vertexBuffer = null;

    public VistasCubemapRenderer(CubemapControl cubemapCtrl) {
        this.projection = new Projection();
        this.projectionMatrix = new ProjectionMatrixBuffer("cubemap");
        this.cubemapCtrl = cubemapCtrl;
    }

    public void draw(Minecraft mc, float alpha) {

        if (this.vertexBuffer == null) {
            this.vertexBuffer = upload(alpha);
        }

        WindowRenderState windowState = mc.gameRenderer.getGameRenderState().windowRenderState;
        this.projection.setupPerspective(0.05F, 10.0F, 85.0F, windowState.width, windowState.height);

        RenderSystem.setProjectionMatrix(
                this.projectionMatrix.getBuffer(this.projection),
                ProjectionType.PERSPECTIVE
        );

        RenderPipeline renderPipeline = RenderPipelines.PANORAMA;
        RenderTarget mainRenderTarget = Minecraft.getInstance().getMainRenderTarget();
        GpuTextureView colorTexture = mainRenderTarget.getColorTextureView();
        GpuTextureView depthTexture = mainRenderTarget.getDepthTextureView();
        RenderSystem.AutoStorageIndexBuffer indices = RenderSystem.getSequentialBuffer(
                VertexFormat.Mode.QUADS
        );
        GpuBuffer indexBuffer = indices.getBuffer(36);

        Matrix4fStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.pushMatrix();
        matrixStack.rotationX((float) Math.PI);
        matrixStack.translate(
                (float) this.cubemapCtrl.visualControl().addedX(),
                (float) this.cubemapCtrl.visualControl().addedY(),
                (float) this.cubemapCtrl.visualControl().addedZ()
        );

        matrixStack.rotate(Axis.XP.rotationDegrees(
                (float) this.cubemapCtrl.rotationControl().getPitch(cubemapCtrl.rotationControl().frozen()
                        ? 0.0D
                        : time
                )
        ));
        matrixStack.rotate(Axis.YP.rotationDegrees(
                (float) this.cubemapCtrl.rotationControl().getYaw(cubemapCtrl.rotationControl().frozen()
                        ? 0.0D : time
                )
        ));
        matrixStack.rotate(Axis.ZP.rotationDegrees(
                (float) this.cubemapCtrl.rotationControl().getRoll(cubemapCtrl.rotationControl().frozen()
                        ? 0.0D
                        : time
                )
        ));

        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().writeTransform(
                new Matrix4f(matrixStack),
                new Vector4f(1.0f, 1.0f, 1.0f, alpha),
                new Vector3f(),
                new Matrix4f()
        );
        matrixStack.popMatrix();

        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(
                        () -> "Cubemap",
                        colorTexture,
                        OptionalInt.empty(),
                        depthTexture,
                        OptionalDouble.empty()
                )
        ) {
            renderPass.setPipeline(renderPipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setVertexBuffer(0, this.vertexBuffer);
            renderPass.setIndexBuffer(indexBuffer, indices.type());
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            AbstractTexture texture = mc.getTextureManager().getTexture(this.cubemapCtrl.cubemapId());
            renderPass.bindTexture("Sampler0", texture.getTextureView(), texture.getSampler());
            renderPass.drawIndexed(0, 0, 36, 1);
        }
    }

    private GpuBuffer upload(float alpha) {
        GpuBuffer buffer;

        int r = Math.round((float) this.cubemapCtrl.visualControl().colorR());
        int g = Math.round((float) this.cubemapCtrl.visualControl().colorG());
        int b = Math.round((float) this.cubemapCtrl.visualControl().colorB());
        int a = Math.round((float) this.cubemapCtrl.visualControl().colorA() * alpha);

        float w = (float) this.cubemapCtrl.visualControl().width() / 2.0f;
        float h = (float) this.cubemapCtrl.visualControl().height() / 2.0f;
        float d = (float) this.cubemapCtrl.visualControl().depth() / 2.0f;

        try (ByteBufferBuilder byteBufferBuilder = ByteBufferBuilder.exactlySized(
                DefaultVertexFormat.POSITION.getVertexSize() * 4 * 6)
        ) {
            BufferBuilder bufferBuilder = new BufferBuilder(
                    byteBufferBuilder,
                    VertexFormat.Mode.QUADS,
                    DefaultVertexFormat.POSITION
            );

            // face 0
            bufferBuilder.addVertex(-w, -h, d).setUv(0.0F, 0.0F).setColor(r, g, b, a);
            bufferBuilder.addVertex(-w, h, d).setUv(0.0F, 1.0F).setColor(r, g, b, a);
            bufferBuilder.addVertex(w, h, d).setUv(1.0F, 1.0F).setColor(r, g, b, a);
            bufferBuilder.addVertex(w, -h, d).setUv(1.0F, 0.0F).setColor(r, g, b, a);

            // face 1
            bufferBuilder.addVertex(w, -h, d).setUv(0.0F, 0.0F).setColor(r, g, b, a);
            bufferBuilder.addVertex(w, h, d).setUv(0.0F, 1.0F).setColor(r, g, b, a);
            bufferBuilder.addVertex(w, h, -d).setUv(1.0F, 1.0F).setColor(r, g, b, a);
            bufferBuilder.addVertex(w, -h, -d).setUv(1.0F, 0.0F).setColor(r, g, b, a);

            // face 2
            bufferBuilder.addVertex(w, -h, -d).setUv(0.0F, 0.0F).setColor(r, g, b, a);
            bufferBuilder.addVertex(w, h, -d).setUv(0.0F, 1.0F).setColor(r, g, b, a);
            bufferBuilder.addVertex(-w, h, -d).setUv(1.0F, 1.0F).setColor(r, g, b, a);
            bufferBuilder.addVertex(-w, -h, -d).setUv(1.0F, 0.0F).setColor(r, g, b, a);

            // face 3
            bufferBuilder.addVertex(-w, -h, -d).setUv(0.0F, 0.0F).setColor(r, g, b, a);
            bufferBuilder.addVertex(-w, h, -d).setUv(0.0F, 1.0F).setColor(r, g, b, a);
            bufferBuilder.addVertex(-w, h, d).setUv(1.0F, 1.0F).setColor(r, g, b, a);
            bufferBuilder.addVertex(-w, -h, d).setUv(1.0F, 0.0F).setColor(r, g, b, a);

            // face 4
            bufferBuilder.addVertex(-w, -h, -d).setUv(0.0F, 0.0F).setColor(r, g, b, a);
            bufferBuilder.addVertex(-w, -h, d).setUv(0.0F, 1.0F).setColor(r, g, b, a);
            bufferBuilder.addVertex(w, -h, d).setUv(1.0F, 1.0F).setColor(r, g, b, a);
            bufferBuilder.addVertex(w, -h, -d).setUv(1.0F, 0.0F).setColor(r, g, b, a);

            // face 5
            bufferBuilder.addVertex(-w, h, d).setUv(0.0F, 0.0F).setColor(r, g, b, a);
            bufferBuilder.addVertex(-w, h, -d).setUv(0.0F, 1.0F).setColor(r, g, b, a);
            bufferBuilder.addVertex(w, h, -d).setUv(1.0F, 1.0F).setColor(r, g, b, a);
            bufferBuilder.addVertex(w, h, d).setUv(1.0F, 0.0F).setColor(r, g, b, a);

            try (MeshData builtBuffer = bufferBuilder.buildOrThrow()) {
                buffer = RenderSystem.getDevice().createBuffer(
                        () -> "Cube map vertex buffer",
                        32,
                        builtBuffer.vertexBuffer()
                );
            }
        }

        return buffer;
    }

    @Override
    public void close() {
        if (this.vertexBuffer != null) {
            this.vertexBuffer.close();
        }
        this.projectionMatrix.close();
    }

    public void registerTextures(TextureManager textureManager) {
        textureManager.registerAndLoad(
                this.cubemapCtrl.cubemapId(),
                new CubeMapTexture(this.cubemapCtrl.cubemapId())
        );
    }

    public CubemapControl getcubemapCtrl() {
        return cubemapCtrl;
    }
}

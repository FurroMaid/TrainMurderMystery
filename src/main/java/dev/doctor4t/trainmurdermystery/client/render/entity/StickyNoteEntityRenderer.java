package dev.doctor4t.trainmurdermystery.client.render.entity;

import dev.doctor4t.trainmurdermystery.entity.StickyNoteEntity;
import dev.doctor4t.trainmurdermystery.index.TMMItems;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.NotNull;

public class StickyNoteEntityRenderer extends EntityRenderer<StickyNoteEntity> {
    private final ItemRenderer itemRenderer;
    private final float scale;

    public StickyNoteEntityRenderer(EntityRendererFactory.Context ctx, float scale) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
        this.scale = scale;
    }

    public StickyNoteEntityRenderer(EntityRendererFactory.Context context) {
        this(context, 1.0F);
    }

    @Override
    public void render(@NotNull StickyNoteEntity note, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (note.age >= 2 || !(this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(note) < 12.25)) {
            matrices.push();
            matrices.translate(0, note.getHeight() / 2f, 0);
            matrices.multiply(note.getDirection().getRotationQuaternion());
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(note.seed));
            matrices.translate(0, note.hashCode() % 24f * .0001f, 0);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            matrices.scale(this.scale * .4f, this.scale * .4f, this.scale * .4f);
            this.itemRenderer.renderItem(TMMItems.STICKY_NOTE.getDefaultStack(), ModelTransformationMode.FIXED, 255, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, note.getWorld(), note.getId());
            matrices.pop();
            super.render(note, yaw, tickDelta, matrices, vertexConsumers, light);
        }
    }

    @Override
    public Identifier getTexture(StickyNoteEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
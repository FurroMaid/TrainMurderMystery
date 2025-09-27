package dev.doctor4t.trainmurdermystery.client.gui;

import dev.doctor4t.trainmurdermystery.cca.PlayerPsychoComponent;
import dev.doctor4t.trainmurdermystery.cca.TMMComponents;
import dev.doctor4t.trainmurdermystery.cca.WorldBlackoutComponent;
import dev.doctor4t.trainmurdermystery.entity.StickyNoteEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;

public class RoleNameRenderer {
    private static TrainRole targetRole = TrainRole.BYSTANDER;
    private static float nametagAlpha = 0f;
    private static float noteAlpha = 0f;
    private static Text nametag = Text.empty();
    private static final Text[] note = new Text[]{Text.empty(), Text.empty(), Text.empty(), Text.empty()};

    public static void renderHud(TextRenderer renderer, @NotNull ClientPlayerEntity player, DrawContext context, RenderTickCounter tickCounter) {
        if (WorldBlackoutComponent.KEY.get(player.getWorld()).isBlackoutActive()) return;
        var component = TMMComponents.GAME.get(player.getWorld());
        if (ProjectileUtil.getCollision(player, entity -> entity instanceof PlayerEntity, 2f) instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof PlayerEntity target) {
            nametagAlpha = MathHelper.lerp(tickCounter.getTickDelta(true) / 4, nametagAlpha, 1f);
            nametag = target.getDisplayName();
            if (component.isHitman(target)) {
                targetRole = TrainRole.HITMAN;
            } else {
                targetRole = TrainRole.BYSTANDER;
            }
            var shouldObfuscate = targetRole == TrainRole.BYSTANDER && PlayerPsychoComponent.KEY.get(target).getPsychoTicks() > 0;
            nametag = shouldObfuscate ? Text.literal("urscrewed" + "X".repeat(Random.createThreadSafe().nextInt(8))).styled(style -> style.withFormatting(Formatting.OBFUSCATED, Formatting.DARK_RED)) : nametag;
        } else {
            nametagAlpha = MathHelper.lerp(tickCounter.getTickDelta(true) / 4, nametagAlpha, 0f);
        }
        if (nametagAlpha > 0.05f) {
            context.getMatrices().push();
            context.getMatrices().translate(context.getScaledWindowWidth() / 2f, context.getScaledWindowHeight() / 2f + 6, 0);
            context.getMatrices().scale(0.6f, 0.6f, 1f);
            var nameWidth = renderer.getWidth(nametag);
            context.drawTextWithShadow(renderer, nametag, -nameWidth / 2, 16, MathHelper.packRgb(1f, 1f, 1f) | ((int) (nametagAlpha * 255) << 24));
            if (component.isRunning()) {
                var playerRole = TrainRole.BYSTANDER;
                if (component.isHitman(player)) playerRole = TrainRole.HITMAN;
                if (playerRole == TrainRole.HITMAN && targetRole == TrainRole.HITMAN) {
                    context.getMatrices().translate(0, 20 + renderer.fontHeight, 0);
                    var roleText = Text.literal("HITMAN COHORT");
                    var roleWidth = renderer.getWidth(roleText);
                    context.drawTextWithShadow(renderer, roleText, -roleWidth / 2, 0, MathHelper.packRgb(1f, 0f, 0f) | ((int) (nametagAlpha * 255) << 24));
                }
            }
            context.getMatrices().pop();
        }
        if (ProjectileUtil.getCollision(player, entity -> entity instanceof StickyNoteEntity, 2f) instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof StickyNoteEntity stickyNote) {
            noteAlpha = MathHelper.lerp(tickCounter.getTickDelta(true) / 4, noteAlpha, 1f);
            note[0] = Text.literal(stickyNote.getLines()[0]);
            note[1] = Text.literal(stickyNote.getLines()[1]);
            note[2] = Text.literal(stickyNote.getLines()[2]);
            note[3] = Text.literal(stickyNote.getLines()[3]);
        } else {
            noteAlpha = MathHelper.lerp(tickCounter.getTickDelta(true) / 4, noteAlpha, 0f);
        }
        if (noteAlpha > 0.05f) {
            context.getMatrices().push();
            context.getMatrices().translate(context.getScaledWindowWidth() / 2f, context.getScaledWindowHeight() / 2f + 6, 0);
            context.getMatrices().scale(0.6f, 0.6f, 1f);
            for (var i = 0; i < note.length; i++) {
                var line = note[i];
                var lineWidth = renderer.getWidth(line);
                context.drawTextWithShadow(renderer, line, -lineWidth / 2, 16 + (i * (renderer.fontHeight + 2)), MathHelper.packRgb(1f, 1f, 1f) | ((int) (noteAlpha * 255) << 24));
            }
            context.getMatrices().pop();
        }
    }

    private enum TrainRole {
        HITMAN,
        BYSTANDER
    }
}
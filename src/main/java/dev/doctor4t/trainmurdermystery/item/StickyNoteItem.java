package dev.doctor4t.trainmurdermystery.item;

import dev.doctor4t.trainmurdermystery.cca.PlayerNoteComponent;
import dev.doctor4t.trainmurdermystery.client.gui.screen.ingame.NoteScreen;
import dev.doctor4t.trainmurdermystery.index.TMMEntities;
import dev.doctor4t.trainmurdermystery.util.AdventureUsable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class StickyNoteItem extends Item implements AdventureUsable {
    public StickyNoteItem(Settings settings) {
        super(settings);
    }

    @Override @Environment(EnvType.CLIENT)
    public TypedActionResult<ItemStack> use(@NotNull World world, PlayerEntity user, Hand hand) {
        if (world.isClient && user.isSneaking()) MinecraftClient.getInstance().setScreen(new NoteScreen());
        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnBlock(@NotNull ItemUsageContext context) {
        var player = context.getPlayer();
        if (player == null || player.isSneaking()) return ActionResult.PASS;
        var component = PlayerNoteComponent.KEY.get(player);
        if (!component.written) {
            player.sendMessage(Text.literal("I should write something first").withColor(MathHelper.hsvToRgb(0F, 1.0F, 0.6F)), true);
            return ActionResult.PASS;
        }
        var world = player.getWorld();
        if (world.isClient) return ActionResult.PASS;
        var stickyNote = TMMEntities.STICKY_NOTE.create(world);
        if (stickyNote == null) return ActionResult.PASS;
        var side = context.getSide();
        stickyNote.setDirection(side);
        stickyNote.setLines(component.text);
        var hitPos = context.getHitPos().subtract(0, stickyNote.getHeight() / 2f, 0);
        stickyNote.setPosition(hitPos.getX(), hitPos.getY(), hitPos.getZ());
        stickyNote.setYaw(player.getHeadYaw());
        world.spawnEntity(stickyNote);
        if (!player.isCreative()) player.getStackInHand(context.getHand()).decrement(1);
        return ActionResult.SUCCESS;
    }
}
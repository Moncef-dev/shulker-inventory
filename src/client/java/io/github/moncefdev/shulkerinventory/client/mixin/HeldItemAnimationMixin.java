package io.github.moncefdev.shulkerinventory.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.moncefdev.shulkerinventory.client.ClientShulkerSession;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Animates the lid of a shulker held by an entity as seen in third person (notably another player's hand). The
// GUI, first-person hand, and dropped-entity paths are instrumented elsewhere; this covers the held item on other
// entities. Around the held-item submit, publishes the stack's animation id to the openness side channel when this
// client is tracking that animation (driven locally for the holder, or by the server broadcast for other viewers).
@Mixin(ItemInHandLayer.class)
public abstract class HeldItemAnimationMixin {
	@Inject(method = "submitArmWithItem", at = @At("HEAD"))
	private void shulkerInventory$markHeld(ArmedEntityRenderState state, ItemStackRenderState itemRenderState,
			ItemStack itemStack, HumanoidArm arm, PoseStack pose, SubmitNodeCollector collector, int light,
			CallbackInfo ci) {
		Long id = ClientShulkerSession.getAnimationIdForStack(itemStack);
		if (id != null && ClientShulkerSession.isAnimating(id)) {
			ClientShulkerSession.setCurrentItemEntityAnimationId(id);
		}
	}

	@Inject(method = "submitArmWithItem", at = @At("RETURN"))
	private void shulkerInventory$unmarkHeld(ArmedEntityRenderState state, ItemStackRenderState itemRenderState,
			ItemStack itemStack, HumanoidArm arm, PoseStack pose, SubmitNodeCollector collector, int light,
			CallbackInfo ci) {
		ClientShulkerSession.setCurrentItemEntityAnimationId(0L);
	}
}

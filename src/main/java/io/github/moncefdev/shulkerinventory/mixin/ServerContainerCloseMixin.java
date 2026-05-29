package io.github.moncefdev.shulkerinventory.mixin;

import io.github.moncefdev.shulkerinventory.menu.InventoryShulkerBoxMenu;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerContainerCloseMixin {
	@Shadow
	public ServerPlayer player;

	// During a shulker swap we close one menu and immediately open another; a late close for the previous menu
	// could otherwise close the freshly opened one. Scoped to OUR menu so we never suppress close packets for
	// other mods' containers: only drop a mismatched close while one of our shulker menus is the open container.
	@Inject(method = "handleContainerClose", at = @At("HEAD"), cancellable = true)
	private void shulkerInventory$ignoreStaleClose(ServerboundContainerClosePacket packet, CallbackInfo ci) {
		if (player.containerMenu instanceof InventoryShulkerBoxMenu
				&& packet.getContainerId() != player.containerMenu.containerId) {
			ci.cancel();
		}
	}
}

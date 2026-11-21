package dev.slotmine.mixin;

import dev.slotmine.SlotMine;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Hook 100% cliente: avisa al motor cuando el cliente rompe un bloque (no envía nada a la red). */
@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    private BlockState slotmine$before;

    @Inject(method = "breakBlock", at = @At("HEAD"))
    private void slotmine$capture(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient mc = MinecraftClient.getInstance();
        slotmine$before = mc.world != null ? mc.world.getBlockState(pos) : null;
    }

    @Inject(method = "breakBlock", at = @At("RETURN"))
    private void slotmine$after(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState before = slotmine$before;
        slotmine$before = null;
        if (!cir.getReturnValueZ() || before == null || SlotMine.engine == null) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;
        SlotMine.engine.onBlockBroken(mc.world, mc.player, pos, before);
    }
}

package ca.fxco.memoryleakfix.mixin.targetEntityLeak;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClient_targetClearMixin {

    @Shadow
    @Nullable
    public Entity targetedEntity;

    @Shadow
    @Nullable
    public HitResult crosshairTarget;


    @Inject(
            method = "reset",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;render(Z)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void resetTarget(Screen screen, CallbackInfo ci) {
        this.targetedEntity = null;
        this.crosshairTarget = null;
    }
}

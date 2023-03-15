package ca.fxco.memoryleakfix.mixin.spongePoweredLeak;

import ca.fxco.memoryleakfix.MemoryLeakFix;
import net.minecraft.client.main.Main;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(Main.class)
public class Main_clientLoadedMixin {


    @Inject(
            method = "main([Ljava/lang/String;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;shouldRenderAsync()Z",
                    shift = At.Shift.BEFORE
            )
    )
    private static void loadAllMixinsThenShouldRenderAsync(String[] args, boolean optimizeDataFixer, CallbackInfo ci) {
        MemoryLeakFix.forceLoadAllMixinsAndClearSpongePoweredCache();
    }
}

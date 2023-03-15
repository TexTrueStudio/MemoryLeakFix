package ca.fxco.memoryleakfix.mixin.spongePoweredLeak;

import ca.fxco.memoryleakfix.MemoryLeakFix;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.main.Main;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@OnlyIn(Dist.CLIENT)
@Mixin(Main.class)
public class Main_clientLoadedMixin {


    @Redirect(
            method = "main([Ljava/lang/String;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;shouldRenderAsync()Z"
            )
    )
    private static boolean loadAllMixinsThenShouldRenderAsync(MinecraftClient instance) {
        MemoryLeakFix.forceLoadAllMixinsAndClearSpongePoweredCache();
        return instance.shouldRenderAsync();
    }
}

package ca.fxco.memoryleakfix.mixin.spongePoweredLeak;

import ca.fxco.memoryleakfix.MemoryLeakFix;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.DEDICATED_SERVER)
@Mixin(MinecraftServer.class)
public class MinecraftServer_loadWorldMixin {


    @Inject(
            method = "loadWorld",
            at = @At("RETURN")
    )
    private void onFinishedLoadingWorlds(CallbackInfo ci) {
        MemoryLeakFix.forceLoadAllMixinsAndClearSpongePoweredCache();
    }
}

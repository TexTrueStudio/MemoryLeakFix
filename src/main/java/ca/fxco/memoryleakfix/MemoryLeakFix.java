package ca.fxco.memoryleakfix;

import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.logging.LoggerAdapterDefault;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;

import java.lang.reflect.Field;
import java.util.*;

@Mod(MemoryLeakFix.MOD_ID)
public class MemoryLeakFix {

    public static final String MOD_ID = "memoryleakfix";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Set<PacketByteBuf> BUFFERS_TO_CLEAR = Collections.synchronizedSet(new HashSet<>());

    public MemoryLeakFix() {
        IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();

        MOD_BUS.addListener(this::onInitialize);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onInitialize(final FMLCommonSetupEvent event) {}

    public static void forceLoadAllMixinsAndClearSpongePoweredCache() {
        LOGGER.info("[MemoryLeakFix] Attempting to ForceLoad All Mixins and clear cache");
        silenceAuditLogger();
        MixinEnvironment.getCurrentEnvironment().audit();
        try { //Why is SpongePowered stealing so much ram for this garbage?
            Field noGroupField = InjectorGroupInfo.Map.class.getDeclaredField("NO_GROUP");
            noGroupField.setAccessible(true);
            Object noGroup = noGroupField.get(null);
            Field membersField = noGroup.getClass().getDeclaredField("members");
            membersField.setAccessible(true);
            ((List<?>) membersField.get(noGroup)).clear(); // Clear spongePoweredCache
            emptyClassInfo();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        LOGGER.info("[MemoryLeakFix] Done ForceLoad and clearing SpongePowered cache");
    }

    private static Class<?> getMixinLoggerClass() throws ClassNotFoundException {
        Class<?> mixinLogger;
        try {
            mixinLogger = Class.forName("net.fabricmc.loader.impl.launch.knot.MixinLogger");
        } catch (ClassNotFoundException err) {
            mixinLogger = Class.forName("org.quiltmc.loader.impl.launch.knot.MixinLogger");
        }
        return mixinLogger;
    }

    private static void silenceAuditLogger() {
        try {
            Field loggerField = getMixinLoggerClass().getDeclaredField("LOGGER_MAP");
            loggerField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, ILogger> loggerMap = (Map<String, ILogger>)loggerField.get(null);
            loggerMap.put("mixin.audit", new LoggerAdapterDefault("mixin.audit"));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
    
    private static final String OBJECT = "java/lang/Object";

    private static void emptyClassInfo() throws NoSuchFieldException, IllegalAccessException {
        if (ModList.get().isLoaded("not-that-cc"))
            return; // Crashes crafty crashes if it crashes
        Field cacheField = ClassInfo.class.getDeclaredField("cache");
        cacheField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, ClassInfo> cache = ((Map<String, ClassInfo>)cacheField.get(null));
        ClassInfo jlo = cache.get(OBJECT);
        cache.clear();
        cache.put(OBJECT, jlo);
    }
}

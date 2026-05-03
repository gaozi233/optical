package net.lpcamors.optical;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBufferCache;
import net.createmod.ponder.foundation.PonderIndex;
import net.lpcamors.optical.ponder.COPonderPlugin;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class CreateOpticalClient {

    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(CreateOpticalClient::clientInit);
    }

    public static void clientInit(final FMLClientSetupEvent event) {

        SuperByteBufferCache cache = SuperByteBufferCache.getInstance();

        cache.registerCompartment(CachedBuffers.PARTIAL);
        cache.registerCompartment(CachedBuffers.DIRECTIONAL_PARTIAL);
        
        COPartialModels.initiate();

        PonderIndex.addPlugin(new COPonderPlugin());

    }

}

package net.lpcamors.optical;

import net.createmod.ponder.foundation.PonderIndex;
import net.lpcamors.optical.ponder.COPonderPlugin;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class CreateOpticalClient {

    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(CreateOpticalClient::clientInit);
    }

    public static void clientInit(final FMLClientSetupEvent event) {
        COPartialModels.initiate();
        PonderIndex.addPlugin(new COPonderPlugin());

    }

}

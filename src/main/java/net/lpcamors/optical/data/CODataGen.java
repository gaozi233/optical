package net.lpcamors.optical.data;

import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;

import net.createmod.ponder.foundation.PonderIndex;
import net.lpcamors.optical.CreateOptical;
import net.lpcamors.optical.ponder.COPonderPlugin;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class CODataGen {

    public static void gatherDataHighPriority(GatherDataEvent event) {
        if (event.getMods().contains(CreateOptical.ID))
            addExtraRegistrateData();
    }

    public static void gatherData(GatherDataEvent event) {
        if (!event.getMods().contains(CreateOptical.ID))
            return;
        //
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        COEntriesProvider generatedEntriesProvider = new COEntriesProvider(output, lookupProvider);
        generator.addProvider(event.includeServer(),
                new COBlockTagsProvider(output, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), generatedEntriesProvider);
        generator.addProvider(event.includeServer(), new COSequencedAssemblyRecipeProvider(output, lookupProvider));
        generator.addProvider(event.includeServer(), new FocusingRecipeGen(output, lookupProvider));

    }

    private static void addExtraRegistrateData() {
        CreateOptical.REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
            BiConsumer<String, String> langConsumer = provider::add;

            //provideDefaultLang("interface", langConsumer);
            //provideDefaultLang("tooltips", langConsumer);
            providePonderLang(langConsumer);
        });

    }
    private static void provideDefaultLang(String fileName, BiConsumer<String, String> consumer) {
        String path = "assets/create_optical/lang/default/" + fileName + ".json";
        JsonElement jsonElement = FilesHelper.loadJsonResource(path);
        if (jsonElement == null) {
            throw new IllegalStateException(String.format("Could not find default lang file: %s", path));
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getAsString();
            consumer.accept(key, value);
        }
    }

    private static void providePonderLang(BiConsumer<String, String> consumer) {
        PonderIndex.addPlugin(new COPonderPlugin());
        PonderIndex.getLangAccess().provideLang(CreateOptical.ID, consumer);
    }

}

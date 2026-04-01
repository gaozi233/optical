package net.lpcamors.optical.data;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.lpcamors.optical.CORecipeTypes;
import net.lpcamors.optical.CreateOptical;
import net.lpcamors.optical.items.COItems;
import net.lpcamors.optical.recipes.FocusingRecipe;
import net.lpcamors.optical.recipes.FocusingRecipeParams;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

public class FocusingRecipeGen extends ProcessingRecipeGen {

    FocusingRecipeGen(PackOutput generator) {
        super(generator, CreateOptical.ID);
    }

    GeneratedRecipe MIRROR = createFocusing(CreateOptical.loc("mirror"),
            f -> ((FocusingRecipe.Builder) f.require(Tags.Items.GLASS_PANES).output(COItems.MIRROR).duration(50))
                    .setBeamTypeCondition(FocusingRecipeParams.BeamTypeCondition.VISIBLE),
            FocusingRecipeParams.BeamTypeCondition.VISIBLE),
            FILTER = createFocusing(CreateOptical.loc("filter"),
                    f -> ((FocusingRecipe.Builder) f.require(Items.TINTED_GLASS).output(COItems.POLARIZING_FILTER)
                            .duration(50))
                            .setBeamTypeCondition(FocusingRecipeParams.BeamTypeCondition.VISIBLE),
                    FocusingRecipeParams.BeamTypeCondition.VISIBLE);

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CORecipeTypes.FOCUSING;

    }

    protected <T extends ProcessingRecipe<?>> GeneratedRecipe createFocusing(ResourceLocation name,
            UnaryOperator<FocusingRecipe.Builder> transform,
            FocusingRecipeParams.BeamTypeCondition b) {
        ProcessingRecipeSerializer<FocusingRecipe> serializer = getSerializer();

        GeneratedRecipe generatedRecipe = c -> transform
                .apply(new FocusingRecipe.Builder(serializer.getFactory(), name).setBeamTypeCondition(b)).build(c);

        all.add(generatedRecipe);
        return generatedRecipe;
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> writer) {
        super.buildRecipes(writer);
    }
}

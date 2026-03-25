package net.lpcamors.optical.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.lpcamors.optical.CreateOptical;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class FocusingRecipeInfo implements IRecipeTypeInfo {

    private ResourceLocation id;
    private ProcessingRecipeSerializer<FocusingRecipe> serializer;
    private RecipeType<FocusingRecipe> type;

    public FocusingRecipeInfo(String name, ProcessingRecipeSerializer<FocusingRecipe> serializer, RecipeType<FocusingRecipe> type) { 
        this.id = new ResourceLocation(CreateOptical.ID, "focusing_" + name);
        this.serializer = serializer;
        this.type = type;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) this.serializer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends RecipeType<?>> T getType() {
        return (T) this.type;
    }

}

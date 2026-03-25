package net.lpcamors.optical.recipes;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.JsonObject;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;

import net.lpcamors.optical.CORecipeTypes;
import net.lpcamors.optical.CreateOptical;
import net.lpcamors.optical.blocks.COBlocks;
import net.lpcamors.optical.blocks.beam_focuser.BeamFocuserBlockEntity;
import net.lpcamors.optical.compat.jei.FocusingAssemblySubcategory;
import net.lpcamors.optical.data.COLang;
import net.lpcamors.optical.recipes.FocusingRecipeParams.BeamTypeCondition;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;

@ParametersAreNonnullByDefault
public class FocusingRecipe extends ProcessingRecipe<RecipeWrapper> implements IAssemblyRecipe {

    private static final String REQUIRED_BEAM_TYPE_KEY = "required_beam_type";

    public FocusingRecipeParams.BeamTypeCondition beamTypeCondition = FocusingRecipeParams.BeamTypeCondition.NONE;

    public static FocusingRecipe focusing(FocusingRecipeParams params, FocusingRecipeParams.BeamTypeCondition type) {
        FocusingRecipe f = new FocusingRecipe(params);
        f.beamTypeCondition = type;
        return f;
    }

    public FocusingRecipe(FocusingRecipeParams params) {
        super(CORecipeTypes.FOCUSING,
                params);
    }

    public FocusingRecipe(ProcessingRecipeParams params) {
        super(
                CORecipeTypes.FOCUSING,
                params);

    }

    @Override
    protected int getMaxInputCount() {
        return 2;
    }

    @Override
    protected int getMaxOutputCount() {
        return 2;
    }

    @Override
    public Component getDescriptionForAssembly() {
        return COLang.Prefixes.JEI.translate("focusing.sequence." + this.beamTypeCondition.getId());
    }

    @Override
    public void addRequiredMachines(Set<ItemLike> list) {
        list.add(COBlocks.BEAM_FOCUSER.get());
    }

    @Override
    public void addAssemblyIngredients(List<Ingredient> list) {
        if (this.ingredients.size() > 1)
            list.add(this.getIngredients().get(1));
    }

    @Override
    public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
        return () -> FocusingAssemblySubcategory::new;
    }

    @Override
    public void readAdditional(JsonObject json) {
        super.readAdditional(json);
        try {
            this.beamTypeCondition = BeamTypeCondition.values()[GsonHelper.getAsInt(json, "beam_type")];
        } catch (Exception ex) {
            this.beamTypeCondition = BeamTypeCondition.NONE;
        }
    }

    @Override
    public void readAdditional(FriendlyByteBuf buffer) {
        super.readAdditional(buffer);
        this.beamTypeCondition = BeamTypeCondition.values()[buffer.readInt()];
    }

    @Override
    public void writeAdditional(FriendlyByteBuf buffer) {
        super.writeAdditional(buffer);
        buffer.writeInt(this.beamTypeCondition.ordinal());
    }

    @Override
    public boolean matches(RecipeWrapper p_44002_, Level p_44003_) {
        if (p_44002_.isEmpty())
            return false;
        boolean f = this.getIngredient().test(p_44002_.getItem(0));
        if (!p_44002_.getItem(1).isEmpty()) {
            f &= this.getSecondIngredient().test(p_44002_.getItem(1));
        }
        return f;
    }

    public Ingredient getIngredient() {
        return this.getIngredients().get(0);
    }

    public Ingredient getSecondIngredient() {
        if (this.ingredients.size() > 1) {
            return this.getIngredients().get(1);
        }
        return Ingredient.EMPTY;
    }

    public ProcessingOutput getOutput() {
        return this.results.get(0);
    }

    @Override
    public int getProcessingDuration() {
        int i = super.getProcessingDuration();
        return i == 0 ? BeamFocuserBlockEntity.PROCESSING_TICK : i;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

    public FocusingRecipeParams.BeamTypeCondition getRequiredBeamType() {
        return this.beamTypeCondition;
    }

    protected FocusingRecipeParams.BeamTypeCondition readRequiredBeamType(JsonObject jsonObject) {
        if (jsonObject.has(REQUIRED_BEAM_TYPE_KEY)) {
            var got = jsonObject.get(REQUIRED_BEAM_TYPE_KEY);
            var parsed = FocusingRecipeParams.BeamTypeCondition.INDEXED.fromJson(got);
            if (parsed != null) {
                return parsed;
            }
        }
        return FocusingRecipeParams.BeamTypeCondition.NONE;
    }

    protected FocusingRecipeParams.BeamTypeCondition readRequiredBeamType(FriendlyByteBuf buffer) {
        int i = buffer.readInt();
        if (i >= 0 && i < FocusingRecipeParams.BeamTypeCondition.values().length) {
            return FocusingRecipeParams.BeamTypeCondition.values()[i];
        }
        return FocusingRecipeParams.BeamTypeCondition.NONE;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return getRollableResults().isEmpty() ? ItemStack.EMPTY
                : getRollableResults().get(0)
                        .getStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CORecipeTypes.FOCUSING.getSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return CORecipeTypes.FOCUSING.getType();
    }

    @Override
    public ResourceLocation getId() {
        return CreateOptical.loc("focusing_" + this.getOutput().getStack().getItem().toString());
    }

    public ItemStack assemble(RecipeWrapper container, RegistryAccess registryAccess) {
        return getResultItem(registryAccess);
    }

    public static class Builder
            extends ProcessingRecipeBuilder<FocusingRecipe> {

        private FocusingRecipeParams.BeamTypeCondition beamTypeCondition;

        public Builder setBeamTypeCondition(FocusingRecipeParams.BeamTypeCondition beamTypeCondition) {
            this.beamTypeCondition = beamTypeCondition;
            return this;
        }

        public FocusingRecipeParams.BeamTypeCondition getBeamTypeCondition() {
            return beamTypeCondition;
        }

        public Builder(ProcessingRecipeFactory<FocusingRecipe> factory, ResourceLocation recipeId) {
            super(factory, recipeId);
        }

    }
}

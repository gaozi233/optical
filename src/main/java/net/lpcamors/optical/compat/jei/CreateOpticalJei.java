package net.lpcamors.optical.compat.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.simibubi.create.compat.jei.CreateJEI;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CRecipes;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.lpcamors.optical.CORecipeTypes;
import net.lpcamors.optical.CreateOptical;
import net.lpcamors.optical.blocks.COBlocks;
import net.lpcamors.optical.data.COLang;
import net.lpcamors.optical.recipes.FocusingRecipe;
import net.lpcamors.optical.recipes.FocusingRecipeCategory;
import net.lpcamors.optical.recipes.FocusingRecipeParams;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

@JeiPlugin
public class CreateOpticalJei implements IModPlugin {

    private static ResourceLocation ID = new ResourceLocation(CreateOptical.ID, "jei_plugin");

    private List<CreateRecipeCategory<?>> categories = new ArrayList<>();
    public IIngredientManager ingredientManager;

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        categories.clear();

        categories.add(builder(FocusingRecipe.class)
                .addAllRecipesIf(FocusingRecipeParams.BeamTypeConditionProfile.SMOKING.getRecipePredicate(),
                        FocusingRecipeParams.BeamTypeConditionProfile.SMOKING
                                .getConverter(Minecraft.getInstance().level.registryAccess()))

                .addAllRecipesIf(FocusingRecipeParams.BeamTypeConditionProfile.ADD_COLOR.getRecipePredicate(),
                        FocusingRecipeParams.BeamTypeConditionProfile.ADD_COLOR
                                .getConverter(Minecraft.getInstance().level.registryAccess()))

                .addAllRecipesIf(FocusingRecipeParams.BeamTypeConditionProfile.SANDPAPER.getRecipePredicate(),
                        FocusingRecipeParams.BeamTypeConditionProfile.SANDPAPER
                                .getConverter(Minecraft.getInstance().level.registryAccess()))
                .addRecipes(() -> FocusingRecipeParams.BeamTypeConditionProfile.SHERDS.getRecipes())
                .addRecipes(() -> FocusingRecipeParams.BeamTypeConditionProfile.PATTERN.getRecipes())
                .addRecipes(() -> FocusingRecipeParams.BeamTypeConditionProfile.DISC.getRecipes())
                .addTypedRecipes(CORecipeTypes.FOCUSING::getType)
                .catalystStack(() -> new ItemStack(COBlocks.BEAM_FOCUSER.get().asItem(), 1))
                .itemIcon(COBlocks.BEAM_FOCUSER)
                .emptyBackground(177, 103)
                .build("focusing", FocusingRecipeCategory::new));

        categories.forEach(registration::addRecipeCategories);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        this.ingredientManager = registration.getIngredientManager();
        categories.forEach(createRecipeCategory -> createRecipeCategory.registerRecipes(registration));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        this.categories.forEach(createRecipeCategory -> {
            createRecipeCategory.registerCatalysts(registration);
        });
    }

    private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
        return new CategoryBuilder<>(recipeClass);
    }

    private static class CategoryBuilder<T extends Recipe<?>> {
        private final Class<? extends T> recipeClass;
        private final Predicate<CRecipes> predicate = cRecipes -> true;

        private IDrawable background;
        private IDrawable icon;

        private final List<Consumer<List<T>>> recipeListConsumers = new ArrayList<>();
        private final List<Supplier<? extends ItemStack>> catalysts = new ArrayList<>();

        public CategoryBuilder(Class<? extends T> recipeClass) {
            this.recipeClass = recipeClass;
        }

        public CategoryBuilder<T> addRecipeListConsumer(Consumer<List<T>> consumer) {
            recipeListConsumers.add(consumer);
            return this;
        }

        public CategoryBuilder<T> addTypedRecipes(Supplier<RecipeType<? extends T>> recipeType) {
            return addRecipeListConsumer(recipes -> CreateJEI.consumeTypedRecipes((Consumer<T>) recipes::add,
                    (RecipeType<?>) recipeType.get()));
        }

        public CategoryBuilder<T> addAllRecipesIf(Predicate<Recipe<?>> pred, Function<Recipe<?>, T> converter) {
            return addRecipeListConsumer(recipes -> CreateJEI.consumeAllRecipes(recipe -> {
                if (pred.test(recipe)) {
                    recipes.add(converter.apply(recipe));
                }
            }));
        }

        public CategoryBuilder<T> addRecipes(Supplier<Collection<? extends T>> collection) {
            return addRecipeListConsumer(recipes -> recipes.addAll(collection.get()));
        }

        public CategoryBuilder<T> catalystStack(Supplier<ItemStack> supplier) {
            catalysts.add(supplier);
            return this;
        }

        public CategoryBuilder<T> icon(IDrawable icon) {
            this.icon = icon;
            return this;
        }

        public CategoryBuilder<T> itemIcon(ItemLike item) {
            icon(new ItemIcon(() -> new ItemStack(item)));
            return this;
        }

        public CategoryBuilder<T> background(IDrawable background) {
            this.background = background;
            return this;
        }

        public CategoryBuilder<T> emptyBackground(int width, int height) {
            background(new EmptyBackground(width, height));
            return this;
        }

        public CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
            Supplier<List<T>> recipesSupplier;
            if (predicate.test(AllConfigs.server().recipes)) {
                recipesSupplier = () -> {
                    List<T> recipes = new ArrayList<>();
                    for (Consumer<List<T>> consumer : recipeListConsumers)
                        consumer.accept(recipes);
                    return recipes;
                };
            } else {
                recipesSupplier = () -> Collections.emptyList();
            }

            CreateRecipeCategory.Info<T> info = new CreateRecipeCategory.Info<>(
                    new mezz.jei.api.recipe.RecipeType<>(CreateOptical.loc(name), recipeClass),
                    COLang.Prefixes.JEI.translate(name), background, icon, recipesSupplier, catalysts);
            CreateRecipeCategory<T> category = factory.create(info);
            return category;
        }
    }

}

package net.lpcamors.optical.blocks.beam_focuser;

import java.util.Optional;

import javax.annotation.Nullable;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;

import net.lpcamors.optical.CORecipeTypes;
import net.lpcamors.optical.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.recipes.FocusingRecipe;
import net.lpcamors.optical.recipes.FocusingRecipeParams;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class BeamFocuserHelper {

    private static final RecipeWrapper WRAPPER = new RecipeWrapper(new ItemStackHandler(2));

    public static boolean canBeProcessed(Level world, ItemStack s1, ItemStack s2, BeamHelper.BeamType beamType) {
        return getFocusingRecipe(world, s1, s2, beamType).isPresent();
    }

    public static Optional<FocusingRecipe> getFocusingRecipe(Level world, ItemStack stack1, ItemStack stack2,
            @Nullable BeamHelper.BeamType beamType) {

        WRAPPER.setItem(0, stack1);
        WRAPPER.setItem(1, stack2);

        Optional<FocusingRecipe> focusingRecipe = SequencedAssemblyRecipe
                .getRecipe(world, WRAPPER, CORecipeTypes.FOCUSING.getType(), FocusingRecipe.class);
        if (focusingRecipe.isEmpty()) {
            focusingRecipe = world.getRecipeManager()
                    .getRecipeFor(CORecipeTypes.FOCUSING.getType(), WRAPPER, world);
        }
        if (focusingRecipe.isEmpty()) {
            focusingRecipe = FocusingRecipeParams.BeamTypeConditionProfile.getRecipeFor(world, WRAPPER, beamType);
        }
        if (focusingRecipe.isPresent()) {
            if (focusingRecipe.get().beamTypeCondition.test(beamType)) {
                return focusingRecipe;
            }
        }
        return Optional.empty();
    }

}

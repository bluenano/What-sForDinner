package com.seanschlaefli.whatsfordinner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;


public class RecipeDescriptionFragment extends Fragment {

    public static final String TAG = "RecipeDescriptionFrag";

    public static final String ARG_RECIPE_ID = "arg_recipe_id";

    private TextView mRecipeName;
    private TextView mIngredients;
    private ImageView mRecipeImage;
    private TextView mDirections;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recipe_description,
                                  container,
                                  false);
        mRecipeName = v.findViewById(R.id.recipe_name_text_view);
        mIngredients = v.findViewById(R.id.recipe_ingredients_text_view);
        mRecipeImage = v.findViewById(R.id.recipe_image_view);
        mDirections = v.findViewById(R.id.recipe_directions_text_view);

        Bundle args = getArguments();
        if (args != null) {
            UUID recipeId = (UUID) args.getSerializable(ARG_RECIPE_ID);
            Recipe recipe = RecipeBook.get(getActivity()).getRecipe(recipeId);
            Log.d(TAG, recipe.toString());
            setUI(recipe);
        }
        return v;


    }

    public static Fragment createFragment() {
        return new RecipeDescriptionFragment();
    }

    private void setUI(Recipe recipe) {
        mRecipeName.setText(recipe.getName());
        mDirections.setText(recipe.getDirections());
        setupIngredientsView(recipe.getIngredientList());
    }

    private void setupIngredientsView(List<Ingredient> ingredients) {
        StringBuilder builder = new StringBuilder();
        for (Ingredient ingredient: ingredients) {
            if (!ingredient.getName().equals("")) {
                builder.append("* ");
                builder.append(ingredient.getName());
                builder.append("\n");
            }
        }
        mIngredients.setText(builder.toString());
    }
}

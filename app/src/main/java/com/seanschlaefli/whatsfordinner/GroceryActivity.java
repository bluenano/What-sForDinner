package com.seanschlaefli.whatsfordinner;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroceryActivity extends AppCompatActivity {


    public String TAG = "GroceryActivity";

    private ListView mGroceryList;
    private ArrayAdapter<String> mAdapter;

    private Map<String, Integer> mIngredientToQuantity;
    private Map<String, String> mIngredientToUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getSupportActionBar();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ab.setLogo(R.drawable.ic_shopping_cart);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        setContentView(R.layout.activity_grocery);

        mGroceryList = (ListView) findViewById(R.id.grocery_list_view);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdapter();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, GroceryActivity.class);
    }

    private void setAdapter() {
        List<Meal> meals = Meals.get(this).getMeals();
        initializeMaps(meals);
        List<String> groceries = getGroceriesFromMaps();
        String[] groceryArr = groceries.toArray(new String[0]);
        mAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                groceryArr);
        mGroceryList.setAdapter(mAdapter);
    }


    private void addSwipeListener() {

    }


    private List<String> getGroceriesFromMaps() {
        List<String> groceryList = new ArrayList<>();
        for (String key: mIngredientToQuantity.keySet()) {
            int value = mIngredientToQuantity.get(key);
            groceryList.add(key + " (" + Integer.toString(value) + " " + mIngredientToUnits.get(key) + ")");
        }
        return groceryList;
    }

    private void initializeMaps(List<Meal> meals) {
        mIngredientToQuantity = new HashMap<>();
        mIngredientToUnits = new HashMap<>();
        RecipeBook recipes = RecipeBook.get(this);
        recipes.printDb();
        for (Meal meal: meals) {
            Recipe recipe = recipes.getRecipe(meal.getRecipeId());
            List<Ingredient> ingredients = recipes.getIngredientsFromRecipe(recipe);
            for (Ingredient ingredient: ingredients) {
                if (!ingredient.getName().equals("")) {
                    updateMaps(ingredient);
                }
            }
        }
    }

    // UPDATE THIS TO HANDLE NUMERATOR/DENOMINATOR
    private void updateMaps(Ingredient ingredient) {
        String name = ingredient.getName();
        if (mIngredientToQuantity.containsKey(name) &&
                mIngredientToUnits.containsKey(name)) {
            int quantity = mIngredientToQuantity.get(name);
            mIngredientToQuantity.put(name, quantity+ingredient.getNumerator());
        } else {
            mIngredientToQuantity.put(name, ingredient.getNumerator());
            mIngredientToUnits.put(name, ingredient.getUnits());
        }
    }
}

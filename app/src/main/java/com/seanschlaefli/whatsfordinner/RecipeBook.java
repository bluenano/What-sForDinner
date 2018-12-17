package com.seanschlaefli.whatsfordinner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.seanschlaefli.whatsfordinner.database.IngredientCursorWrapper;
import com.seanschlaefli.whatsfordinner.database.RecipeCursorWrapper;
import com.seanschlaefli.whatsfordinner.database.WhatsForDinnerDbHelper;
import com.seanschlaefli.whatsfordinner.database.WhatsForDinnerDbSchema.IngredientTable;
import com.seanschlaefli.whatsfordinner.database.WhatsForDinnerDbSchema.RecipeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecipeBook {

    public static final String TAG = "RecipeBook";

    private static RecipeBook sRecipeBook;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private RecipeBook(Context context) {
        mContext = context;
        mDatabase =  new WhatsForDinnerDbHelper(context).getWritableDatabase(); // change this
    }

    public static RecipeBook get(Context context) {
        if (sRecipeBook == null) {
            sRecipeBook = new RecipeBook(context);
        }
        return sRecipeBook;
    }

    public void addRecipe(Recipe recipe) {
        //Log.d(TAG, "inserting recipe:\n" + recipe.toString());
        ContentValues values = getContentValues(recipe);
        mDatabase.insert(RecipeTable.NAME, null, values);
        for (Ingredient ingredient: recipe.getIngredientList()) {
            addIngredient(recipe, ingredient);
        }
    }

    private void addIngredient(Recipe recipe, Ingredient ingredient) {
        ContentValues values = getContentValues(recipe, ingredient);
        mDatabase.insert(IngredientTable.NAME, null, values);
    }


    public void updateRecipe(Recipe recipe) {
        ContentValues values = getContentValues(recipe);
        String recipeId = recipe.getId().toString();
        mDatabase.update(
                RecipeTable.NAME,
                values,
                RecipeTable.Cols.UUID + " = ?",
                new String[] { recipeId }
        );
        for (Ingredient ingredient: recipe.getIngredientList()) {
            updateIngredient(recipe, ingredient);
        }
    }

    public void updateIngredient(Recipe recipe, Ingredient ingredient) {
        ContentValues values = getContentValues(recipe, ingredient);
        String whereClause = IngredientTable.Cols.RECIPE_ID + " = ? and " +
                IngredientTable.Cols.UUID + " = ?";
        mDatabase.update(IngredientTable.NAME,
                values,
                whereClause,
                new String[] { recipe.getId().toString(), ingredient.getId().toString() }
                );
    }

    public void deleteRecipe(Recipe recipe) {
        List<Ingredient> ingredients = recipe.getIngredientList();
        String whereClause = RecipeTable.Cols.UUID + " = ?";
        mDatabase.delete(RecipeTable.NAME, whereClause, new String[] { recipe.getId().toString() });
        for (Ingredient ingredient: ingredients) {
            deleteIngredient(ingredient);
        }
    }


    public void deleteIngredient(Ingredient ingredient) {
        String whereClause = IngredientTable.Cols.UUID + " = ?";
        mDatabase.delete(IngredientTable.NAME, whereClause, new String[] { ingredient.getId().toString() });
    }


    public Recipe getRecipe(UUID recipeId) {
        RecipeCursorWrapper cursor = queryRecipes(
                RecipeTable.Cols.UUID + " = ?",
                new String[] { recipeId.toString() }
        );
        try {
            if (cursor.getCount() == 0) {
                return new Recipe(recipeId, "", "", "");
            }
            cursor.moveToFirst();
            Recipe recipe = cursor.getRecipe();
            setIngredients(recipe);
            return recipe;
        } finally {
            cursor.close();
        }
    }

    public Recipe getRecipeByName(String name) {
        RecipeCursorWrapper cursor = queryRecipes(
                RecipeTable.Cols.NAME + " = ?",
                new String[] { name }
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            Recipe recipe = cursor.getRecipe();
            setIngredients(recipe);
            return recipe;
        } finally {
            cursor.close();
        }
    }

    public boolean isUniqueRecipe(String recipeId, String name) {
        RecipeCursorWrapper cursor = queryRecipes(
                RecipeTable.Cols.NAME + " = ? AND " +
                RecipeTable.Cols.UUID + " <> ?",
                new String[] { name, recipeId }
        );
        try {
            return cursor.getCount() == 0;
        } finally {
            cursor.close();
        }
    }

    public boolean isRecipeInDatabase(Recipe recipe) {
        String id = recipe.getId().toString();
        RecipeCursorWrapper cursor = queryRecipes(
                RecipeTable.Cols.UUID + " = ?",
                new String[] { id }
        );
        return cursor.getCount() != 0;
    }

    public boolean isIngredientInDatabase(Ingredient ingredient) {
        String id = ingredient.getId().toString();
        IngredientCursorWrapper cursor = queryIngredients(
                IngredientTable.Cols.UUID + " = ?",
                new String[] { id }
        );
        return cursor.getCount() != 0;
    }

    public List<Recipe> getRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        RecipeCursorWrapper cursor = queryRecipes(null, null);
        try {
            if (cursor.getCount() == 0) {
                return recipes;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Recipe recipe = cursor.getRecipe();
                recipes.add(recipe);
                setIngredients(recipe);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return recipes;
    }


    private void setIngredients(Recipe recipe) {
        List<Ingredient> ingredients = getIngredientsFromRecipe(recipe);
        recipe.setIngredientList(ingredients);
    }


    public List<Ingredient> getIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        IngredientCursorWrapper cursor = queryIngredients(null, null);
        try {
            if (cursor.getCount() == 0) {
                return ingredients;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ingredients.add(cursor.getIngredient());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return ingredients;
    }


    public List<Ingredient> getIngredientsFromRecipe(Recipe recipe) {
        List<Ingredient> ingredients = new ArrayList<>();
        String recipeId = recipe.getId().toString();
        IngredientCursorWrapper cursor = queryIngredients(
                IngredientTable.Cols.RECIPE_ID + " = ?",
                new String[] { recipeId }
        );
        Log.d(TAG, "recipe id: " + recipeId);
        try {
            if (cursor.getCount() == 0) {
                return ingredients;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Ingredient i = cursor.getIngredient();
                ingredients.add(i);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return ingredients;
    }



    private RecipeCursorWrapper queryRecipes(String where, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                RecipeTable.NAME,
                null,
                where,
                whereArgs,
                null,
                null,
                null,
                null
        );
        return new RecipeCursorWrapper(cursor);
    }


    private IngredientCursorWrapper queryIngredients(String where, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                IngredientTable.NAME,
                null,
                where,
                whereArgs,
                null,
                null,
                null,
                null
        );
        return new IngredientCursorWrapper(cursor);
    }


    private static ContentValues getContentValues(Recipe recipe) {
        ContentValues values = new ContentValues();
        values.put(RecipeTable.Cols.UUID, recipe.getId().toString());
        values.put(RecipeTable.Cols.NAME, recipe.getName());
        values.put(RecipeTable.Cols.DIRECTIONS, recipe.getDirections());
        return values;
    }

    private static ContentValues getContentValues(Recipe recipe, Ingredient ingredient) {
        ContentValues values = new ContentValues();
        values.put(IngredientTable.Cols.UUID, ingredient.getId().toString());
        values.put(IngredientTable.Cols.NAME, ingredient.getName());
        values.put(IngredientTable.Cols.NUMERATOR, ingredient.getNumerator());
        values.put(IngredientTable.Cols.DENOMINATOR, ingredient.getDenominator());
        values.put(IngredientTable.Cols.UNITS, ingredient.getUnits());
        values.put(IngredientTable.Cols.RECIPE_ID, recipe.getId().toString());
        return values;
    }

    public void printDb() {
        RecipeBook recipes = RecipeBook.get(null);
        List<Recipe> current = recipes.getRecipes();
        for (Recipe recipe: current) {
            Log.d(TAG, "check db recipe in db: " + recipe.toString());
        }
    }

}

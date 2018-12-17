package com.seanschlaefli.whatsfordinner.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.seanschlaefli.whatsfordinner.Recipe;
import com.seanschlaefli.whatsfordinner.database.WhatsForDinnerDbSchema.RecipeTable;

import java.util.UUID;

public class RecipeCursorWrapper extends CursorWrapper {


    public RecipeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Recipe getRecipe() {
        UUID id = UUID.fromString(getString(getColumnIndex(RecipeTable.Cols.UUID)));
        String name = getString(getColumnIndex(RecipeTable.Cols.NAME));
        String directions = getString(getColumnIndex(RecipeTable.Cols.DIRECTIONS));
        String imageFile = getString(getColumnIndex(RecipeTable.Cols.IMAGE));
        return new Recipe(id, name, directions, imageFile);
    }

    public String getRecipeId() {
        int id = getInt(getColumnIndex(RecipeTable.Cols.ID));
        return Integer.toString(id);
    }


}

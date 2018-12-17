package com.seanschlaefli.whatsfordinner.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.seanschlaefli.whatsfordinner.Meal;
import com.seanschlaefli.whatsfordinner.database.WhatsForDinnerDbSchema.MealTable;

import java.util.UUID;

public class MealCursorWrapper extends CursorWrapper {

    public MealCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Meal getMeal() {
        UUID id = UUID.fromString(getString(getColumnIndex(MealTable.Cols.UUID)));
        UUID recipeId = UUID.fromString(getString(getColumnIndex(MealTable.Cols.RECIPE_ID)));
        int day = getInt(getColumnIndex(MealTable.Cols.DAY));
        int time = getInt(getColumnIndex(MealTable.Cols.TIME));
        return new Meal(id, recipeId, day, time);
    }
}

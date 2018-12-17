package com.seanschlaefli.whatsfordinner.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.seanschlaefli.whatsfordinner.Ingredient;
import com.seanschlaefli.whatsfordinner.database.WhatsForDinnerDbSchema.IngredientTable;

import java.util.UUID;

public class IngredientCursorWrapper extends CursorWrapper {


    public IngredientCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Ingredient getIngredient() {
        UUID id = UUID.fromString(getString(getColumnIndex(IngredientTable.Cols.UUID)));
        String name = getString(getColumnIndex(IngredientTable.Cols.NAME));
        String units = getString(getColumnIndex(IngredientTable.Cols.UNITS));
        int numerator = getInt(getColumnIndex(IngredientTable.Cols.NUMERATOR));
        int denominator = getInt(getColumnIndex(IngredientTable.Cols.DENOMINATOR));
        return new Ingredient(id, name, units, numerator, denominator);
    }
}

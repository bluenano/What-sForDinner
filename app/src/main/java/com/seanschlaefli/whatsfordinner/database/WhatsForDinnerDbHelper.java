package com.seanschlaefli.whatsfordinner.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.seanschlaefli.whatsfordinner.database.WhatsForDinnerDbSchema.RecipeTable;
import com.seanschlaefli.whatsfordinner.database.WhatsForDinnerDbSchema.IngredientTable;
import com.seanschlaefli.whatsfordinner.database.WhatsForDinnerDbSchema.MealTable;

public class WhatsForDinnerDbHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "recipeBase.db";

    public WhatsForDinnerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create the dbs
        db.execSQL("create table " + RecipeTable.NAME + "(" +
                   RecipeTable.Cols.ID + " integer primary key autoincrement, " +
                   RecipeTable.Cols.UUID + ", " +
                   RecipeTable.Cols.NAME + ", " +
                   RecipeTable.Cols.DIRECTIONS + " VARCHAR(250), " +
                   RecipeTable.Cols.IMAGE + ")"
        );

        db.execSQL("create table " + IngredientTable.NAME + "(" +
                   IngredientTable.Cols.ID + " integer primary key autoincrement, " +
                   IngredientTable.Cols.UUID + ", " +
                   IngredientTable.Cols.NAME + ", " +
                   IngredientTable.Cols.NUMERATOR + ", " +
                   IngredientTable.Cols.DENOMINATOR + "," +
                   IngredientTable.Cols.UNITS + ", " +
                   IngredientTable.Cols.RECIPE_ID + ")"
         );

        db.execSQL("create table " + MealTable.NAME + "(" +
                MealTable.Cols.ID + " integer primary key autoincrement," +
                MealTable.Cols.UUID + ", " +
                MealTable.Cols.DAY + ", " +
                MealTable.Cols.TIME + ", " +
                MealTable.Cols.RECIPE_ID + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

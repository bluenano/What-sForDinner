package com.seanschlaefli.whatsfordinner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.seanschlaefli.whatsfordinner.database.MealCursorWrapper;
import com.seanschlaefli.whatsfordinner.database.WhatsForDinnerDbHelper;
import com.seanschlaefli.whatsfordinner.database.WhatsForDinnerDbSchema.MealTable;

import java.util.ArrayList;
import java.util.List;

public class Meals {

    public static String TAG = "Meals";

    private static Meals sMeals;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private Meals(Context context) {
        mContext = context;
        mDatabase = new WhatsForDinnerDbHelper(context).getWritableDatabase();
    }

    public static Meals get(Context context) {
        if (sMeals == null) {
            sMeals = new Meals(context);
        }
        return sMeals;
    }

    public void addMeal(Meal meal) {
        ContentValues values = getContentValues(meal);
        mDatabase.insert(MealTable.NAME, null, values);
    }

    public void updateMeal(Meal meal) {
        ContentValues values = getContentValues(meal);
        String recipeId = meal.getRecipeId().toString();
        String whereClause = MealTable.Cols.UUID + " = ? and " +
                MealTable.Cols.RECIPE_ID + " = ?";
        mDatabase.update(MealTable.NAME,
                values,
                whereClause,
                new String[] { meal.getId().toString(), recipeId }
                );
    }

    public void deleteMeal(Meal meal) {
        String whereClause = MealTable.Cols.UUID + " = ?";
        mDatabase.delete(MealTable.NAME, whereClause, new String[] { meal.getId().toString() });
    }


    public void deleteMeals() {
        mDatabase.delete(MealTable.NAME, null, null);
    }


    public List<Meal> getMeals() {
        List<Meal> meals = new ArrayList<>();
        MealCursorWrapper cursor = queryMeals(null, null);
        try {
            if (cursor.getCount() == 0) {
                return meals;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Meal meal = cursor.getMeal();
                meals.add(meal);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return meals;
    }


    /*
    public Meal getMeal(int day, int time) {
        MealCursorWrapper cursor = queryMeals(
                MealTable.Cols.DAY + " = ? AND " +
                        MealTable.Cols.TIME + " = ?",
                new String[] { Integer.toString(day), Integer.toString(time) }
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
        } finally {
            cursor.close();
        }

    }
    */

    public String getRecipeName(Meal meal) {
        Recipe recipe = RecipeBook.get(mContext).getRecipe(meal.getRecipeId());
        return recipe.getName();
    }


    private MealCursorWrapper queryMeals(String where, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                MealTable.NAME,
                null,
                where,
                whereArgs,
                null,
                null,
                null,
                null
                );
        return new MealCursorWrapper(cursor);
    }


    private ContentValues getContentValues(Meal meal) {
        ContentValues values = new ContentValues();
        values.put(MealTable.Cols.UUID, meal.getId().toString());
        values.put(MealTable.Cols.RECIPE_ID, meal.getRecipeId().toString());
        values.put(MealTable.Cols.DAY, meal.getDay());
        values.put(MealTable.Cols.TIME, meal.getTime());
        return values;
    }

    public void printMeals() {
        List<Meal> meals = getMeals();
        for (Meal meal: meals) {
            Log.d(TAG, meal.toString());
        }
    }


}

package com.seanschlaefli.whatsfordinner;

import java.util.UUID;

public class Meal {

    private UUID mId;
    private UUID mRecipeId;
    private int mDay;
    private int mTime;

    public Meal() {
        mId = UUID.randomUUID();
        mRecipeId = null;
        mDay = -1;
        mTime = -1;
    }

    public Meal(UUID id, UUID recipeId, int day, int time) {
        mId = id;
        mRecipeId = recipeId;
        mDay = day;
        mTime = time;
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public UUID getRecipeId() {
        return mRecipeId;
    }

    public void setRecipeId(UUID recipeId) {
        mRecipeId = recipeId;
    }

    public int getDay() {
        return mDay;
    }

    public void setDay(int day) {
        mDay = day;
    }

    public int getTime() {
        return mTime;
    }

    public void setTime(int time) {
        mTime = time;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "mId=" + mId +
                ", mRecipeId=" + mRecipeId +
                ", mDay=" + mDay +
                ", mTime=" + mTime +
                '}';
    }

    public static Meal createFromRecipe(Recipe recipe) {
        Meal meal = new Meal();
        meal.setRecipeId(recipe.getId());
        return meal;
    }
}


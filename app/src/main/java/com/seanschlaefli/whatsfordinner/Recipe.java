package com.seanschlaefli.whatsfordinner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Recipe {

    private static final int MAX_INGREDIENTS = 10;

    private UUID mId;
    private String mName;
    private String mDirections;
    private String mImageFile;
    private List<Ingredient> mIngredientList;


    public Recipe() {
        mId = UUID.randomUUID();
        mName = "";
        mDirections = "";
        mImageFile = "";
        mIngredientList = new ArrayList<>();
        for (int i = 0; i < MAX_INGREDIENTS; i++) {
            mIngredientList.add(new Ingredient());
        }
    }

    public Recipe(UUID id, String name, String directions, String imageFile) {
        mId = id;
        mName = name;
        mDirections = directions;
        mImageFile = imageFile;
        mIngredientList = new ArrayList<>();
        for (int i = 0; i < MAX_INGREDIENTS; i++) {
            mIngredientList.add(new Ingredient());
        }
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDirections() {
        return mDirections;
    }

    public void setDirections(String directions) {
        mDirections = directions;
    }

    public String getImageFile() {
        return mImageFile;
    }

    public void setImageFile(String imageFile) {
        mImageFile = imageFile;
    }

    public List<Ingredient> getIngredientList() {
        return mIngredientList;
    }

    public void setIngredientList(List<Ingredient> ingredientList) {
        mIngredientList = ingredientList;
    }

    @Override
    public String toString() {
        String s = "Recipe{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mDirections='" + mDirections + '\'' + '\n' +
                ", mIngredientList=\n";
        for (Ingredient i : mIngredientList) {
            s += i.toString() + '\n';
        }
        return s;

    }
}

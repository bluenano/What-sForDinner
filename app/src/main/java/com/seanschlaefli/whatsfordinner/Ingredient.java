package com.seanschlaefli.whatsfordinner;

import java.util.UUID;

public class Ingredient {

    private UUID mId;
    private String mName;
    private String mUnits;
    private int mNumerator;
    private int mDenominator;

    public Ingredient() {
        mId = UUID.randomUUID();
        mName = "";
        mUnits = "";
        mNumerator = 0;
        mDenominator = 1;
    }

    public Ingredient(UUID id, String name, String units, int numerator, int denominator) {
        mId = id;
        mName = name;
        mUnits = units;
        mNumerator = numerator;
        mDenominator = denominator;
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

    public String getUnits() {
        return mUnits;
    }

    public void setUnits(String units) {
        mUnits = units;
    }

    public int getNumerator() {
        return mNumerator;
    }

    public void setNumerator(int numerator) {
        mNumerator = numerator;
    }

    public int getDenominator() {
        return mDenominator;
    }

    public void setDenominator(int denominator) {
        mDenominator = denominator;
    }

    @Override
    public String toString() {
        String quantity = "";
        if (mDenominator == 1) {
            quantity = Integer.toString(mNumerator);
        } else {
            quantity = Integer.toString(mNumerator) + "/" + Integer.toString(mDenominator);
        }
        return quantity + " " + mUnits + " " + mName;
    }
}

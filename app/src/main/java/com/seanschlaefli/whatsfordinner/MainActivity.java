package com.seanschlaefli.whatsfordinner;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

    public void doAppInfo(View view) {
        FragmentManager fm = getSupportFragmentManager();
        AppInfoDialogFragment fragment = AppInfoDialogFragment.newInstance();
        fragment.show(fm, TAG);

    }


    public void doMeals(View view) {
        Intent intent = MealActivity.newIntent(this);
        startActivity(intent);

    }

    public void doRecipes(View view) {
        Intent intent = RecipeActivity.newIntent(this);
        startActivity(intent);
    }

    public void doGroceries(View view) {
        Intent intent = GroceryActivity.newIntent(this);
        startActivity(intent);

    }

    public void doDishes(View view) {
        Intent intent = DishActivity.newIntent(this, UUID.randomUUID());
        startActivity(intent);
    }
}

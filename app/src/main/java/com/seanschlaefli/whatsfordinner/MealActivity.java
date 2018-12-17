package com.seanschlaefli.whatsfordinner;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealActivity extends AppCompatActivity {

    public static final String TAG = "MealActivity";
    private static final String DEFAULT = "Eating out";

    private List<List<TextView>> mMealTextViews = new ArrayList<>();
    private List<List<Spinner>> mealOptions = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;

    private Map<String, Integer> mAvailableMealCount = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getSupportActionBar();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ab.setLogo(R.drawable.ic_dates);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        Log.d(TAG, "first print meal");
        Meals.get(this).printMeals();
        setContentView(R.layout.activity_meal);

        setMealCount();

        mAdapter =  new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        TableLayout table = (TableLayout) findViewById(R.id.table_layout_id);
        createViewLists(table);
        setSpinners();
        setMeals();

        addMealOptionListeners();
    }


    public static Intent newIntent(Context context) {
        return new Intent(context, MealActivity.class);
    }

    public void doSave(View v) {
        saveMeals();
        showToast(getResources().getString(R.string.save_meals));
    }

    public void doClear(View v) {
        Meals.get(this).deleteMeals();
        resetUI();
        resetModel();
        showToast(getResources().getString(R.string.clear_meals));
    }

    private void setMealCount() {
        List<Meal> availableMeals = Meals.get(this).getMeals();
        RecipeBook recipes = RecipeBook.get(this);
        for (Meal meal: availableMeals) {
            Recipe recipe = recipes.getRecipe(meal.getRecipeId());
            String name = recipe.getName();
            int value = 0;
            if (meal.getDay() == -1 && meal.getTime() == -1) {
                value = 1;
                if (mAvailableMealCount.containsKey(name)) {
                    int old = mAvailableMealCount.get(name);
                    mAvailableMealCount.remove(name);
                    value += old;
                }
            }
            mAvailableMealCount.put(name, value);
        }
        Log.d(TAG, "set meal count print");
        printMap(mAvailableMealCount);
    }

    private void setSpinners() {
        setAdapter();
        for (List<Spinner> spinners: mealOptions) {
            for (Spinner spinner: spinners) {
                spinner.setAdapter(mAdapter);
            }
        }
    }

    private void setAdapter() {
        mAdapter.clear();
        mAdapter.add(getResources().getString(R.string.eating_out));
        for (String key: mAvailableMealCount.keySet()) {
            int count = mAvailableMealCount.get(key);
            String s = key + " (" + Integer.toString(count) + ")";
            mAdapter.add(s);
        }
    }

    private void saveMeals() {
        Meals meals = Meals.get(this);
        meals.deleteMeals();
        Log.d(TAG, "after clearing meals table");
        meals.printMeals();
        insertAssignedMeals();
        padMealsTable();
    }


    private void setMeals() {
        Log.d(TAG, "set meals");
        Meals.get(this).printMeals();
        List<Meal> availableMeals = Meals.get(this).getMeals();
        for (Meal meal: availableMeals) {
            int day = meal.getDay();
            int time = meal.getTime();
            setMeal(meal, day, time);
        }
    }


    private void setMeal(Meal meal, int day, int time) {
        if (day != -1 && time != -1) {
            String name = Meals.get(this).getRecipeName(meal);
            TextView target = getTextView(day, time);
            Log.d(TAG, "setting target text view: " + name);
            target.setText(name);
            updateSpinner(name, day, time);
        }
    }


    private void updateMealCount(Meal meal, int add) {
        Recipe recipe = RecipeBook.get(this).getRecipe(meal.getRecipeId());
        String name = recipe.getName();
        int value = 0;
        if (mAvailableMealCount.containsKey(name)) {
            value = mAvailableMealCount.get(name);
            mAvailableMealCount.remove(name);
        }
        mAvailableMealCount.put(name, value+add);
    }


    private void resetUI() {
        resetTextViews();
        mAdapter.clear();
        mAdapter.add(DEFAULT);
    }

    private void resetModel() {
        mAvailableMealCount.clear();
    }

    private void resetTextViews() {
        for (List<TextView> meals: mMealTextViews) {
            for (TextView current: meals) {
                current.setText(DEFAULT);
            }
        }
    }

    public void createViewLists(TableLayout table) {
        // start at 1 because first row is buttons
        for (int i = 1; i < table.getChildCount(); i++) {
            View row = table.getChildAt(i);
            if (row instanceof TableRow) {
                updateViewLists((TableRow) row);
            }
        }
    }


    private void updateViewLists(TableRow row) {
        List<TextView> times = new ArrayList<>();
        List<Spinner> options = new ArrayList<>();
        for (int i = 0; i < row.getChildCount(); i++) {
            View v = row.getChildAt(i);
            if (v instanceof LinearLayout) {
                LinearLayout current = (LinearLayout) v;
                for (int j = 0; j < current.getChildCount(); j++) {
                    v = current.getChildAt(j);
                    if (v instanceof LinearLayout) {
                        LinearLayout group = (LinearLayout) v;
                        for (int k = 0; k < group.getChildCount(); k++) {
                            View target = group.getChildAt(k);
                            addView(target, times, options);
                        }
                    }
                }
            }
        }
        mMealTextViews.add(times);
        mealOptions.add(options);
    }



    private Map<String, Integer> getAssignedMealCount() {
        Map<String, Integer> mealCount = new HashMap<>();
        for (int i = 0; i < mMealTextViews.size(); i++) {
            List<TextView> currentDay = mMealTextViews.get(i);
            for (int j = 0; j < currentDay.size(); j++) {
                String mealName = currentDay.get(j).getText().toString();
                if (!mealName.equals(DEFAULT)) {
                    int value = 1;
                    if (mealCount.containsKey(mealName)) {
                        value = mealCount.get(mealName) + 1;
                        mealCount.remove(mealName);
                    }
                    mealCount.put(mealName, value);
                }
            }
        }
        Log.d(TAG, "getAssignedMealCount");
        printMap(mealCount);
        return mealCount;
    }


    private Map<String, Integer> getUnassignedMealCount() {
        Log.d(TAG, "printing unassigned meal count");
        printMap(mAvailableMealCount);
        return new HashMap<>();
    }


    private void padMealsTable() {
        Meals meals = Meals.get(this);
        Log.d(TAG, "before meal pad");
        meals.printMeals();
        RecipeBook recipes = RecipeBook.get(this);
        for (String key: mAvailableMealCount.keySet()) {
            int unassignedCount = mAvailableMealCount.get(key);
            for (int i = 0; i < unassignedCount; i++) {
                Recipe recipe = recipes.getRecipeByName(key);
                if (recipe != null) {
                    Meal meal = Meal.createFromRecipe(recipe);
                    meals.addMeal(meal);
                }
            }
        }
        Log.d(TAG, "after meal pad");
        meals.printMeals();
    }


    private void insertAssignedMeals() {
        Meals meals = Meals.get(this);
        RecipeBook recipes = RecipeBook.get(this);
        for (int i = 0; i < mMealTextViews.size(); i++) {
            List<TextView> currentDay = mMealTextViews.get(i);
            for (int j = 0; j < currentDay.size(); j++) {
                String mealName = currentDay.get(j).getText().toString();
                Recipe recipe = recipes.getRecipeByName(mealName);
                if (recipe != null) {
                    Meal meal = Meal.createFromRecipe(recipe);
                    meal.setDay(i);
                    meal.setTime(j);
                    meals.addMeal(meal);
                }
            }
        }
    }


    private void addView(View target, List<TextView> times, List<Spinner> options) {
        if (target instanceof TextView) {
            times.add((TextView) target);
        } else if (target instanceof Spinner) {
            options.add((Spinner) target);
        }
    }


    private TextView getTextView(int day, int time) {
        return mMealTextViews.get(day).get(time);
    }

    public void addMealOptionListeners() {
        for (int i = 0; i < mealOptions.size(); i++) {
            final int day = i;
            List<Spinner> spinners = mealOptions.get(i);
            for (int j = 0; j < spinners.size(); j++) {
                final int time = j;
                final Spinner spinner = spinners.get(j);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selection = (String) parent.getItemAtPosition(position);
                        TextView mealTextView = getTextView(day, time);
                        String previousMeal = mealTextView.getText().toString();
                        if (selection.equals(DEFAULT)) {
                            if (!previousMeal.equals(DEFAULT)) {
                                Log.d(TAG, "meal is " + previousMeal);
                                mealTextView.setText(DEFAULT);
                                printMap(mAvailableMealCount);
                                updateMealModel(previousMeal, 1);
                                setAdapter();
                            }
                        } else {
                            String mealName = extractName(selection);
                            TextView update = getTextView(day, time);
                            Log.d(TAG, "meal name is " + mealName);
                            int old = mAvailableMealCount.get(mealName);
                            if (old > 0) {
                                updateMealModel(mealName, -1);
                                update.setText(mealName);
                                updateMealModel(previousMeal, 1);
                                setAdapter();
                            } else if (old == 0) {
                                int defaultPos = mAdapter.getPosition(DEFAULT);
                                showToast(getResources().getString(R.string.select_error));
                                spinner.setSelection(defaultPos);
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }


    }


    private void updateSpinner(String name, int day, int time) {
        int position = findPosition(name);
        if (position != -1) {
            mealOptions.get(day).get(time).setSelection(position);
        }
    }


    private void updateMealModel(String mealName, int update) {
        if (!mealName.equals(DEFAULT)) {
            int old = mAvailableMealCount.get(mealName);
            mAvailableMealCount.remove(mealName);
            mAvailableMealCount.put(mealName, old+update);
        }
    }


    private String extractName(String spinnerItem) {
        String[] items = spinnerItem.split(" ");
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < items.length-1; i++) {
            name.append(items[i]);
            if (i != items.length-2) {
                name.append(" ");
            }
        }
        return name.toString();
    }


    private int findPosition(String name) {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            String item = extractName(mAdapter.getItem(i));
            if (name.equals(item)) {
                return i;
            }
        }
        return -1;
    }


    private String makeSpinnerItem(String meal) {
        if (mAvailableMealCount.containsKey(meal)) {
            int count = mAvailableMealCount.get(meal);
            return meal + " (" + Integer.toString(count) + ")";
        }
        return DEFAULT;
    }


    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) {
            v.setGravity(Gravity.CENTER);
        }
        toast.show();
    }

    private void printMap(Map<String, Integer> map) {
        Log.d(TAG, "printing map");
        for (String key: map.keySet()) {
            int value = map.get(key);
            Log.d(TAG, key + " count: " + Integer.toString(value));
        }
    }
}

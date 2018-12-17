package com.seanschlaefli.whatsfordinner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecipeListFragment extends Fragment {

    public static final String TAG = "RecipeListFragment";
    public static final String ARG_ORIENTATION = "arg_orientation";

    private OnRecipeSelectedListener mCallback;

    public interface OnRecipeSelectedListener {
        void onRecipeSelected(UUID id);
    }

    private ListView mRecipeList;
    private ArrayAdapter<String> mAdapter;

    private boolean mIsLandscape = false;
    private List<Recipe> mRecipes = new ArrayList<Recipe>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recipe_list,
                                  container,
                                  false);
        mRecipeList = v.findViewById(R.id.recipe_list_view);
        Bundle args = getArguments();

        if (args != null) {
            mIsLandscape = true;
        } else {
            mIsLandscape = false;
        }

        addClickListener();
        addLongClickListener();
        return v;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (OnRecipeSelectedListener) context;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "printing the meals");
        Meals meals = Meals.get(getContext());
        meals.printMeals();

    }

    private void setAdapter() {
        mRecipes = RecipeBook.get(getContext()).getRecipes();
        ArrayList<String> names = new ArrayList<>();
        for (Recipe recipe: mRecipes) {
            names.add(recipe.getName());
        }
        for (String s: names) {
            Log.d(TAG, s);
        }
        String[] nameArr = names.toArray(new String[0]);
        mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,
               nameArr);
        mRecipeList.setAdapter(mAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        setAdapter();
    }


    private void addClickListener() {
        mRecipeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Recipe recipe = mRecipes.get(position);
                if (mIsLandscape) {
                    mCallback.onRecipeSelected(recipe.getId());
                }
                Meal meal = Meal.createFromRecipe(recipe);
                Meals.get(getContext()).addMeal(meal);
            }
        });
    }


    private void addLongClickListener() {
        mRecipeList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Recipe clicked = mRecipes.get(position);
                // start DishActivity
                Intent intent = DishActivity.newIntent(getContext(), clicked.getId());
                startActivity(intent);
                Log.d(TAG, "long clicked: " + clicked.toString());
                return false;
            }
        });
    }


    public static Fragment createFragment() {
        return new RecipeListFragment();
    }


}

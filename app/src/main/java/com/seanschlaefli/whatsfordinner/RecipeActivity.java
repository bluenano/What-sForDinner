package com.seanschlaefli.whatsfordinner;

import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import java.util.UUID;

public class RecipeActivity extends AppCompatActivity
    implements RecipeListFragment.OnRecipeSelectedListener {

    public static String TAG = "RecipeActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();
        ActionBar ab = getSupportActionBar();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            removeFragments(fm);
            Log.d(TAG, "in landscape mode");
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            Bundle args = new Bundle();
            args.putString(RecipeListFragment.ARG_ORIENTATION, "landscape");
            addFragment(fm,
                        RecipeListFragment.TAG,
                        R.id.recipe_list_container_id,
                        args);


        } else {
            removeFragments(fm);
            Log.d(TAG, "in portrait mode");
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ab.setLogo(R.drawable.ic_recipes);
            ab.setDisplayUseLogoEnabled(true);
            ab.setDisplayShowHomeEnabled(true);

            addFragment(fm,
                        RecipeListFragment.TAG,
                        R.id.recipe_list_container_id,
                       null);
        }
        setContentView(R.layout.activity_recipe);


    }


    public void onRecipeSelected(UUID id) {
        Log.d(TAG, "received message from fragment");
        FragmentManager fm = getSupportFragmentManager();
        removeFragment(fm, RecipeDescriptionFragment.TAG);
        Bundle args = new Bundle();
        args.putSerializable(RecipeDescriptionFragment.ARG_RECIPE_ID, id);
        addFragment(fm,
                RecipeDescriptionFragment.TAG,
                R.id.recipe_description_container_id,
                args);
    }


    private void removeFragments(FragmentManager fm) {
        removeFragment(fm, RecipeListFragment.TAG);
        removeFragment(fm, RecipeDescriptionFragment.TAG);
    }



    private void addFragment(FragmentManager fm, String tag, int fragmentId, Bundle args) {
        Fragment fragment = getFragmentFromTag(tag);
        fragment.setArguments(args);
        fm.beginTransaction()
                .add(fragmentId, fragment, tag)
                .commit();

    }

    private void removeFragment(FragmentManager fm, String tag) {
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment != null) {
            fm.beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }


    private Fragment getFragmentFromTag(String tag) {
        if (tag.equals(RecipeDescriptionFragment.TAG)) {
            return RecipeDescriptionFragment.createFragment();
        } else if (tag.equals(RecipeListFragment.TAG)) {
            return RecipeListFragment.createFragment();
        }
        return null;
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, RecipeActivity.class);
    }
}

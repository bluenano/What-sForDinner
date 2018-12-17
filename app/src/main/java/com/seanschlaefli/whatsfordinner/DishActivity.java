package com.seanschlaefli.whatsfordinner;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DishActivity extends AppCompatActivity
implements ImageDialogFragment.OnURLSelectedListener {

    public static final String TAG = "DishActivity";

    public static final String ARG_RECIPE_ID = "com.seanschlaefli.whatsfordinner" +
            "recipe_id";
    private static final String DEFAULT = "default";
    private static final int REQUEST_URL = 0;

    private EditText mRecipeName;
    private ImageView mRecipeImage;
    private ImageButton mAddButton;
    private EditText mDirections;

    private List<EditText> mIngredientList = new ArrayList<>();
    private List<Spinner> mSpinnerList = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;


    private Recipe mRecipe;


    private static final int ROWS = 10;
    private static final int EDIT_TEXT_INDEX = 0;
    private static final int SPINNER_INDEX = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish);

        mAdapter =  new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mRecipeName = (EditText) findViewById(R.id.recipe_name_edit_text);
        mRecipeImage = (ImageView) findViewById(R.id.recipe_image_view);
        mAddButton = (ImageButton) findViewById(R.id.add_image_button);
        mDirections = (EditText) findViewById(R.id.directions_edit_text);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            UUID id = (UUID) bundle.getSerializable(ARG_RECIPE_ID);
            Log.d(TAG, id.toString());
            initializeModel(id);
        }

        printModel();
        mRecipeName.setText(mRecipe.getName());
        mDirections.setText(mRecipe.getDirections());

        addRecipeNameListener(mRecipeName);
        setupIngredientView((TableLayout) findViewById(R.id.table_layout_id));

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    public void onURLSelected(String URL) {
        Log.d(TAG, "got url: " + URL);
        ImageDownloader downloader = new ImageDownloader(URL);
        Thread thread = new Thread(downloader);
        thread.start();
    }

    public void onLocalFileSelected(String path) {
        // write the code to load the file
    }


    public static Intent newIntent(Context context, UUID recipeId) {
        Intent intent = new Intent(context, DishActivity.class);
        intent.putExtra(ARG_RECIPE_ID, recipeId);
        return intent;
    }

    public void doAddImage(View view) {
        // start the ImageDialogFragment
        // on return, start the asynctask
        FragmentManager fm = getSupportFragmentManager();
        ImageDialogFragment dialog = ImageDialogFragment.newInstance();
        dialog.show(fm, TAG);
    }

    public void doSubmit(View view) {
        String name = mRecipeName.getText().toString();
        // add condition to check all ingredients
        if (isUniqueRecipe(mRecipe.getId(), name) &&
                name.length() != 0 &&
                checkIngredientFormat()) {
            sendRecipeToDatabase();
            finish();
        } else {
            showAlert(getResources().getString(R.string.duplicate_recipe));
        }
    }

    private void setSpinners() {
        mAdapter.add(DEFAULT);
        for (Ingredient item: mRecipe.getIngredientList()) {
            String name = item.getName();
            if (!name.equals("")) {
                updateAdapter(name);
            }
        }
        for (Spinner spinner: mSpinnerList) {
            spinner.setAdapter(mAdapter);
        }
    }


    private void addRecipeNameListener(EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    String name = mRecipeName.getText().toString();
                    if (!isUniqueRecipe(mRecipe.getId(), name)) {
                        showAlert(getResources().getString(R.string.duplicate_recipe));
                    } else {
                        mRecipe.setName(name);
                    }
                }

            }
        });
    }

    private void setupIngredientView(TableLayout table) {
        List<Ingredient> ingredients = mRecipe.getIngredientList();
        for (int i = 0; i < ROWS; i++) {
            TableRow row = (TableRow) table.getChildAt(i);
            EditText editText = (EditText) row.getChildAt(EDIT_TEXT_INDEX);
            Ingredient current = ingredients.get(i);
            if (!current.getName().equals("")) {
                editText.setText(current.toString());
            }
            Spinner spinner = (Spinner) row.getChildAt(SPINNER_INDEX);
            mIngredientList.add(editText);
            mSpinnerList.add(spinner);
            addIngredientListener(editText, i);
            addSpinnerListener(spinner, i);
        }
        setSpinners();
    }

    private void addIngredientListener(EditText editText, final int modelPos) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String s = ((EditText) v).getText().toString();
                    if (validateIngredientString(s)) {
                        //updateIngredientList(s, modelPos);
                        updateAdapter(getName(s));
                    } else {
                        showAlert(getResources().getString(R.string.ingredient_format_msg));
                    }
                }
            }
        });
    }

    private void addSpinnerListener(Spinner spinner, final int modelPos) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String ingredient = (String) parent.getItemAtPosition(position);
                    setEditText(ingredient, modelPos);
                    //updateIngredientList(ingredient, modelPos);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    private void addDirectionsListener(EditText editText) {

    }

    private void setEditText(String value, int pos) {
        EditText update = mIngredientList.get(pos);
        update.setText(value);
    }

    private void updateIngredientList(String name, int pos) {
        List<Ingredient> ingredients = mRecipe.getIngredientList();
        ingredients.get(pos).setName(name);
    }


    private boolean isUniqueRecipe(UUID id, String name) {
        RecipeBook recipes = RecipeBook.get(this);
        return recipes.isUniqueRecipe(id.toString(), name);
    }


    private void sendRecipeToDatabase() {
        updateModel();
        printModel();
        RecipeBook recipes = RecipeBook.get(this);
        if (!mRecipe.getName().equals("")) {
            if (!recipes.isRecipeInDatabase(mRecipe)) {
                recipes.addRecipe(mRecipe);
            } else {
                recipes.updateRecipe(mRecipe);
            }
        }
        checkDb();
    }


    private void initializeModel(UUID id) {
        RecipeBook recipes = RecipeBook.get(this);
        mRecipe = recipes.getRecipe(id);
    }


    private void updateModel() {
        mRecipe.setName(mRecipeName.getText().toString());
        mRecipe.setDirections(mDirections.getText().toString());
        List<Ingredient> ingredients = mRecipe.getIngredientList();
        for (int i = 0; i < ROWS; i++) {
            EditText current = mIngredientList.get(i);
            // TODO
            // parse edit text input and set ingredient variable appropriately
            // format: quantity units name
            if (!current.getText().toString().equals("")) {
                updateIngredient(ingredients.get(i), current);
            }
        }
        Log.d(TAG, "updated model: ");
        Log.d(TAG, mRecipe.toString());
    }


    private void updateAdapter(String s) {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            String current = mAdapter.getItem(i);
            if (current.equals(s)) {
                return;
            }
        }
        mAdapter.add(s);
    }


    private void updateIngredient(Ingredient ingredient, EditText editText) {
        String input = editText.getText().toString();
        if (validateIngredientString(input)) {
            String[] parts = input.split(" ");
            String quantity = parts[0];
            updateQuantity(ingredient, quantity);
            String units = parts[1];
            String name = buildNameFromInput(parts);
            ingredient.setUnits(units);
            ingredient.setName(name);
        } else {
            editText.setError("");
            showAlert(getResources().getString(R.string.ingredient_format));
        }
    }

    private String getName(String editTextInput) {
        String[] parts = editTextInput.split(" ");
        return buildNameFromInput(parts);
    }


    private String buildNameFromInput(String[] parts) {
        StringBuilder name = new StringBuilder();
        for (int i = 2; i < parts.length; i++) {
            name.append(parts[i]);
            if (i != parts.length-1) {
                name.append(" ");
            }
        }
        return name.toString();
    }


    private void updateQuantity(Ingredient ingredient, String quantity) {
        int numerator = 0;
        int denominator = 1;
        if (isValidFractionFormat(quantity)) {
            String[] numAndDenom = quantity.split("/");
            numerator = Integer.parseInt(numAndDenom[0]);
            denominator = Integer.parseInt(numAndDenom[1]);
        } else {
            numerator = Integer.parseInt(quantity);
        }
        ingredient.setNumerator(numerator);
        ingredient.setDenominator(denominator);
    }


    // format: quantity units name
    // format for fraction: num/denom units name
    private boolean validateIngredientString(String s) {
        String[] input = s.split(" ");
        if (input.length >= 3) {
            String quantity = input[0];
            if (isValidFractionFormat(quantity)) {
                String[] values = quantity.split("/");
                return canConvertToInt(values[0]) && canConvertToInt(values[1]);
            } else {
                return canConvertToInt(quantity);
            }
        }
        return false;
    }


    private boolean checkIngredientFormat() {
        boolean validFormat = true;
        for (EditText ingredient: mIngredientList) {
            String inputFormat = ingredient.getText().toString();
            if (inputFormat.length() > 0 && !validateIngredientString(inputFormat)) {
                validFormat = false;
                ingredient.setError("");
                showAlert(getResources().getString(R.string.ingredient_format));
            }
        }
        return validFormat;
    }

    private boolean isValidFractionFormat(String s) {
        if (s.contains("/")) {
            String[] numAndDenom = s.split("/");
            return numAndDenom.length == 2;
        }
        return false;
    }

    private boolean canConvertToInt(String s) {
        try {
            int n = Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private void showAlert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    private void printModel() {
        Log.d(TAG, "printing model\n" + mRecipe.toString());
    }


    private void checkDb() {
        RecipeBook recipes = RecipeBook.get(this);
        List<Recipe> current = recipes.getRecipes();
        for (Recipe recipe: current) {
            Log.d(TAG, "check db recipe in db: " + recipe.toString());
        }
    }

}

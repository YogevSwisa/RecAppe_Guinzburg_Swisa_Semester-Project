package com.example.top47.recappe;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WatchCategoryActivity extends AppCompatActivity {

    ArrayList<Recipe> recipes = null;
    DatabaseHelper recipesDbHelper;
    long category_id;
    int checkedItem ;
    Integer[] displayIdArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        Intent intent = getIntent();
        getSupportActionBar().setTitle(intent.getStringExtra("selected_item"));
        recipesDbHelper = new DatabaseHelper(this,"recipes_db",null,1);

//        recipesDbHelper.getCategories();
        category_id = intent.getLongExtra("id",-1);
        recipesDbHelper = new DatabaseHelper(this,"recipes_db",null,1);


        Cursor recipes_cursor = recipesDbHelper.getCategoryRecipes(category_id);
        recipes = new ArrayList<Recipe>();
        Recipe recipe;
        while(recipes_cursor.moveToNext()){
            recipe = new Recipe(recipes_cursor.getLong(0), recipes_cursor.getString(1), recipes_cursor.getString(2)
                    , recipes_cursor.getString(3), recipes_cursor.getString(4), recipes_cursor.getString(5),
                    recipes_cursor.getString(6));
            recipes.add(recipe);
        }
        recipes_cursor.close();

        ListCustomAdapter listAdapter = new ListCustomAdapter(recipes, this, recipesDbHelper,Integer.parseInt(category_id +""));


        //handle listview and assign listAdapter
        ListView lView = (ListView)findViewById(R.id.category_items_list_view);
        lView.setAdapter(listAdapter);
    }

    public void addRecipeToCategory(View view) {


        // --------------------------   All Recipes -----------------------------------------
        recipesDbHelper = new DatabaseHelper(this,"recipes_db",null,1);
        Cursor recipes_cursor = recipesDbHelper.getRecipes();
        recipes = new ArrayList<Recipe>();
        Recipe recipe;
        while(recipes_cursor.moveToNext()){
            recipe = new Recipe(recipes_cursor.getLong(0), recipes_cursor.getString(1), recipes_cursor.getString(2)
                    , recipes_cursor.getString(3), recipes_cursor.getString(4), recipes_cursor.getString(5),
                    recipes_cursor.getString(6));
            recipes.add(recipe);
        }
        recipes_cursor.close();


        // -------------------------- Category Recipes -----------------------------------------
        Cursor category_recipes_cursor = recipesDbHelper.getCategoryRecipes(category_id);
        ArrayList<Recipe> category_recipes = new ArrayList<Recipe>();
        Recipe category_recipe;
        while(category_recipes_cursor.moveToNext()){
            category_recipe = new Recipe(category_recipes_cursor.getLong(0), category_recipes_cursor.getString(1), category_recipes_cursor.getString(2)
                    , category_recipes_cursor.getString(3), category_recipes_cursor.getString(4), category_recipes_cursor.getString(5),
                    category_recipes_cursor.getString(6));
            category_recipes.add(category_recipe);
        }
        category_recipes_cursor.close();

        // --------------------------   All Recipes Minus Category Recipes -----------------------------------------
        Long[] allRecipesArrayIDS = listToLongArray(recipes);
        List<Long> categoryRecipesArrayIDS = Arrays.asList(listToLongArray(category_recipes));
        ArrayList<String> displayRecipes = new ArrayList<String>();
        List<Integer> displayRecipesIdList = new  ArrayList<Integer>();
        Cursor goodRecipeCursor ;
        for(Long recipe1: allRecipesArrayIDS){
            if(!categoryRecipesArrayIDS.contains(recipe1)){
                goodRecipeCursor = recipesDbHelper.getRecipe(recipe1);
                if(goodRecipeCursor.getCount() > 0 ){
                    goodRecipeCursor.moveToFirst();
                    displayRecipes.add(goodRecipeCursor.getString(1));
//                    int index = Arrays.asList(allRecipesArrayIDS).indexOf(recipe1);
//                    int id = Integer.parseInt(recipes.get(index).getId()+"");
                    displayRecipesIdList.add(Integer.parseInt(goodRecipeCursor.getLong(0) + ""));
                }


            }
        }
        String[] displayRecipesArray = displayRecipes.toArray(new String[displayRecipes.size()]);
        displayIdArray = displayRecipesIdList.toArray(new Integer[displayRecipesIdList.size()]);

        if(displayRecipesArray.length > 0) {
            showRecipesDialog(displayRecipesArray);
        }else{
            showToast("No recipes to show");
        }


    }

    public Long[] listToLongArray(ArrayList<Recipe> recipes){
        Long[] array = new Long[recipes.size()];
        int index = 0;
        for (Recipe value : recipes) {
            array[index] = value.getId();
            index++;
        }
        return array;
    }

    public void showRecipesDialog(String [] displayRecipesArray){
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an recipe");

        // add a radio button list
        builder.setSingleChoiceItems(displayRecipesArray, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedItem = which;
            }
        });

        // add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recipesDbHelper.addCategoryToRecipe(category_id,displayIdArray[checkedItem]);
                recipesDbHelper.addRecipeToCategory(category_id,displayIdArray[checkedItem]);

                Cursor recipes_cursor = recipesDbHelper.getCategoryRecipes(category_id);
                recipes = new ArrayList<Recipe>();
                Recipe recipe;
                while(recipes_cursor.moveToNext()){
                    recipe = new Recipe(recipes_cursor.getLong(0), recipes_cursor.getString(1), recipes_cursor.getString(2)
                            , recipes_cursor.getString(3), recipes_cursor.getString(4), recipes_cursor.getString(5),
                            recipes_cursor.getString(6));
                    recipes.add(recipe);
                }
                recipes_cursor.close();

                ListCustomAdapter listAdapter = new ListCustomAdapter(recipes, WatchCategoryActivity.this, recipesDbHelper, Integer.parseInt(category_id +""));
                ListView lView = (ListView)findViewById(R.id.category_items_list_view);
                lView.setAdapter(listAdapter);


            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showToast(CharSequence text){
        Context context = getApplicationContext();

        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        View view = toast.getView();
        view.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        TextView text1 = view.findViewById(android.R.id.message);
        text1.setTextColor(Color.WHITE);
        toast.show();
    }
}

package com.example.top47.recappe;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {


    ArrayList<Recipe> recipes = null;
    DatabaseHelper recipesDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set Color of the top status bar & Set toolbar title
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("All Recipes");

        // init the DB helper
        recipesDbHelper = new DatabaseHelper(this,"recipes_db",null,1);

        // init list of Recipe objects with all of the recipes
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

        // create the custom list adapter
        ListCustomAdapter listAdapter = new ListCustomAdapter(recipes, this, recipesDbHelper,null);
        
        //handle listview and assign listAdapter
        ListView lView = (ListView)findViewById(R.id.recipes_list_view);
        lView.setAdapter(listAdapter);


    }


    // set the top bar menu buttons ( categories)
    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.categories, menu);

        return super.onCreateOptionsMenu(menu);
    }


    // Categories Button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_categories:
                Intent categoriesIntent = new Intent(getBaseContext(), CategoriesActivity.class);
                startActivity(categoriesIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    // get all recipes again on resume to main activity
    @Override
    protected void onResume() {
        super.onResume();

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

        ListCustomAdapter listAdapter = new ListCustomAdapter(recipes, this, recipesDbHelper, null);


        //handle listview and assign listAdapter
        ListView lView = (ListView)findViewById(R.id.recipes_list_view);
        lView.setAdapter(listAdapter);
    }

    // start the create recipe activity
    public void addRecipe(View view) {
        Intent categoriesIntent = new Intent(getBaseContext(), CreateRecipeActivity.class);
        startActivityForResult(categoriesIntent, 1);

    }

    // open the RecipeActivity (recipe info page), after result from CreateActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(data != null) {
                long result = data.getLongExtra("id", -1);
                Intent intent = new Intent(getBaseContext(), RecipeActivity.class);
                intent.putExtra("id", result);
                startActivity(intent);
            }
        }
    }
}

package com.example.top47.recappe;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

public class RecipeActivity extends AppCompatActivity implements RecipeInfoFragment.OnFragmentInteractionListener {

    DatabaseHelper dbHelper;
    String recipe_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_recipe);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        dbHelper = new DatabaseHelper(this,"recipes_db",null,1);
        Cursor recipe_cursor = dbHelper.getRecipe(intent.getLongExtra("id",-1));
        recipe_cursor.moveToFirst();

        recipe_name = recipe_cursor.getString(1);
        getSupportActionBar().setTitle(recipe_name);

        RecipeInfoFragment fh1 = new RecipeInfoFragment();
        Bundle args = new Bundle();
        args.putInt("id", recipe_cursor.getInt(0));
        args.putString("name", recipe_cursor.getString(1));
        args.putString("ingredients", recipe_cursor.getString(2));
        args.putString("preparation_method", recipe_cursor.getString(3));
        args.putString("notes", recipe_cursor.getString(4));
        args.putString("image_path", recipe_cursor.getString(5));


        fh1.setArguments(args);
        FragmentManager fm1 = getFragmentManager();
        FragmentTransaction ft1 = fm1.beginTransaction();
        ft1.replace(R.id.fragment_placeholder, fh1);
//        ft1.addToBackStack("");
        ft1.commit();

    }


    public void scheduleRecipe(View view) {

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("title", "Make a " + recipe_name);
        Calendar cal = Calendar.getInstance();
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();
        return true;
    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}

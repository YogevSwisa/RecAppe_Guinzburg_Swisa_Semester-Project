package com.example.top47.recappe;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CategoriesActivity extends AppCompatActivity {

    DatabaseHelper categoriesDbHelper;
    ArrayList<Category> categories = null;
    ListView lView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        // Set Color of the top status bar & Set toolbar title
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // init list of Category objects with all of the recipes
        categoriesDbHelper = new DatabaseHelper(this,"recipes_db",null,1);
        Cursor categories_cursor = categoriesDbHelper.getCategories();
        categories = new ArrayList<Category>();
        Category category;
        while(categories_cursor.moveToNext()){
            category = new Category(categories_cursor.getLong(0), categories_cursor.getString(1),categories_cursor.getString(2));
            categories.add(category);
        }
        categories_cursor.close();

        // create the custom list adapter
        CategoriesListAdapter listAdapter = new CategoriesListAdapter(categories, this, categoriesDbHelper);

        //handle listview and assign listAdapter
        lView = (ListView)findViewById(R.id.categories_list_view);
        lView.setAdapter(listAdapter);
    }

    public void addCategory(View view) {

        this.categoryNameDialog();
    }

    //open the category name dialog and create a category on approval
    public void categoryNameDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Category Name:");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);

        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public String categoryNameInput;

            @Override
            public void onClick(DialogInterface dialog, int which) {
                categoryNameInput = input.getText().toString();

                String reg = "^[a-zA-Z0-9\\s]*$"; // english sentence (prevent hebrew)

                if(categoryNameInput.matches(reg)&&!categoryNameInput.isEmpty()){
                    String[] values = {categoryNameInput,""};
                    long new_category_id = categoriesDbHelper.addCategory(values);
                    Category category = new Category(new_category_id,categoryNameInput,"");
                    categories.add(category);
                    lView.invalidateViews();
                }else{
                    showToast("Enter a valid name in english.");
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // show a toast on the screen with the selected text
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

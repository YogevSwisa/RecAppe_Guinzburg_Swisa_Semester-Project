package com.example.top47.recappe;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class EditRecipeActivity extends AppCompatActivity {

    ImageView image;
    String image_path;
    long id;

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    private ImageButton mSpeakBtn;
    DatabaseHelper recipesDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set Color of the top status bar & Set toolbar title
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        recipesDbHelper = new DatabaseHelper(this,"recipes_db",null,1);
        EditText name = (EditText) findViewById(R.id.recipeNameEditText);
        EditText ingredients = (EditText) findViewById(R.id.ingredientsEditText);
        EditText preparation_method = (EditText) findViewById(R.id.preperationMethodEditText);
        EditText notes = (EditText) findViewById(R.id.notesEditText);
        image = (ImageView) findViewById(R.id.myImage);

        id = getIntent().getLongExtra("id",-1);
        name.setText(getIntent().getStringExtra("name"));
        ingredients.setText(getIntent().getStringExtra("ingredients"));
        preparation_method.setText(getIntent().getStringExtra("preparation_method"));
        notes.setText(getIntent().getStringExtra("notes"));

        image_path = getIntent().getStringExtra("image_path");

        File imgFile = new File(image_path);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageView myImage = (ImageView) findViewById(R.id.recipeImage);
            image.setImageBitmap(myBitmap);
        }

        mVoiceInputTv = (TextView) findViewById(R.id.recipeNameEditText);
        mSpeakBtn = (ImageButton) findViewById(R.id.btnSpeak);
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static final int PICK_IMAGE = 2;

    // dialog for user to select image from gallery or select from camera
    public void startDialog(final View view){
        AlertDialog.Builder myImageDialog = new AlertDialog.Builder(this);
        myImageDialog.setTitle("Select Image");
        myImageDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                openGallery();
            }
        });
        myImageDialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dispatchTakePictureIntent();
            }
        });
        myImageDialog.show();
    }

    // open gallery
    public void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    // image from camera
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // save the image from camera or image from gallery, also handle voice input for recipe name
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image.setImageBitmap(imageBitmap);
            saveImage(imageBitmap);
        }
        else if (requestCode == PICK_IMAGE) {
            if (data != null) {
                // Get the URI of the selected file
                final Uri uri = data.getData();
                recipesDbHelper = new DatabaseHelper(this,"recipes_db",null,1);



                useImage(uri);
            }
        }
        else if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String str = result.get(0);
                String[] strArray = str.split(" ");
                StringBuilder builder = new StringBuilder();
                for (String s : strArray) {
                    String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                    builder.append(cap + " ");
                }
                mVoiceInputTv.setText(builder.toString());
            }
        }
    }

    // save image
    void saveImage(Bitmap resizedbitmap){

        Cursor editedRecipeCursor = recipesDbHelper.getRecipe(id);
        editedRecipeCursor.moveToFirst();
        String imageToDeletePath = editedRecipeCursor.getString(5);
        editedRecipeCursor.close();
        File file = new File(imageToDeletePath);
        file.delete();

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        String newImageName = "0";
        if(directory.listFiles().length > 0){
            String[] imagesList = directory.list();
            Arrays.sort(imagesList);
            String biggestImage = imagesList[directory.listFiles().length-1];
            biggestImage = biggestImage.substring(0, biggestImage.indexOf("."));
            int newImageInt = Integer.parseInt(biggestImage) + 1;
            newImageName = newImageInt +"";
        }

        File mypath = new File(directory, newImageName +".png");
        image_path = mypath.getPath();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            resizedbitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            Log.e("SAVE_IMAGE", e.getMessage(), e);
        }
    }

    // convert image to bitmap before save
    void useImage(Uri uri)
    {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //use the bitmap as you like
        image.setImageBitmap(bitmap);
        saveImage(bitmap);
    }

    // edit recipe main logic - save in db and input validation.
    public void editRecipe(View view) {

        // Gets the data repository in write mode

        EditText recipeNameEditText = (EditText) findViewById(R.id.recipeNameEditText);
        EditText notesEditText = (EditText) findViewById(R.id.notesEditText);
        EditText ingredientsEditText = (EditText) findViewById(R.id.ingredientsEditText);
        EditText preperationMethodEditText = (EditText) findViewById(R.id.preperationMethodEditText);



        String recipeName = recipeNameEditText.getText().toString();
        String notes = notesEditText.getText().toString();
        String ingredients = ingredientsEditText.getText().toString();
        String preperationMethod = preperationMethodEditText.getText().toString();

        if (TextUtils.isEmpty(recipeName) || TextUtils.isEmpty(notes) || TextUtils.isEmpty(ingredients)
                || TextUtils.isEmpty(preperationMethod) || TextUtils.isEmpty(image_path)  ) {
            showToast("Please fill in all fields");
            return;
        }

        recipesDbHelper = new DatabaseHelper(this,"recipes_db",null,1);
        String[] values = {recipeName, ingredients, preperationMethod, notes, image_path};




        boolean edit_success = recipesDbHelper.editRecipe(id, values);
        showToast("Recipe Updated Successfully");
        finish();





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

    // start voice Recognizer
    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say your recipe name");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

}

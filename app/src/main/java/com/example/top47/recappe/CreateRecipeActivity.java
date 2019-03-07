package com.example.top47.recappe;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
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
import java.util.Locale;

public class CreateRecipeActivity extends AppCompatActivity {

    ImageView mImageView;
    String image_path;

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    private ImageButton mSpeakBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        // Set Color of the top status bar & Set toolbar title
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        image_path = null;
        mImageView = (ImageView) findViewById(R.id.myImage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mVoiceInputTv = (TextView) findViewById(R.id.recipeNameEditText);
        mSpeakBtn = (ImageButton) findViewById(R.id.btnSpeak);
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });

    }

    public static final int PICK_IMAGE = 2;


    // open gallery
    public void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

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
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Bitmap rotatedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
            mImageView.setImageBitmap(imageBitmap);
            saveImage(imageBitmap);
        }
        else if (requestCode == PICK_IMAGE) {
            if (data != null) {
                // Get the URI of the selected file
                final Uri uri = data.getData();
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
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }

        String newImageName = "0";
        if(directory.listFiles().length > 0){
            String[] imagesList = directory.list();
            Arrays.sort(imagesList);
            String biggestImage = imagesList[directory.list().length-1];
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
        mImageView.setImageBitmap(bitmap);
        saveImage(bitmap);
    }

    // create recipe main logic - save in db and input validation.
    public void createRecipe(View view) {

        // Gets the data repository in write mode

        EditText recipeNameEditText = (EditText) findViewById(R.id.recipeNameEditText);

        EditText ingredientsEditText = (EditText) findViewById(R.id.ingredientsEditText);
        EditText preperationMethodEditText = (EditText) findViewById(R.id.preperationMethodEditText);
        EditText notesEditText = (EditText) findViewById(R.id.notesEditText);



        String recipeName = recipeNameEditText.getText().toString();
        String ingredients = ingredientsEditText.getText().toString();
        String preperationMethod = preperationMethodEditText.getText().toString();
        String notes = notesEditText.getText().toString();

        if (TextUtils.isEmpty(recipeName) || TextUtils.isEmpty(notes) || TextUtils.isEmpty(ingredients)
                || TextUtils.isEmpty(preperationMethod) || TextUtils.isEmpty(image_path)  ) {
            showToast("Please fill in all fields");
            return;
        }

        DatabaseHelper recipesDbHelper = new DatabaseHelper(this,"recipes_db",null,1);
        String[] values = {recipeName,ingredients, preperationMethod, notes, image_path};
        long new_recipe_id = recipesDbHelper.addRecipe(values);
        showToast("Recipe Added Successfully");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("id",new_recipe_id);
        setResult(1,returnIntent);
        finish();
//
    }

    // show toast with a message
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

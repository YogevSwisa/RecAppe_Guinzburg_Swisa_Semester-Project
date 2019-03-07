package com.example.top47.recappe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListCustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<Recipe> list = new ArrayList<Recipe>();
    private Context context;
    private DatabaseHelper dbHelper;
    private int categoryId;
    String deleteTitle = "Delete Recipe";
    String deleteMessage = "Are you sure you want to delete this recipe?";



    public ListCustomAdapter(ArrayList<Recipe> list, Context context, DatabaseHelper dbHelper, Integer categoryId) {
        this.list = list;
        this.context = context;
        this.dbHelper = dbHelper;
        this.categoryId = categoryId != null ? categoryId : -1;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos).getName();
    }

    @Override
    public long getItemId(int pos) {
        if(list.size() == 1){
            return list.get(0).getId();
        }
        if(pos == list.size() && list.size() > 1){
            return list.get(pos-1).getId();
        }
        if(list.isEmpty()){
            return 0;
        }
        return list.get(pos).getId();
    }


    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.recipe_item_layout, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        TextView listItemText1 = (TextView)view.findViewById(R.id.category_string);
        String recipe_name = list.get(position).getName();
        String recipe_categories = list.get(position).getRecipe_categories();
        String firstCategoryName = "";

        if(recipe_categories.length() > 0 ) {
            long firstCategoryId = -1;
            if(recipe_categories.contains(",")) {
                firstCategoryId = Long.parseLong(recipe_categories.substring(0, recipe_categories.indexOf(",")));
            }else{
                firstCategoryId = Long.parseLong(recipe_categories);
            }
            Cursor firstCategoryCursor = dbHelper.getCategory(firstCategoryId);
            if (firstCategoryCursor.getCount() > 0) {
                firstCategoryCursor.moveToFirst();
                firstCategoryName = firstCategoryCursor.getString(1);
            }
        }



        listItemText.setText(recipe_name);
        listItemText1.setText(firstCategoryName);


        File imgFile = new File(list.get(position).getImage_path());
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            CircleImageView circleImage = (CircleImageView) view.findViewById(R.id.circleImage);
            circleImage.setImageBitmap(myBitmap);
        }


        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
        Button editBtn = (Button)view.findViewById(R.id.editBtn);



        if(context instanceof  WatchCategoryActivity){
            deleteTitle = "Remove Recipe";
            deleteMessage = "Remove this recipe from category?";
        }

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                new AlertDialog.Builder(context)
                        .setTitle(deleteTitle)
                        .setMessage(deleteMessage)

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(context instanceof  MainActivity) {
                                    Cursor deletedRecipeCursor  = dbHelper.getRecipe(getItemId(position));
                                    deletedRecipeCursor.moveToFirst();
                                    String imageToDeletePath = deletedRecipeCursor.getString(5);
                                    deletedRecipeCursor.close();
                                    File file = new File(imageToDeletePath);
                                    file.delete();


                                    boolean deleted = dbHelper.deleteRecipe(getItemId(position));
                                    list.remove(position); //or some other task
                                    notifyDataSetChanged();
                                }else{
                                    dbHelper.removeCategoryFromRecipe(ListCustomAdapter.this.categoryId, getItemId(position));
                                    dbHelper.removeRecipeFromCategory(ListCustomAdapter.this.categoryId, getItemId(position));
                                    list.remove(position); //or some other task
                                    notifyDataSetChanged();
                                }
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditRecipeActivity.class);
                intent.putExtra("id", getItemId(position));
                intent.putExtra("name", list.get(position).getName());
                intent.putExtra("ingredients", list.get(position).getIngredients());
                intent.putExtra("preparation_method", list.get(position).getPreparation_method());
                intent.putExtra("notes", list.get(position).getNotes());
                intent.putExtra("image_path", list.get(position).getImage_path());
                intent.putExtra("categories", list.get(position).getRecipe_categories());

                context.startActivity(intent);

                //do something
                notifyDataSetChanged();
            }
        });

        final TextView textView = (TextView) view.findViewById(R.id.list_item_string);
        final TextView textView1 = (TextView) view.findViewById(R.id.category_string);
        final CircleImageView recipeImage = (CircleImageView) view.findViewById(R.id.circleImage);

        textView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String item = textView.getText().toString();
                Intent intent;
                if( context instanceof  CategoriesActivity){
                    intent = new Intent(context, WatchCategoryActivity.class);
                }else{
                    intent = new Intent(context, RecipeActivity.class);
                    intent.putExtra("id", getItemId(position));
                }


                intent.putExtra("selected_item",item);
                context.startActivity(intent);
            }
        });

        textView1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String item = textView.getText().toString();
                Intent intent;
                if( context instanceof  CategoriesActivity){
                    intent = new Intent(context, WatchCategoryActivity.class);
                }else{
                    intent = new Intent(context, RecipeActivity.class);
                    intent.putExtra("id", getItemId(position));
                }


                intent.putExtra("selected_item",item);
                context.startActivity(intent);
            }
        });

        recipeImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String item = textView.getText().toString();
                Intent intent;
                if( context instanceof  CategoriesActivity){
                    intent = new Intent(context, WatchCategoryActivity.class);
                }else{
                    intent = new Intent(context, RecipeActivity.class);
                    intent.putExtra("id", getItemId(position));
                }


                intent.putExtra("selected_item",item);
                context.startActivity(intent);
            }
        });

        return view;
    }
}
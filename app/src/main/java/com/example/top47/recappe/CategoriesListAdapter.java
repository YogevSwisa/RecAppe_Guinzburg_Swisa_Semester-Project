package com.example.top47.recappe;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoriesListAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<Category> list = new ArrayList<Category>();
    private Context context;
    private DatabaseHelper dbHelper;
    public String categoryNameInput;
    public int categoryPosition;



    // Constructor
    public CategoriesListAdapter(ArrayList<Category> list, Context context, DatabaseHelper dbHelper) {
        this.list = list;
        this.context = context;
        this.dbHelper = dbHelper;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int pos) {
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

    // open category name dialog for edit category name & update the name on approval
    public void categoryNameDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Category Name:");

        // Set up the input
        final EditText input = new EditText(context);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
        input.setText(getItem(categoryPosition));
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {
                categoryNameInput = input.getText().toString();
                String reg = "^[a-zA-Z0-9\\s]*$";
                if(categoryNameInput.matches(reg)&&!categoryNameInput.isEmpty()) {
                    boolean edited = dbHelper.editCategory(getItemId(categoryPosition), categoryNameInput);
                    list.get(categoryPosition).setName(categoryNameInput);
                    notifyDataSetChanged();
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.category_item_layout, null);
        }

        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        String category_name = list.get(position).getName();
        listItemText.setText(category_name);


        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
        Button editBtn = (Button)view.findViewById(R.id.editBtn);

        // delete a category on approval
        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new android.app.AlertDialog.Builder(context)
                        .setTitle("Delete Category")
                        .setMessage("Are you sure you want to delete this Caregory?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                boolean deleted = dbHelper.deleteCategory(getItemId(position));
                                list.remove(position); //or some other task
                                notifyDataSetChanged();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        // update category name on approval
        editBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                CategoriesListAdapter.this.categoryPosition = position;
                CategoriesListAdapter.this.categoryNameDialog();
            }
        });

        final TextView textView = (TextView) view.findViewById(R.id.list_item_string);

        // start WatchCategoryActivity of selected category
        textView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String item = textView.getText().toString();
                Intent intent;
                if( context instanceof  CategoriesActivity){
                    intent = new Intent(context, WatchCategoryActivity.class);
                    intent.putExtra("id", getItemId(position));
                }else{
                    return;
                }

                intent.putExtra("selected_item",item);
                context.startActivity(intent);
            }
        });


        return view;
    }

    // show a toast on the screen with the selected text
    public void showToast(CharSequence text){
        Context context = CategoriesListAdapter.this.context;

        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        View view = toast.getView();
        view.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        TextView text1 = view.findViewById(android.R.id.message);
        text1.setTextColor(Color.WHITE);
        toast.show();
    }
}
package com.example.top47.recappe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String TABLE_NAME_RECIPES = "recipes";
    public static final String COL1_RECIPES = "id";
    public static final String COL2_RECIPES = "recipe_name";
    public static final String COL3_RECIPES = "recipe_ingredients";
    public static final String COL4_RECIPES = "recipe_preparation_method";
    public static final String COL5_RECIPES = "recipe_notes";
    public static final String COL6_RECIPES = "recipe_image_path";
    public static final String COL7_RECIPES = "recipe_categories";
    public static final String TABLE_NAME_CATEGORIES = "categories";
    public static final String COL1_CATEGORIES = "id";
    public static final String COL2_CATEGORIES = "category_name";
    public static final String COL3_CATEGORIES = "category_recipes";


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }


    // create the db tables
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableRecipes = "CREATE TABLE " + TABLE_NAME_RECIPES + " (" + COL1_RECIPES +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2_RECIPES + " TEXT, " +
                COL3_RECIPES + " TEXT, " +
                COL4_RECIPES + " TEXT, " +
                COL5_RECIPES + " TEXT, " +
                COL6_RECIPES + " TEXT, " +
                COL7_RECIPES + " TEXT)";

        sqLiteDatabase.execSQL(createTableRecipes);

        String createTableCategories = "CREATE TABLE " + TABLE_NAME_CATEGORIES + " (" + COL1_CATEGORIES +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2_CATEGORIES + " TEXT, " +
                COL3_CATEGORIES + " TEXT)";

        sqLiteDatabase.execSQL(createTableCategories);



    }

    // recreate tables on upgrade
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME_RECIPES);
        sqLiteDatabase.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME_CATEGORIES);
        onCreate(sqLiteDatabase);
    }


    // add new recipe
    public long addRecipe(String[] values1){
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL2_RECIPES, values1[0]);
        values.put(DatabaseHelper.COL3_RECIPES, values1[1]);
        values.put(DatabaseHelper.COL4_RECIPES, values1[2]);
        values.put(DatabaseHelper.COL5_RECIPES, values1[3]);
        values.put(DatabaseHelper.COL6_RECIPES, values1[4]);
        values.put(DatabaseHelper.COL7_RECIPES, "");

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DatabaseHelper.TABLE_NAME_RECIPES, null, values);
        db.close();
        return newRowId;

    }

    // add new category
    public long addCategory(String[] values1){
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL2_CATEGORIES, values1[0]);
        values.put(DatabaseHelper.COL3_CATEGORIES, values1[1]);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(DatabaseHelper.TABLE_NAME_CATEGORIES, null, values);
        db.close();
        return newRowId;

    }

    // get all recipes
    public Cursor getRecipes(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_RECIPES;
        Cursor data = db.rawQuery(query,null);

        return  data;
    }

    // get recipes of specific category
    public Cursor getCategoryRecipes(long categoryId){

        String [] selectionArgs = {"%" + categoryId + "%"};
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_RECIPES + " WHERE " + COL7_RECIPES + " LIKE ?" ;
        Cursor data = db.rawQuery(query,selectionArgs);

        return  data;
    }

    // get all categories
    public Cursor getCategories(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_CATEGORIES;
        Cursor data = db.rawQuery(query,null);

        return  data;
    }

    // get specific category
    public Cursor getCategory(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_CATEGORIES +" WHERE " + COL1_CATEGORIES +"=?", new String[] {id + ""});

        return  cursor;
    }

    // delete specific recipe
    public boolean deleteRecipe(long id){
        Cursor recipe_cursor = getRecipe(id);
        recipe_cursor.moveToFirst();
        String recipe_categories = recipe_cursor.getString(6);
        List<String> items = new ArrayList<String>(Arrays.asList(recipe_categories.split(",")));
        String[] items_array = listToStringArray(items);

        for (String category : items_array) {
            if(category != "") {
                removeRecipeFromCategory(Long.valueOf(category), id);
            }
        }
        SQLiteDatabase db = this.getWritableDatabase();
        boolean res = db.delete(TABLE_NAME_RECIPES, COL1_RECIPES + " = ?", new String []{String.valueOf(id)}) > 0;
        db.close();

        return res;
    }

    // delete specific category
    public boolean deleteCategory(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        boolean res =db.delete(TABLE_NAME_CATEGORIES, COL1_CATEGORIES + " = ?", new String []{String.valueOf(id)}) > 0;

        Cursor category_recipes_cursor = getCategoryRecipes(id);
        while(category_recipes_cursor.moveToNext()) {
            removeCategoryFromRecipe(id,category_recipes_cursor.getLong(0));
        }


        return res;
    }

    // get specific recipe
    public Cursor getRecipe(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_RECIPES +" WHERE " + COL1_RECIPES +"=?", new String[] {id + ""});
        return cursor;

    }

    // edit specific recipe
    public boolean editRecipe(long id, String[] values1){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL2_RECIPES, values1[0]);
        values.put(DatabaseHelper.COL3_RECIPES, values1[1]);
        values.put(DatabaseHelper.COL4_RECIPES, values1[2]);
        values.put(DatabaseHelper.COL5_RECIPES, values1[3]);
        values.put(DatabaseHelper.COL6_RECIPES, values1[4]);
        boolean res = db.update(TABLE_NAME_RECIPES, values, COL1_RECIPES + " = ?", new String []{String.valueOf(id)}) > 0;
        db.close();
        return res;
    }

    // edit specific category
    public boolean editCategory(long id, String categoryName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL2_CATEGORIES, categoryName);
        boolean res = db.update(TABLE_NAME_CATEGORIES, values, COL1_CATEGORIES + " = ?", new String []{String.valueOf(id)}) > 0;
        db.close();
        return res;
    }

    // add a category to the list of categories of specific recipe
    public boolean addCategoryToRecipe(long categoryId, long recipeId){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_RECIPES +" WHERE " + COL1_RECIPES +"=?", new String[] {String.valueOf(recipeId)});
        cursor.moveToFirst();
        String recipeCategories="";
        if( cursor.getString(6) != null){
            recipeCategories = cursor.getString(6);
        }
        if(cursor.getCount() > 0 ) {

            List<String> items = Arrays.asList(recipeCategories.split("\\s*,\\s*"));
            if(!items.contains(String.valueOf(categoryId))){
                recipeCategories += "," + categoryId;
            }
            if (recipeCategories.length() > 0 && recipeCategories.substring(0,1).equals(",")) {
                recipeCategories = recipeCategories.substring(1);
            }

            ContentValues category_value = new ContentValues();
            category_value.put(DatabaseHelper.COL7_RECIPES, recipeCategories);
            boolean res = db.update(TABLE_NAME_RECIPES, category_value, COL1_RECIPES + " = ?", new String[]{String.valueOf(recipeId)}) > 0;
            db.close();
            return res;
        }else{
            return false;
        }
    }

    // add a recipe to the list of recipes of specific category
    public boolean addRecipeToCategory(long categoryId, long recipeId){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_CATEGORIES +" WHERE " + COL1_CATEGORIES +"=?", new String[] {String.valueOf(categoryId)});
        cursor.moveToFirst();
        String categoryRecipes = cursor.getString(2);
        List<String> items = Arrays.asList(categoryRecipes.split("\\s*,\\s*"));

        if(!items.contains(String.valueOf(recipeId))){
            categoryRecipes += "," + recipeId;
        }
        if (categoryRecipes.length() > 0 && categoryRecipes.substring(0,1).equals(",")) {
            categoryRecipes = categoryRecipes.substring(1);
        }

        ContentValues recipes_value = new ContentValues();
        recipes_value.put(DatabaseHelper.COL3_CATEGORIES, categoryRecipes);
        boolean res = db.update(TABLE_NAME_CATEGORIES, recipes_value, COL1_CATEGORIES + " = ?", new String []{String.valueOf(categoryId)}) > 0;
        db.close();
        return res;
    }

    // // remove a category from the list of categories of specific recipe
    public boolean removeCategoryFromRecipe(long categoryId, long recipeId){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_RECIPES +" WHERE " + COL1_RECIPES +"=?", new String[] {String.valueOf(recipeId)});
        cursor.moveToFirst();
        String recipeCategories="";
        if( cursor.getString(6) != null){
            recipeCategories = cursor.getString(6);
        }
        if(cursor.getCount() > 0 ) {

            List<String> items = new ArrayList<String>(Arrays.asList(recipeCategories.split(",")));
            int index = items.indexOf(categoryId+"");

            items.remove(index);

            String[] newRecipeCategories = listToStringArray(items);
            String recipeCategoriesStr = "";
            for(String category: newRecipeCategories){
                if (!category.equals("")) {
                    recipeCategoriesStr += "," + category;
                }
            }
            if(recipeCategoriesStr.length() >0 && recipeCategoriesStr.substring(0,1).equals(",")) {
                recipeCategoriesStr = recipeCategoriesStr.substring(1);
            }

            ContentValues category_value = new ContentValues();
            category_value.put(DatabaseHelper.COL7_RECIPES, recipeCategoriesStr);
            boolean res = db.update(TABLE_NAME_RECIPES, category_value, COL1_RECIPES + " = ?", new String[]{String.valueOf(recipeId)}) > 0;
            db.close();

            return res;
        }else{
            return false;
        }
    }

    // remove a category from the list of categories of specific recipe
    public boolean removeRecipeFromCategory(long categoryId, long recipeId){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_CATEGORIES +" WHERE " + COL1_CATEGORIES +"=?", new String[] {String.valueOf(categoryId)});
        cursor.moveToFirst();

        String categoryRecipes="";
        if( cursor.getString(2) != null){
            categoryRecipes = cursor.getString(2);
        }
        if(cursor.getCount() > 0 ) {
            List<String> items = new ArrayList<String>(Arrays.asList(categoryRecipes.split(",")));
            int index = items.indexOf(recipeId + "");
            if (index == -1) {
                return true;
            }
            items.remove(index);
            String[] newCategoryRecipes = listToStringArray(items);
            String categoryRecipesStr = "";
            for (String category : newCategoryRecipes) {
                if (!category.equals("")) {
                    categoryRecipesStr += "," + category;
                }
            }
            if (categoryRecipesStr.length() > 0 && categoryRecipesStr.substring(0,1).equals(",")) {
                categoryRecipesStr = categoryRecipesStr.substring(1);
            }

        ContentValues recipes_value = new ContentValues();
        recipes_value.put(DatabaseHelper.COL3_CATEGORIES, categoryRecipesStr);
        boolean res = db.update(TABLE_NAME_CATEGORIES, recipes_value, COL1_CATEGORIES + " = ?", new String []{String.valueOf(categoryId)}) > 0;
        db.close();
        return res;
        }else{
            return false;
        }
    }

    // convert list of string to strings array
    public String[] listToStringArray(List<String> recipes){
        String[] array = new String[recipes.size()];
        int index = 0;
        for (String value : recipes) {
            array[index] = value;
            index++;
        }
        return array;
    }








}

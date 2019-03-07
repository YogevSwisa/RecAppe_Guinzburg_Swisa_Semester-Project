package com.example.top47.recappe;

public class Recipe {

    private long id;
    private String name;
    private String ingredients;
    private String preparation_method;
    private String notes;
    private String image_path;
    private String recipe_categories;

    // Class represents a Recipe

    public Recipe(long id, String name, String ingredients, String preparation_method, String notes, String image_path, String recipe_categories){
        this.id=id;
        this.name=name;
        this.ingredients=ingredients;
        this.preparation_method=preparation_method;
        this.notes=notes;
        this.image_path=image_path;
        this.recipe_categories=recipe_categories;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getPreparation_method() {
        return preparation_method;
    }

    public void setPreparation_method(String preparation_method) {
        this.preparation_method = preparation_method;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getRecipe_categories() {
        return recipe_categories;
    }

    public void setRecipe_categories(String recipe_categories) {
        this.recipe_categories = recipe_categories;
    }
}

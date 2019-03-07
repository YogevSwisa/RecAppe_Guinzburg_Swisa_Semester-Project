
package com.example.top47.recappe;

public class Category {

    private long id;
    private String name;
    private String recipes;

    // Class represents a category

    public Category(long id, String name, String recipes){
        this.id=id;
        this.name=name;
        this.recipes=recipes;
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

    public String getRecipes() {
        return recipes;
    }

    public void setRecipes(String recipes) {
        this.recipes = recipes;
    }
}




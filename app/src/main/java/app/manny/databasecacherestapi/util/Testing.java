package app.manny.databasecacherestapi.util;

import android.util.Log;

import java.util.List;

import app.manny.databasecacherestapi.models.Recipe;

public class Testing {

    public static void printRecipes(List<Recipe>list, String tag){
        for(Recipe recipe: list){
            Log.d(tag, "onChanged: " + recipe.getTitle());
        }
    }
}

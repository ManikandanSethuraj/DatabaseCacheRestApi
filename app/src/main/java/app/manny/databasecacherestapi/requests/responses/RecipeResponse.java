package app.manny.databasecacherestapi.requests.responses;


import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import app.manny.databasecacherestapi.models.Recipe;

public class RecipeResponse {

    @SerializedName("recipe")
    @Expose()
    private Recipe recipe;


    @SerializedName("error")
    @Expose
    private String error;

    @Nullable
    public Recipe getRecipe(){
        return recipe;
    }


    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "RecipeResponse{" +
                "recipe=" + recipe +
                ", error='" + error + '\'' +
                '}';
    }
}

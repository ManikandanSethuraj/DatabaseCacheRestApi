package app.manny.databasecacherestapi.requests.responses;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import app.manny.databasecacherestapi.models.Recipe;

;

public class RecipeSearchResponse {

    // SerializedName actually gets the name from the response. "count","recipes" are objects in the api response call
    // Expose the serialize and de-serialize the response.
    @SerializedName("count")
    @Expose()
    private int count;

    @SerializedName("recipes")
    @Expose()
    private List<Recipe> recipes;

    public int getCount() {
        return count;
    }

    @Nullable
    public List<Recipe> getRecipes() {
        return recipes;
    }

    @Override
    public String toString() {
        return "RecipeSearchResponse{" +
                "count=" + count +
                ", recipes=" + recipes +
                '}';
    }
}

package app.manny.databasecacherestapi.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import app.manny.databasecacherestapi.AppExecutors;
import app.manny.databasecacherestapi.models.Recipe;
import app.manny.databasecacherestapi.persistance.RecipeDAO;
import app.manny.databasecacherestapi.persistance.RecipeDatabase;
import app.manny.databasecacherestapi.requests.responses.ApiResponse;
import app.manny.databasecacherestapi.requests.responses.RecipeSearchResponse;
import app.manny.databasecacherestapi.util.NetworkBoundResource;
import app.manny.databasecacherestapi.util.Resource;

public class RecipeRepository {
    
    private RecipeRepository instance;

    private RecipeDAO recipeDAO;

    public RecipeRepository getInstance(Context context){
        if (instance == null){
            instance = new RecipeRepository(context);
        }
        return instance;
    }



    public RecipeRepository(Context context){
        recipeDAO = RecipeDatabase.getInstance(context).getRecipeDAO();
    }


    public LiveData<Resource<List<Recipe>>> searchRecipes(final String query, final int pageNumber){
        return new NetworkBoundResource<List<Recipe>, RecipeSearchResponse>(AppExecutors.getInstance()){

            @Override
            protected void saveCallResult(@NonNull @NotNull RecipeSearchResponse item) {

            }

            @Override
            protected boolean shouldFetch(@Nullable @org.jetbrains.annotations.Nullable List<Recipe> data) {
                return true;
            }

            @NonNull
            @NotNull
            @Override
            protected LiveData<List<Recipe>> loadFromDb() {
                return recipeDAO.searchRecipes(query,pageNumber);
            }

            @NonNull
            @NotNull
            @Override
            protected LiveData<ApiResponse<RecipeSearchResponse>> createCall() {
                return null;
            }
        }.getAsLiveData();
    }
}

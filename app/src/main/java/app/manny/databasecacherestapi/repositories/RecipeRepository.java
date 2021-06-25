package app.manny.databasecacherestapi.repositories;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import app.manny.databasecacherestapi.AppExecutors;
import app.manny.databasecacherestapi.models.Recipe;
import app.manny.databasecacherestapi.persistance.RecipeDAO;
import app.manny.databasecacherestapi.persistance.RecipeDatabase;
import app.manny.databasecacherestapi.requests.ServiceGenerator;
import app.manny.databasecacherestapi.requests.responses.ApiResponse;
import app.manny.databasecacherestapi.requests.responses.RecipeSearchResponse;
import app.manny.databasecacherestapi.util.NetworkBoundResource;
import app.manny.databasecacherestapi.util.Resource;

public class RecipeRepository {

    private static final String TAG = "RecipeRepository";
    
    private static RecipeRepository instance;

    private RecipeDAO recipeDAO;

    public static RecipeRepository getInstance(Context context){
        if (instance == null){
            instance = new RecipeRepository(context);
        }
        return instance;
    }



    private RecipeRepository(Context context){
        recipeDAO = RecipeDatabase.getInstance(context).getRecipeDAO();
    }


    public LiveData<Resource<List<Recipe>>> searchRecipesApi(final String query, final int pageNumber){
        return new NetworkBoundResource<List<Recipe>, RecipeSearchResponse>(AppExecutors.getInstance() ){

            @Override
            public void saveCallResult(@NonNull RecipeSearchResponse item) {
                if(item.getRecipes() != null){ // recipe list will be null if api key is expired
                    Recipe[] recipes = new Recipe[item.getRecipes().size()];

                    int index = 0;
                    for(long rowId: recipeDAO.insertAllRecipes((Recipe[])(item.getRecipes().toArray(recipes)))){
                        if(rowId == -1){ // conflict detected
                            Log.d(TAG, "saveCallResult: CONFLICT... This recipe is already in cache.");
                            // if already exists, I don't want to set the ingredients or timestamp b/c they will be erased
                            recipeDAO.updateRecipe(
                                    recipes[index].getRecipe_id(),
                                    recipes[index].getTitle(),
                                    recipes[index].getPublisher(),
                                    recipes[index].getImage_url(),
                                    recipes[index].getSocial_rank()
                            );
                        }
                        index++;
                    }
                }
            }

            @Override
            public boolean shouldFetch(@Nullable List<Recipe> data) {
                return true; // always query the network since the queries can be anything
            }

            @NonNull
            @Override
            public LiveData<List<Recipe>> loadFromDb() {
                return recipeDAO.searchRecipes(query, pageNumber);
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<RecipeSearchResponse>> createCall() {
                return ServiceGenerator.getRecipeApi().searchRecipe(
                        query,
                        String.valueOf(pageNumber)
                );
            }

        }.getAsLiveData();
    }
}

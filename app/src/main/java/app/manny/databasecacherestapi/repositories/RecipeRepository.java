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
import app.manny.databasecacherestapi.requests.responses.RecipeResponse;
import app.manny.databasecacherestapi.requests.responses.RecipeSearchResponse;
import app.manny.databasecacherestapi.util.Constants;
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



    public LiveData<Resource<Recipe>> searchRecipeApi(final String recipeId){
        return new NetworkBoundResource<Recipe, RecipeResponse>(AppExecutors.getInstance()){

            @Override
            public void saveCallResult(@NonNull RecipeResponse item) {

                // Recipe will be NULL if API key is expired
                if(item.getRecipe() != null){
                    item.getRecipe().setTimpstamp((int)(System.currentTimeMillis() / 1000)); // save time in seconds
                    recipeDAO.insertRecipe(item.getRecipe());
                }
            }

            @Override
            public boolean shouldFetch(@Nullable Recipe data) {
                Log.d(TAG, "shouldFetch: recipe: " + data.toString());
                int currentTime = (int)(System.currentTimeMillis() / 1000);
                Log.d(TAG, "shouldFetch: current time: " + currentTime);
                int lastRefresh = data.getTimpstamp();
                Log.d(TAG, "shouldFetch: last refresh: " + lastRefresh);
                Log.d(TAG, "shouldFetch: it's been " + ((currentTime - lastRefresh) / 60 / 60 / 24)
                        + " days since this recipe was refreshed. 30 days must elapse.");
                if(((System.currentTimeMillis() / 1000) - data.getTimpstamp()) >= Constants.RECIPE_REFRESH_TIME){
                    Log.d(TAG, "shouldFetch: SHOULD REFRESH RECIPE? " + true);
                    return true;
                }
                Log.d(TAG, "shouldFetch: SHOULD REFRESH RECIPE? " + false);
                return false;
            }

            @NonNull
            @Override
            public LiveData<Recipe> loadFromDb() {
                return (recipeDAO.getRecipe(recipeId));
            }

            @NonNull
            @Override
            public LiveData<ApiResponse<RecipeResponse>> createCall() {
                return ServiceGenerator.getRecipeApi().getRecipe(
                        recipeId
                );
            }

        }.getAsLiveData();
    }
}

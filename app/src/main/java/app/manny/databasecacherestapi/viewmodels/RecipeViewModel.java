package app.manny.databasecacherestapi.viewmodels;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import app.manny.databasecacherestapi.models.Recipe;
import app.manny.databasecacherestapi.repositories.RecipeRepository;
import app.manny.databasecacherestapi.util.Resource;


public class RecipeViewModel extends AndroidViewModel {


    private RecipeRepository mRecipeRepository;

    public RecipeViewModel(@NonNull Application application) {
        super(application);
        mRecipeRepository = RecipeRepository.getInstance(application);
    }

    public LiveData<Resource<Recipe>> searchRecipeApi(String recipeId){
        return mRecipeRepository.searchRecipeApi(recipeId);
    }




}






















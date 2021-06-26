package app.manny.databasecacherestapi.viewmodels;


import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import app.manny.databasecacherestapi.models.Recipe;
import app.manny.databasecacherestapi.repositories.RecipeRepository;
import app.manny.databasecacherestapi.util.Resource;


public class RecipeListViewModel extends AndroidViewModel {

    private static final String TAG = "RecipeListViewModel";
    private RecipeRepository recipeRepository;

    public enum ViewState {CATEGORIES, RECIPES};

    private MutableLiveData<ViewState> viewState;
    private MediatorLiveData<Resource<List<Recipe>>> recipes = new MediatorLiveData<>();

    // Query Extras
    private boolean isQueryExhuasted;
    private boolean isPerformingQuery;
    private int pageNumber;
    private String query;
    private boolean cancelRequest;
    private long requestStartTime;

    public static final String QUERY_EXHAUSTED = "Query is exhausted.";

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        recipeRepository = RecipeRepository.getInstance(application);
        init();
    }

    private void init(){
        if (viewState == null){
            viewState = new MutableLiveData<>();
            viewState.setValue(ViewState.CATEGORIES);
        }
    }

    public LiveData<ViewState> getViewState(){
        return viewState;
    }


    public LiveData<Resource<List<Recipe>>> getRecipes(){
        return recipes;
    }



    public int getPageNumber(){
        return pageNumber;
    }


    public void searchRecipes(String query, int pageNumber){
        if (!isPerformingQuery){
            if (pageNumber == 0){
                pageNumber = 1;
            }
            this.query = query;
            this.pageNumber = pageNumber;
            isQueryExhuasted = false;
            executeSearch();
        }
    }

    public void setViewCategories(){
        viewState.setValue(ViewState.CATEGORIES);
    }

    public void searchNextPage(){
        if(!isQueryExhuasted && !isPerformingQuery){
            pageNumber++;
            executeSearch();
        }
    }

    private void executeSearch(){
        requestStartTime = System.currentTimeMillis();
        cancelRequest = false;
        isPerformingQuery = true;
        viewState.setValue(ViewState.RECIPES);
        final LiveData<Resource<List<Recipe>>> repositorySource = recipeRepository.searchRecipesApi(query, pageNumber);
        recipes.addSource(repositorySource, listResource -> {
            if(!cancelRequest){
                if(listResource != null){
                    recipes.setValue(listResource);
                    if(listResource.status == Resource.Status.SUCCESS ){
                        Log.d(TAG, "executeSearch: TimeSucess:"+((System.currentTimeMillis() - requestStartTime)/1000)+ " Seconds");
                        isPerformingQuery = false;
                        if(listResource.data != null) {
                            if (listResource.data.size() == 0) {
                                Log.d(TAG, "onChanged: query is EXHAUSTED...");
                                recipes.setValue(new Resource<List<Recipe>>(
                                        Resource.Status.ERROR,
                                        listResource.data,
                                        QUERY_EXHAUSTED
                                ));
                                isPerformingQuery = true;
                            }
                        }
                        // must remove or it will keep listening to repository
                        recipes.removeSource(repositorySource);
                    }
                    else if(listResource.status == Resource.Status.ERROR ){
                        Log.d(TAG, "executeSearch: TimeError:"+((System.currentTimeMillis() - requestStartTime)/1000)+ " Seconds");

                        isPerformingQuery = false;
                        recipes.removeSource(repositorySource);
                    }
                }
                else{
                    recipes.removeSource(repositorySource);
                }
            }
            else{
                recipes.removeSource(repositorySource);
            }
        });
    }

    public void cancelSearchRequest(){
        if(isPerformingQuery){
            Log.d(TAG, "cancelSearchRequest: canceling the search request.");
            cancelRequest = true;
            isPerformingQuery = false;
            pageNumber = 1;
        }
    }


}
















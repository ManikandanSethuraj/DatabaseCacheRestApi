package app.manny.databasecacherestapi.requests;

import androidx.lifecycle.LiveData;

import app.manny.databasecacherestapi.requests.responses.ApiResponse;
import app.manny.databasecacherestapi.requests.responses.RecipeResponse;
import app.manny.databasecacherestapi.requests.responses.RecipeSearchResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApi {

    // SEARCH
    @GET("api/search")
    LiveData<ApiResponse<RecipeSearchResponse>> searchRecipe(
            @Query("q") String query,
            @Query("page") String page
    );

    // GET RECIPE REQUEST
    @GET("api/get")
    LiveData<ApiResponse<RecipeResponse>> getRecipe(
            @Query("rId") String recipe_id
    );
}

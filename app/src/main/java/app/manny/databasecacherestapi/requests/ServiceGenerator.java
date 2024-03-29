package app.manny.databasecacherestapi.requests;

import java.util.concurrent.TimeUnit;

import app.manny.databasecacherestapi.util.Constants;
import app.manny.databasecacherestapi.util.LiveDataCallAdapterFactory;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static app.manny.databasecacherestapi.util.Constants.CONNECTION_TIMEOUT;
import static app.manny.databasecacherestapi.util.Constants.READ_TIMEOUT;
import static app.manny.databasecacherestapi.util.Constants.WRITE_TIMEOUT;

public class ServiceGenerator {


    private static OkHttpClient client = new OkHttpClient.Builder()
            // establish connection with server
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)

            // time between each byte read from server
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)

            // time between each byte sent to server
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)

            .retryOnConnectionFailure(false)

            .build();
    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                   // .client(client)
                    .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = retrofitBuilder.build();

    private static RecipeApi recipeApi = retrofit.create(RecipeApi.class);

    public static RecipeApi getRecipeApi(){
        return recipeApi;
    }
}

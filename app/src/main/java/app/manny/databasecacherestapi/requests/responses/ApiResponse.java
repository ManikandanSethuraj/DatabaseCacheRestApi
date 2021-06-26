package app.manny.databasecacherestapi.requests.responses;

import android.util.Log;

import java.io.IOException;

import retrofit2.Response;
/**
 * Generic class for handling responses from Retrofit
 * @param <T>
 */
public class ApiResponse<T> {

    private static final String TAG = "ApiResponse";

    public ApiResponse<T> create(Throwable error){
        Log.d(TAG, "create: Throwable");
        return new ApiErrorResponse<T>(!error.getMessage().equals("") ? error.getMessage() : "unKnown Error \n Check Connnection");
    }

    public ApiResponse<T> create(Response<T> response){
        Log.d(TAG, "create: Response");
        if (response.isSuccessful()){
            Log.d(TAG, "create: 1");
            T body = response.body();

            // make sure api key is valid and not expired
            if(body instanceof RecipeSearchResponse){
                if(!CheckRecipeApiKey.isRecipeApiKeyValid((RecipeSearchResponse)body)){
                    String errorMsg = "Api key invalid or expired.";
                    return new ApiErrorResponse<>(errorMsg);
                }
            }
            else if(body instanceof RecipeResponse){
                if(!CheckRecipeApiKey.isRecipeApiKeyValid((RecipeResponse)body)){
                    String errorMsg = "Api key invalid or expired.";
                    return new ApiErrorResponse<>(errorMsg);
                }
            }

            if (body != null || response.code() != 204){
                Log.d(TAG, "create: 2");
                Log.d(TAG, "create: "+response.body());
                return new ApiSuccessResponse<>(response.body());
            }else {
                Log.d(TAG, "create: 3");
                return new ApiEmptyResponse<>();
            }

        }else {
          String errorMessage = "";
          try{
              errorMessage = response.errorBody().string();
          }catch (IOException e){
              e.printStackTrace();
              errorMessage = response.message();
          }
          return new ApiErrorResponse<>(errorMessage);
        }
    }



    /**
     * Generic success response from api
     * @param <T>
     */
    public class ApiSuccessResponse<T> extends ApiResponse<T>{

        private T body;

        public ApiSuccessResponse(T body){
            this.body = body;
        }

        public T getBody(){
            return body;
        }

    }


    /**
     * Generic Error response from API
     * @param <T>
     */
    public class ApiErrorResponse<T> extends ApiResponse<T>{

        private String errorMessage;

        public ApiErrorResponse(String errorMessage){
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage(){return errorMessage;}

    }
    /**
     * separate class for HTTP 204 resposes so that we can make ApiSuccessResponse's body non-null.
     */
    public class ApiEmptyResponse<T> extends ApiResponse<T>{

    }
}

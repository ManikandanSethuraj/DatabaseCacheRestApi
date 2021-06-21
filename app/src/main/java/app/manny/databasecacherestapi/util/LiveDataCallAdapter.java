package app.manny.databasecacherestapi.util;

import androidx.lifecycle.LiveData;

import java.lang.reflect.Type;

import app.manny.databasecacherestapi.requests.responses.ApiResponse;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveDataCallAdapter<R> implements CallAdapter<R, LiveData<ApiResponse<R>>> {

    private Type responseType;

   public LiveDataCallAdapter(Type responseType){
       this.responseType = responseType;
   }


    @Override
    public Type responseType() {
        return null;
    }

    @Override
    public LiveData<ApiResponse<R>> adapt(Call<R> call) {
        return new LiveData<ApiResponse<R>>() {
            @Override
            protected void onActive() {
                super.onActive();
                final ApiResponse apiResponse = new ApiResponse();
                call.enqueue(new Callback<R>() {
                    @Override
                    public void onResponse(Call<R> call, Response<R> response) {
                        apiResponse.create(response);
                    }

                    @Override
                    public void onFailure(Call<R> call, Throwable t) {
                        apiResponse.create(t);
                    }
                });
            }
        };
    }
}

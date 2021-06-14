package app.manny.databasecacherestapi.requests.responses;

import java.io.IOException;

import retrofit2.Response;

public class ApiResponse<T> {


    public ApiResponse<T> create(Throwable error){
        return new ApiErrorResponse<T>(!error.getMessage().equals("") ? error.getMessage() : "unKnown Error \n Check Connnection");
    }

    public ApiResponse<T> create(Response<T> response){
        if (response.isSuccessful()){
            T body = response.body();
            if (body != null || response.code() != 203){
                return new ApiSuccessResponse<>(response.body());
            }else {
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




    public class ApiSuccessResponse<T> extends ApiResponse<T>{

        private T body;

        public ApiSuccessResponse(T body){
            this.body = body;
        }

        public T getBody(){
            return body;
        }

    }


    public class ApiErrorResponse<T> extends ApiResponse<T>{

        private String errorMessage;

        public ApiErrorResponse(String errorMessage){
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage(){return errorMessage;}

    }

    public class ApiEmptyResponse<T> extends ApiResponse<T>{

    }
}

package app.manny.databasecacherestapi.util;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import app.manny.databasecacherestapi.AppExecutors;
import app.manny.databasecacherestapi.requests.responses.ApiResponse;

// CacheObject: Type for the Resource data. (database cache)
// RequestObject: Type for the API response. (network request)
public abstract class NetworkBoundResource<CacheObject, RequestObject> {

    private static final String TAG = "NetworkBoundResource";

    private MediatorLiveData<Resource<CacheObject>> results = new MediatorLiveData<>();

    private AppExecutors appExecutors;

    public NetworkBoundResource(AppExecutors appExecutors){
        this.appExecutors = appExecutors;
        init();
    }

    private void init(){

        // Update LiveData for loading status..
        results.postValue((Resource<CacheObject>) Resource.loading(null));

        // Observer LiveData source from Local DB
        final LiveData<CacheObject> dbLocal = loadFromDb();

        results.addSource(dbLocal, new Observer<CacheObject>() {
            @Override
            public void onChanged(CacheObject cacheObject) {
                // Removing the sousrce , coz the Observer would still be listening the database
                results.removeSource(dbLocal);

                // Deciding whether the data should be retrived from Cache or Remote.
                if (shouldFetch(cacheObject)){
                    // Fetch data from the remote network
                    fetchFromNetwork(dbLocal);
                }else {
                    results.addSource(dbLocal, new Observer<CacheObject>() {
                        @Override
                        public void onChanged(CacheObject cacheObject) {
                            setValue(Resource.success(cacheObject));
                        }
                    });
                }
            }
        });



    }


    /**
     * 1) observe local db
     * 2) if <condition/> query the network
     * 3) stop observing the local db
     * 4) insert new data into local db
     * 5) begin observing local db again to see the refreshed data from network
     * @param dbSource
     */
    private void fetchFromNetwork(final LiveData<CacheObject> dbSource){
        results.addSource(dbSource, new Observer<CacheObject>() {
            @Override
            public void onChanged(CacheObject cacheObject) {
                setValue(Resource.loading(cacheObject));
            }
        });

        final LiveData<ApiResponse<RequestObject>> apiResponse = createCall();


        results.addSource(apiResponse, new Observer<ApiResponse<RequestObject>>() {
            @Override
            public void onChanged(ApiResponse<RequestObject> requestObjectApiResponse) {
                results.removeSource(dbSource);
                results.removeSource(apiResponse);

                /**
                 * 3 cases:
                 * 1) ApiResponseSuccess
                 * 2) ApiResponseError
                 * 3) ApiResponseEmpty
                 */

                if (requestObjectApiResponse instanceof ApiResponse.ApiSuccessResponse){

                    Log.d(TAG, "ApiSuccess");

                    // All the Database Room operations have to be done in the background thread, if not it may lead to Crashes
                    appExecutors.getmDiskIO().execute(new Runnable() {
                        @Override
                        public void run() {

                            // this is the database call i.e. why the background thread is called.
                         //   saveCallResult(null);
                            saveCallResult((RequestObject) processResponse((ApiResponse.ApiSuccessResponse) requestObjectApiResponse) );

                            // this call is going to update the UI, so the mainthread is called.
                            appExecutors.getMainThreadExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                   results.addSource(loadFromDb(), new Observer<CacheObject>() {
                                       @Override
                                       public void onChanged(CacheObject cacheObject) {
                                           setValue(Resource.success(cacheObject));
                                       }
                                   });
                                }
                            });

                        }
                    });

                }else if (requestObjectApiResponse instanceof ApiResponse.ApiErrorResponse){
                    Log.d(TAG, "ApiError");
                    appExecutors.getMainThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            results.addSource(loadFromDb(), new Observer<CacheObject>() {
                                @Override
                                public void onChanged(CacheObject cacheObject) {
                                    setValue(Resource.success(cacheObject));
                                }
                            });
                        }
                    });

                }else if (requestObjectApiResponse instanceof ApiResponse.ApiEmptyResponse){
                    Log.d(TAG, "ApiEmpty");
                    appExecutors.getMainThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            results.addSource(dbSource, new Observer<CacheObject>() {
                                @Override
                                public void onChanged(CacheObject cacheObject) {
                                    setValue(Resource.error(((ApiResponse.ApiErrorResponse)requestObjectApiResponse).getErrorMessage(), cacheObject));
                                }
                            });
                        }
                    });

                }

            }
        });

    }


    private void setValue(Resource<CacheObject> newValue){
        if (results.getValue() != newValue){
            results.postValue(newValue);
        }
    }

    private CacheObject processResponse(ApiResponse.ApiSuccessResponse response){
        return (CacheObject) response.getBody();
    }

    // Called to save the result of the API response into the database.
    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestObject item);

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    @MainThread
    protected abstract boolean shouldFetch(@Nullable CacheObject data);

    // Called to get the cached data from the database.
    @NonNull @MainThread
    protected abstract LiveData<CacheObject> loadFromDb();

    // Called to create the API call.
    @NonNull @MainThread
    protected abstract LiveData<ApiResponse<RequestObject>> createCall();

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    public final LiveData<Resource<CacheObject>> getAsLiveData(){
        return results;
    };
}

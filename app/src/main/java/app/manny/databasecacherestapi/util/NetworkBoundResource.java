package app.manny.databasecacherestapi.util;

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

       
    }


    private void setValue(Resource<CacheObject> newValue){
        if (results.getValue() != newValue){
            results.postValue(newValue);
        }
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

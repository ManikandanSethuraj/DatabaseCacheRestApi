package app.manny.databasecacherestapi;


import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class AppExecutors {

    private static AppExecutors instance;

    public static AppExecutors getInstance(){
        if(instance == null){
            instance = new AppExecutors();
        }
        return instance;
    }

 // These are removed cause the data is gonna be loaded from Database

 //   private final ScheduledExecutorService mNetworkIO = Executors.newScheduledThreadPool(3);

 //   public ScheduledExecutorService networkIO(){
 //       return mNetworkIO;
 //   }


    // We need only one Background thread to view the database cache
    private final Executor mDiskIO = Executors.newSingleThreadExecutor();

    public Executor getmDiskIO(){
        return mDiskIO;
    }


    // Posting Data to the UI
    private final Executor mainThreadExe =new MainThreadExecutor();

    public Executor getMainThreadExecutor(){
        return mainThreadExe;
    }


    private static class MainThreadExecutor implements Executor{


        private Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {

            handler.post(command);
        }
    }

}

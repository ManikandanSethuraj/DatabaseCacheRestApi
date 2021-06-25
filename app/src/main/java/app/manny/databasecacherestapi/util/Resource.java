package app.manny.databasecacherestapi.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Resource<T> {

    private static final String TAG = "Resource::";
    @NonNull
    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    public Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> success(@NonNull T data) {
        Log.d(TAG, "success: ");
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(@NonNull String msg, @Nullable T data) {
        Log.d(TAG, "error: ");
        return new Resource<>(Status.ERROR, data, msg);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        Log.d(TAG, "loading: ");
        return new Resource<>(Status.LOADING, data, null);
    }

    public enum Status { SUCCESS, ERROR, LOADING}
}

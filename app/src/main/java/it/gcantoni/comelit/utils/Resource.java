package it.gcantoni.comelit.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A generic class that holds a value with its loading status.
 * Inspired by Google's Architectural Components to handle network states
 * (Loading, Success, Error) in a consistent way across the app.
 *
 * @param <T> The type of the resource data (e.g., Product, List<Product>)
 */
public class Resource<T> {

    @NonNull
    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    /**
     * Possible states of a resource fetch operation.
     */
    public enum Status { SUCCESS, ERROR, LOADING }

    private Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    /**
     * Creates a Resource with SUCCESS status and the attached data.
     */
    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    /**
     * Creates a Resource with ERROR status and a descriptive message.
     */
    public static <T> Resource<T> error(@Nullable String msg) {
        return new Resource<>(Status.ERROR, null, msg);
    }

    /**
     * Creates a Resource with LOADING status, indicating an ongoing operation.
     */
    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null);
    }

    // --- Helper Methods for UI check ---
    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public boolean isLoading() {
        return status == Status.LOADING;
    }

    public boolean isError() {
        return status == Status.ERROR;
    }
}

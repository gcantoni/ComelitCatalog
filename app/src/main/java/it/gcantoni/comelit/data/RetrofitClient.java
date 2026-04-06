package it.gcantoni.comelit.data;

import java.util.concurrent.TimeUnit;

import it.gcantoni.comelit.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton factory for the Retrofit instance.
 * Configures the network layer with specific timeouts and logging for debugging.
 */
public class RetrofitClient {

    private static final String BASE_URL = BuildConfig.BASE_URL;

    private static Retrofit retrofit = null;

    /**
     * Provides a thread-safe Singleton instance of Retrofit.
     *
     * @return The configured Retrofit client.
     */
    public static synchronized Retrofit getClient() {
        if (retrofit == null) {

            // Network logging interceptor for debugging API instability
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Configure OkHttpClient with custom timeouts to handle "slow responses"
            // as specified in the assignment constraints
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS) // Increased for slow network conditions
                    .readTimeout(20, TimeUnit.SECONDS)    // Increased to wait for slow API responses
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .retryOnConnectionFailure(true)       // Automatically retry on transient network failures
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }
}

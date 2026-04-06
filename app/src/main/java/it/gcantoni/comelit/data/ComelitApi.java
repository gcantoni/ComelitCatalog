package it.gcantoni.comelit.data;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Path;

/**
 * REST API definition for the Comelit Product Catalog.
 * Handles authentication and product data retrieval.
 */
public interface ComelitApi {

    /**
     * Authenticates the user and returns an access token.
     *
     * @param request The login credentials (username and password).
     * @return A Call object for LoginResponse.
     */
    @POST("api/v1/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    /**
     * Retrieves a paginated list of products with optional search and category filters.
     *
     * @param authorizationHeader The "Bearer <token>" string for authentication.
     * @param page                The current page index (1-based).
     * @param pageSize            The number of items per page.
     * @param searchByQuery       Optional text string to search within product names/descriptions.
     * @param filterByCategory    Optional category string to filter results.
     * @return A Call object for ProductResponse containing the product list and metadata.
     */
    @GET("api/v1/products")
    Call<ProductResponse> getProducts(
            @Header("Authorization") String authorizationHeader,
            @Query("page") int page,
            @Query("page_size") int pageSize,
            @Query("query") String searchByQuery,
            @Query("category") String filterByCategory
    );

    /**
     * Retrieves comprehensive details for a specific product, including documents and accessories.
     *
     * @param authorizationHeader The "Bearer <token>" string for authentication.
     * @param productSlug         The unique identifier (slug or code) for the product.
     * @return A Call object for ProductDetail.
     */
    @GET("api/v1/products/{slug}")
    Call<ProductDetail> getProductDetail(
            @Header("Authorization") String authorizationHeader,
            @Path("slug") String productSlug
    );
}
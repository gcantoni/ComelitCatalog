package it.gcantoni.comelit.data;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * Data model for the paginated product list response.
 * Maps the API "items" array to a list of Product objects.
 */
public class ProductResponse {

    @SerializedName("items")
    private List<Product> items;

    /**
     * Retrieves the list of products for the current page.
     *
     * @return A non-null list of Product objects. Returns an empty list
     *         if the "items" field is missing in the JSON.
     */
    public List<Product> getProducts() {
        return items != null ? items : new ArrayList<>();
    }

    /**
     * Helper to check if the current response contains any data.
     *
     * @return True if the product list is not empty.
     */
    public boolean hasResults() {
        return items != null && !items.isEmpty();
    }
}

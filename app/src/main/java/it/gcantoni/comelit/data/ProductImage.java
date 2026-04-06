package it.gcantoni.comelit.data;

import com.google.gson.annotations.SerializedName;

/**
 * Data model representing a product image asset.
 * Encapsulates the media URL provided by the catalog API.
 */
public class ProductImage {

    @SerializedName("url")
    private String url;

    /**
     * Retrieves the direct link to the image resource.
     *
     * @return A non-null URL string. Returns an empty string if the URL is missing
     *         to prevent crashes in image loading libraries.
     */
    public String getUrl() {
        return url != null ? url : "";
    }

    /**
     * Checks if the image URL is valid and not empty.
     *
     * @return True if the URL can be processed, false otherwise.
     */
    public boolean isValid() {
        return url != null && !url.trim().isEmpty();
    }
}
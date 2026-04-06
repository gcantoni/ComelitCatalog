package it.gcantoni.comelit.data;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * Data model for the detailed view of a product.
 * Provides extended information such as full descriptions, image galleries, and documentation.
 */
public class ProductDetail {
    @SerializedName("code")
    private String code;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("images")
    private List<ProductImage> images;

    @SerializedName("documents")
    private List<Product.Document> documents;

    // --- Getters with Fallback Logic ---
    public String getCode() {
        return code != null ? code : "N/A";
    }

    public String getName() {
        return name != null ? name : "Unnamed Product";
    }

    /**
     * @return The full product description or a placeholder if missing.
     */
    public String getDescription() {
        return description != null ? description : "No detailed description available.";
    }

    /**
     * Retrieves the gallery of product images.
     * @return A non-null list of ProductImage objects.
     */
    public List<ProductImage> getImages() {
        return images != null ? images : new ArrayList<>();
    }

    /**
     * Retrieves the list of technical documents.
     * Reuses the Document inner class from the Product model for consistency.
     * @return A non-null list of Product.Document objects.
     */
    public List<Product.Document> getDocuments() {
        return documents != null ? documents : new ArrayList<>();
    }
}
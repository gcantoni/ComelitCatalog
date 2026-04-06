package it.gcantoni.comelit.data;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * Core data model representing a Product in the catalog.
 * Includes nested classes for Documents and Accessories as per the API structure.
 */
public class Product {
    @SerializedName("code")
    private String code;

    @SerializedName("name")
    private String name;

    @SerializedName("category")
    private String category;

    @SerializedName("subcategory")
    private String subcategory;

    @SerializedName("availability")
    private String availability;

    @SerializedName("thumbnail_url")
    private String thumbnailUrl;

    @SerializedName("short_description")
    private String shortDescription;

    @SerializedName("has_documents")
    private boolean hasDocuments;

    @SerializedName("source_url")
    private String sourceUrl;

    @SerializedName("description")
    private String description;

    @SerializedName("slug")
    private String slug;

    @SerializedName("images")
    private List<ProductImage> images;

    @SerializedName("documents")
    private List<Document> documents;

    @SerializedName("accessories")
    private List<Accessory> accessories;

    // --- Getters with Fallback Logic ---
    public String getCode() {
        return code != null ? code : "N/A";
    }

    public String getName() {
        return name != null ? name : "Unnamed Product";
    }

    public String getCategory() {
        return category != null ? category : "Uncategorized";
    }

    public String getSubcategory() {
        return subcategory != null ? subcategory : "";
    }

    public String getAvailability() {
        return availability != null ? availability : "unknown";
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getShortDescription() {
        return shortDescription != null ? shortDescription : "";
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getSlug() {
        return slug;
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    /**
     * @return A non-null list of product images.
     */
    public List<ProductImage> getImages() {
        return images != null ? images : new ArrayList<>();
    }

    /**
     * @return A non-null list of technical documents.
     */
    public List<Document> getDocuments() {
        return documents != null ? documents : new ArrayList<>();
    }

    /**
     * @return A non-null list of compatible accessories.
     */
    public List<Accessory> getAccessories() {
        return accessories != null ? accessories : new ArrayList<>();
    }

    // --- Inner Classes ---

    /**
     * Represents a downloadable technical document (e.g., PDF datasheet).
     */
    public static class Document {
        @SerializedName("title") private String title;
        @SerializedName("url") private String url;

        public String getTitle() {
            return title != null ? title : "Document";
        }

        public String getUrl() {
            return url;
        }
    }

    /**
     * Represents a related product accessory.
     */
    public static class Accessory {
        @SerializedName("code") private String code;
        @SerializedName("name") private String name;

        public String getCode() { return code; }
        public String getName() { return name; }
    }
}
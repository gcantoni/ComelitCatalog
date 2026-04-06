package it.gcantoni.comelit.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;
import it.gcantoni.comelit.R;
import it.gcantoni.comelit.data.Product;

/**
 * Adapter for the product list.
 * Manages the data binding between the Product model and the item_product layout.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList = new ArrayList<>();
    private final OnProductClickListener listener;

    /**
     * Interface to handle click events on list items.
     */
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    /**
     * Updates the dataset.
     * Note: In a production environment, DiffUtil would be used for better performance.
     */
    public void setProducts(List<Product> products) {
        this.productList = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        Context context = holder.itemView.getContext();

        // Basic text binding - Null safety is now handled inside the Product model
        holder.tvName.setText(product.getName());
        holder.tvCode.setText(String.format("Codice: %s", product.getCode()));

        // Category Chip management
        String category = product.getCategory();
        if (category != null && !category.isEmpty()) {
            holder.chipCategory.setVisibility(View.VISIBLE);
            holder.chipCategory.setText(category);
        } else {
            holder.chipCategory.setVisibility(View.GONE);
        }

        // Availability Badge Logic
        setupAvailabilityBadge(holder, product);

        // Image loading with fallback and caching
        Glide.with(context)
                .load(product.getThumbnailUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(android.R.drawable.progress_horizontal)
                .error(android.R.drawable.stat_notify_error)
                .centerCrop()
                .into(holder.imgProduct);

        holder.itemView.setOnClickListener(v -> listener.onProductClick(product));
    }

    /**
     * Configures the visual state of the availability badge based on product data.
     */
    private void setupAvailabilityBadge(ProductViewHolder holder, Product product) {
        boolean isAvailable = "available".equalsIgnoreCase(product.getAvailability());

        holder.tvAvailabilityBadge.setText(isAvailable ? "Disponibile" : "Non disp.");

        int color = isAvailable ? Color.parseColor("#4CAF50") : Color.parseColor("#F44336");
        holder.tvAvailabilityBadge.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    /**
     * ViewHolders provide a direct reference to the views for each data item.
     */
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        final ImageView imgProduct;
        final TextView tvName;
        final TextView tvCode;
        final TextView tvAvailabilityBadge;
        final Chip chipCategory;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvCode = itemView.findViewById(R.id.tv_product_code);
            chipCategory = itemView.findViewById(R.id.chip_category);
            tvAvailabilityBadge = itemView.findViewById(R.id.tv_availability_badge);
        }
    }
}
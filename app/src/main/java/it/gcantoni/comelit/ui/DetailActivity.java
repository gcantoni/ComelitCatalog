package it.gcantoni.comelit.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import it.gcantoni.comelit.R;

/**
 * Activity responsible for displaying detailed information about a selected product.
 * Receives product data via Intent extras.
 */
public class DetailActivity extends AppCompatActivity {

    // Constants for Intent Extras to avoid typos and centralize keys
    public static final String EXTRA_NAME = "EXTRA_NAME";
    public static final String EXTRA_CODE = "EXTRA_CODE";
    public static final String EXTRA_IMAGE = "EXTRA_IMAGE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initializeUI();
    }

    /**
     * Extracts data from the Intent and populates the UI components.
     */
    private void initializeUI() {
        ImageView imgDetail = findViewById(R.id.img_detail);
        TextView tvName = findViewById(R.id.tv_detail_name);
        TextView tvCode = findViewById(R.id.tv_detail_code);

        // Extracting data with safe fallbacks
        String name = getIntent().getStringExtra(EXTRA_NAME);
        String code = getIntent().getStringExtra(EXTRA_CODE);
        String imageUrl = getIntent().getStringExtra(EXTRA_IMAGE);

        // Populating text fields with basic null checks
        tvName.setText(name != null ? name : getString(R.string.unnamed_product));
        tvCode.setText(String.format("Code: %s", code != null ? code : "N/A"));

        // Optimized Image Loading with Glide
        Glide.with(this)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache both original and resized images
                .placeholder(android.R.drawable.progress_horizontal) // Custom placeholder (as per requirements)
                .error(android.R.drawable.stat_notify_error)             // Fallback for missing images
                .centerInside()
                .into(imgDetail);
    }
}
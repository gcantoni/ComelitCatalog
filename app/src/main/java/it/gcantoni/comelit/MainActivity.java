package it.gcantoni.comelit;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;

import it.gcantoni.comelit.data.ComelitApi;
import it.gcantoni.comelit.data.LoginRequest;
import it.gcantoni.comelit.data.LoginResponse;
import it.gcantoni.comelit.data.Product;
import it.gcantoni.comelit.data.ProductDetail;
import it.gcantoni.comelit.data.ProductResponse;
import it.gcantoni.comelit.data.RetrofitClient;
import it.gcantoni.comelit.ui.ProductAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Main Activity handling the product catalog, pagination, search, and filtering.
 * Implements robust error handling for unstable API conditions.
 */
public class MainActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    // UI Components
    private RecyclerView recyclerView;
    private View progressBar, layoutError;
    private TextView tvErrorMessage, tvResultsCount;
    private Button btnRetry;
    private ImageView btnSearch, btnFilters;
    private SwipeRefreshLayout swipeRefreshLayout;

    // Logic & Data
    private ProductAdapter adapter;
    private String accessToken = "";
    private ComelitApi apiService;

    // Pagination State
    private int currentPage = 1;
    private final int pageSize = 50;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private final List<Product> allProducts = new ArrayList<>();

    // Search & Filter State
    private String currentQuery = null;
    private String currentCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize API Service via Singleton client
        apiService = RetrofitClient.getClient().create(ComelitApi.class);

        initViews();
        setupRecyclerView();
        executeLogin(); // Start by authenticating
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_products);
        progressBar = findViewById(R.id.progress_bar);
        layoutError = findViewById(R.id.layout_error);
        tvErrorMessage = findViewById(R.id.tv_error_message);
        tvResultsCount = findViewById(R.id.tv_results_count);
        btnRetry = findViewById(R.id.btn_retry);
        btnSearch = findViewById(R.id.search);
        btnFilters = findViewById(R.id.filters);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);

        // Configure Swipe-to-Refresh (addresses API blackout/retry requirement)
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#005FB8"));
        swipeRefreshLayout.setOnRefreshListener(this::resetAndReload);

        tvResultsCount.setVisibility(View.GONE);

        btnRetry.setOnClickListener(v -> {
            if (accessToken.isEmpty()) executeLogin();
            else resetAndReload();
        });

        btnSearch.setOnClickListener(v -> openSearchFilterDialog("Search Products", true));
        btnFilters.setOnClickListener(v -> openSearchFilterDialog("Filter by Category", false));
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Infinite Scrolling Logic
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && !isLoading && !isLastPage) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    // Load next page when 5 items from the bottom
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5
                            && firstVisibleItemPosition >= 0) {
                        currentPage++;
                        loadProducts(currentPage);
                    }
                }
            }
        });
    }

    /**
     * Dialog for both Search and Category filtering.
     */
    private void openSearchFilterDialog(String title, boolean isSearch) {
        final EditText editText = new EditText(this);
        editText.setText(isSearch ? (currentQuery != null ? currentQuery : "") : (currentCategory != null ? currentCategory : ""));

        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setView(editText)
                .setPositiveButton("Applica", (d, w) -> {
                    String input = editText.getText().toString().trim();
                    if (isSearch) currentQuery = input.isEmpty() ? null : input;
                    else currentCategory = input.isEmpty() ? null : input;
                    resetAndReload();
                })
                .setNeutralButton("Pulisci", (d, w) -> {
                    if (isSearch) currentQuery = null; else currentCategory = null;
                    resetAndReload();
                })
                .setNegativeButton("Cancella", null)
                .show();
    }

    private void resetAndReload() {
        currentPage = 1;
        isLastPage = false;
        loadProducts(1);
    }

    /**
     * Initial authentication. Required before fetching any product data.
     */
    private void executeLogin() {
        showLoading();

        LoginRequest credentials = new LoginRequest(BuildConfig.API_USER, BuildConfig.API_PASS);

        apiService.login(credentials).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    accessToken = response.body().getAccessToken();
                    loadProducts(1);
                } else {
                    showError("Errore di autenticazione. Verifica le credenziali in build configuration.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                showError("Errore di rete: " + t.getMessage());
            }
        });
    }

    /**
     * Fetches products from the API with current pagination and filter states.
     */
    private void loadProducts(int page) {
        if (page == 1 && !swipeRefreshLayout.isRefreshing()) showLoading();
        isLoading = true;

        apiService.getProducts("Bearer " + accessToken, page, pageSize, currentQuery, currentCategory)
                .enqueue(new Callback<ProductResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                        isLoading = false;
                        swipeRefreshLayout.setRefreshing(false);

                        if (response.isSuccessful() && response.body() != null) {
                            List<Product> newData = response.body().getProducts();

                            if (newData == null || newData.isEmpty()) {
                                if (page == 1) {
                                    allProducts.clear();
                                    adapter.setProducts(allProducts);
                                    showError("Nessun prodotto trovato per questa ricerca");
                                    tvResultsCount.setVisibility(View.GONE);
                                } else isLastPage = true;
                            } else {
                                if (page == 1) allProducts.clear();
                                allProducts.addAll(newData);
                                adapter.setProducts(allProducts);
                                updateResultsCount();
                                if (newData.size() < pageSize) isLastPage = true;
                                showSuccess();
                            }
                        } else {
                            handlePaginationError(page);
                        }
                    }
                    @Override public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                        isLoading = false;
                        swipeRefreshLayout.setRefreshing(false);
                        handlePaginationError(page);
                    }
                });
    }

    private void handlePaginationError(int page) {
        if (page == 1) showError("Connessione non riuscita. Scorri verso il basso per aggiornare o riprova");
        else {
            currentPage--; // Allow the user to try scrolling down again
            Toast.makeText(this, "Errore nel caricamento di altri prodotti", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateResultsCount() {
        if (currentQuery != null || currentCategory != null) {
            int count = allProducts.size();
            String text = count + (count == 1 ? " prodotto" : " prodotti") + " trovati";
            tvResultsCount.setText(text);
            tvResultsCount.setVisibility(View.VISIBLE);
        } else {
            tvResultsCount.setVisibility(View.GONE);
        }
    }

    /**
     * Handles product selection. Opens a BottomSheet with detailed info and
     * triggers a specific API call for extra details (documents, accessories).
     */
    @Override
    public void onProductClick(Product product) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View v = getLayoutInflater().inflate(R.layout.components_bottom_sheet, null);
        dialog.setContentView(v);

        // UI Binding inside BottomSheet
        CircularProgressIndicator loader = v.findViewById(R.id.detail_progress_bar);
        ImageView img = v.findViewById(R.id.img_detail);
        TextView tvName = v.findViewById(R.id.tv_detail_name);
        TextView tvCode = v.findViewById(R.id.tv_detail_code);
        TextView tvShortDesc = v.findViewById(R.id.tv_detail_short_description);
        TextView tvFullDesc = v.findViewById(R.id.tv_detail_description);
        Chip chipCat = v.findViewById(R.id.chip_detail_category);
        Chip chipSub = v.findViewById(R.id.chip_detail_subcategory);
        View dot = v.findViewById(R.id.view_detail_availability_dot);
        MaterialButton btnPdf = v.findViewById(R.id.btn_detail_download);
        MaterialButton btnWeb = v.findViewById(R.id.btn_detail_web);

        // Populate with data already available from the list
        tvName.setText(product.getName());
        tvCode.setText(String.format("Codice: %s", product.getCode()));
        tvShortDesc.setText(product.getShortDescription());
        tvFullDesc.setText(product.getShortDescription()); // Fallback until detail loads
        chipCat.setText(product.getCategory());

        if (product.getSubcategory() != null && !product.getSubcategory().isEmpty()) {
            chipSub.setVisibility(View.VISIBLE);
            chipSub.setText(product.getSubcategory());
        } else chipSub.setVisibility(View.GONE);

        int color = "available".equalsIgnoreCase(product.getAvailability()) ? Color.parseColor("#4CAF50") : Color.parseColor("#F44336");
        dot.setBackgroundTintList(ColorStateList.valueOf(color));
        Glide.with(this).load(product.getThumbnailUrl()).into(img);

        if (product.getSourceUrl() != null) {
            btnWeb.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(product.getSourceUrl()))));
        }

        // Fetching Extra Details
        btnPdf.setEnabled(false);
        btnPdf.setText("Caricamento...");
        dialog.show();
        loader.setVisibility(View.VISIBLE);

        apiService.getProductDetail("Bearer " + accessToken, product.getCode()).enqueue(new Callback<ProductDetail>() {
            @Override
            public void onResponse(@NonNull Call<ProductDetail> call, @NonNull Response<ProductDetail> response) {
                loader.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    ProductDetail d = response.body();
                    if (d.getDescription() != null) tvFullDesc.setText(d.getDescription());

                    // Update image if detail provides higher quality or different images
                    if (!d.getImages().isEmpty()) {
                        Glide.with(MainActivity.this).load(d.getImages().get(0).getUrl()).into(img);
                    }

                    // Handle PDF documents
                    if (!d.getDocuments().isEmpty()) {
                        String pdfUrl = d.getDocuments().get(0).getUrl();
                        btnPdf.setEnabled(true);
                        btnPdf.setText("Scarica documento");
                        btnPdf.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))));
                    } else btnPdf.setText("Nessun documento disponibile");
                }
            }
            @Override public void onFailure(@NonNull Call<ProductDetail> call, @NonNull Throwable t) {
                loader.setVisibility(View.GONE);
                btnPdf.setText("Detail loading failed");
            }
        });
    }

    // --- UI State Management Helpers ---

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
    }

    private void showSuccess() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
    }

    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        tvErrorMessage.setText(message);
    }
}
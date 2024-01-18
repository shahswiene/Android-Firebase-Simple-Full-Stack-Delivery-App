package com.example.theva;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class UserCategoryFragment extends Fragment {

    private Spinner categorySpinner;
    private ArrayList<CategoryItem> categoryList;
    private CategoryAdapter adapter;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    public UserCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_category, container, false);

        categorySpinner = view.findViewById(R.id.category_spinner);
        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(getContext(), categoryList);
        categorySpinner.setAdapter(adapter);
        recyclerView = view.findViewById(R.id.recycler_view_products);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);

        productAdapter.setOnProductClickListener(product -> {
            // Handle product click, navigate to CartFragment with product details
            navigateToCartFragment(product);
        });

        loadCategories();


        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CategoryItem selectedCategory = (CategoryItem) parent.getItemAtPosition(position);
                loadProductsForCategory(selectedCategory.getCategoryName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: handle the case where no category is selected
            }
        });

        return view;
    }

    private void loadCategories() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String name = documentSnapshot.getString("name");
                        String imageUrl = documentSnapshot.getString("image");
                        categoryList.add(new CategoryItem(name, imageUrl));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    private void loadProductsForCategory(String categoryName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .whereEqualTo("category", categoryName) // Filter by category
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    productList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String name = document.getString("name");
                        String category = document.getString("category");
                        String price = document.getString("price");
                        String quantity = document.getString("quantity");
                        String description = document.getString("description");
                        String imageUrl = document.getString("image");
                        productList.add(new Product(name, category, price, quantity, description, imageUrl));
                    }
                    productAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error loading products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToCartFragment(Product product) {
        CartFragment cartFragment = new CartFragment();

        // Pass data using Bundle (simple way)
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", product); // Make sure your Product class implements Serializable
        cartFragment.setArguments(bundle);

        // Assuming you're in an activity that can manage fragment transactions
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, cartFragment)
                .addToBackStack(null)
                .commit();
    }

}

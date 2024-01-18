package com.example.theva;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserProductFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_product, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_products);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);

        productAdapter.setOnProductClickListener(product -> {
            navigateToCartFragment(product);
        });

        loadProducts();

        return view;
    }

    private void loadProducts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                productList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("name");
                    String category = document.getString("category");
                    String price = document.getString("price");
                    String quantity = document.getString("quantity");
                    String description = document.getString("description");
                    String imageUrl = document.getString("image");
                    productList.add(new Product(name, category, price, quantity, description, imageUrl));
                }
                productAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Error loading products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToCartFragment(Product product) {
        CartFragment cartFragment = new CartFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("product", product);
        cartFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, cartFragment)
                .addToBackStack(null)
                .commit();
    }
}

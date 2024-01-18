package com.example.theva;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class CartFragment extends Fragment {

    private Product selectedProduct;
    private TextView textQuantity;
    private int currentQuantity = 0;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        textQuantity = view.findViewById(R.id.text_quantity);
        Button buttonIncrease = view.findViewById(R.id.button_increase);
        Button buttonDecrease = view.findViewById(R.id.button_decrease);
        Button buttonAddToCart = view.findViewById(R.id.button_add_to_cart);
        buttonAddToCart.setOnClickListener(v -> {
            if (selectedProduct != null && currentQuantity > 0) {
                addProductToCart();
            } else {
                Toast.makeText(getContext(), "Please select a quantity", Toast.LENGTH_SHORT).show();
            }
        });


        if (getArguments() != null) {
            selectedProduct = (Product) getArguments().getSerializable("product");
            displayProductDetails(view);
        }

        buttonIncrease.setOnClickListener(v -> {
            if (currentQuantity < Integer.parseInt(selectedProduct.getQuantity())) {
                currentQuantity++;
                textQuantity.setText(String.valueOf(currentQuantity));
            } else {
                Toast.makeText(getContext(), "Maximum quantity reached", Toast.LENGTH_SHORT).show();
            }
        });

        buttonDecrease.setOnClickListener(v -> {
            if (currentQuantity > 0) {
                currentQuantity--;
                textQuantity.setText(String.valueOf(currentQuantity));
            }
        });

        return view;
    }

    private void displayProductDetails(View view) {
        if (selectedProduct != null) {
            ((TextView) view.findViewById(R.id.product_name)).setText(selectedProduct.getName());
            ((TextView) view.findViewById(R.id.product_price)).setText(selectedProduct.getPrice());
            ((TextView) view.findViewById(R.id.product_category)).setText(selectedProduct.getCategory());
            ((TextView) view.findViewById(R.id.product_quantity_left)).setText(selectedProduct.getQuantity());
            ((TextView) view.findViewById(R.id.product_description)).setText(selectedProduct.getDescription());
            ImageView imageView = view.findViewById(R.id.product_image);
            Glide.with(this).load(selectedProduct.getImageUrl()).into(imageView);
            // Reset the quantity to 0 every time the product details are displayed
            currentQuantity = 0;
            textQuantity.setText(String.valueOf(currentQuantity));
        }
    }

    private void addProductToCart() {
        String uid = "";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
            // Use this UID to save data under the user's collection
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a Map or a POJO to represent the cart item
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("productName", selectedProduct.getName());
        cartItem.put("quantity", currentQuantity);
        cartItem.put("price", selectedProduct.getPrice());
        cartItem.put("imageUrl", selectedProduct.getImageUrl());

        // Save to Firestore
        db.collection("users").document(uid)
                .collection("cart").add(cartItem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Product added to cart", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error adding to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}

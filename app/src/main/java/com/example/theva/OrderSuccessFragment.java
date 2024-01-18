package com.example.theva;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class OrderSuccessFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Button buttonReturnHome;

    public OrderSuccessFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_success, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        buttonReturnHome = view.findViewById(R.id.button_return_home);

        if (getArguments() != null) {
            processOrder(getArguments());
        }

        buttonReturnHome.setOnClickListener(v -> navigateToHome());

        return view;
    }

    private void processOrder(Bundle bundle) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            String totalAmount = String.valueOf(bundle.getDouble("totalAmount"));
            String deliveryAddress = bundle.getString("deliveryAddress");
            String orderDateTime = bundle.getString("deliveryDateTime");
            String paymentMethod = bundle.getString("paymentMethod");
            List<CartItem> cartItems = (List<CartItem>) bundle.getSerializable("cartItems");

            Order order = new Order(uid, deliveryAddress, orderDateTime, totalAmount, paymentMethod, "Confirmed", cartItems);

            db.collection("users").document(uid).collection("orders")
                    .add(order)
                    .addOnSuccessListener(documentReference -> {
                        updateProductQuantities(cartItems);
                        clearUserCart(uid);
                        Toast.makeText(getContext(), "Order placed successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error processing order", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearUserCart(String userId) {
        db.collection("users").document(userId).collection("cart").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    document.getReference().delete();
                }
            } else {
                Toast.makeText(getContext(), "Error clearing cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProductQuantities(List<CartItem> cartItems) {
        for (CartItem item : cartItems) {
            final String productName = item.getProductName();
            final int quantityToDeduct = item.getQuantity();

            db.collection("products")
                    .whereEqualTo("name", productName)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            try {
                                String quantityString = documentSnapshot.getString("quantity");
                                Long currentQuantity = Long.parseLong(quantityString);

                                // Check if the product has enough quantity
                                if (currentQuantity >= quantityToDeduct) {
                                    Long newQuantity = currentQuantity - quantityToDeduct;
                                    documentSnapshot.getReference()
                                            .update("quantity", newQuantity.toString())
                                            .addOnSuccessListener(aVoid -> Log.d("UpdateQuantity", "Product quantity updated for " + productName))
                                            .addOnFailureListener(e -> Log.e("UpdateQuantity", "Error updating product quantity for " + productName, e));
                                } else {
                                    Toast.makeText(getContext(), "Insufficient stock for " + productName, Toast.LENGTH_SHORT).show();
                                }
                            } catch (NumberFormatException e) {
                                Log.e("updateProductQuantities", "Error parsing quantity as number for " + productName, e);
                            }
                        } else {
                            Toast.makeText(getContext(), "Product not found: " + productName, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error accessing product: " + productName, Toast.LENGTH_SHORT).show());
        }
    }

    private void navigateToHome() {
        UserHomeFragment userHomeFragment = new UserHomeFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, userHomeFragment)
                .commit();
    }

}

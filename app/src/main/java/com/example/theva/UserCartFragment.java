package com.example.theva;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class UserCartFragment extends Fragment implements CartAdapter.OnItemRemoveListener {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private TextView totalAmountText;
    private Button checkoutButton;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_cart, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_cart);
        totalAmountText = view.findViewById(R.id.total_amount_text);
        checkoutButton = view.findViewById(R.id.checkout_button);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItemList, this);
        recyclerView.setAdapter(cartAdapter);

        db = FirebaseFirestore.getInstance();
        loadCartItems();

        checkoutButton.setOnClickListener(v -> navigateToCheckOutFragment());

        return view;
    }

    private void loadCartItems() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            db.collection("users").document(uid).collection("cart")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        cartItemList.clear();
                        double totalAmount = 0;
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            CartItem item = document.toObject(CartItem.class);
                            cartItemList.add(item);
                            totalAmount += item.getQuantity() * Double.parseDouble(item.getPrice());
                        }
                        totalAmountText.setText(String.format("MYR %.2f", totalAmount));
                        cartAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error loading cart items", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onItemRemove(int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            CartItem itemToRemove = cartItemList.get(position);

            db.collection("users").document(uid).collection("cart")
                    .whereEqualTo("productName", itemToRemove.getProductName())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            db.collection("users").document(uid).collection("cart")
                                    .document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Item removed from cart", Toast.LENGTH_SHORT).show();
                                        cartItemList.remove(position);
                                        cartAdapter.notifyItemRemoved(position);
                                        updateTotalAmount(); // Update the total amount after removing an item
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error removing item", Toast.LENGTH_SHORT).show());
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error finding item to remove", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onQuantityChange() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            for (CartItem item : cartItemList) {
                db.collection("users").document(uid).collection("cart")
                        .whereEqualTo("productName", item.getProductName())
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                db.collection("users").document(uid).collection("cart")
                                        .document(document.getId())
                                        .update("quantity", item.getQuantity())
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Quantity updated", Toast.LENGTH_SHORT).show();
                                            updateTotalAmount(); // Update the total amount after changing the quantity
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Error updating quantity", Toast.LENGTH_SHORT).show());
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Error finding item to update", Toast.LENGTH_SHORT).show());
            }
        }
    }

    private void updateTotalAmount() {
        double totalAmount = 0;
        for (CartItem item : cartItemList) {
            totalAmount += item.getQuantity() * Double.parseDouble(item.getPrice());
        }
        totalAmountText.setText(String.format("MYR %.2f", totalAmount));
    }

    private void navigateToCheckOutFragment() {
        CheckOutFragment checkOutFragment = new CheckOutFragment();

        // Calculate total amount
        double totalAmount = 0;
        for (CartItem item : cartItemList) {
            totalAmount += item.getQuantity() * Double.parseDouble(item.getPrice());
        }

        // Create a bundle and put the total amount in it
        Bundle bundle = new Bundle();
        bundle.putDouble("totalAmount", totalAmount);
        checkOutFragment.setArguments(bundle);

        // Navigate to CheckOutFragment
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, checkOutFragment)
                .addToBackStack(null)
                .commit();
    }


}

package com.example.theva;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserViewOrderFragment extends Fragment {

    private TextView textViewOrderId, textViewUserId, textViewItems, textViewOrderDateTime, textViewPaymentMethod, textViewAddress, textViewStatus, textViewTotalAmount;
    private View divider1, divider2, divider3;
    private FirebaseFirestore db;
    private String orderId;

    public UserViewOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_view_order, container, false);

        // Initialize Views
        textViewOrderId = view.findViewById(R.id.textView_orderId);
        textViewUserId = view.findViewById(R.id.textView_userId);
        textViewItems = view.findViewById(R.id.textView_items);
        textViewOrderDateTime = view.findViewById(R.id.textView_orderDateTime);
        textViewPaymentMethod = view.findViewById(R.id.textView_paymentMethod);
        textViewAddress = view.findViewById(R.id.textView_address);
        textViewStatus = view.findViewById(R.id.textView_status);
        textViewTotalAmount = view.findViewById(R.id.textView_totalAmount);

        // Initialize Divider Views
        divider1 = view.findViewById(R.id.view_divider1);
        divider2 = view.findViewById(R.id.view_divider2);
        divider3 = view.findViewById(R.id.view_divider3);

        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            orderId = getArguments().getString("orderId");
            loadOrderDetails();
        }

        return view;
    }

    private void loadOrderDetails() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userId).collection("orders").document(orderId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Order order = documentSnapshot.toObject(Order.class);
                        displayOrderDetails(order);
                        updateOrderTrackingSteps(order.getStatus());
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors here
                });
    }

    private void displayOrderDetails(Order order) {
        if (order != null) {
            textViewOrderId.setText(orderId);
            textViewUserId.setText(order.getUserId());
            textViewItems.setText(buildItemsString(order.getItems()));
            textViewOrderDateTime.setText(order.getOrderDateTime());
            textViewPaymentMethod.setText(order.getPaymentMethod());
            textViewAddress.setText(order.getAddress());
            textViewStatus.setText(order.getStatus());
            textViewTotalAmount.setText("MYR " + order.getTotalAmount());
        }
    }

    private String buildItemsString(List<CartItem> items) {
        StringBuilder itemsString = new StringBuilder();
        for (CartItem item : items) {
            itemsString.append(item.getProductName()).append(" - Qty: ").append(item.getQuantity()).append("\n");
        }
        return itemsString.toString();
    }

    private void updateOrderTrackingSteps(String status) {
        int activeColor = getResources().getColor(R.color.green); // Replace with your active color
        int defaultColor = getResources().getColor(R.color.black); // Replace with your default color

        // Update colors based on the status
        switch (status) {
            case "Confirmed":
                divider1.setBackgroundColor(defaultColor);
                divider2.setBackgroundColor(defaultColor);
                divider3.setBackgroundColor(defaultColor);
                break;
            case "Packed":
                divider1.setBackgroundColor(activeColor);
                divider2.setBackgroundColor(defaultColor);
                divider3.setBackgroundColor(defaultColor);
                break;
            case "Shipped":
                divider1.setBackgroundColor(activeColor);
                divider2.setBackgroundColor(activeColor);
                divider3.setBackgroundColor(defaultColor);
                break;
            case "Delivered":
                divider1.setBackgroundColor(activeColor);
                divider2.setBackgroundColor(activeColor);
                divider3.setBackgroundColor(activeColor);
                break;
            default:
                // Handle default case
                break;
        }
    }
}

package com.example.theva;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminViewOrderFragment extends Fragment {

    private TextView textViewOrderId, textViewUserId, textViewItems, textViewOrderDateTime, textViewPaymentMethod, textViewAddress, textViewTotalAmount;
    private Spinner spinnerOrderStatus;
    private FirebaseFirestore db;
    private String orderId;
    private View divider1, divider2, divider3;

    public AdminViewOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_view_order, container, false);

        // Initialize Views
        textViewOrderId = view.findViewById(R.id.textView_orderId);
        textViewUserId = view.findViewById(R.id.textView_userId);
        textViewItems = view.findViewById(R.id.textView_items);
        textViewOrderDateTime = view.findViewById(R.id.textView_orderDateTime);
        textViewPaymentMethod = view.findViewById(R.id.textView_paymentMethod);
        textViewAddress = view.findViewById(R.id.textView_address);
        textViewTotalAmount = view.findViewById(R.id.textView_totalAmount);
        spinnerOrderStatus = view.findViewById(R.id.spinner_orderStatus);

        // Initialize Divider Views
        divider1 = view.findViewById(R.id.view_divider1);
        divider2 = view.findViewById(R.id.view_divider2);
        divider3 = view.findViewById(R.id.view_divider3);


        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            orderId = getArguments().getString("orderId");
            loadOrderDetails();
        }

        spinnerOrderStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newStatus = parent.getItemAtPosition(position).toString();
                updateOrderStatusInDatabase(newStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle case where nothing is selected
            }
        });



        return view;
    }

    private void loadOrderDetails() {
        db.collectionGroup("orders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Order order = documentSnapshot.toObject(Order.class);
                        if (order != null && orderId.equals(documentSnapshot.getId())) {
                            displayOrderDetails(order);
                            break; // Break out of the loop once the correct order is found
                        }
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
            textViewTotalAmount.setText("MYR " + order.getTotalAmount());
            // Set the current status in the spinner
            setSpinnerToValue(spinnerOrderStatus, order.getStatus());
            updateOrderTrackingSteps(order.getStatus());
        }
    }

    private String buildItemsString(List<CartItem> items) {
        StringBuilder itemsString = new StringBuilder();
        for (CartItem item : items) {
            itemsString.append(item.getProductName()).append(" - Qty: ").append(item.getQuantity()).append("\n");
        }
        return itemsString.toString();
    }

    private void setSpinnerToValue(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void updateOrderStatusInDatabase(String newStatus) {
        String userId = textViewUserId.getText().toString();

        if (!userId.isEmpty()) {
            db.collection("users").document(userId).collection("orders").document(orderId)
                    .update("status", newStatus)
                    .addOnSuccessListener(aVoid -> Log.d("AdminViewOrderFragment", "Order status updated successfully"))
                    .addOnFailureListener(e -> Log.e("AdminViewOrderFragment", "Error updating order status", e));
        } else {
            Log.e("AdminViewOrderFragment", "UserId is empty, cannot update status");
        }
        updateOrderTrackingSteps(newStatus);
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

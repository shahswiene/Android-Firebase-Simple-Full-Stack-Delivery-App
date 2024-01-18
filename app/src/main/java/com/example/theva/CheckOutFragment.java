package com.example.theva;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CheckOutFragment extends Fragment {

    private TextInputEditText dateAndTimePickerEditText;
    private EditText editTextAddress;
    private TextView textViewTotal;
    private RadioGroup radioGroupPayment;
    private Button buttonOrder;
    private FirebaseFirestore db;
    private double totalAmount; // Assuming you pass this value when navigating to this fragment

    public CheckOutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_out, container, false);

        db = FirebaseFirestore.getInstance();
        dateAndTimePickerEditText = view.findViewById(R.id.textInputEditText_date_time);
        editTextAddress = view.findViewById(R.id.editText_address);
        textViewTotal = view.findViewById(R.id.textView_total);
        radioGroupPayment = view.findViewById(R.id.radioGroup_payment);
        buttonOrder = view.findViewById(R.id.button_order);

        // Retrieve the total amount from arguments
        if (getArguments() != null) {
            totalAmount = getArguments().getDouble("totalAmount", 0.0);
        }
        textViewTotal.setText(String.format("Total: MYR %.2f", totalAmount));

        loadUserAddress();
        dateAndTimePickerEditText.setOnClickListener(v -> showDateTimePicker());
        buttonOrder.setOnClickListener(v -> fetchCartItemsAndProcessOrder());

        return view;
    }

    private void loadUserAddress() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            db.collection("users").document(uid).collection("accountDetails")
                    .document("userInformation")
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String address = documentSnapshot.getString("houseAddress");
                            editTextAddress.setText(address);
                        }
                    })
                    .addOnFailureListener(e -> editTextAddress.setText("Error loading address"));
        }
    }

    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (DatePicker view, int year1, int monthOfYear, int dayOfMonth) ->
                        showTimePicker(year1, monthOfYear, dayOfMonth), year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker(int year, int month, int day) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                (TimePicker view, int hourOfDay, int minute1) -> {
                    String dateTime = String.format("%04d-%02d-%02d %02d:%02d", year, month + 1, day, hourOfDay, minute1);
                    dateAndTimePickerEditText.setText(dateTime);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void fetchCartItemsAndProcessOrder() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            db.collection("users").document(uid).collection("cart")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<CartItem> cartItems = new ArrayList<>();
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            CartItem item = snapshot.toObject(CartItem.class);
                            if (item != null) {
                                cartItems.add(item);
                            }
                        }
                        processOrder(cartItems); // Process order with the retrieved cart items
                    })
                    .addOnFailureListener(e -> {
                        // Handle the error
                        Toast.makeText(getContext(), "Error fetching cart items", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void processOrder(List<CartItem> cartItems) {
        int selectedPaymentMethodId = radioGroupPayment.getCheckedRadioButtonId();
        RadioButton selectedPaymentMethod = getView().findViewById(selectedPaymentMethodId);
        String paymentMethod = selectedPaymentMethod != null ? selectedPaymentMethod.getText().toString() : "Unknown";
        String deliveryDateTime = dateAndTimePickerEditText.getText().toString();
        if (deliveryDateTime.isEmpty()) {
            Toast.makeText(getContext(), "Please select a date and time", Toast.LENGTH_SHORT).show();
            return; // Don't proceed if date and time are not set
        }

        Bundle bundle = new Bundle();
        bundle.putDouble("totalAmount", totalAmount);
        bundle.putString("deliveryDateTime", deliveryDateTime);
        bundle.putString("deliveryAddress", editTextAddress.getText().toString());
        bundle.putString("paymentMethod", paymentMethod);
        bundle.putSerializable("cartItems", (Serializable) cartItems);

        if (selectedPaymentMethod != null && selectedPaymentMethod.getId() == R.id.radioButton_credit_card) {
            // Navigate to CardFragment
            CardFragment cardFragment = new CardFragment();
            cardFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, cardFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            // Navigate directly to OrderSuccessFragment
            OrderSuccessFragment orderSuccessFragment = new OrderSuccessFragment();
            orderSuccessFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, orderSuccessFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}

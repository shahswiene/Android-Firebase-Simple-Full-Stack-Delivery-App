package com.example.theva;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class CardFragment extends Fragment {

    private EditText editTextCardNumber, editTextExpiryDate, editTextCvv;
    private Button buttonPay;

    public CardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card, container, false);

        editTextCardNumber = view.findViewById(R.id.editTextCardNumber);
        editTextExpiryDate = view.findViewById(R.id.editTextExpiryDate);
        editTextCvv = view.findViewById(R.id.editTextCvv);
        buttonPay = view.findViewById(R.id.button_pay);

        buttonPay.setOnClickListener(v -> validateAndProceed());

        return view;
    }

    private void validateAndProceed() {
        String cardNumber = editTextCardNumber.getText().toString();
        String expiryDate = editTextExpiryDate.getText().toString();
        String cvv = editTextCvv.getText().toString();

        if (TextUtils.isEmpty(cardNumber) || TextUtils.isEmpty(expiryDate) || TextUtils.isEmpty(cvv)) {
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
        } else {
            // Assume validation is successful and proceed to OrderSuccessFragment
            Bundle bundle = getArguments(); // Get the existing bundle
            if (bundle != null) {
                OrderSuccessFragment orderSuccessFragment = new OrderSuccessFragment();
                orderSuccessFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, orderSuccessFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }
}

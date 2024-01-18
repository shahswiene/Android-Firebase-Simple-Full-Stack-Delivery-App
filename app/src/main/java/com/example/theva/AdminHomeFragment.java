package com.example.theva;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdminHomeFragment extends Fragment {

    private TextView tvAmountToday, tvAmountMonthly, tvAmountLifetime, tvAmountCategories;
    private FirebaseFirestore db;
    private Button signOutButton;
    private FirebaseAuth firebaseAuth;


    public AdminHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // Initialize TextViews
        tvAmountToday = view.findViewById(R.id.tv_amount_today);
        tvAmountMonthly = view.findViewById(R.id.tv_amount_monthly);
        tvAmountLifetime = view.findViewById(R.id.tv_amount_lifetime);
        tvAmountCategories = view.findViewById(R.id.tv_amount_categories);
        firebaseAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        loadDashboardData();

        signOutButton = view.findViewById(R.id.btn_sign_out);

        // Set up click listener for the Sign Out button
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });


        return view;
    }

    private void loadDashboardData() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        String todayString = dateFormat.format(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String monthStartString = dateFormat.format(calendar.getTime());

        db.collectionGroup("orders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalToday = 0.0;
                    double totalMonth = 0.0;
                    double totalLifetime = 0.0;
                    int orderCount = queryDocumentSnapshots.size();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String orderDateTime = document.getString("orderDateTime");
                        String totalAmountString = document.getString("totalAmount");

                        if (orderDateTime != null && totalAmountString != null) {
                            try {
                                Date orderDate = dateFormat.parse(orderDateTime.substring(0, 10));
                                double totalAmount = Double.parseDouble(totalAmountString);

                                // Today's sales
                                if (orderDate != null && dateFormat.format(orderDate).equals(todayString)) {
                                    totalToday += totalAmount;
                                }

                                // Monthly sales
                                if (orderDate != null && orderDate.after(dateFormat.parse(monthStartString))) {
                                    totalMonth += totalAmount;
                                }

                                // Lifetime sales
                                totalLifetime += totalAmount;
                            } catch (ParseException | NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    tvAmountToday.setText(String.format(Locale.getDefault(), "%.2f MYR", totalToday));
                    tvAmountMonthly.setText(String.format(Locale.getDefault(), "%.2f MYR", totalMonth));
                    tvAmountLifetime.setText(String.valueOf(orderCount));
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminHomeFragment", "Error fetching orders", e);
                });

        // Load number of categories
        loadCategoryCount();
    }

    private void loadCategoryCount() {
        db.collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int categoryCount = queryDocumentSnapshots.size();
                    tvAmountCategories.setText(String.valueOf(categoryCount));
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminHomeFragment", "Error fetching categories", e);
                });
    }

    private void signOut() {
        firebaseAuth.signOut();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish(); // Finish the current activity
    }
}

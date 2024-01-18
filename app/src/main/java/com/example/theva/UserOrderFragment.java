package com.example.theva;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserOrderFragment extends Fragment {

    private RecyclerView recyclerViewOrders;
    private OrderAdapter orderAdapter;
    private List<OrderWithId> orderList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public UserOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_order, container, false);

        recyclerViewOrders = view.findViewById(R.id.recyclerView_orders);
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewOrders.setLayoutManager(layoutManager);
        recyclerViewOrders.setAdapter(orderAdapter);

        orderAdapter.setOnOrderClickListener(orderWithId -> {
            // Handle order click here
            Bundle bundle = new Bundle();
            bundle.putString("orderId", orderWithId.getOrderId());
            UserViewOrderFragment userViewOrderFragment = new UserViewOrderFragment();
            userViewOrderFragment.setArguments(bundle);

            // Assuming you are within a FragmentActivity
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, userViewOrderFragment)
                    .addToBackStack(null)
                    .commit();
        });

        loadOrders();

        return view;
    }

    private void loadOrders() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            db.collection("users").document(uid).collection("orders")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<OrderWithId> ordersWithId = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Order order = documentSnapshot.toObject(Order.class);
                            ordersWithId.add(new OrderWithId(documentSnapshot.getId(), order));
                        }
                        orderAdapter.setOrders(ordersWithId);
                        orderAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("UserOrderFragment", "Error loading orders", e);
                        Toast.makeText(getContext(), "Error loading orders", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}

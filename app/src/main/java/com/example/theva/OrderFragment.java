    package com.example.theva;

    import android.os.Bundle;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;

    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentManager;
    import androidx.fragment.app.FragmentTransaction;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import com.google.firebase.firestore.DocumentSnapshot;
    import com.google.firebase.firestore.FirebaseFirestore;

    import java.util.ArrayList;
    import java.util.List;

    public class OrderFragment extends Fragment {

        private RecyclerView recyclerViewOrders;
        private OrderAdapter orderAdapter;
        private FirebaseFirestore db;

        public OrderFragment() {
            // Required empty public constructor
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_order, container, false);

            recyclerViewOrders = view.findViewById(R.id.recyclerView_orders);
            db = FirebaseFirestore.getInstance();

            // Initialize the adapter
            orderAdapter = new OrderAdapter(new ArrayList<>());
            recyclerViewOrders.setAdapter(orderAdapter);
            recyclerViewOrders.setLayoutManager(new LinearLayoutManager(getContext()));

            // Set up click listener
            orderAdapter.setOnOrderClickListener(orderWithId -> {
                navigateToOrderDetail(orderWithId.getOrderId());
            });

            loadOrders();

            return view;
        }

        private void loadOrders() {
            db.collectionGroup("orders")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<OrderWithId> ordersWithId = new ArrayList<>();
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            Order order = snapshot.toObject(Order.class);
                            String orderId = snapshot.getId();
                            ordersWithId.add(new OrderWithId(orderId, order));
                        }
                        orderAdapter.setOrders(ordersWithId);
                    })
                    .addOnFailureListener(e -> Log.e("OrderFragment", "Error fetching orders", e));
        }

        private void navigateToOrderDetail(String orderId) {
            AdminViewOrderFragment adminViewOrderFragment = new AdminViewOrderFragment();
            Bundle args = new Bundle();
            args.putString("orderId", orderId);
            adminViewOrderFragment.setArguments(args);

            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager != null) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, adminViewOrderFragment);
                fragmentTransaction.addToBackStack(null); // Add to back stack for proper navigation
                fragmentTransaction.commit();
            }
        }
    }

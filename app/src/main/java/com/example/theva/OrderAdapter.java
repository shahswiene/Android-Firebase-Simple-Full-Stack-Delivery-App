package com.example.theva;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<OrderWithId> orderList;
    private OnOrderClickListener onOrderClickListener;

    public OrderAdapter(List<OrderWithId> orderList) {
        this.orderList = orderList;
    }

    public interface OnOrderClickListener {
        void onOrderClicked(OrderWithId orderWithId);
    }

    public void setOnOrderClickListener(OnOrderClickListener onOrderClickListener) {
        this.onOrderClickListener = onOrderClickListener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderWithId orderWithId = orderList.get(position);
        Order order = orderWithId.getOrder();

        holder.textViewOrderId.setText("Order ID: " + orderWithId.getOrderId());
        holder.textViewPaymentMethod.setText("Payment Method: " + order.getPaymentMethod());
        holder.textViewStatus.setText("Status: " + order.getStatus());
        holder.textViewNumItems.setText("Items: " + order.getItems().size());

        holder.itemView.setOnClickListener(v -> {
            if (onOrderClickListener != null) {
                onOrderClickListener.onOrderClicked(orderWithId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public void setOrders(List<OrderWithId> ordersWithId) {
        this.orderList = ordersWithId;
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewOrderId, textViewPaymentMethod, textViewStatus, textViewNumItems;

        public OrderViewHolder(View view) {
            super(view);
            textViewOrderId = view.findViewById(R.id.textView_order_id);
            textViewPaymentMethod = view.findViewById(R.id.textView_payment_method);
            textViewStatus = view.findViewById(R.id.textView_status);
            textViewNumItems = view.findViewById(R.id.textView_num_items);
        }
    }
}

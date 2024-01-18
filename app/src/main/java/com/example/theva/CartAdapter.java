package com.example.theva;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItemList;
    private OnItemRemoveListener removeListener;
    private FirebaseFirestore db;

    public CartAdapter(List<CartItem> cartItemList, OnItemRemoveListener removeListener) {
        this.cartItemList = cartItemList;
        this.removeListener = removeListener;
        this.db = FirebaseFirestore.getInstance();
    }

    public interface OnItemRemoveListener {
        void onItemRemove(int position);
        void onQuantityChange();
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);
        holder.productName.setText(item.getProductName());
        holder.productPrice.setText("MYR " + item.getPrice());
        holder.textQuantity.setText(String.valueOf(item.getQuantity()));
        Glide.with(holder.itemView.getContext()).load(item.getImageUrl()).into(holder.productImage);

        holder.buttonRemove.setOnClickListener(v -> {
            if (removeListener != null) {
                removeListener.onItemRemove(position);
            }
        });

        holder.buttonIncrease.setOnClickListener(v -> {
            db.collection("products")
                    .whereEqualTo("name", item.getProductName())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            int maxQuantity = Integer.parseInt(queryDocumentSnapshots.getDocuments().get(0).getString("quantity"));
                            if (item.getQuantity() < maxQuantity) {
                                item.setQuantity(item.getQuantity() + 1);
                                holder.textQuantity.setText(String.valueOf(item.getQuantity()));
                                if (removeListener != null) {
                                    removeListener.onQuantityChange();
                                }
                            } else {
                                Toast.makeText(holder.itemView.getContext(), "Maximum quantity reached", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(holder.itemView.getContext(), "Error updating quantity", Toast.LENGTH_SHORT).show());
        });

        holder.buttonDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                holder.textQuantity.setText(String.valueOf(item.getQuantity()));
                if (removeListener != null) {
                    removeListener.onQuantityChange();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, textQuantity;
        ImageView productImage, buttonRemove;
        Button buttonIncrease, buttonDecrease;

        CartViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            textQuantity = itemView.findViewById(R.id.text_quantity);
            productImage = itemView.findViewById(R.id.product_image);
            buttonRemove = itemView.findViewById(R.id.button_remove);
            buttonIncrease = itemView.findViewById(R.id.button_increase);
            buttonDecrease = itemView.findViewById(R.id.button_decrease);
        }
    }
}

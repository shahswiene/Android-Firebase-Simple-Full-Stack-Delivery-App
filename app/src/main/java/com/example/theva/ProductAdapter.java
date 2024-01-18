package com.example.theva;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<Product> productList;
    private OnProductClickListener listener;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.textViewName.setText(product.getName());
        holder.textViewPrice.setText("Price: " + product.getPrice());
        holder.textViewQuantity.setText("Quantity: " + product.getQuantity());
        holder.textViewDescription.setText("Description: " + product.getDescription());
        Glide.with(holder.imageView.getContext()).load(product.getImageUrl()).into(holder.imageView);

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewPrice, textViewQuantity, textViewDescription;
        ImageView imageView;
        CardView cardView;

        ProductViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_product_name);
            textViewPrice = itemView.findViewById(R.id.text_view_product_price);
            textViewQuantity = itemView.findViewById(R.id.text_view_product_quantity);
            textViewDescription = itemView.findViewById(R.id.text_view_product_description);
            imageView = itemView.findViewById(R.id.image_view_product);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}

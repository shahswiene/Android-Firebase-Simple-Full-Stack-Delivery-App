package com.example.theva;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ProductFragment extends Fragment {

    public ProductFragment() {
        // Required empty public constructor
    }

    public static ProductFragment newInstance() {
        return new ProductFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        Button btnAddProduct = view.findViewById(R.id.btn_add_product);
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddProductFragment();
            }
        });


        Button btnViewProduct = view.findViewById(R.id.btn_view_product);
        btnViewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToViewProductFragment();
            }
        });

        return view;
    }

    private void goToAddProductFragment() {
        AddProductFragment addProductFragment = new AddProductFragment();
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, addProductFragment); // Ensure you have a container (FrameLayout or similar) in your activity's layout
            fragmentTransaction.addToBackStack(null); // Optional, adds the transaction to the back stack
            fragmentTransaction.commit();
        }
    }

    private void goToViewProductFragment() {
        ViewProductFragment viewProductFragment = new ViewProductFragment();
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, viewProductFragment); // Ensure you have a container (FrameLayout or similar) in your activity's layout
            fragmentTransaction.addToBackStack(null); // Optional, adds the transaction to the back stack
            fragmentTransaction.commit();
        }
    }
}

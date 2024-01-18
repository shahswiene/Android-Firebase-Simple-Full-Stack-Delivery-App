package com.example.theva;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import android.widget.LinearLayout;
import android.widget.HorizontalScrollView;


public class UserHomeFragment extends Fragment {

    private ViewFlipper viewFlipper;
    private LinearLayout categoriesContainer, newArrivalContainer;


    public UserHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);

        viewFlipper = view.findViewById(R.id.view_flipper);
        categoriesContainer = view.findViewById(R.id.categories_container);
        newArrivalContainer = view.findViewById(R.id.new_arrival_container);
        TextView textViewSeeAllNewArrival = view.findViewById(R.id.textView_see_all_new_arrival);
        textViewSeeAllNewArrival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to navigate to ViewProductFragment
                goToViewProductFragment();
            }
        });

        TextView textView_view_all = view.findViewById(R.id.textView_view_all);
        textView_view_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to navigate to ViewProductFragment
                goToViewCategoryFragment();
            }
        });

        loadProductImages();
        loadCategories();
        loadNewArrival();

        return view;
    }

    private void loadProductImages() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String imageUrl = documentSnapshot.getString("image");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            addImageToViewFlipper(imageUrl);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error loading images: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void addImageToViewFlipper(String imageUrl) {
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Set layout parameters for ImageView
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        imageView.setLayoutParams(layoutParams);

        Glide.with(this).load(imageUrl).into(imageView);
        viewFlipper.addView(imageView);
    }

    private void loadCategories() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    categoriesContainer.removeAllViews(); // Clear existing views
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String imageUrl = documentSnapshot.getString("image");
                        String categoryName = documentSnapshot.getString("name");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            addCategoryToContainer(imageUrl, categoryName);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error loading categories: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void addCategoryToContainer(String imageUrl, String categoryName) {
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        // Assuming you have some drawable resource that is a circle or use a library to make ImageView circular
        imageView.setBackgroundResource(R.drawable.rounded_background); // Replace with actual drawable resource

        int size = getResources().getDimensionPixelSize(R.dimen.category_icon_size); // Define this dimension in your dimens.xml
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
        layoutParams.setMargins(10, 10, 10, 10); // Add suitable margins
        imageView.setLayoutParams(layoutParams);

        // Use Glide to load the image into the ImageView
        Glide.with(this).load(imageUrl).into(imageView);

        imageView.setContentDescription(categoryName); // Accessibility for category name

        // Add click listener for each category
        imageView.setOnClickListener(v -> {
            // Handle the category click event
            Toast.makeText(getActivity(), "Clicked on " + categoryName, Toast.LENGTH_SHORT).show();
        });

        categoriesContainer.addView(imageView); // Add the imageView to the container
    }

    private void loadNewArrival() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    newArrivalContainer.removeAllViews(); // Clear existing views
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String imageUrl = documentSnapshot.getString("image");
                        String categoryName = documentSnapshot.getString("name");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            addNewArrivalToContainer(imageUrl, categoryName);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error loading categories: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void addNewArrivalToContainer(String imageUrl, String categoryName) {
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setBackgroundResource(R.drawable.new_arrival_background);

        int size = getResources().getDimensionPixelSize(R.dimen.new_arrival_image_size); // Define this dimension in your dimens.xml
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
        layoutParams.setMargins(10, 10, 10, 10); // Add suitable margins
        imageView.setLayoutParams(layoutParams);

        // Use Glide to load the image into the ImageView
        Glide.with(this).load(imageUrl).into(imageView);

        imageView.setContentDescription(categoryName); // Accessibility for category name

        // Add click listener for each category
        imageView.setOnClickListener(v -> {
            // Handle the category click event
            Toast.makeText(getActivity(), "Clicked on " + categoryName, Toast.LENGTH_SHORT).show();
        });

        newArrivalContainer.addView(imageView); // Add the imageView to the container
    }

    private void goToViewProductFragment() {
        UserProductFragment userProductFragment = new UserProductFragment();
        FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getFragmentManager() if you're using androidx
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, userProductFragment); // Replace 'R.id.fragment_container' with the ID of the container in your activity's layout
            fragmentTransaction.addToBackStack(null); // Optional, adds the transaction to the back stack
            fragmentTransaction.commit();
        }
    }

    private void goToViewCategoryFragment() {
        UserCategoryFragment userCategoryFragment = new UserCategoryFragment();
        FragmentManager fragmentManager = getParentFragmentManager(); // Use getParentFragmentManager() instead of getFragmentManager() if you're using androidx
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, userCategoryFragment); // Replace 'R.id.fragment_container' with the ID of the container in your activity's layout
            fragmentTransaction.addToBackStack(null); // Optional, adds the transaction to the back stack
            fragmentTransaction.commit();
        }
    }

}


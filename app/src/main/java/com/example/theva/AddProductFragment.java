package com.example.theva;

import android.app.Activity;
import android.content.Intent;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddProductFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Uri imageUri;
    private Spinner categorySpinner;
    private EditText editProductName, editProductPrice, editProductQuantity, editProductDescription;
    private ArrayAdapter<String> categoriesAdapter;
    private List<String> categoriesList = new ArrayList<>();

    public AddProductFragment() {
        // Required empty public constructor
    }

    public static AddProductFragment newInstance() {
        AddProductFragment fragment = new AddProductFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        imageView = view.findViewById(R.id.image_product);
        categorySpinner = view.findViewById(R.id.spinner_category);
        editProductName = view.findViewById(R.id.edit_product_name);
        editProductPrice = view.findViewById(R.id.edit_product_price);
        editProductQuantity = view.findViewById(R.id.edit_product_quantity);
        editProductDescription = view.findViewById(R.id.edit_product_description);
        Button saveProductButton = view.findViewById(R.id.btn_save_product);

        imageView.setOnClickListener(v -> openFileChooser());
        saveProductButton.setOnClickListener(v -> saveProduct());

        categoriesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoriesList);
        categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoriesAdapter);

        loadCategories();

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void loadCategories() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("categories").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String categoryName = document.getString("name");
                    categoriesList.add(categoryName);
                }
                categoriesAdapter.notifyDataSetChanged();
            } else {
                // Handle error
            }
        });
    }

    private void saveProduct() {
        String productName = editProductName.getText().toString().trim();
        String productPrice = editProductPrice.getText().toString().trim();
        String productQuantity = editProductQuantity.getText().toString().trim();
        String productDescription = editProductDescription.getText().toString().trim();
        String selectedCategory = categorySpinner.getSelectedItem().toString();

        if (!productName.isEmpty() && !productPrice.isEmpty() && !productQuantity.isEmpty() && !productDescription.isEmpty() && imageUri != null) {
            uploadProductImage(productName, productPrice, productQuantity, productDescription, selectedCategory);
        } else {
            Toast.makeText(getActivity(), "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadProductImage(String productName, String productPrice, String productQuantity, String productDescription, String selectedCategory) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("product_images");
        StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        saveProductToFirestore(productName, productPrice, productQuantity, productDescription, selectedCategory, imageUrl);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProductToFirestore(String productName, String productPrice, String productQuantity, String productDescription, String selectedCategory, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> product = new HashMap<>();
        product.put("name", productName);
        product.put("price", productPrice);
        product.put("quantity", productQuantity);
        product.put("description", productDescription);
        product.put("category", selectedCategory);
        product.put("image", imageUrl);

        db.collection("products").add(product).addOnSuccessListener(documentReference -> {
            Toast.makeText(getActivity(), "Product Added", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}

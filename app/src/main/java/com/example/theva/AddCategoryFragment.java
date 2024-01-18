package com.example.theva;

import android.app.Activity;
import android.content.Intent;
import android.content.ContentResolver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddCategoryFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Uri imageUri;
    private EditText categoryInput;
    private LinearLayout categoriesContainer;

    public AddCategoryFragment() {
        // Required empty public constructor
    }

    public static AddCategoryFragment newInstance() {
        AddCategoryFragment fragment = new AddCategoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_category, container, false);

        imageView = view.findViewById(R.id.image_category);
        categoryInput = view.findViewById(R.id.edit_category);
        Button saveCategoryButton = view.findViewById(R.id.btn_save_category);
        categoriesContainer = view.findViewById(R.id.container_categories);

        imageView.setOnClickListener(v -> openFileChooser());

        saveCategoryButton.setOnClickListener(v -> {
            String categoryName = categoryInput.getText().toString().trim();
            if (!categoryName.isEmpty() && imageUri != null) {
                uploadImageToFirebaseStorage(categoryName);
            } else {
                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
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

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void uploadImageToFirebaseStorage(String categoryName) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("category_images");
        StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                + "." + getFileExtension(imageUri));

        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        saveCategoryToFirestore(categoryName, imageUrl);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getActivity(), "Failed to retrieve image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getActivity(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void saveCategoryToFirestore(String categoryName, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> category = new HashMap<>();
        category.put("name", categoryName);
        category.put("image", imageUrl);

        db.collection("categories").add(category).addOnSuccessListener(documentReference -> {
            Toast.makeText(getActivity(), "Category Added Successfully", Toast.LENGTH_SHORT).show();
            loadCategories(); // Reload categories after adding new one
        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "Error adding category: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadCategories() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("categories").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                categoriesContainer.removeAllViews(); // Clear existing views
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String categoryName = document.getString("name");
                    // Create TextView for each category
                    TextView textView = new TextView(getContext());
                    textView.setText(categoryName);
                    textView.setTextSize(18);
                    textView.setTextColor(Color.BLACK);
                    textView.setPadding(16, 16, 16, 16);
                    categoriesContainer.addView(textView);
                }
            } else {
                Toast.makeText(getActivity(), "Error loading categories", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
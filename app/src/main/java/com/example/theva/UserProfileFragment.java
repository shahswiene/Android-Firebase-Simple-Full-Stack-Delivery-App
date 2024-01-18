package com.example.theva;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;

public class UserProfileFragment extends Fragment {

    private TextView textViewFullName, textViewEmail, textViewGender, textViewAge, textViewHouseAddress;
    private Button buttonLogout;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        // Initialize Firebase components
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        textViewFullName = view.findViewById(R.id.textViewFullName);
        textViewEmail = view.findViewById(R.id.textViewEmail);
        textViewGender = view.findViewById(R.id.textViewGender);
        textViewAge = view.findViewById(R.id.textViewAge);
        textViewHouseAddress = view.findViewById(R.id.textViewHouseAddress);
        buttonLogout = view.findViewById(R.id.buttonLogout);

        loadUserProfile();
        buttonLogout.setOnClickListener(v -> logoutUser());

        return view;
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection("users").document(uid).collection("accountDetails")
                    .document("userInformation").get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            textViewFullName.setText(documentSnapshot.getString("fullName"));
                            textViewEmail.setText(documentSnapshot.getString("email"));
                            textViewGender.setText(documentSnapshot.getString("gender"));
                            textViewAge.setText(documentSnapshot.getString("age"));
                            textViewHouseAddress.setText(documentSnapshot.getString("houseAddress"));
                        } else {
                            Toast.makeText(getContext(), "User profile not found", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error loading profile", Toast.LENGTH_SHORT).show());
        }
    }

    private void logoutUser() {
        mAuth.signOut();
        // Navigate to LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish(); // Finish the current activity
    }

}

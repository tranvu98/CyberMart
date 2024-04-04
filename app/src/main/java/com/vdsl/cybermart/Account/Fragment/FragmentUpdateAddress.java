package com.vdsl.cybermart.Account.Fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vdsl.cybermart.Account.Model.AddressModel;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.databinding.FragmentAddAddressBinding;

import java.util.HashMap;
import java.util.Map;

public class FragmentUpdateAddress extends Fragment {
    FragmentAddAddressBinding binding;
    DatabaseReference databaseReference, addressRef;
    FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddAddressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnDeleteAddress.setVisibility(View.VISIBLE);
        showInitData();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Account");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        binding.btnDeleteAddress.setOnClickListener(v -> {
            deleteAddress();
        });

        binding.btnSaveAddress.setOnClickListener(v -> {
            upDateAddres();
        });

    }


    private void showInitData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String fullName = bundle.getString("fullName");
            String address = bundle.getString("address");
            if (address != null) {
                String[] parts = address.split(" - ");
                binding.edtFullName.setText(fullName);
                if (parts.length == 4) {
                    binding.edtCountry.setText(parts[0]);
                    binding.edtCity.setText(parts[1]);
                    binding.edtDistrict.setText(parts[2]);
                    binding.edtDescription.setText(parts[3]);
                }
            }
        }
    }

    private void upDateAddres() {
        String fullName = binding.edtFullName.getText().toString().trim();
        String country = binding.edtCountry.getText().toString().trim();
        String city = binding.edtCity.getText().toString().trim();
        String district = binding.edtDistrict.getText().toString().trim();
        String descriptiom = binding.edtDescription.getText().toString().trim();
        boolean error = false;
        if (fullName.isEmpty()) {
            binding.edtFullName.setError("Please enter your name!");
            error = true;
        }
        if (country.isEmpty()) {
            binding.edtCountry.setError("Please enter your country!");
            error = true;
        }
        if (city.isEmpty()) {
            binding.edtCity.setError("Please enter your city!");
            error = true;
        }
        if (district.isEmpty()) {
            binding.edtDistrict.setError("Please enter your district!");
            error = true;
        }
        if (descriptiom.isEmpty()) {
            binding.edtDescription.setError("Please enter description!");
            error = true;
        }

        if ((!error)) {
            if (currentUser != null) {
                databaseReference.orderByChild("email").equalTo(currentUser.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String userId = dataSnapshot.getKey();
                                if (userId != null) {
                                    Bundle bundle = getArguments();
                                    String addressId = bundle.getString("addressId");
//                                    String addressId = databaseReference.push().getKey();
                                    Log.d("addressId", "onDataChange: " + addressId);
                                    addressRef = FirebaseDatabase.getInstance().getReference().child("Account").child(userId).child("address");
                                    AddressModel addressModel = new AddressModel(fullName, country + " - " + city + " - " + district + " - " + descriptiom);
                                    Map<String, Object> updateAddress = new HashMap<>();
                                    updateAddress.put("fullName", fullName);
                                    updateAddress.put("address", country + " - " + city + " - " + district + " - " + descriptiom);
                                    addressRef.child(addressId).updateChildren(updateAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                binding.btnSaveAddress.setText("BACK");
                                                binding.btnSaveAddress.setBackgroundColor(Color.GRAY);
                                                binding.btnSaveAddress.setOnClickListener(v -> {
                                                    getActivity().getSupportFragmentManager().popBackStack();
                                                });
                                                Toast.makeText(getActivity(), "Address Updated Successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getActivity(), "Address Updated Failed!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }
    }

    private void deleteAddress() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("DELETE");
        builder.setMessage("Are you sure to delete this address?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (currentUser != null) {
                    databaseReference.orderByChild("email").equalTo(currentUser.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String userId = dataSnapshot.getKey();
                                    if (userId != null) {
                                        Bundle bundle = getArguments();
                                        String addressId = bundle.getString("addressId");
                                        Log.d("addressId", "onDataChange: " + addressId);
                                        addressRef = FirebaseDatabase.getInstance().getReference().child("Account").child(userId).child("address");
                                        addressRef.child(addressId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getActivity(), "Address Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                    getActivity().getSupportFragmentManager().popBackStack();

                                                } else {
                                                    Toast.makeText(getActivity(), "Address Deleted Failed!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               builder.create().dismiss();
            }
        });
        builder.create().show();

    }
}


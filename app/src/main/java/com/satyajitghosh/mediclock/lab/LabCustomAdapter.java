package com.satyajitghosh.mediclock.lab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.satyajitghosh.mediclock.R;

import java.util.ArrayList;

public class LabCustomAdapter extends ArrayAdapter<LabTestDataModel> {

    //This codes helps to get the reference of the FireBase Database and the instances of it.
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
    FirebaseUser user;
    private ArrayList<LabTestDataModel> arrayList;

    public LabCustomAdapter(@NonNull Context context, ArrayList<LabTestDataModel> arrayList) {
        super(context, 0, arrayList);
        this.arrayList = new ArrayList<>();
        this.arrayList = arrayList;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentItemView = convertView;
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.lab_custom_list_view, parent, false);
        }

        TextView lab_name_view = currentItemView.findViewById(R.id.lab_name_view);
        TextView lab_doctor = currentItemView.findViewById(R.id.lab_doctor);
        TextView lab_date_view = currentItemView.findViewById(R.id.lab_time_view);
        ImageView lab_delete_btn = currentItemView.findViewById(R.id.lab_delete_btn);

        LabTestDataModel labTestDataModel = arrayList.get(position);

        lab_name_view.setText(labTestDataModel.getTestName());
        lab_doctor.setText(labTestDataModel.getDoctorName());

        String result = labTestDataModel.getDay() + "/" + (labTestDataModel.getMonth()) + "/" + labTestDataModel.getYear();
        lab_date_view.setText(result);


        //This code is for delete button
        lab_delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LabTestDataModel obj = arrayList.get(position);
                myRef.child("LabTestRecord").child(user.getUid()).child(obj.key).removeValue(); //This removes the child from the FireBase database .
                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                //Cancel the alarm code here
            }
        });


        return currentItemView;
    }
}

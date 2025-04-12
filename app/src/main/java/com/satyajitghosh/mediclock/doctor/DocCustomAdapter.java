package com.satyajitghosh.mediclock.doctor;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.satyajitghosh.mediclock.R;

import java.util.ArrayList;

public class DocCustomAdapter extends ArrayAdapter<DoctorDataModel> {

    private final FirebaseAuth mAuth;
    private final FirebaseUser user;
    //This codes helps to get the reference of the FireBase Database and the instances of it.
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    private ArrayList<DoctorDataModel> arrayList;


    public DocCustomAdapter(@NonNull Context context, ArrayList<DoctorDataModel> arrayList) {
        super(context, 0, arrayList);
        this.arrayList = new ArrayList<>();
        this.arrayList = arrayList;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentItemView = convertView;
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.doctor_custom_list_view, parent, false);
        }
        TextView doc_name = currentItemView.findViewById(R.id.doc_name_view);
        TextView doc_reason = currentItemView.findViewById(R.id.doc_reason);
        TextView doc_time = currentItemView.findViewById(R.id.doc_time_view);
        ImageView delete_btn = currentItemView.findViewById(R.id.doc_delete_btn);

        DoctorDataModel doctorDataModel = arrayList.get(position);

        doc_name.setText(doctorDataModel.getName());
        doc_reason.setText(doctorDataModel.getReason());
        String result = doctorDataModel.getDate() + "/" + (doctorDataModel.getMonth()) + "/" + doctorDataModel.getYear();
        doc_time.setText(result);


        //This code is for delete button
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoctorDataModel ddm = arrayList.get(position);
                myRef.child("AppointmentRecord").child(user.getUid()).child(ddm.key).removeValue(); //This removes the child from the FireBase database .
                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }
        });


        return currentItemView;
    }
}

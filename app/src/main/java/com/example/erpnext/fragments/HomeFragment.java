package com.example.erpnext.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.erpnext.Greetings;
import com.example.erpnext.LeaveReportActivity;
import com.example.erpnext.R;
import com.example.erpnext.activities.PaySlipActivity2;
import com.example.erpnext.activities.drawerActivities.HolidayActivity;
import com.example.erpnext.models.UserInfo;
import com.example.erpnext.session.UserSessionManager;
import com.example.erpnext.activities.Login;
import com.example.erpnext.services.ApiClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    UserSessionManager sessionManager ;
    TextView greting, first_name,txviewslip, viewallholidays;

    LinearLayout payslip, claims, leave, attendance;
    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        String greeting = Greetings.getGreeting();
        first_name = view.findViewById(R.id.firstname);
        greting = view.findViewById(R.id.greetings);
        greting.setText(greeting);
        payslip  = view.findViewById(R.id.payslipview);
        leave = view.findViewById(R.id.leaveview);
        viewallholidays = view.findViewById(R.id.viewallholidays);
        txviewslip = view.findViewById(R.id.textViewslip);

        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), LeaveReportActivity.class));
            }
        });

        viewallholidays.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), HolidayActivity.class));
            }
        });

        sessionManager = new UserSessionManager(getContext());
        if (sessionManager.getUserFirstName()== null){
           // Toast.makeText(getContext(), "First Name is null", Toast.LENGTH_SHORT).show();
        }
        else {
            first_name.setText(sessionManager.getUserFirstName().toUpperCase());
        }

        payslip.setOnClickListener(v ->
                startActivity(new Intent(getContext(), PaySlipActivity2.class)));
        return view;
    }



}
package com.example.erpnext.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.erpnext.R;
import com.example.erpnext.activities.drawerActivities.AppointmentsActivity;
import com.example.erpnext.activities.drawerActivities.BenefitsActivity;
import com.example.erpnext.activities.drawerActivities.HolidayActivity;
import com.example.erpnext.activities.drawerActivities.ProfileActivity;
import com.example.erpnext.activities.drawerActivities.RaiseIssueActivity;
import com.example.erpnext.activities.drawerActivities.SettingsActivity;
import com.example.erpnext.activities.drawerActivities.TaskInformationActivity;
import com.example.erpnext.activities.drawerActivities.TrainingActivity;
import com.example.erpnext.fragments.AttendanceFragment;
import com.example.erpnext.fragments.HomeFragment;
import com.example.erpnext.fragments.LeaveFragment;
import com.example.erpnext.fragments.MarkAttendanceFragment;
import com.example.erpnext.fragments.ProfileFragment;
import com.example.erpnext.models.EmployeeDataResponse;
import com.example.erpnext.models.PermissionError;
import com.example.erpnext.services.ApiClient;
import com.example.erpnext.session.UserSessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    UserSessionManager sessionManager;
    RelativeLayout errorlayout;
    TextView roletxt, permissiontxt;
    FrameLayout frameLayout;
    AppCompatButton loginbtnerror;
    View customView, logoutview;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (isDarkThemePreferred()) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme_Light);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        replaceFragment(new HomeFragment());
        sessionManager = new UserSessionManager(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.drawer_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        frameLayout = findViewById(R.id.frame_layout);


        getEmployeeData();
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView nametxt = headerView.findViewById(R.id.header_name_txt);
        roletxt = headerView.findViewById(R.id.header_role_txt);

        nametxt.setText(sessionManager.getFullName());
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    return true;

                case R.id.attendance:
                    replaceFragment(new AttendanceFragment());
                    return true;

                case R.id.leave:
                    replaceFragment(new LeaveFragment());
                    return true;

                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    return true;

                case R.id.mark_attendance:
                    replaceFragment(new MarkAttendanceFragment());
                    return true;
            }
            return true;
        });


    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));

                break;
            case R.id.drawer_appointments:
                startActivity(new Intent(MainActivity.this, AppointmentsActivity.class));
                break;
            case R.id.drawer_benefits:
                startActivity(new Intent(MainActivity.this, BenefitsActivity.class));

                break;
            case R.id.drawer_holidays:
                startActivity(new Intent(MainActivity.this, HolidayActivity.class));

                break;
            case R.id.raise_issue:
                startActivity(new Intent(MainActivity.this, RaiseIssueActivity.class));

                break;

            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));

                break;
            case R.id.drawer_training:
                startActivity(new Intent(MainActivity.this, TrainingActivity.class));

                break;

            case R.id.drawer_tax:
                startActivity(new Intent(MainActivity.this, TaskInformationActivity.class));

                break;
            case R.id.drawer_logout:
                logout();
        }

        return true;
    }

    public void logout() {

        ApiClient.getApiClient().logout().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    logoutview = getLayoutInflater().inflate(R.layout.customalertlayout, null);
                    //AppCompatButton button = customView.findViewById(R.id.loginbuttonerror);
                   // TextView textView = customView.findViewById(R.id.textView);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setView(logoutview);
                    builder.setCancelable(false);
                    // Set a positive button and its click listener
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Perform any action you want when the "Yes" button is clicked
                            Toast.makeText(MainActivity.this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, Login.class));
                            finish();
                            sessionManager.clearSession();

                        }
                    });
                    // Set a negative button and its click listener
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Perform any action you want when the "No" button is clicked
                            dialog.dismiss();
                        }
                    });

                    // Create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Error Occurred");
                if (t.getMessage().equals("timeout")) {
                    builder.setMessage("Kindly check your internet connection then try again");

                    // Set a positive button and its click listener
                    builder.setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                } else {
                    builder.setMessage(t.getMessage());

                    // Set a positive button and its click listener
                    builder.setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                }
                // Create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                builder.setTitle("Error occurred");
            }
        });
    }

    public void getEmployeeData() {
        ApiClient.getApiClient().getEmployeeData("Employee", sessionManager.getKeyEmployeeNamingSeries(), sessionManager.getUserId()).enqueue(new Callback<EmployeeDataResponse>() {
            @Override
            public void onResponse(Call<EmployeeDataResponse> call, Response<EmployeeDataResponse> response) {
                if (response.isSuccessful()) {
                    EmployeeDataResponse responseModel = response.body();
                    if (responseModel != null && responseModel.getData() != null) {
                        EmployeeDataResponse.Data data = responseModel.getData();
                        // Access the designation from the data model
                        String designation = data.getDesignation();
                        roletxt.setText(designation);
                        // Set the designation in a TextView
                        sessionManager.setUserFirstName(data.getFirstName());
                        //Toast.makeText(MainActivity.this, "First Name"+sessionManager.getUserFirstName(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Null data", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (response.errorBody() != null) {
                        try {
                            String errorResponseJson = response.errorBody().string();
                            if (response.code() == 403) {
                                PermissionError errorResponse = new Gson().fromJson(errorResponseJson, PermissionError.class);

                                customView = getLayoutInflater().inflate(R.layout.customalertbuilder, null);
                                //AppCompatButton button = customView.findViewById(R.id.loginbuttonerror);
                                TextView textView = customView.findViewById(R.id.textView);
                                textView.setText(errorResponse.getExcType());

                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setView(customView)
                                        .setCancelable(false);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();

                               /* Drawable image = getResources().getDrawable(R.drawable.error);
                                CustomAlertDialogBuilder builder = new CustomAlertDialogBuilder(MainActivity.this);
                                builder.setImage(image)
                                        .setTitle("Title")
                                        .setMessage("Message")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Positive button click listener
                                                // Add your logic here
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Negative button click listener
                                                // Add your logic here
                                            }
                                        });

                                android.app.AlertDialog alertDialog = builder.create();
                                alertDialog.show();*/

                               /* frameLayout.setVisibility(View.GONE);
                                //drawerLayout.setVisibility(View.GONE);
                                bottomNavigationView.setVisibility(View.GONE);
                                errorlayout.setVisibility(View.VISIBLE);
                                loginbtnerror.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(MainActivity.this, Login.class));
                                    }
                                });
                                toolbar.setVisibility(View.GONE);
                                permissiontxt.setText(errorResponse.getExcType());*/
                                /*AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle(errorResponse.getExcType());
                                builder.setMessage("Your session expired, please logout then login to access your account");
                                builder.setPositiveButton("Dismiss", (dialog, which) -> dialog.dismiss());

                                AlertDialog dialog = builder.create();
                                dialog.show();*/
                            } else {
                                Toast.makeText(MainActivity.this, "Server error occurred, please reload your page", Toast.LENGTH_SHORT).show();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<EmployeeDataResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error occurred " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isDarkThemePreferred() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String themePreference = sharedPreferences.getString("theme_preference", "system");

        if (themePreference.equals("dark")) {
            return true;
        } else if (themePreference.equals("light")) {
            return false;
        } else {
            // If the theme preference is set to "system", use the system default
            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
        }
    }
}
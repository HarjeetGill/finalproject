package com.techai.shiftme.ui.customer;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.techai.shiftme.R;
import com.techai.shiftme.data.model.SignUpModel;
import com.techai.shiftme.databinding.ActivityCustomerBinding;
import com.techai.shiftme.preferences.SharedPrefUtils;
import com.techai.shiftme.utils.Constants;

public class CustomerActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityCustomerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarCustomer.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_send_request, R.id.nav_profile)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_customer);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        updateProfileDetails();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_customer);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.customer_toolbar_menu, menu);
        showToolbarMenu(false);
        return true;
    }

    public void showToolbarMenu(Boolean bool) {
        binding.appBarCustomer.toolbar.getMenu().findItem(R.id.action_edit).setVisible(bool);
    }

    public Toolbar getToolbarView() {
        return binding.appBarCustomer.toolbar;
    }

    public void updateProfileDetails() {
        SignUpModel signUpModel = SharedPrefUtils.getObject(CustomerActivity.this, Constants.SIGN_UP_MODEL, SignUpModel.class);
        ((TextView) binding.navView.getHeaderView(0).findViewById(R.id.tvName)).setText(signUpModel.getFullName());
        ((TextView) binding.navView.getHeaderView(0).findViewById(R.id.tvEmail)).setText(signUpModel.getEmailId());
    }

}
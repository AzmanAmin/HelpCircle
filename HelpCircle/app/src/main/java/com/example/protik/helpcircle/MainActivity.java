package com.example.protik.helpcircle;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private ImageView iv_notifications;
    private ImageView iv_search;
    private ImageView iv_location;
    private ImageView btn_help_here;
    private ImageView btn_panic;


    DrawerLayout dl;
    ActionBarDrawerToggle abdt;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

//        mToolbar = findViewById(R.id.main_page_toolbar);
//        setSupportActionBar(mToolbar);
        setUpToolbar();
        getSupportActionBar().setTitle("Help Circle");

        iv_notifications = findViewById(R.id.iv_notifications);
        iv_search = findViewById(R.id.iv_search);
        iv_location = findViewById(R.id.iv_location);
        btn_help_here = findViewById(R.id.btn_help_here);
        btn_panic = findViewById(R.id.btn_panic);

        iv_notifications.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        iv_location.setOnClickListener(this);
        btn_help_here.setOnClickListener(this);
        btn_panic.setOnClickListener(this);

//        setOptionsActivity();

        NavigationView nav_view = findViewById(R.id.nav_view);
        NavigationClass navigationClass = new NavigationClass(MainActivity.this);
        nav_view.setNavigationItemSelectedListener(navigationClass);
    }

    public void setUpToolbar() {
        dl = findViewById(R.id.dl);
        toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        abdt = new ActionBarDrawerToggle(this,dl,toolbar,R.string.Open,R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(abdt);
        abdt.syncState();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (abdt.onOptionsItemSelected(item)) {
            return true;
        }
        MenuInflaterClass obj = new MenuInflaterClass(item, MainActivity.this);
        return obj.yo();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.iv_notifications) {
            Toast.makeText(MainActivity.this, "notification", Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.iv_search) {
            Toast.makeText(MainActivity.this, "search", Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.iv_location) {
            Toast.makeText(MainActivity.this, "location", Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.btn_help_here) {
            Toast.makeText(MainActivity.this, "help here", Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.btn_panic) {
            Toast.makeText(MainActivity.this, "search", Toast.LENGTH_SHORT).show();
        }
    }
}

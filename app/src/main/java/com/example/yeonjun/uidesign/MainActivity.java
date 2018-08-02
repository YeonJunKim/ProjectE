package com.example.yeonjun.uidesign;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    SectionPageAdapter mSectionPageAdapter;
    ViewPager mViewPager;

    private void SetupViewPager(ViewPager viewPager){
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.AddFragment(new DrowsyFragment(), "DrowsyFragment");
        adapter.AddFragment(new RealTimeFragment(), "RealTimeFragment");
        adapter.AddFragment(new MapFragment(), "MapFragment");
        adapter.AddFragment(new HistoryFragment(), "HistoryFragment");
        viewPager.setAdapter(adapter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mSectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        SetupViewPager(mViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.drowsyDriving) {
            toolbar.setTitle("Drowsy Driving Detection");
            mViewPager.setCurrentItem(0);
        } else if (id == R.id.realTime) {
            toolbar.setTitle("Real Time Data View");
            mViewPager.setCurrentItem(1);
        } else if (id == R.id.mapView) {
            toolbar.setTitle("Map View");
            mViewPager.setCurrentItem(2);
        } else if (id == R.id.history) {
            toolbar.setTitle("History Data View");
            mViewPager.setCurrentItem(3);
        } else if (id == R.id.changePw) {
            Intent intent = new Intent(
                    getApplicationContext(),
                    ChangePasswordActivity.class);
            startActivity(intent);
        } else if (id == R.id.idCancellation) {
            Intent intent = new Intent(
                    getApplicationContext(),
                    IdCancellationActivity.class);
            startActivity(intent);
        } else if (id == R.id.logout) {
            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.sh_pref), MODE_PRIVATE).edit();
            editor.remove("token");
            editor.commit();

            Intent intent = new Intent(
                    getApplicationContext(),
                    LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

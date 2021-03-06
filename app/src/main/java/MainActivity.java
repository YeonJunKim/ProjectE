package com.example.yeonjun.uidesign;

import android.content.IntentFilter;
import android.hardware.Sensor;
import android.net.Uri;
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
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    SectionPageAdapter mSectionPageAdapter;
    NonSwipeableViewPager mViewPager;
    TextView accountIdText;
    public static SharedPreferences sp;

    private void SetupViewPager(ViewPager viewPager){
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.AddFragment(new DrowsyFragment(), "DrowsyFragment");
        adapter.AddFragment(new RealTimeFragment(), "RealTimeFragment");
        adapter.AddFragment(new HeartFragment(), "HeartFragment");
        adapter.AddFragment(new MapFragment(), "MapFragment");
        adapter.AddFragment(new HistoryFragment(), "HistoryFragment");
        adapter.AddFragment(new BluetoothChatFragment(), "SensorFragment");

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
        navigationView.setCheckedItem(R.id.drowsyDriving);
        accountIdText = navigationView.getHeaderView(0).findViewById(R.id.accountID);
        accountIdText.setText(getIntent().getStringExtra("id"));

        mSectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        mViewPager = (NonSwipeableViewPager) findViewById(R.id.viewPager);
        SetupViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(5);

        sp = getSharedPreferences(getString(R.string.sh_pref), MODE_PRIVATE);
        activatePolar();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        startService(new Intent(MainActivity.this, GPSCollecter.class));
        super.onResume();
    }

    @Override
    protected void onPause() {
        stopService(new Intent(MainActivity.this, GPSCollecter.class));
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            toolbar.setTitle("Drowsy Driving Detection");
            mViewPager.setCurrentItem(0);
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
            toolbar.setTitle("Air Quality");
            mViewPager.setCurrentItem(1);
        } else if(id == R.id.heartView){
            toolbar.setTitle("Heart");
            mViewPager.setCurrentItem(2);
        } else if (id == R.id.mapView) {
            toolbar.setTitle("Map View");
            mViewPager.setCurrentItem(3);
        } else if (id == R.id.history) {
            toolbar.setTitle("History");
            mViewPager.setCurrentItem(4);
        } else if(id == R.id.sensor) {
            toolbar.setTitle("Sensor View");
            mViewPager.setCurrentItem(5);
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

    @Override
    protected void onDestroy() {
        if(BluetoothChatFragment.mChatService != null){
            BluetoothChatFragment.resetDeviceInfo();
            BluetoothChatFragment.mChatService.stop();
        }
        deactivatePolar();
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        super.onDestroy();
    }

    private final MyPolarBleReceiver mPolarBleUpdateReceiver = new MyPolarBleReceiver() {};

    protected void activatePolar() {
        Log.w(this.getClass().getName(), "activatePolar()");
        registerReceiver(mPolarBleUpdateReceiver, makePolarGattUpdateIntentFilter());
    }

    protected void deactivatePolar() {
        unregisterReceiver(mPolarBleUpdateReceiver);
    }

    private static IntentFilter makePolarGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyPolarBleReceiver.ACTION_GATT_CONNECTED);
        intentFilter.addAction(MyPolarBleReceiver.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(MyPolarBleReceiver.ACTION_HR_DATA_AVAILABLE);
        return intentFilter;
    }
}

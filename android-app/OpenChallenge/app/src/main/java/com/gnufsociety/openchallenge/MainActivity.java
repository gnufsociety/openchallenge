package com.gnufsociety.openchallenge;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    //last button pressed
    private BottomButton last = null;
    private Toolbar myToolbar = null;
    private FirebaseAuth auth;
    public SearchFragment searchFragment;
    private GoogleMap mMap;

    private Fragment1 f1;
    private Fragment3 f3;
    private Fragment4 f4;
    private SupportMapFragment f2;
    private Fragment5 f5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extra = getIntent().getExtras();
        searchFragment = new SearchFragment();
        searchFragment.setContext(this);

        //If is a new user, start configuration activity
        boolean newUser = extra.getBoolean("new", false);
        if (newUser) {
            startActivity(new Intent(this, ConfigurationActivity.class));
        }
        f1 = new Fragment1();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_fragment, f1, Fragment1.TAG)
                .addToBackStack(Fragment1.TAG).commit();


        //get instance of Firebase authentication
        auth = FirebaseAuth.getInstance();


        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, myToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set last as home button
        last = (BottomButton) findViewById(R.id.home_bottom);
        last.clickIt();


        System.out.println("lal");
        System.out.println("Tiemmodify");
        System.out.println("Sdcmmodify");
        System.out.println("TheMonkeyKing");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                getSupportFragmentManager().popBackStackImmediate();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                AsyncTask<String, Void, ArrayList<User>> task = new AsyncTask<String, Void, ArrayList<User>>() {
                    @Override
                    protected ArrayList<User> doInBackground(String... params) {
                        ApiHelper api = new ApiHelper();
                        return api.searchUsers(params[0]);
                    }

                    @Override
                    protected void onPostExecute(ArrayList<User> users) {
                        searchFragment.adapter.swapList(users);
                    }
                };

                task.execute(query);


                return true;

            }

            @Override
            public boolean onQueryTextChange(String newText){

                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.home_fragment, searchFragment, "search")
                    .addToBackStack("search").commit();
        }
        else if (id == R.id.action_logout){
            auth.signOut();
            startActivity(new Intent(this,RegistrationActivity.class));
            finish();
        }


        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    /**
     * Override onBackPressed, if there are no other fragment in the stack close application else
     * close pop last fragment.
     * Change the color of last button pressed
     **/
    @Override
    public void onBackPressed() {
        if (!getSupportActionBar().getTitle().equals("Open Challenge"))
            getSupportActionBar().setTitle("Open Challenge");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 1) {
            finish();
        } else {
            BottomButton current = null;
            last.clickIt();
            //last.setColorFilter(Color.WHITE);
            getSupportFragmentManager().popBackStackImmediate();
            FragmentManager.BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1);
            String tag = entry.getName();
            if (tag.equals(Fragment1.TAG)) {
                current = (BottomButton) findViewById(R.id.home_bottom);
            } else if (tag.equals(Fragment2.TAG)) {
                current = (BottomButton) findViewById(R.id.map_bottom);
            } else if (tag.equals(Fragment3.TAG)) {
                current = (BottomButton) findViewById(R.id.add_bottom);
            } else if (tag.equals(Fragment4.TAG)) {
                current = (BottomButton) findViewById(R.id.favorite_bottom);
            } else if (tag.equals(Fragment5.TAG)) {
                current = (BottomButton) findViewById(R.id.profile_bottom);
            }

            //set black color to current button
            current.clickIt();
            last = current;
        }

    }


    /**
     * Change color of the button pressed and show the new fragment
     **/
    public void clickBottomBar(View view) {
        if (!getSupportActionBar().getTitle().equals("Open Challenge"))
            getSupportActionBar().setTitle("Open Challenge");
        BottomButton btn = (BottomButton) view;
        if (btn == last)
            return;
        //set black the clicked button
        btn.clickIt();
        //set white last button
        if (last != null)
            last.clickIt();
        if (btn == last)
            return;
        last = btn;
        FragmentManager manager = getSupportFragmentManager();
        switch (btn.getId()) {
            case R.id.home_bottom:
                //Fragment1 f = null;
                //f1 = (Fragment1) manager.findFragmentByTag(Fragment1.TAG);
                if (f1 == null)
                    f1 = new Fragment1();
                manager.beginTransaction()
                        .replace(R.id.home_fragment, f1, Fragment1.TAG)
                        .addToBackStack(Fragment1.TAG)
                        .commit();
                break;
            case R.id.favorite_bottom:
                //f4 = (Fragment4) manager.findFragmentByTag(Fragment4.TAG);
                if (f4 == null)
                    f4 = new Fragment4();
                /*else
                    manager.popBackStackImmediate(Fragment4.TAG,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                */manager.beginTransaction()
                        .replace(R.id.home_fragment, f4, Fragment4.TAG)
                        .addToBackStack(Fragment4.TAG)
                        .commit();
                break;
            case R.id.add_bottom:
                //f3 = (Fragment3) manager.findFragmentByTag(Fragment3.TAG);
                if (f3 == null) {
                    f3 = new Fragment3();
                    f3.setMainActivity(this);

                }
                manager.beginTransaction()
                        .replace(R.id.home_fragment, f3, Fragment3.TAG)
                        .addToBackStack(Fragment3.TAG)
                        .commit();
                break;
            case R.id.map_bottom:
                //f2 = (SupportMapFragment) manager.findFragmentByTag(Fragment2.TAG);
                if (f2 == null)
                    f2 = new SupportMapFragment();
                /*else
                    manager.popBackStackImmediate(Fragment2.TAG,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                */manager.beginTransaction()
                        .replace(R.id.home_fragment, f2, Fragment2.TAG)
                        .addToBackStack(Fragment2.TAG)
                        .commit();
                f2.getMapAsync(this);
                break;
            case R.id.profile_bottom:
                //f5 = (Fragment5) manager.findFragmentByTag(Fragment5.TAG);
                if (f5 == null)
                    f5 = new Fragment5();
                /*else
                    manager.popBackStackImmediate(Fragment5.TAG,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                */manager.beginTransaction()
                        .replace(R.id.home_fragment, f5, Fragment5.TAG)
                        .addToBackStack(Fragment5.TAG)
                        .commit();
                break;
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.side_profile) {
            // Handle the profile clicked action
            Snackbar.make(findViewById(R.id.frame_navigation), "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (id == R.id.side_achievements) {
            Snackbar.make(findViewById(R.id.frame_navigation), "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (id == R.id.side_settings) {
            Snackbar.make(findViewById(R.id.frame_navigation), "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (id == R.id.side_share) {
            Snackbar.make(findViewById(R.id.frame_navigation), "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (id == R.id.side_info) {
            Snackbar.make(findViewById(R.id.frame_navigation), "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Sydney and move the camera
        int i = 0;
        for (Challenge c : f1.adapter.list) {
            LatLng lat = new LatLng(c.lat,c.lng);
            Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(lat)
                            .title(c.name));
            marker.setTag(i);
            i++;
        }
        if (f1.adapter.list.size() > 3){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(f1.adapter.list.get(2).lat,f1.adapter.list.get(2).lng)));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18),2000,null);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle bundle = new Bundle();
                int pos = (int) marker.getTag();
                bundle.putSerializable("challenge",f1.adapter.list.get(pos));
                Intent i = new Intent(myToolbar.getContext(),ChallengeActivity.class);
                i.putExtras(bundle);
                myToolbar.getContext().startActivity(i);

                return false;
            }
        });
    }
}

package com.gnufsociety.openchallenge;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.gnufsociety.openchallenge.customui.BottomButton;
import com.gnufsociety.openchallenge.mainfrags.FavoriteFragment;
import com.gnufsociety.openchallenge.mainfrags.Fragment2;
import com.gnufsociety.openchallenge.mainfrags.HomeFragment;
import com.gnufsociety.openchallenge.mainfrags.LocationHelper;
import com.gnufsociety.openchallenge.mainfrags.OrganizeFragment;
import com.gnufsociety.openchallenge.mainfrags.ProfileFragment;
import com.gnufsociety.openchallenge.model.Challenge;
import com.gnufsociety.openchallenge.model.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    //last button pressed
    private BottomButton last = null;
    private Toolbar myToolbar = null;
    private FirebaseAuth auth;
    public SearchFragment searchFragment;
    private GoogleMap mMap;

    public LocationHelper loc;

    private boolean okPressed = false;

    //private BottomNavigationView bottomBar;

    private HomeFragment f1;
    private OrganizeFragment f3;
    private FavoriteFragment f4;
    private SupportMapFragment f2;
    private ProfileFragment f5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(null);

        //get instance of Firebase authentication
        auth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            startActivity(RegistrationActivity.createIntent(this));
            finish();
            return;
        }

        //If is a new user, start configuration activity
        final Context context = this;
        //If signed-in user is not in database, start configuration activity
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                // to be replaced with a database query with uid as field
                // (make it an index in the model)
                return new ApiHelper().isPresent(currentUser.getUid());
            }

            @Override
            protected void onPostExecute(Boolean res) {
                if (!res)
                    startActivity(new Intent(context, ConfigurationActivity.class));
                onResumeCreate();
            }
        };
        task.execute();
    }


    public void onResumeCreate() {
        setContentView(R.layout.activity_main);
        searchFragment = new SearchFragment();
        searchFragment.setContext(this);

        f1 = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_fragment, f1, HomeFragment.TAG)
                .addToBackStack(HomeFragment.TAG).commit();

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
        System.out.println("TheMonkeyKing ");
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
            public boolean onQueryTextChange(String newText) {

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
        } else if (id == R.id.action_logout) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
                            finish();
                        }
                    });
        } else if (id == R.id.action_filter) {
            showFilterDialog();

        }

        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //add to string.xml
        builder.setTitle("Filter");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.filter_dialog, null);
        builder.setView(dialogView);

        final Spinner s = (Spinner) dialogView.findViewById(R.id.spinner_filter);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);


        builder.setPositiveButton("Ok", null);
        builder.setNeutralButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final AlertDialog mDialog = builder.create();

        loc = new LocationHelper(this) {
            @Override
            public void onLocationChanged(Location location) {
                super.onLocationChanged(location);
                if (okPressed) {
                    mDialog.dismiss();
                    okPressed = false;
                }
            }
        };


        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button ok = mDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (s.getSelectedItemPosition()){
                            case 0:
                                if (loc.currLocation != null) {
                                    f1.orderByPosition(loc.currLocation);

                                } else {
                                    okPressed = true;
                                    Toast.makeText(f1.getContext(), "Attendi che vedo la tua cazzo di posizione", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                break;
                            case 1:
                                f1.downloadChall();

                                break;

                        }
                        dialog.dismiss();
                        loc.disconnectApi();


                    }
                });
            }
        });
        mDialog.show();

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        loc.buildGoogleApi();
                        break;
                    case 1:

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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

            getSupportFragmentManager().popBackStackImmediate();
            FragmentManager.BackStackEntry entry = getSupportFragmentManager()
                    .getBackStackEntryAt(getSupportFragmentManager()
                            .getBackStackEntryCount() - 1);
            String tag = entry.getName();
            if (tag.equals(HomeFragment.TAG)) {
                current = (BottomButton) findViewById(R.id.home_bottom);
            } else if (tag.equals(Fragment2.TAG)) {
                current = (BottomButton) findViewById(R.id.map_bottom);
            } else if (tag.equals(OrganizeFragment.TAG)) {
                current = (BottomButton) findViewById(R.id.add_bottom);
            } else if (tag.equals(FavoriteFragment.TAG)) {
                current = (BottomButton) findViewById(R.id.favorite_bottom);
            } else if (tag.equals(ProfileFragment.TAG)) {
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
                if (f1 == null)
                    f1 = new HomeFragment();
                manager.beginTransaction()
                        .replace(R.id.home_fragment, f1, HomeFragment.TAG)
                        .addToBackStack(HomeFragment.TAG)
                        .commit();
                break;
            case R.id.favorite_bottom:
                if (f4 == null)
                    f4 = new FavoriteFragment();
                manager.beginTransaction()
                        .replace(R.id.home_fragment, f4, FavoriteFragment.TAG)
                        .addToBackStack(FavoriteFragment.TAG)
                        .commit();
                break;
            case R.id.add_bottom:
                if (f3 == null) {
                    f3 = new OrganizeFragment();
                    f3.setMainActivity(this);

                }
                manager.beginTransaction()
                        .replace(R.id.home_fragment, f3, OrganizeFragment.TAG)
                        .addToBackStack(OrganizeFragment.TAG)
                        .commit();
                break;
            case R.id.map_bottom:
                if (f2 == null)
                    f2 = new SupportMapFragment();
                manager.beginTransaction()
                        .replace(R.id.home_fragment, f2, Fragment2.TAG)
                        .addToBackStack(Fragment2.TAG)
                        .commit();
                f2.getMapAsync(this);
                break;
            case R.id.profile_bottom:
                if (f5 == null)
                    f5 = new ProfileFragment();
                manager.beginTransaction()
                        .replace(R.id.home_fragment, f5, ProfileFragment.TAG)
                        .addToBackStack(ProfileFragment.TAG)
                        .commit();
                break;
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.side_profile) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.home_fragment, new ProfileFragment(), ProfileFragment.TAG)
                    .addToBackStack(ProfileFragment.TAG)
                    .commit();
            return true;
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
            LatLng lat = new LatLng(c.lat, c.lng);
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(lat)
                    .title(c.name));
            marker.setTag(i);
            i++;
        }
        loc = new LocationHelper(this){
            @Override
            public void onLocationChanged(Location location) {
                super.onLocationChanged(location);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(loc.currLocation.getLatitude(), loc.currLocation.getLongitude())));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);

            }
        };
        loc.buildGoogleApi();

        /*if (f1.adapter.list.size() > 3) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(f1.adapter.list.get(2).lat, f1.adapter.list.get(2).lng)));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
        }*/

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Bundle bundle = new Bundle();
                int pos = (int) marker.getTag();
                bundle.putSerializable("challenge", f1.adapter.list.get(pos));
                Intent i = new Intent(myToolbar.getContext(), ChallengeActivity.class);
                i.putExtras(bundle);
                myToolbar.getContext().startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 12) {
            if (resultCode == RESULT_OK) {
                loc.foo();
            }
        }
    }


    public static Intent createIntent(Context context, IdpResponse idpResponse) {
        Intent in = IdpResponse.getIntent(idpResponse);
        in.setClass(context, MainActivity.class);
        return in;
    }
}

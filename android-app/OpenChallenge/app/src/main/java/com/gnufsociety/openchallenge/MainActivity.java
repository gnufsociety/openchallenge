package com.gnufsociety.openchallenge;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    //last button pressed
    private ImageButton last = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //set last as home button
        last = (ImageButton) findViewById(R.id.home_bottom);
        last.setColorFilter(Color.BLACK);
        Fragment1 f = new Fragment1();
        getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment,f,f.TAG)
                .addToBackStack(null).commit();

        System.out.println("lol");
        System.out.println("Tiemmodify");
        System.out.println("Sdcmmodify");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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
     * **/
    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 1) {
            finish();
        } else {
            ImageButton current = null;
            last.getDrawable().clearColorFilter();
            //last.setColorFilter(Color.WHITE);
            getSupportFragmentManager().popBackStackImmediate();
            FragmentManager.BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1);
            String tag = entry.getName();
            if (tag == null){
                current = (ImageButton) findViewById(R.id.home_bottom);
            }
            else if (tag.equals(Fragment2.TAG)){
                current = (ImageButton) findViewById(R.id.map_bottom);
            }
            else if (tag.equals(Fragment3.TAG)){
                current = (ImageButton) findViewById(R.id.add_bottom);
            }
            else if (tag.equals(Fragment4.TAG)){
                current = (ImageButton) findViewById(R.id.favorite_bottom);
            }
            else if (tag.equals(Fragment5.TAG)){
                current = (ImageButton) findViewById(R.id.profile_bottom);
            }

            //set black color to current button
            current.getDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.DST);
            last = current;
        }

    }


    /**
     * Change color of the button pressed and show the new fragment
     * **/
    public void clickBottomBar(View view){
        ImageButton btn = (ImageButton) view;
        if (btn == last)
            return;
        //set black the clicked button
        btn.getDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.DST);
        //set white last button
        if (last != null)
            last.getDrawable().clearColorFilter();
        if (btn == last)
            return;
        last = btn;
        FragmentManager manager = getSupportFragmentManager();
        switch (btn.getId()){
            case R.id.home_bottom:
                Fragment1 f = null;
                f = (Fragment1) manager.findFragmentByTag(f.TAG);
                if (f == null)
                    f = new Fragment1();
                manager.beginTransaction()
                        .replace(R.id.home_fragment,f,f.TAG)
                        .addToBackStack(f.TAG)
                        .commit();
                break;
            case R.id.favorite_bottom:
                Fragment4 f4 = null;
                f4 = (Fragment4) manager.findFragmentByTag(f4.TAG);
                if (f4 == null)
                    f4 = new Fragment4();
                manager.beginTransaction()
                        .replace(R.id.home_fragment,f4,f4.TAG)
                        .addToBackStack(f4.TAG)
                        .commit();
                break;
            case R.id.add_bottom:
                Fragment3 f3 = null;
                f3 = (Fragment3) manager.findFragmentByTag(f3.TAG);
                if (f3 == null)
                    f3 = new Fragment3();
                manager.beginTransaction()
                        .replace(R.id.home_fragment,f3,f3.TAG)
                        .addToBackStack(f3.TAG)
                        .commit();
                break;
            case R.id.map_bottom:
                Fragment2 f2 = null;
                f2 = (Fragment2) manager.findFragmentByTag(f2.TAG);
                if (f2 == null)
                    f2 = new Fragment2();
                manager.beginTransaction()
                        .replace(R.id.home_fragment,f2,f2.TAG)
                        .addToBackStack(f2.TAG)
                        .commit();
                break;
            case R.id.profile_bottom:
                Fragment5 f5 = null;
                f5 = (Fragment5) manager.findFragmentByTag(f5.TAG);
                if (f5 == null)
                    f5 = new Fragment5();
                manager.beginTransaction()
                        .replace(R.id.home_fragment,f5,f5.TAG)
                        .addToBackStack(f5.TAG)
                        .commit();
                break;
        }
    }
}

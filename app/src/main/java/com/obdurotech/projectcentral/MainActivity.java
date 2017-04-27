package com.obdurotech.projectcentral;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private View navHeader;
    SimpleDraweeView draweeView;
    private TextView txtName, txtMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String toNavigate = intent.getStringExtra("toNavigate");

        if (toNavigate != null)
        {
            Fragment fragment = null;

            if (toNavigate.equals("home"))
                fragment = new MainScreen();
            if (toNavigate.equals("settings"))
                fragment = new ProfileScreen();
            if (toNavigate.equals("about"))
                fragment = new AboutFragment();
            if (fragment != null)
            {
                fragment.setEnterTransition(new Slide(Gravity.LEFT));
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.flContent, fragment);
                ft.commit();
            }
        }

        else
        {
            if (savedInstanceState == null) {
                Fragment fragment = new MainScreen();
                fragment.setEnterTransition(new Slide(Gravity.LEFT));
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.flContent, fragment);
                ft.commit();
            }
        }


        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        navHeader = nvDrawer.getHeaderView(0);

        draweeView = (SimpleDraweeView) navHeader.findViewById(R.id.profileImageView);
        txtName = (TextView) navHeader.findViewById(R.id.nameTxt);
        txtMail = (TextView) navHeader.findViewById(R.id.emailTxt);

        loadNavHeader();

        setupDrawerContent(nvDrawer);

        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

    }

    private void loadNavHeader() {

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        txtName.setText(user.getDisplayName());
        txtMail.setText(user.getEmail());

        if (user.getPhotoUrl() != null)
        {
            Uri uri = user.getPhotoUrl();
            draweeView.setImageURI(uri);
        }
        else
        {
            draweeView.setImageResource(R.drawable.logo);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        switch(menuItem.getItemId()) {
            case R.id.nav_home:
                fragment = new MainScreen();
                break;
            case R.id.nav_reminders:
                //fragment = new front_page();
                break;
            case R.id.nav_notes:
                //fragment = new RecyclerViewFragment();
                break;
            case R.id.nav_settings:
                fragment = new ProfileScreen();
                break;
            case R.id.nav_about:
                fragment = new AboutFragment();
                break;
            case R.id.nav_task4:
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
            fragment.setExitTransition(new Slide(Gravity.START));
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContent, fragment);
            ft.commit();
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();

    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

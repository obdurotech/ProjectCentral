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
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProjectHome extends AppCompatActivity {

    FirebaseAuth mAuth;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private View navHeader;
    SimpleDraweeView draweeView;
    private TextView projectTitle, txtName, txtMail;

    CardView tasksCard;
    CardView remindersCard;
    CardView mediaCard;
    CardView notesCard;

    String projectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_home);

        Intent intent = getIntent();
        projectName = intent.getStringExtra("project_name");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        projectTitle = (TextView) findViewById(R.id.project_title);

        tasksCard = (CardView) findViewById(R.id.tasks_item);
        remindersCard = (CardView) findViewById(R.id.reminders_item);
        mediaCard = (CardView) findViewById(R.id.media_item);
        notesCard = (CardView) findViewById(R.id.notes_item);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        navHeader = nvDrawer.getHeaderView(0);

        draweeView = (SimpleDraweeView) navHeader.findViewById(R.id.profileImageView);
        txtName = (TextView) navHeader.findViewById(R.id.nameTxt);
        txtMail = (TextView) navHeader.findViewById(R.id.emailTxt);

        projectTitle.setText(projectName);

        loadNavHeader();
        setupDrawerContent(nvDrawer);

        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        setTitle("ProjectHome");

        //Open Media ViewPager
        mediaCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MediaHome.class);
                intent.putExtra("project_name", projectName);
                v.getContext().startActivity(intent);
            }
        });

        //Open Tasks Activity
        tasksCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent taskIntent = new Intent(v.getContext(), TaskHolder.class);
                taskIntent.putExtra("project_name", projectName);
                v.getContext().startActivity(taskIntent);
            }
        });

        //Open Reminders screen
        remindersCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent reminderIntent = new Intent(v.getContext(), ReminderHolder.class);
                reminderIntent.putExtra("project_name", projectName);
                v.getContext().startActivity(reminderIntent);
            }
        });

        //Open Notes screen
        notesCard.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent notesIntent = new Intent(v.getContext(), NotesHolder.class);
                notesIntent.putExtra("project_name", projectName);
                v.getContext().startActivity(notesIntent);
            }
        });
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
        Intent in;
        Fragment fragment = null;
        switch(menuItem.getItemId()) {
            case R.id.nav_home:
                in = new Intent(this, MainActivity.class);
                in.putExtra("toNavigate", "home");
                startActivity(in);
                break;
            case R.id.nav_quicknotes:
                Intent notesIntent = new Intent(getApplicationContext(), NotesHolder.class);
                notesIntent.putExtra("project_name", "none_none");
                startActivity(notesIntent);
                break;
            case R.id.nav_settings:
                in = new Intent(this, MainActivity.class);
                in.putExtra("toNavigate", "settings");
                startActivity(in);
                break;
            case R.id.nav_about:
                in = new Intent(this, MainActivity.class);
                in.putExtra("toNavigate", "about");
                startActivity(in);
                break;
            case R.id.nav_contact:
                in = new Intent(this, MainActivity.class);
                in.putExtra("toNavigate", "contact");
                startActivity(in);
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

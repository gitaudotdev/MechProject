package com.mercury.ubermechanic;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
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
import android.widget.Toast;


import com.mercury.ubermechanic.Common.Common;
import com.mercury.ubermechanic.Fragments.CustomerFragment;
import com.mercury.ubermechanic.Fragments.Home;
import com.mercury.ubermechanic.Fragments.TodoFragment;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences sharedPreference;
//    TextView tvEmail; tvname;


    public static int navItemIndex =0;
    String title ="Home";
    private int backCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);





        final DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header =navigationView.getHeaderView(0);

        TextView stars = header.findViewById(R.id.stars);
        TextView name =header.findViewById(R.id.mechanics_name);
        CircleImageView avatar = header.findViewById(R.id.image_avatar);


        stars.setText(Common.currentUser.getRates());
        name.setText(String.format("%s",Common.currentUser.getName()));


        if(Common.currentUser.getAvatarURL() != null && !TextUtils.isEmpty(Common.currentUser.getAvatarURL())) {
            Picasso.get()
                    .load(Common.currentUser.getAvatarURL())
                    .into(avatar);
        }

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawers();
            }
        });
        navItemIndex =0;

        setFragment(Home.getInstance(),"Mech App");
//        initProfile();

//        if(!isLoggedIn()){
//            startActivity(MainActivity.getIntent(this));
//            finish();
//        }


    }

//    private void initProfile() {
//        User me = sharedPreference.getUser();
//        if (me.getName().trim().isEmpty()) {
//            return;
//        }
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }


        if (navItemIndex == 0) { // If in first page exit app
            if(backCounter == 0){
                backCounter++;
                Toast.makeText(this, "You are on the home screen. Press back again to exit the application", Toast.LENGTH_LONG).show();
            } else{
                finish();
            }
        } else {  // Move to first page
            navItemIndex = 0;
            setFragment(Home.getInstance(), "Mech App");
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(HomeActivity.this,ProfileActivity.class);
            startActivity(intent);
            title="PROFILE SETTINGS";

        }else if(id ==R.id.action_logout){
            Paper.init(this);
            Paper.book().destroy();
            
            Intent intent = new Intent(HomeActivity.this,MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        android.support.v4.app.Fragment fragment = null;

        if (id == R.id.nav_todo) {
            navItemIndex = 1;
            fragment = TodoFragment.getInstance();
            title = "TO DOs";
        } else if (id == R.id.nav_customers) {
            navItemIndex = 2;
            fragment = CustomerFragment.getInstance();
            title = "CUSTOMERS";
        } else if (id == R.id.nav_CheckCustomers) {
            navItemIndex = 3;
           Intent intent = new Intent(HomeActivity.this,Maps.class);
           startActivity(intent);
            title = "To do list";
        } else if (id == R.id.nav_completed_jobs) {
            navItemIndex = 4;
            Intent intent = new Intent(HomeActivity.this,HistoryActivity.class);
            startActivity(intent);
            title = "COMPLETED JOBS";
        }  else if (id == R.id.nav_share) {
            navItemIndex = 4;

            title = "Share";
        }else if (id == R.id.nav_send) {
            navItemIndex = 5;

            title = "Customer";
        }
        if (fragment != null)  setFragment(fragment, title);


        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setFragment(android.support.v4.app.Fragment fragment, String title){
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.placeholder, fragment).addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
        setTitle(title);
    }
}

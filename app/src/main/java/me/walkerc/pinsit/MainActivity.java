package me.walkerc.pinsit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Class fragmentClass;

            switch (item.getItemId()) {
                case R.id.navigation_map:
                    fragmentClass = MapFragment.class;
                    break;
                case R.id.navigation_post:
                    fragmentClass = PostFragment.class;
                    break;
                default:
                    fragmentClass = MapFragment.class;
            }

            setActiveFragment(fragmentClass);

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Show default fragment
        setActiveFragment(MapFragment.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logoutButton) {
            AlertDialog alert = new AlertDialog.Builder(this).create();
            alert.setTitle("Logout");
            alert.setMessage("Are you sure you're ready to logout?");
            alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alert.setButton(AlertDialog.BUTTON_POSITIVE, "Logout",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Logout user and start login activity
                            FirebaseAuth.getInstance().signOut();
                            Intent ai = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(ai);
                        }
                    });
            alert.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setActiveFragment(Class fragmentClass) {
        Fragment fragment;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }
}

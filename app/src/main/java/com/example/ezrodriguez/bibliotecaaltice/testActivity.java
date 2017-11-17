package com.example.ezrodriguez.bibliotecaaltice;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ezrodriguez.bibliotecaaltice.entity.UserProfile;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class testActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , GoogleApiClient.OnConnectionFailedListener , HomeFragment.OnFragmentInteractionListener
        , ProfileFragment.OnFragmentInteractionListener{

    private GoogleApiClient mGoogleApiClient;
    private ImageView hProfileImage;
    private TextView hUserName, hUserMail;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference userReference;
    private FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    private NavigationView navigationView;
    public FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#131313"));
        setTitle(R.string.app_name);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        GoogleSignInOptions mGoogleSignInOptions= new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(testActivity.this)
                .enableAutoManage(testActivity.this,testActivity.this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,mGoogleSignInOptions)
                .build();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //goMainScreen();
                    setUserData(user);
                }else{
                    goLogInScreen();
                }
            }
        };

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setUserData(FirebaseUser user) {
        View hView = navigationView.getHeaderView(0);
        hUserName = hView.findViewById(R.id.userNameMenu);
        hUserMail = hView.findViewById(R.id.userMailMenu);
        hProfileImage = hView.findViewById(R.id.profileImageMenu);

        hUserMail.setText(user.getEmail());
        if(user.getDisplayName() != null && user.getDisplayName() != ""){
            hUserName.setText(user.getDisplayName());
        }else{
            userReference = FirebaseDatabase.getInstance().getReference("userProfile");
            userReference.child(user.getUid()).child("name");
            // Read from the database
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    Iterable<DataSnapshot> children =
                            dataSnapshot.getChildren();
                    UserProfile userProfile = new UserProfile();
                    for (DataSnapshot child : children) {
                        userProfile = child.getValue(UserProfile.class);
                    }
                    hUserName.setText(userProfile.getName());
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(testActivity.this
                            ,"Failed to read value username."
                            ,Toast.LENGTH_SHORT).show();
                }
            });
        }
        if(user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(hProfileImage);
        }else{
            hProfileImage.setImageResource(R.drawable.man);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthListener);
        getSupportFragmentManager().
                beginTransaction().
                add(R.id.home_fragment,
                        HomeFragment.newInstance("",""))
                .addToBackStack("Home")
                .commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mFirebaseAuthListener != null)
            mFirebaseAuth.removeAuthStateListener(mFirebaseAuthListener);
    }

    public void goLogInScreen() {
        Intent intent = new Intent(this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
                int count = getFragmentManager().getBackStackEntryCount();
                if (count == 0) {
                    super.onBackPressed();
                    getFragmentManager().popBackStack();
                } else {
                    getFragmentManager().popBackStack();
                }
            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportFragmentManager().
                beginTransaction().
                replace(R.id.home_fragment,
                        HomeFragment.newInstance("",""))
                .addToBackStack("Home")
                .commit();
            // Handle the camera action
        } else if (id == R.id.nav_catalog) {
            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.home_fragment,
                            CatalogFragment.newInstance("",""))
                    .addToBackStack("Catalog")
                    .commit();

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_account_profile) {
            Bundle bundle = new Bundle();
            bundle.putStringArray("UserData",new String[]{hUserName.getText().toString()
                    ,hUserMail.getText().toString()
                    , user.getPhotoUrl().toString()});

            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            ProfileFragment profileFragment = new ProfileFragment();
            profileFragment.setArguments(bundle);


            fragmentTransaction.replace(R.id.home_fragment,profileFragment)
                    .addToBackStack(null)
                    .commit();

        } else if (id == R.id.nav_log_out) {
            AlertDialog alert = new AlertDialog.Builder(this)
                    .setTitle("Cerrar sesión")
                    .setMessage("¿Esta seguro de finalizar su sesión?")
                    .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mFirebaseAuth.signOut();
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                                @Override
                                public void onResult(@NonNull Status status) {
                                    if(status.isSuccess()){
                                        goLogInScreen();
                                    }else{
                                        Toast.makeText(testActivity.this, R.string.error_log_out
                                                ,Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }
                    })
                    .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create();
            alert.show();


        } else if (id == R.id.nav_contact_us) {

        } else if (id == R.id.nav_about_us) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

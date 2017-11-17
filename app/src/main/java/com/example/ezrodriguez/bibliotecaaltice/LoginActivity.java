package com.example.ezrodriguez.bibliotecaaltice;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private SignInButton mGoogleSignInButton;
    private EditText mEmail, mPassword;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    private LinearLayout mLoginView;
    private ProgressBar mProgressBar;

    public static final int SIGN_IN_CODE = 777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginView = (LinearLayout) findViewById(R.id.login_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        findViewById(R.id.button_SignUp).setOnClickListener(this);
        findViewById(R.id.button_login).setOnClickListener(this);
        mEmail = (EditText) findViewById(R.id.login_username);
        mPassword = (EditText) findViewById(R.id.login_password);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        mGoogleSignInButton = (SignInButton) findViewById(R.id.signInButton);
        mGoogleSignInButton.setOnClickListener(this);
        mGoogleSignInButton.setSize(SignInButton.SIZE_WIDE);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                   goMainScreen();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mFirebaseAuthListener != null)
            mFirebaseAuth.removeAuthStateListener(mFirebaseAuthListener);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View view) {
        int Id = view.getId();
        Intent intent;
        switch (Id){
            case R.id.signInButton:
                intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(intent,SIGN_IN_CODE);
                break;
            case R.id.button_SignUp:
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.button_login:
                mLoginView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                ValidateUser();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount signInAccount) {

        mLoginView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken()
                ,null);

        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mLoginView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        if(!task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, R.string.error_not_firebase_auth
                                    ,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()){
            firebaseAuthWithGoogle(result.getSignInAccount());
        }else{
            Toast.makeText(this,R.string.not_log_in,Toast.LENGTH_SHORT).show();
        }
    }

    private void ValidateUser() {
        mFirebaseAuth.signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            mLoginView.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                            goMainScreen();

                        } else {
                            mLoginView.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
    private void goMainScreen() {
        Intent intent = new Intent(this,testActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
        | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

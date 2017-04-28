package com.obdurotech.projectcentral; //change the package name to your project's package name

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;


public class LoginActivity extends AppCompatActivity{

    EditText nameET;
    String name;
    EditText userNameET;
    EditText passwordET;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 123;
    private static final String USER_CREATION_SUCCESS =  "Successfully created user";
    private static final String USER_CREATION_ERROR =  "User creation error";
    private static final String EMAIL_INVALID =  "email is invalid :";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){

                    if (user.getDisplayName() == null) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name).build();
                        user.updateProfile(profileUpdates);
                    }

                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);  //Replace MainActivity.class with your launcher class from previous assignments
                    //myIntent.putExtra("display_name", user.getDisplayName());
                    LoginActivity.this.startActivity(myIntent);
                    finish();
                }else{

                }

            }
        };

        nameET = (EditText) findViewById(R.id.edit_text_name);
        userNameET = (EditText)findViewById(R.id.edit_text_email);
        passwordET = (EditText)findViewById(R.id.edit_text_password);
        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()
                                        ))
                                .build(),
                        RC_SIGN_IN);
            }
        });

        Button createButton = (Button) findViewById(R.id.button);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == ResultCodes.OK) {

                Intent myIntent = new Intent(LoginActivity.this, MainActivity.class); //Replace MainActivity.class with your launcher class from previous assignments
                LoginActivity.this.startActivity(myIntent);
                finish();

                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    showSnackbar("Sign in cancelled");
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackbar("No network connnection");
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackbar("Error occured while signing in");
                    return;
                }
            }

            showSnackbar("Error occured while signing in");
        }
    }

    public void showSnackbar(String s){
        Snackbar snackbar = Snackbar.make(userNameET,s,Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    // Validate email address for new accounts.
    private boolean isEmailValid(String email) {
        boolean isGoodEmail = (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            userNameET.setError(EMAIL_INVALID + email);
            return false;
        }
        return true;
    }

    // create a new user in Firebase
    public void createUser() {
        if(userNameET.getText() == null ||  !isEmailValid(userNameET.getText().toString())) {
            return;
        }

        name = nameET.getText().toString();

        mAuth.createUserWithEmailAndPassword(userNameET.getText().toString(),passwordET.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Snackbar snackbar = Snackbar.make(userNameET, USER_CREATION_SUCCESS, Snackbar.LENGTH_SHORT);
                    snackbar.show();

                }else{
                    Snackbar snackbar = Snackbar.make(userNameET, USER_CREATION_ERROR, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });
    }
}

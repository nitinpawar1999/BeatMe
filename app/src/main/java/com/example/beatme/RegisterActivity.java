package com.example.beatme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;
    private boolean emailValidate = false;
    private boolean passwordValidate = false;
    private boolean confirmPasswordValidate = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        final EditText nameEditText = findViewById(R.id.name);
        final EditText emailEditText = findViewById(R.id.registerEmail);
        final EditText passwordEditText = findViewById(R.id.registerPassword);
        final  EditText confirmPasswordEditText = findViewById(R.id.confirmPassword);
        final Button registerBtn = findViewById(R.id.registerBtn);
        final TextView loginPage = findViewById(R.id.loginPage);
        final ProgressBar progressBar = findViewById(R.id.registerProgress);

        registerBtn.setEnabled(false);
        if(nameEditText.getText().length() == 0)
            nameEditText.setError("Enter Name!");
        if(emailEditText.getText().length() == 0)
            emailEditText.setError("Enter Email!");
        if(passwordEditText.getText().length() == 0)
            passwordEditText.setError("Enter Password!");
        if(confirmPasswordEditText.getText().length() == 0)
            confirmPasswordEditText.setError("Enter Password!");

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(Patterns.EMAIL_ADDRESS.matcher(s).matches() &&  s.length()>0){
                    emailValidate = true;
                }else{
                    emailValidate=false;
                    emailEditText.setError("Invalid Email!");
                }

                if(emailValidate && passwordValidate && confirmPasswordValidate){
                    registerBtn.setEnabled(true);
                }
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 7){
                    passwordValidate=true;
                }else{
                    passwordValidate=false;
                    passwordEditText.setError("Password must be greater than 8 Character!");
                }

                if(emailValidate && passwordValidate && confirmPasswordValidate){
                    registerBtn.setEnabled(true);
                }
            }
        });

        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(confirmPasswordEditText.getText().toString().equals(passwordEditText.getText().toString())){
                    confirmPasswordValidate = true;
                }else{
                    confirmPasswordValidate = false;
                    confirmPasswordEditText.setError("Password doesn't match!");
                }

                if(emailValidate && passwordValidate && confirmPasswordValidate){
                    registerBtn.setEnabled(true);
                }
            }
        });

        registerBtn.setOnClickListener(v -> {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            hideKeyboard(RegisterActivity.this);
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Log.d(TAG, "creaeteUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest
                                .Builder().setDisplayName(name).build();
                        user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Log.d(TAG, "User name added.");
                                    Toast.makeText(RegisterActivity.this, "User Registration Success...Redirecting to Login Page",
                                            Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            }
                        });

                    }else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterActivity.this, "User Registration failed.\n"+task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            progressBar.setVisibility(ProgressBar.INVISIBLE);
        });
        loginPage.setOnClickListener((View v) -> {
            onBackPressed();
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}

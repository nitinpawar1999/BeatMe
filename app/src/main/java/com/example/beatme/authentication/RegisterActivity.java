package com.example.beatme.authentication;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beatme.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

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
        TextInputLayout nameEditText = findViewById(R.id.name);
        TextInputLayout email = findViewById(R.id.registerEmail);
        TextInputLayout password = findViewById(R.id.registerPassword);
        TextInputLayout confirmPassword = findViewById(R.id.confirmPassword);
        Button registerBtn = findViewById(R.id.registerBtn);
        TextView loginPage = findViewById(R.id.loginPage);
        LinearProgressIndicator progressBar = findViewById(R.id.registerProgress);


        registerBtn.setEnabled(false);
        registerBtn.setBackgroundColor(-7829368);

        Objects.requireNonNull(email.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Patterns.EMAIL_ADDRESS.matcher(s).matches() &&  s.length()>0){
                    emailValidate = true;
                    email.setErrorEnabled(false);
                }else{
                    emailValidate=false;
                    email.setError("Invalid Email!");
                }
                if(emailValidate && passwordValidate && confirmPasswordValidate) {
                    registerBtn.setEnabled(true);
                    registerBtn.setBackgroundColor(Color.rgb(0,121,107));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Objects.requireNonNull(password.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 7){
                    passwordValidate=true;
                    password.setErrorEnabled(false);
                }else{
                    passwordValidate=false;
                    password.setError("Password must be greater than 8 Character!");
                }
                if(emailValidate && passwordValidate && confirmPasswordValidate) {
                    registerBtn.setEnabled(true);
                    registerBtn.setBackgroundColor(Color.rgb(0,121,107));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Objects.requireNonNull(confirmPassword.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!password.getEditText().getText().toString().equals(s.toString())){
                    confirmPassword.setError("Password Doesn't Match");
                    confirmPasswordValidate = false;
                }else{
                    confirmPassword.setErrorEnabled(false);
                    confirmPasswordValidate = true;
                }

                if(emailValidate && passwordValidate && confirmPasswordValidate) {
                    registerBtn.setEnabled(true);
                    registerBtn.setBackgroundColor(Color.rgb(0,121,107));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        registerBtn.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            hideKeyboard(RegisterActivity.this);
            String name = Objects.requireNonNull(nameEditText.getEditText()).getText().toString();
            if(name.isEmpty()) name = "default_name";
            String emailString = email.getEditText().getText().toString();
            String passwordString = password.getEditText().getText().toString();

            String finalName = name;
            mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(this, task -> {
                if(task.isSuccessful()) {
                    Log.d(TAG, "creaeteUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest
                            .Builder().setDisplayName(finalName).build();
                    assert user != null;
                    user.updateProfile(profileChangeRequest).addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()) {
                            Log.d(TAG, "User name added.");
                            Toast.makeText(RegisterActivity.this, "User Registration Success...Redirecting to Login Page",
                                    Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            onBackPressed();
                        }
                    });

                }else {
                    // If sign in fails, display a message to the user.
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(RegisterActivity.this, "User Registration failed.\n"+ Objects.requireNonNull(task.getException()).getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }) ;
        });
        loginPage.setOnClickListener((View v) -> onBackPressed());
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

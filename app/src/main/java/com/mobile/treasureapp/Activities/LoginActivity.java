package com.mobile.treasureapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobile.treasureapp.MainActivity;
import com.mobile.treasureapp.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {      //The login screen


    private EditText etUserEmail,etUserPassword;
    private TextView tvSmallEmail,tvSmallPassword;
    private Button btnSignup,btnLogin;
    private FirebaseAuth mAuth;
    private DatabaseReference mdatabase;
    private String Email,Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        Window window = getWindow();                   //setting status bar color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.NiceBlue));
        CheckifLoggedIn();

        setContentView(R.layout.activity_login);
        etUserEmail=findViewById(R.id.etUserEmail);
        etUserPassword=findViewById(R.id.etUserPassword);
        btnLogin=findViewById(R.id.btnLogin);
        btnSignup=findViewById(R.id.btnSignUp);
        tvSmallEmail=findViewById(R.id.tvEmailSmall);
        tvSmallPassword=findViewById(R.id.tvPasswordSmall);
        mAuth = FirebaseAuth.getInstance();

        mdatabase = FirebaseDatabase.getInstance().getReference("User");
        btnSignup.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        EnableTextListeners();
    }

    public void CheckifLoggedIn(){
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
    }

    public void SignUpUser(){
        mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, UserNameActivity.class);
                            intent.putExtra("UserEmail",Email);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Exception e = task.getException();
                            if (e.getMessage().equals("The email address is badly formatted.")){
                                Toast.makeText(LoginActivity.this, "Invalid Email Address",Toast.LENGTH_LONG).show();
                                tvSmallEmail.setTextColor(Color.parseColor("#FF0000"));
                            }

                            if (Password.length()<6){
                                Toast.makeText(LoginActivity.this, "Password must be at least 6 characters",Toast.LENGTH_LONG).show();
                                tvSmallPassword.setTextColor(Color.parseColor("#FF0000"));
                            }

                        }
                    }
                });

    }


    public void LoginUser(){
        mAuth.signInWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login success.",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failure",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void EnableTextListeners(){                          //as user types, either an "email" or "password" will appear above the respective box-better UI
        etUserEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    //not using
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvSmallEmail.setTextColor(Color.parseColor("#000000"));

                    if (etUserEmail.getText().length()>0){
                        tvSmallEmail.setVisibility(View.VISIBLE);

                    }
                    else{
                        tvSmallEmail.setVisibility(View.INVISIBLE);
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {
                    //not using
            }
        });

        etUserPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //not using
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               tvSmallPassword.setTextColor(Color.parseColor("#000000"));

                if (etUserPassword.getText().length()>0){
                    tvSmallPassword.setVisibility(View.VISIBLE);
                }

                else{
                    tvSmallPassword.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //not using
            }
        });

    }

    @Override
    public void onClick(View v) {
        Email=etUserEmail.getText().toString().trim();
        Password=etUserPassword.getText().toString().trim();

        if (Email.length()==0){
            Toast.makeText(LoginActivity.this, "Please Enter Email Address",Toast.LENGTH_LONG).show();
        }
        if (Password.length()==0) {      //crashes when empty password entered
            Toast.makeText(LoginActivity.this, "Please Enter Password",Toast.LENGTH_LONG).show();
        }

        switch (v.getId()){

            case R.id.btnSignUp:
                if (Password.length()>0 && Email.length()>0){
                    SignUpUser();
                }
                break;

            case R.id.btnLogin:
                if (Password.length()>0 && Email.length()>0){
                    LoginUser();
                }
                break;
        }

    }
}
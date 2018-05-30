package com.example.biespiel_pc.and_2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.biespiel_pc.and_2.Model.LoginModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText Lemail;
    private EditText Lpassword;
    private Button Lsignin;
    private Button Lsignup;
    private TextView Lforgotpass;

    private FirebaseAuth LFirebaseAuth;
    private FirebaseAuth.AuthStateListener LAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Lemail = (EditText) findViewById(R.id.loginemail);
        Lpassword = (EditText)findViewById(R.id.loginpassword);
        Lsignin = (Button) findViewById(R.id.btnsignin);
        Lsignup = (Button) findViewById(R.id.btnsignup);
        Lforgotpass = (TextView) findViewById(R.id.btnForgotPass);

        LFirebaseAuth = FirebaseAuth.getInstance();

        LAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    String uid = user.getUid();
                    Intent intent = new Intent(getApplicationContext(), NaviagationDrawer.class);
                    startActivity(intent);
                }
            }
        };

        Lsignin.setOnClickListener(this);
        Lsignup.setOnClickListener(this);
        Lforgotpass.setOnClickListener(this);
    }

    @Override
    public void onStart(){
        super.onStart();
        LFirebaseAuth.addAuthStateListener(LAuthListener);
    }

    protected void onStop() {
        super.onStop();
        if(LAuthListener!=null){
            finish();
            LFirebaseAuth.removeAuthStateListener(LAuthListener);
        }
    }

    @Override
    public void onClick(View v){
        if(v == Lsignin){
            String email = Lemail.getText().toString().trim();
            String pass = Lpassword.getText().toString().trim();

            LoginModel loginModel = new LoginModel(email,pass);
            if(ValidateUser(loginModel)){
                UserLogin(loginModel);
            }
            else reset();
        }
        else if(v == Lsignup){
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
        else if(v == Lforgotpass){
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        }
    }

    private boolean ValidateUser(LoginModel loginModel) {
        boolean validate = false;
        CharSequence targetEmail = loginModel.getLemail();

        if(TextUtils.isEmpty(loginModel.getLemail())&& TextUtils.isEmpty(loginModel.getLpass())){
            Toast.makeText(this, "Please input your email and password",Toast.LENGTH_SHORT);
        }else if(TextUtils.isEmpty(loginModel.getLemail())){
            Toast.makeText(this, "Please input your email",Toast.LENGTH_SHORT);
        }else if(TextUtils.isEmpty(loginModel.getLpass())){
            Toast.makeText(this, "Please input your password",Toast.LENGTH_SHORT);
        }else if(!Patterns.EMAIL_ADDRESS.matcher(targetEmail).matches()){
            Toast.makeText(this, "Please type your email correctly",Toast.LENGTH_SHORT);
        }else validate=true;
        return validate;
    }

    private void UserLogin(LoginModel loginModel) {

        LFirebaseAuth.signInWithEmailAndPassword(loginModel.getLemail(),loginModel.getLpass())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            finish();
                            Intent intent = new Intent(getApplicationContext(),NaviagationDrawer.class);
                            startActivity(intent);
                        }
                        else{
                            try{
                                throw  task.getException();
                            } catch (FirebaseAuthInvalidUserException e){
                                Toast.makeText(LoginActivity.this,
                                        "Your Email is incorrect",Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                Toast.makeText(LoginActivity.this,
                                        "Your Password is incorrect",Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(LoginActivity.this,
                                        "Could not login at the moment, please try again later", Toast.LENGTH_SHORT).show();
                            }
                            reset();
                        }
                    }
                });
    }

    private void reset() {
        Lemail.setText("");
        Lpassword.setText("");
    }
}

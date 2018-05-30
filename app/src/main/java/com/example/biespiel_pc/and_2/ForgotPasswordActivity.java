package com.example.biespiel_pc.and_2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mEmail;
    private Button mSend;

    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mProgressDialog = new ProgressDialog(this);

        mEmail = (EditText) findViewById(R.id.forgotEmail);
        mSend = (Button) findViewById(R.id.btnSendEmail);

        mSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String email = mEmail.getText().toString().trim();

        if(view == mSend){
            mFirebaseAuth = FirebaseAuth.getInstance();
            resetUser(email);
        }
    }

    private void resetUser(String email) {
        mProgressDialog.setMessage("Sending message...");
        mProgressDialog.show();

        mFirebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialog.dismiss();
                        if(task.isSuccessful()){
                            finish();
                            Toast.makeText(ForgotPasswordActivity.this, "Sending successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            try{
                                throw  task.getException();
                            } catch (FirebaseAuthInvalidUserException e){
                                Toast.makeText(ForgotPasswordActivity.this,
                                        "Sorry, email not registered yet", Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(ForgotPasswordActivity.this,
                                        "Sending failed... Please Try Again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}

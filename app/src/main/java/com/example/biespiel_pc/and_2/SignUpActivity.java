package com.example.biespiel_pc.and_2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.biespiel_pc.and_2.Model.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText suUsername, suFullname, suEmail, suAge, supassword, sucopass;
    private Button signupButton;

    private RadioGroup suGroupGender;
    private RadioButton suGender;

    private FirebaseAuth suFirebaseAuth;
    private FirebaseUser regUser;
    private FirebaseDatabase suDatabase;
    private DatabaseReference suReference;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    private Profile suProfile;
    private ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        suUsername  = (EditText) findViewById(R.id.su_username);
        suFullname = (EditText) findViewById(R.id.su_fullname);
        suEmail = (EditText) findViewById(R.id.su_email);
        suGroupGender = (RadioGroup) findViewById(R.id.su_radiogroupgender);
        suAge = (EditText) findViewById(R.id.su_age);
        supassword = (EditText) findViewById(R.id.su_password);
        sucopass = (EditText) findViewById(R.id.su_cpassword);
        signupButton = (Button) findViewById(R.id.sign_up_button);

        mDialog = new ProgressDialog(this);

        signupButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v)
    {
        suGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == 0){
                    suGender = (RadioButton) findViewById(R.id.su_male);
                } else if(i == 1){
                    suGender = (RadioButton) findViewById(R.id.su_female);
                } else {
                    Toast.makeText(SignUpActivity.this,
                            "Please select your gender",Toast.LENGTH_SHORT).show();
                }

            }
        });

        String username = suUsername.getText().toString().trim();
        String Fullname = suFullname.getText().toString().trim();
        String email = suEmail.getText().toString().trim();
        suGender = findViewById(suGroupGender.getCheckedRadioButtonId());
        int age = Integer.parseInt(suAge.getText().toString().trim());
        String gender = suGender.getText().toString();

        suProfile = new Profile(username,Fullname,email,gender,age);
        if(v == signupButton){
            suFirebaseAuth = FirebaseAuth.getInstance();
            suDatabase = FirebaseDatabase.getInstance();
            mStorage = FirebaseStorage.getInstance();

            if(validateUser(suProfile)){
                regiterUser(suProfile);


            }
        }

    }


    private boolean validateUser(Profile Profile){
        boolean validate = false;
        CharSequence targetEmail = Profile.getEmail();

        if(TextUtils.isEmpty(Profile.getUserName()) ||
                TextUtils.isEmpty(Profile.getFullName())||
                TextUtils.isEmpty(Profile.getEmail())||
                TextUtils.isEmpty(Profile.getGender())||
                TextUtils.isEmpty(String.valueOf(Profile.getAge()))||
                TextUtils.isEmpty(supassword.getText().toString().trim())||
                TextUtils.isEmpty(sucopass.getText().toString().trim())){
            Toast.makeText(this, "Please fill up the form", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(targetEmail).matches()){
            Toast.makeText(this, "Please type your email correctly", Toast.LENGTH_SHORT).show();
        } else if(!supassword.getText().toString().trim().equals(sucopass.getText().toString().trim())){
            Toast.makeText(this, "The confirmed password and the current password doesn't match", Toast.LENGTH_SHORT).show();
        } else {
            validate = true;
        }

        return validate;
    }

    private void regiterUser(final Profile suProfile) {
        mDialog.setMessage("Signing Up...");
        mDialog.show();

        suFirebaseAuth.createUserWithEmailAndPassword(suEmail.getText().toString().trim(),supassword.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this,
                                    "Success", Toast.LENGTH_SHORT).show();

                            regUser = suFirebaseAuth.getCurrentUser();
                            final String uid = regUser.getUid().toString();
                            suReference = suDatabase.getReference();
                            suReference.child(uid).setValue(suProfile ,new DatabaseReference.CompletionListener(){
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if(databaseError!=null){
                                        Toast.makeText(SignUpActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Map<String, Object> uidUpdate = new HashMap<>();
                                        uidUpdate.put("uid", uid);
                                        suReference.child(uid).updateChildren(uidUpdate, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                if(databaseError!=null)
                                                    Toast.makeText(SignUpActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                                                else
                                                    inputDisplayPict(uid);
                                            }
                                        });
                                    }
                                }
                            });
                        }
                        else {
                            try
                            {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e){
                                Toast.makeText(SignUpActivity.this,
                                        "Email already regitered", Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(SignUpActivity.this,
                                        "Could not register at the moment, please try again later", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });


    }


    private void inputDisplayPict(final String uid) {
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.github_256);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data2 = baos.toByteArray();

        mStorageRef = mStorage.getReference("Display Picture").child(uid+".JPEG");
        UploadTask uploadTask = mStorageRef.putBytes(data2);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(SignUpActivity.this, "Fail", Toast.LENGTH_SHORT)
                        .show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                String url = downloadUrl.toString();
                Toast.makeText(SignUpActivity.this, "Success", Toast.LENGTH_SHORT)
                        .show();

                Map<String, Object> pictUpdate = new HashMap<>();
                pictUpdate.put("pict", url);
                suReference.child(uid).updateChildren(pictUpdate, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        mDialog.dismiss();
                        if(databaseError!=null)
                            Toast.makeText(SignUpActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                        else{
                            finish();
                            Intent intent = new Intent(SignUpActivity.this, NaviagationDrawer.class);
                            startActivity(intent);
                        }

                    }
                });
            }
        });
    }
}

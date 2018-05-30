package com.example.biespiel_pc.and_2;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.biespiel_pc.and_2.Model.Profile;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditProfileActivity extends AppCompatActivity {

    private EditText mUserName, mFullName, mAge;
    private ImageView mDisplayPict;
    private Button mSave, mCancel;
    private RadioGroup mRadioGender;
    private RadioButton mButtonGender;

    private final int REQUEST_GALLERY = 200;
    private final int REQUEST_CROP = 400;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mUserName = (EditText) findViewById(R.id.editUserName);
        mFullName = (EditText) findViewById(R.id.editFullName);
        mAge = (EditText) findViewById(R.id.editAge);
        mDisplayPict = (ImageView) findViewById(R.id.image);

        mSave = (Button) findViewById(R.id.profileSave);
        mCancel = (Button) findViewById(R.id.profileCancel);

        mRadioGender = (RadioGroup) findViewById(R.id.radioGender);
        mDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference("Display Picture").child(uid+".JPEG");

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference(uid);
        getPreviousData();

        mDisplayPict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_GALLERY);
            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.setMessage("Saving...");
                mDialog.show();

                //Save Data to Database
                mReference.child("userName").setValue(mUserName.getText().toString());
                mReference.child("fullName").setValue(mFullName.getText().toString());
                mReference.child("age").setValue(Integer.parseInt(mAge.getText().toString()));

                int id = mRadioGender.getCheckedRadioButtonId();
                mButtonGender = (RadioButton) findViewById(id);
                mReference.child("gender").setValue(mButtonGender.getText().toString());

                //Save Picture to Storage
                mDisplayPict.setDrawingCacheEnabled(true);
                mDisplayPict.buildDrawingCache();

                Bitmap bitmap = mDisplayPict.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data2 = baos.toByteArray();

                UploadTask uploadTask = mStorageRef.putBytes(data2);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(EditProfileActivity.this, "Fail", Toast.LENGTH_SHORT)
                                .show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        mDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Success", Toast.LENGTH_SHORT)
                                .show();
                        finish();
//                        Intent intent = new Intent(EditProfileActivity.this, NaviagationDrawer.class);
//                        startActivityForResult(intent, 100);
//                        setResult(Activity.RESULT_OK);
                    }
                });
            }
        });

        mCancel = (Button) findViewById(R.id.profileCancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void getPreviousData(){
        mDialog.setMessage("Loading...");
        mDialog.show();

        //Get Database Data
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile profile = dataSnapshot.getValue(Profile.class);

                mUserName.setText(profile.getUserName());
                mFullName.setText(profile.getFullName());
                mAge.setText(String.valueOf(profile.getAge()));

                String gender = profile.getGender().trim();
                int id;
                if(gender.equals("Male")){
                    id = R.id.radioMale;
                }else if(gender.equals("Female")){
                    id = R.id.radioFemale;
                } else {
                    id = 0;
                }
                mRadioGender.check(id);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditProfileActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });

        //Get Display Picture
        Glide.with(EditProfileActivity.this)
                .using(new FirebaseImageLoader())
                .load(mStorageRef)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(new SimpleTarget<Bitmap>(280, 280){
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        CircularImage ci = new CircularImage();
                        mDisplayPict.setImageBitmap(ci.setCircularImage(resource));
                        mDialog.dismiss();
                    }
                });
    }

    private void performCrop(Uri picUri) {
        try {
            //Start Crop Activity
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, REQUEST_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_GALLERY){
                try {
                    Uri imageUri = data.getData();
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                    performCrop(imageUri);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(EditProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            } else if (requestCode == REQUEST_CROP){
                Bundle extras = data.getExtras();
                Bitmap selectedImage = extras.getParcelable("data");
                CircularImage ci = new CircularImage();
                Bitmap outputImage = ci.setCircularImage(selectedImage);

                mDisplayPict.setImageBitmap(outputImage);
            }

        } else {
            Toast.makeText(EditProfileActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}

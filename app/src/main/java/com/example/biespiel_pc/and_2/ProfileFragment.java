package com.example.biespiel_pc.and_2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.biespiel_pc.and_2.Model.Profile;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Res Non Verba on 16/11/2017.
 */

public class ProfileFragment extends Fragment {

    private TextView mUserName, mFullName, mEmail, mGender, mAge;
    private Button mEditButton;
    private ImageView mDisplayPict;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    private ProgressDialog mDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUserName = (TextView) view.findViewById(R.id.userName);
        mFullName = (TextView) view.findViewById(R.id.fullName);
        mEmail = (TextView) view.findViewById(R.id.email);
        mGender = (TextView) view.findViewById(R.id.gender);
        mAge = (TextView) view.findViewById(R.id.age);
        mDisplayPict = (ImageView) view.findViewById(R.id.image);
        mDialog = new ProgressDialog(getActivity());

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference("Display Picture").child(uid+".JPEG");

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference(uid);

        mEditButton = (Button) view.findViewById(R.id.editProfile);
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    public void getData(){
        mDialog.setMessage("Loading...");
        mDialog.show();

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Profile profile = dataSnapshot.getValue(Profile.class);

                mUserName.setText(profile.getUserName());
                mFullName.setText(profile.getFullName());
                mEmail.setText(profile.getEmail());
                mGender.setText(profile.getGender());
                mAge.setText(String.valueOf(profile.getAge()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });

        //Get Display Picture
        Glide.with(getActivity())
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
}

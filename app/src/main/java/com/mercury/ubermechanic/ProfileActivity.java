package com.mercury.ubermechanic;



import android.app.ProgressDialog;
import android.content.Intent;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mercury.ubermechanic.Common.Common;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ProfileActivity extends AppCompatActivity {
    MaterialEditText edtEmail, edtPhone, edtBirthday;
    ImageView imageAvatar;
    Button btnCancel, btnSave;
    RadioGroup radio;

    FirebaseStorage storage;
    StorageReference store;

    private String mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("My Profile");


        storage  = FirebaseStorage.getInstance();
        store = storage.getReference();


        radio = findViewById(R.id.radioGroup);
        edtEmail =findViewById(R.id.edtEmail);
        edtBirthday = findViewById(R.id.edtbirthday);
        edtPhone = findViewById(R.id.edtPhone);
        imageAvatar = findViewById(R.id.imageAvatar);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String email = edtEmail.getText().toString();
                                String phone = edtPhone.getText().toString();
                                String Birthday = edtBirthday.getText().toString();
                                int select = radio.getCheckedRadioButtonId();
                                final RadioButton radios = findViewById(select);

                                if (radios.getText() == null) {
                                    Toast.makeText(ProfileActivity.this, "Please Select Service", Toast.LENGTH_SHORT).show();

                                }

                                mService = radios.getText().toString();

                                Map<String, Object> update = new HashMap<>();
                                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(Birthday)) {
                                    update.put("email", email);
                                    update.put("phone", phone);
                                    update.put("Birthday", Birthday);
                                    update.put("Service", mService);
                                }
                                if (update.get("Service") != null)
                                    mService = update.get("Service").toString();
                                switch (mService) {
                                    case "maintenance":
                                        radio.check(R.id.maintenance);
                                        break;
                                    case "Truck Servicing":
                                        radio.check(R.id.trucks);
                                        break;
                                    case "Bike Repairs":
                                        radio.check(R.id.bikes);
                                        break;
                                    case "Engine Repair":
                                        radio.check(R.id.engine);
                                        break;

                                }

                        DatabaseReference driverInfo = FirebaseDatabase.getInstance().getReference(Common.user_mechanic_tbl);
                        driverInfo.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                    Toast.makeText(ProfileActivity.this, "Information Updated!...", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(ProfileActivity.this, "Information Wasn't updated Try Again Later!!", Toast.LENGTH_SHORT).show();
                            }
                        });






                Intent intent = new Intent(ProfileActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });


        imageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageandUpload();
            }
        });


    }

    private void chooseImageandUpload() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data !=null && data.getData() != null){
            Uri saveUri = data.getData();
            if(saveUri != null){
                final ProgressDialog mdialog = new ProgressDialog(this);
                mdialog.setMessage("Uploading....");
                mdialog.show();

                String image = UUID.randomUUID().toString();
                final StorageReference imagefolder = store.child("photos/"+image);
                imagefolder.putFile(saveUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                mdialog.dismiss();
                                Toast.makeText(ProfileActivity.this, "Uploaded...", Toast.LENGTH_SHORT).show();
                                imagefolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(final Uri uri) {

                                                //updating the url to the user
                                                Map<String,Object> image = new HashMap<>();
                                                image.put("avatarUrl",uri.toString());



                                                DatabaseReference dataref = FirebaseDatabase.getInstance().getReference(Common.user_mechanic_tbl);
                                                dataref.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .updateChildren(image)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                    Toast.makeText(ProfileActivity.this, "Uploaded...", Toast.LENGTH_SHORT).show();
                                                                else
                                                                    Toast.makeText(ProfileActivity.this, "Upload Failed...", Toast.LENGTH_SHORT).show();

                                                            }
                                                        });
                                            }



                                });

                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                                mdialog.setMessage("Uploading..."+progress+"%");
                            }
                        });


            }
        }
    }
}





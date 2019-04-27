package com.example.mahmud.travelmate;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahmud.travelmate.Interface.CheckPermission;
import com.example.mahmud.travelmate.Interface.LoginAction;
import com.example.mahmud.travelmate.Interface.RegToLoginListener;
import com.example.mahmud.travelmate.Interface.SignUpAction;
import com.example.mahmud.travelmate.POJO.User;
import com.example.mahmud.travelmate.POJO.UserWithImg;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pl.droidsonroids.gif.GifImageView;


public class RegisterFragment extends Fragment {
    //Declaring View Widget-------------------------------------------------------------------------
    private EditText mNameET,mEmailET,mPhoneET,mPasswordET;
    private Button mRegisterBT;
    //Declaring Firebase Variables
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mUserRef;
    private LoginAction loginAction;
    private RegToLoginListener regToLoginListener;
    private CheckPermission checkPermission;
    private GifImageView mLoadingGIF;
    //Essential
    private Context context;
    //Constructor
    public RegisterFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        loginAction = (LoginAction) context;
        regToLoginListener = (RegToLoginListener) context;
        checkPermission = (CheckPermission) context;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Setting Action Bar Title
        //getActivity().setTitle("Sign Up");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        //Initializing View Widget------------------------------------------------------------------
        mNameET = v.findViewById(R.id.name_et_fr);
        mEmailET = v.findViewById(R.id.email_et_fr);
        mPhoneET = v.findViewById(R.id.phone_et_fr);
        mPasswordET = v.findViewById(R.id.password_et_fr);
        mRegisterBT = v.findViewById(R.id.register_bt_fr);
        mLoadingGIF = v.findViewById(R.id.loading_gif_rf);


        //Listener Operation
        mRegisterBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email,password,name,phone;
                name = mNameET.getText().toString();
                phone = mPhoneET.getText().toString();
                email = mEmailET.getText().toString();
                password = mPasswordET.getText().toString();
                //Validation
                if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()){
                    Toast.makeText(context, "Fill all the blanks", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!checkPermission.isPermissionGiven()){
                    Toast.makeText(context, "You haven't given permission to location and storage", Toast.LENGTH_SHORT).show();
                    return;
                }
                mLoadingGIF.setVisibility(View.VISIBLE);
                //Creating Database User
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //Getting User References
                            mAuth = FirebaseAuth.getInstance();
                            mUser = mAuth.getCurrentUser();
                            //User Firebase references
                            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
                            UserWithImg userWithImg = new UserWithImg(name,phone," "," ");
                            //Writing to firebase
                            mUserRef.child(mUser.getUid()).setValue(userWithImg);
                            //Starting new Activity
                            mLoadingGIF.setVisibility(View.GONE);
                            loginAction.onLoginSuccessfull();
                            /*Intent intent = new Intent(context,MainActivity.class);
                            startActivity(intent);*/
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mLoadingGIF.setVisibility(View.GONE);
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        //Handling back press
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    regToLoginListener.backToLogin();
                    return true;
                }
                return false;
            }
        });
        return v;
    }



}

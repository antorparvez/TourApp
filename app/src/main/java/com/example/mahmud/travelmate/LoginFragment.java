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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahmud.travelmate.Interface.CheckPermission;
import com.example.mahmud.travelmate.Interface.HandlingBackInEventListener;
import com.example.mahmud.travelmate.Interface.LoginAction;
import com.example.mahmud.travelmate.Interface.SignUpAction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pl.droidsonroids.gif.GifImageView;


public class LoginFragment extends Fragment {
    //View Widget Reference-------------------------------------------------------------------------
    private EditText mEmailET,mPasswordET;
    private Button mLoginBT,mSignUpBT;
    private GifImageView mLoadingIMG;
    //Interfaces------------------------------------------------------------------------------------
    private LoginAction loginAction;
    private SignUpAction signUpAction;
    private HandlingBackInEventListener backInEventListener;
    private CheckPermission checkPermission;
    //Firebase--------------------------------------------------------------------------------------
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    //Essential-------------------------------------------------------------------------------------
    private Context context;

    //Constructor-----------------------------------------------------------------------------------
    public LoginFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        loginAction = (LoginAction) context;
        signUpAction = (SignUpAction) context;
        backInEventListener = (HandlingBackInEventListener) context;
        checkPermission = (CheckPermission) context;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        //Initializing View Widgets-----------------------------------------------------------------
        mEmailET = v.findViewById(R.id.email_et_lf);
        mPasswordET = v.findViewById(R.id.password_et_lf);
        mLoginBT = v.findViewById(R.id.login_bt_lf);
        mSignUpBT = v.findViewById(R.id.sign_up_bt_lf);
        mLoadingIMG = v.findViewById(R.id.loading_login_img_lf);

        //getting data on rotate-----------------------------------sssssssssssssssssssssssssssssssss
        /*if (savedInstanceState != null && savedInstanceState.containsKey("Email") && savedInstanceState.containsKey("Pass")){
            Toast.makeText(context, "Bungle not null", Toast.LENGTH_SHORT).show();
            String eemail = savedInstanceState.getString("Email","");
            String ppass = savedInstanceState.getString("Pass","");
            Toast.makeText(context, "oncreate view Email is "+eemail+" :::Pass is "+ppass, Toast.LENGTH_SHORT).show();
            mEmailET.setText(eemail);
            mPasswordET.setText(ppass);
        } else {
            Toast.makeText(context, "Bungle is null", Toast.LENGTH_SHORT).show();
        }*/
        //getting data on rotate------------------------------------tttttttttttttttttttttttttttttttt

        //Setting Listener--------------------------------------------------------------------------
        mLoginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingIMG.setVisibility(View.VISIBLE);
                String email,password;
                email = mEmailET.getText().toString();
                password = mPasswordET.getText().toString();
                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(context, "Please Fill up all", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!checkPermission.isPermissionGiven()){
                    Toast.makeText(context, "You haven't given permission to location and storage", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            mLoadingIMG.setVisibility(View.GONE);
                            loginAction.onLoginSuccessfull();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        mLoadingIMG.setVisibility(View.GONE);
                    }
                });
            }
        });
        mSignUpBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpAction.signUpOperation();
            }
        });

        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
                    backInEventListener.eventInBackListener();
                }
                return true;
            }
        });


        return v;
    }
    //Saving data on rotate------------------------------------------sssssssssssssssssssssssssssssss
    /*@Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Toast.makeText(context, "saving called", Toast.LENGTH_SHORT).show();
        String eemail = mEmailET.getText().toString();
        String ppass = mPasswordET.getText().toString();
        if (eemail.isEmpty() || ppass.isEmpty()){
            return;
        }
        outState.putString("Email",eemail);
        outState.putString("Pass",ppass);
        Toast.makeText(context, "Email is "+eemail+" :::Pass is "+ppass, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);


    }*/
    //Saving data on rotate------------------------------------------ttttttttttttttttttttttttttttttt
}

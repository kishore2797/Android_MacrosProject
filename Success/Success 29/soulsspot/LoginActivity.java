package com.deffe.macros.soulsspot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity
{

    private Toolbar LoginToolbar;

    private EditText LoginUserMobileNumber;

    private EditText LoginUserReceivedOTPCode;

    private Button LoginUserSendOTPCodeButton;

    private Button LoginUserReceivedOTPCodeVerifyButton;
    private Button LoginUserResendOTPCodeButton;

    private String mVerificationId;
    private static String userID;

    private boolean check = true;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference storeUserDefaultDataReference;
    private DatabaseReference CheckDataReference;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        storeUserDefaultDataReference = FirebaseDatabase.getInstance().getReference().child("Users");

        LoginUserMobileNumber = (EditText)findViewById(R.id.login_user_mobile_number);

        LoginUserReceivedOTPCode = (EditText) findViewById(R.id.login_user_received_otp_code);

        LoginUserSendOTPCodeButton = (Button) findViewById(R.id.login_user_send_otp_code_button);

        LoginUserReceivedOTPCodeVerifyButton = (Button) findViewById(R.id.login_user_received_otp_code_verify_button);
        LoginUserResendOTPCodeButton = (Button) findViewById(R.id.login_user_resend_otp_code_button);

        getUserCountry(LoginActivity.this);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
        {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e)
            {
                Toast.makeText(LoginActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();

                if (e instanceof FirebaseAuthInvalidCredentialsException)
                {
                    Toast.makeText(LoginActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                }
                else if (e instanceof FirebaseTooManyRequestsException)
                {

                }
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken)
            {
                super.onCodeSent(s, forceResendingToken);

                Toast.makeText(LoginActivity.this, "Code has been Sent", Toast.LENGTH_SHORT).show();

                mVerificationId = s;

                mResendToken = forceResendingToken;

                LoginUserMobileNumber.setVisibility(View.GONE);
                LoginUserSendOTPCodeButton.setVisibility(View.GONE);

                LoginUserReceivedOTPCode.setVisibility(View.VISIBLE);
                LoginUserReceivedOTPCodeVerifyButton.setVisibility(View.VISIBLE);
                LoginUserResendOTPCodeButton.setVisibility(View.VISIBLE);
            }
        };

        LoginUserSendOTPCodeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String userMobileNumber = LoginUserMobileNumber.getText().toString();

                RegisterUserAccount(userMobileNumber);

                PhoneAuthProvider.getInstance().verifyPhoneNumber
                        (
                                "+91" + userMobileNumber, 60, TimeUnit.SECONDS, LoginActivity.this, mCallbacks
                        );


            }
        });
        LoginUserReceivedOTPCodeVerifyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String code = LoginUserReceivedOTPCode.getText().toString();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

                signInWithPhoneAuthCredential(credential);
            }
        });
        LoginUserResendOTPCodeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String mobileNumber = LoginUserMobileNumber.getText().toString();

                PhoneAuthProvider.getInstance().verifyPhoneNumber
                        (
                                "+91" + mobileNumber,60, TimeUnit.SECONDS, LoginActivity.this, mCallbacks, mResendToken
                        );
            }
        });

    }
    public static String getUserCountry(Context context)
    {
        try
        {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2)
            {
                // SIM country code is available

                Toast.makeText(context, simCountry.toLowerCase(Locale.US), Toast.LENGTH_SHORT).show();

                return simCountry.toLowerCase(Locale.US);
            }
            else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA)
            {
                // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2)
                {
                    // network country code is available
                    Toast.makeText(context, networkCountry.toLowerCase(Locale.US), Toast.LENGTH_SHORT).show();

                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        }
        catch (Exception e)
        {

        }
        return null;
    }










    private void RegisterUserAccount(String userMobileNumber)
    {
        if (TextUtils.isEmpty(userMobileNumber))
        {
            Toast.makeText(this, "Please Enter Your Mobile Number Properly", Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    Toast.makeText(LoginActivity.this, "Verification Completed", Toast.LENGTH_SHORT).show();

                    final String online_user_id = firebaseAuth.getCurrentUser().getUid();
                    final String DeviceToken = FirebaseInstanceId.getInstance().getToken();
                    final String userMobileNumber = LoginUserMobileNumber.getText().toString();

                    CheckDataReference = FirebaseDatabase.getInstance().getReference();

                    CheckDataReference.child("Users").child(online_user_id).addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.getValue() != null)
                            {
                                storeUserDefaultDataReference.child(online_user_id).child("device_token").setValue(DeviceToken)
                                        .addOnSuccessListener(new OnSuccessListener<Void>()
                                        {
                                            @Override
                                            public void onSuccess(Void aVoid)
                                            {
                                                Intent userInviteProfileIntent = new Intent(LoginActivity.this,InviteProfileActivity.class);
                                                userInviteProfileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(userInviteProfileIntent);
                                                finish();
                                            }
                                        });
                            }
                            else
                            {
                                storeUserDefaultDataReference.child(online_user_id).child("user_mobile_number").setValue(userMobileNumber);
                                storeUserDefaultDataReference.child(online_user_id).child("user_welcome_status").setValue("Hey Hi Guys,I'm using Macro-Grids");
                                storeUserDefaultDataReference.child(online_user_id).child("user_img").setValue("default_profile");
                                storeUserDefaultDataReference.child(online_user_id).child("user_thumb_img").setValue("default_image");
                                storeUserDefaultDataReference.child(online_user_id).child("user_unique_id").setValue(online_user_id);
                                storeUserDefaultDataReference.child(online_user_id).child("friends").setValue("0");
                                storeUserDefaultDataReference.child(online_user_id).child("device_token").setValue(DeviceToken)
                                        .addOnCompleteListener(new OnCompleteListener<Void>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if (task.isSuccessful())
                                                {

                                                    Intent userInviteProfileIntent = new Intent(LoginActivity.this, InviteProfileActivity.class);
                                                    userInviteProfileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(userInviteProfileIntent);
                                                    finish();
                                                }
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {

                        }
                    });
                }
                else
                {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                    {
                        Toast.makeText(LoginActivity.this, "Invalid Verification", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}

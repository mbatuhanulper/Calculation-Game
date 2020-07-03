package com.dtsetr.gamekitdemo.huawei;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dtsetr.gamekitdemo.huawei.features.common.Constant;
import com.dtsetr.gamekitdemo.huawei.features.fragments.MenuFragment;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.api.services.drive.DriveScopes;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.auth.api.signin.HuaweiIdSignIn;
import com.huawei.hms.auth.api.signin.HuaweiIdSignInClient;
import com.huawei.hms.auth.api.signin.HuaweiIdSignInResult;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.Player;
import com.huawei.hms.jos.games.PlayersClient;
import com.huawei.hms.support.api.entity.auth.Scope;
import com.huawei.hms.support.api.hwid.HuaweiId;
import com.huawei.hms.support.api.hwid.HuaweiIdSignInOptions;
import com.huawei.hms.support.api.hwid.SignInHuaweiId;

import org.json.JSONException;

import java.util.Objects;

import static com.dtsetr.gamekitdemo.huawei.features.common.Constant.REQUEST_SIGN_IN_LOGIN;

public class MainActivity extends AppCompatActivity {
    public static String TAG="Main";
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    protected SignInHuaweiId mSignInHuaweiId;
    private final static int SIGN_IN_INTENT = 1001;
    private Button loginButton;
    private ProgressBar progressBar;
    private Context context;
    protected String accessToken;
    protected String unionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progressBarLogin);
        getToken();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable())
                {
                    signIn();
                    progressBar.setVisibility(View.VISIBLE);
                }
                else{
                    Toast.makeText(v.getContext(),"No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        requestPermissions(PERMISSIONS_STORAGE, 1);


    }
    private void getToken() {
        Log.i(TAG, "get token: begin");
        new Thread() {
            @Override
            public void run() {
                try {
                    String appId = AGConnectServicesConfig.fromContext(MainActivity.this).getString("client/app_id");
                    String pushToken = HmsInstanceId.getInstance(MainActivity.this).getToken(appId, "HCM");
                    if(!TextUtils.isEmpty(pushToken)) {
                        Log.i(TAG, "get token:" + pushToken);
                    }
                    else
                        Log.i(TAG, "null token");
                } catch (Exception e) {
                    Log.i(TAG,"getToken failed, " + e);
                }
            }
        }.start();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void signIn(){
        HuaweiIdSignInOptions signInOptions = new HuaweiIdSignInOptions.Builder(HuaweiIdSignInOptions.DEFAULT_SIGN_IN)
                .requestAccessToken()
                .requestIdToken("")
                .requestScopes(HuaweiId.HUAEWEIID_BASE_SCOPE)
                .requestScopes(new Scope(DriveScopes.DRIVE))
                .requestScopes(new Scope(DriveScopes.DRIVE_METADATA))
                .requestScopes(new Scope(DriveScopes.DRIVE_METADATA_READONLY))
                .requestScopes(new Scope(DriveScopes.DRIVE_READONLY))
                .build();
        HuaweiIdSignInClient client = HuaweiIdSignIn.getClient(MainActivity.this, signInOptions);
        startActivityForResult(client.getSignInIntent(), REQUEST_SIGN_IN_LOGIN);
        //TODO after presentation will se silent login
        /* HuaweiIdSignInOptions mSignInOptions = null;
        HuaweiIdSignInOptions huaweiIdSignInOptions = new HuaweiIdSignInOptions.Builder(HuaweiIdSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestAccessToken()
                .requestIdToken("")
                .requestScopes(HuaweiId.HUAEWEIID_BASE_SCOPE)
                .requestScopes(new Scope(DriveScopes.DRIVE))
                .requestScopes(new Scope(DriveScopes.DRIVE_METADATA))
                .requestScopes(new Scope(DriveScopes.DRIVE_METADATA_READONLY))
                .requestScopes(new Scope(DriveScopes.DRIVE_READONLY))
                .build();
        Task<SignInHuaweiId> signInHuaweiIdTask = HuaweiIdSignIn.getClient(MainActivity.this, huaweiIdSignInOptions).silentSignIn();
        HuaweiIdSignInClient mSignInClient = HuaweiIdSignIn.getClient(MainActivity.this, mSignInOptions);
        startActivityForResult(mSignInClient.getSignInIntent(), REQUEST_SIGN_IN_LOGIN);
        signInHuaweiIdTask.addOnSuccessListener(new OnSuccessListener<SignInHuaweiId>() {
            @Override
            public void onSuccess(SignInHuaweiId signInHuaweiId) {
                mSignInHuaweiId = signInHuaweiId;
                loginButton.setVisibility(View.GONE);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container,new MenuFragment());
                ft.commit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    signInNewWay();
                }
            }
        });*/
    }

    /*public void signInNewWay() {
        HuaweiIdSignInOptions huaweiIdSignInOptions =
                new HuaweiIdSignInOptions.Builder(HuaweiIdSignInOptions.DEFAULT_GAMES_SIGN_IN).build();
        Intent intent = HuaweiIdSignIn.getClient(MainActivity.this, huaweiIdSignInOptions).getSignInIntent();
        startActivityForResult(intent, SIGN_IN_INTENT);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (SIGN_IN_INTENT == requestCode) {
            Task<SignInHuaweiId> signInHuaweiIdTask = HuaweiIdSignIn.getSignedInAccountFromIntent(data);
            if (signInHuaweiIdTask.isSuccessful()) {
                ((FrameLayout) findViewById(R.id.container)).removeAllViews();
                handleSignInResult(data);
                SignInHuaweiId huaweiAccount = signInHuaweiIdTask.getResult();
                accessToken = huaweiAccount.getAccessToken();
                unionId = huaweiAccount.getUnionId();
                loginButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);


                Bundle args = new Bundle();
                args.putString("accessToken", accessToken);
                args.putString("unionId",unionId);
                MenuFragment menuFragment = new MenuFragment();
                menuFragment.setArguments(args);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container,menuFragment);
                ft.commit();

               /* FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container, new MenuFragment());
                ft.commit();*/
            }
        }
        else{
            FragmentManager mFragmentManager = getSupportFragmentManager();;
            for(Fragment fragment : mFragmentManager.getFragments()){
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void handleSignInResult(Intent data) {
        if (null == data) {
            return;
        }

        String jsonSignInResult = data.getStringExtra("HUAWEIID_SIGNIN_RESULT");
        if (TextUtils.isEmpty(jsonSignInResult)) {
            return;
        }
        try {
            HuaweiIdSignInResult signInResult = new HuaweiIdSignInResult().fromJson(jsonSignInResult);
            if (0 == signInResult.getStatus().getStatusCode()) {
                mSignInHuaweiId = signInResult.getSignInHuaweiId();
            } else {
                mSignInHuaweiId = null;
            }
        } catch (JSONException var7) {
            mSignInHuaweiId = null;
        }
    }

    public SignInHuaweiId getSignInHuaweiId(){
        return mSignInHuaweiId;
    }

}



package com.dtsetr.gamekitdemo.huawei.features.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsetr.gamekitdemo.huawei.MainActivity;
import com.dtsetr.gamekitdemo.huawei.R;
import com.dtsetr.gamekitdemo.huawei.features.achievement.AchievementListFragment;
import com.dtsetr.gamekitdemo.huawei.features.common.ConnectClientSupport;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.auth.api.signin.HuaweiIdSignIn;
import com.huawei.hms.auth.api.signin.HuaweiIdSignInClient;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.JosApps;
import com.huawei.hms.jos.JosAppsClient;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.Player;
import com.huawei.hms.jos.games.PlayersClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.game.HuaweiGame;
import com.huawei.hms.support.api.game.ShowFloatWindowResult;
import com.huawei.hms.support.api.hwid.SignInHuaweiId;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;
import static com.huawei.hms.framework.network.upload.internal.core.UploadRequestProcessor.TAG;


public class MenuFragment extends Fragment {

    private TextView userName;
    private Context context;
    private SignInHuaweiId mSignInHuaweiId;
    private HuaweiIdSignInClient mSignInClient;
    private String accessToken;
    private String unionId;

    public MenuFragment() {
        // Required empty public constructor
    }


    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            accessToken=getArguments().getString("accessToken");
            unionId=getArguments().getString("unionId");
        }
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        ImageButton logout = getActivity().findViewById(R.id.logout_button);
        Button newGame = getActivity().findViewById(R.id.newGame_button);
        Button achievmentButton = getActivity().findViewById(R.id.achievment_button);
        Button loadGameButton = getActivity().findViewById(R.id.loadGame_button);
        userName = getActivity().findViewById(R.id.userNameText);
        context= view.getContext();
        mSignInHuaweiId=((MainActivity)getActivity()).getSignInHuaweiId();
        mSignInClient= HuaweiIdSignIn.getClient(context, null);
        init();
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                args.putString("accessToken", accessToken);
                args.putString("unionId",unionId);
                GameFragment gameFragment = new GameFragment();
                gameFragment.setArguments(args);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container,gameFragment);
                ft.addToBackStack(null).commit();

            }
        });

        loadGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("Save", MODE_PRIVATE);
                if(prefs.getInt("score",0)!=0){
                    Bundle args = new Bundle();
                    args.putInt("score", prefs.getInt("score", 0));
                    GameFragment gameFragment = new GameFragment();
                    gameFragment.setArguments(args);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.container,gameFragment);
                    ft.addToBackStack(null).commit();
            }
                else{
                    Toast.makeText(getActivity(),"You haven't any saved game !", Toast.LENGTH_SHORT).show();
                }
            }
        });


        achievmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container,new AchievementListFragment());
                ft.addToBackStack(null).commit();

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitDialog();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        showFloatWindow();
    }

    private static class IShowFloatConnectCallBack implements  ConnectClientSupport.IConnectCallBack {
        private MainActivity activity;
        private IShowFloatConnectCallBack(MainActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onResult(HuaweiApiClient apiClient) {
            //if (apiClient != null && activity!= null && !activity.isFinishing())
            if (apiClient != null && activity!= null && !activity.isFinishing()) {
                PendingResult<ShowFloatWindowResult> pendingRst = HuaweiGame.HuaweiGameApi.showFloatWindow(apiClient,activity);
                pendingRst.setResultCallback(new ResultCallback<ShowFloatWindowResult>() {
                    @Override
                    public void onResult(ShowFloatWindowResult showFloatWindowResult) {

                    }
                });
            }
        }
    }

    private void showFloatWindow() {
        ConnectClientSupport.get().connect(getActivity(), new IShowFloatConnectCallBack(((MainActivity)getActivity())));
    }

    private void quitDialog(){

        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Attention!")
                .setContentText("Are you sure want to quit ?")
                .setConfirmText("YES")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        signOut();
                    }
                })
                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();

    }

    private void init(){
        JosAppsClient appsClient = JosApps.getJosAppsClient(Objects.requireNonNull(getActivity()), mSignInHuaweiId);
        appsClient.init();
        getCurrentPlayer();
    }

    private void signOut() {
        Task<Void> signOutTask = mSignInClient.signOut();
        signOutTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "signOut Success");


                Task<Void> signOutTask = mSignInClient.signOut();
                signOutTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "signOut Success");
                        Objects.requireNonNull(getActivity()).finish();
                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.i(TAG, "signOut fail");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.i(TAG, "signOut fail");
            }
        });
    }


    private void getCurrentPlayer() {
        PlayersClient client = Games.getPlayersClient(Objects.requireNonNull(getActivity()), mSignInHuaweiId);
        Task<Player> task = client.getCurrentPlayer();
        task.addOnSuccessListener(new OnSuccessListener<Player>() {
            @Override
            public void onSuccess(Player player) {
                String result = "displayName:" + player.getDisplayName() + "\n" + "playerId:" + player.getPlayerId()
                        + "\n" + "playerlevel:" + player.getLevelInfo().getCurrentLevel().getLevelNumber() + "\n" + "ts:"
                        + player.getSignTs() + "\n" + "playerSign:" + player.getPlayerSign();

                userName.setText(player.getDisplayName());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    String result = "rtnCode:" + ((ApiException) e).getStatusCode();

                }
            }
        });
    }

}

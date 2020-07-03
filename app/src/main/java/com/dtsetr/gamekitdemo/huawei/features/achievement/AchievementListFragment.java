package com.dtsetr.gamekitdemo.huawei.features.achievement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.dtsetr.gamekitdemo.huawei.MainActivity;
import com.dtsetr.gamekitdemo.huawei.R;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.jos.games.AchievementsClient;
import com.huawei.hms.jos.games.AnnotatedData;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.jos.games.achievement.Achievement;
import com.huawei.hms.jos.games.achievement.AchievementBuffer;
import com.huawei.hms.support.api.hwid.SignInHuaweiId;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;


public class AchievementListFragment extends Fragment {
    private SignInHuaweiId signInHuaweiId;
    private RecyclerView recyclerView;
    private AchievementsClient client;
    private ArrayList<Achievement> achievements = new ArrayList<>();
    private Context context;
    private ProgressBar progressBarAchieve;


    public AchievementListFragment() {
    }


    public static AchievementListFragment newInstance(String param1, String param2) {
        AchievementListFragment fragment = new AchievementListFragment();
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
        return inflater.inflate(R.layout.fragment_achievment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        signInHuaweiId =((MainActivity) Objects.requireNonNull(getActivity())).getSignInHuaweiId();
        recyclerView= getActivity().findViewById(R.id.items_recycler);
        context=view.getContext();
        progressBarAchieve=getActivity().findViewById(R.id.progressBarAchieve);
        initData();
        requestData();
    }

    private void requestData() {
        Task<AnnotatedData<AchievementBuffer>> task = client.load(true);
        task.addOnSuccessListener(new OnSuccessListener<AnnotatedData<AchievementBuffer>>() {
            @Override
            public void onSuccess(AnnotatedData<AchievementBuffer> data) {
                AchievementBuffer achievementBuffer = data.get();
                if (achievementBuffer == null) {
                    return;
                }
                Iterator<Achievement> iterator = achievementBuffer.iterator();
                achievements.clear();
                while (iterator.hasNext()) {
                    Achievement achievement = iterator.next();
                    achievements.add(achievement);
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(layoutManager);
                AchievementListAdapter adapter = new AchievementListAdapter(context, achievements);
                recyclerView.setAdapter(adapter);
                progressBarAchieve.setVisibility(View.GONE);
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

    private void initData() {
        client = Games.getAchievementsClient(Objects.requireNonNull(getActivity()), signInHuaweiId);
        progressBarAchieve.setVisibility(View.VISIBLE);
    }

}

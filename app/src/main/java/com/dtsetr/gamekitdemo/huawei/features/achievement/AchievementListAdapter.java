package com.dtsetr.gamekitdemo.huawei.features.achievement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dtsetr.gamekitdemo.huawei.R;
import com.huawei.hms.jos.games.achievement.Achievement;

import java.util.List;

public class AchievementListAdapter extends RecyclerView.Adapter<AchievementListAdapter.ViewHolder> {
    private static final String TAG = "AchievementListAdapter";

    private final Context context;

    //private OnBtnClickListener mBtnClickListener;

    private List<Achievement> achievementList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView achievementImage;
        CheckBox cBox;
        TextView achievementName;
        TextView achievementDes;
        TextView achivementStat;



        ViewHolder(View view) {
            super(view);

            achievementImage = view.findViewById(R.id.achievement_image);
            achievementName = view.findViewById(R.id.achievement_name);
            achievementDes = view.findViewById(R.id.achievement_des);
            achivementStat = view.findViewById(R.id.achieveStat);

        }
    }

    AchievementListAdapter(Context mContext, List<Achievement> achievements) {
        context = mContext;
        achievementList = achievements;
        //mBtnClickListener = btnClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.achievement_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull
                                 final ViewHolder holder, final int position) {
        final Achievement achievement = achievementList.get(position);
        int type = achievement.getType();

        final String achievementId = achievement.getAchievementId();
        Glide.with(context).load(achievement.getUnlockedImageUri()).into(holder.achievementImage);
        holder.achievementName.setText(achievement.getName());
        holder.achievementDes.setText(achievement.getDescription());
        holder.achivementStat.setText("Level : "+String.valueOf(achievement.getState()));
        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnClickListener.onItemClick(position);
            }
        });

        holder.achievementUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnClickListener.Unlock(achievementId, holder.cBox.isChecked());
            }
        });
        holder.achievementReveal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnClickListener.reveal(achievementId, holder.cBox.isChecked());
            }
        });
        holder.achievementIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnClickListener.increment(achievementId, holder.cBox.isChecked());
            }
        });
        holder.achievementStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnClickListener.setStep(achievementId, holder.cBox.isChecked());
            }
        });*/
    }

    private boolean allBtnInvisibility(TextView achievementUnlock, TextView achievementReveal,
                                       TextView achievementIncrement, TextView achievementStep) {
        return achievementUnlock.getVisibility() == View.GONE && achievementReveal.getVisibility() == View.GONE
                && achievementIncrement.getVisibility() == View.GONE && achievementStep.getVisibility() == View.GONE;
    }

    @Override
    public int getItemCount() {
        return achievementList.size();
    }

    /*public interface OnBtnClickListener {
        void onItemClick(int position);

        void Unlock(String achievementId, boolean isChecked);

        void reveal(String achievementId, boolean isChecked);

        void increment(String achievementId, boolean isChecked);

        void setStep(String achievementId, boolean isChecked);
    }*/
}

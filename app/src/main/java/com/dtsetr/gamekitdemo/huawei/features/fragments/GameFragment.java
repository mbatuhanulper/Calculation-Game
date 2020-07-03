package com.dtsetr.gamekitdemo.huawei.features.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsetr.gamekitdemo.huawei.MainActivity;
import com.dtsetr.gamekitdemo.huawei.features.common.Constant;
import com.dtsetr.gamekitdemo.huawei.R;
import com.huawei.api.huaweiapis.auth.DriveCredential;
import com.huawei.api.huaweiapis.exception.DriveCode;
import com.huawei.api.services.drive.Drive;
import com.huawei.api.services.drive.DriveManager;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.HiAnalyticsTools;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.framework.common.Logger;
import com.huawei.hms.jos.games.AchievementsClient;
import com.huawei.hms.jos.games.Games;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.api.entity.iap.ConsumePurchaseReq;
import com.huawei.hms.support.api.entity.iap.GetBuyIntentReq;
import com.huawei.hms.support.api.entity.iap.GetPurchaseReq;
import com.huawei.hms.support.api.entity.iap.OrderStatusCode;
import com.huawei.hms.support.api.entity.iap.SkuDetail;
import com.huawei.hms.support.api.entity.iap.SkuDetailReq;
import com.huawei.hms.support.api.hwid.SignInHuaweiId;
import com.huawei.hms.support.api.iap.BuyResultInfo;
import com.huawei.hms.support.api.iap.ConsumePurchaseResult;
import com.huawei.hms.support.api.iap.GetBuyIntentResult;
import com.huawei.hms.support.api.iap.GetPurchasesResult;
import com.huawei.hms.support.api.iap.SkuDetailResult;
import com.huawei.hms.support.api.iap.json.Iap;
import com.huawei.hms.support.api.iap.json.IapApiException;
import com.huawei.hms.support.api.iap.json.IapClient;

import static android.content.Context.MODE_PRIVATE;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.huawei.hms.analytics.type.HAEventType.HA_POST_SCORE;
import static com.huawei.hms.analytics.type.HAParamType.HA_SCORE;
import static com.huawei.hms.framework.network.upload.internal.core.UploadRequestProcessor.TAG;


public class GameFragment extends Fragment implements View.OnClickListener{


    private TextView operation;
    private EditText answerText;
    private TextView timer;
    private Context context;
    private int result;
    private Integer answer;
    private int trueAnswer;
    private boolean expired=false;
    private TextView scoreBoard;
    private CountDownTimer countDownTimer;
    private long[] remaininTime;
    private boolean isFalseAnswer=false;
    private boolean level2=false;
    private String accessToken;
    private  String unionId;
    private View v;
    private com.huawei.api.services.drive.model.File directoryCreated;
    private String mPath;


    private Button num0;
    private Button num1;
    private Button num2;
    private Button num3;
    private Button num4;
    private Button num5;
    private Button num6;
    private Button num7;
    private Button num8;
    private Button num9;
    private Button submitButton;
    private ImageButton clearButton;

    private SweetAlertDialog sweetAlertDialog;
    private HiAnalyticsInstance instance;
    private SharedPreferences.Editor editor;
    private Drive drive;


    public GameFragment() {
        // Required empty public constructor
    }

    public static GameFragment newInstance(String param1, String param2) {
        GameFragment fragment = new GameFragment();
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
        View v = inflater.inflate(R.layout.fragment_game, container, false);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
                return false;
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        submitButton= Objects.requireNonNull(getActivity()).findViewById(R.id.submit_button);
        clearButton=getActivity().findViewById(R.id.clearButton);
        operation=getActivity().findViewById(R.id.operationText);
        answerText=getActivity().findViewById(R.id.answerText);
        timer=getActivity().findViewById(R.id.timerText);
        scoreBoard=getActivity().findViewById(R.id.score_txt);
        num0=getActivity().findViewById(R.id.num0_button);
        num1=getActivity().findViewById(R.id.num1_button);
        num2=getActivity().findViewById(R.id.num2_button);
        num3=getActivity().findViewById(R.id.num3_button);
        num4=getActivity().findViewById(R.id.num4_button);
        num5=getActivity().findViewById(R.id.num5_button);
        num6=getActivity().findViewById(R.id.num6_button);
        num7=getActivity().findViewById(R.id.num7_button);
        num8=getActivity().findViewById(R.id.num8_button);
        num9=getActivity().findViewById(R.id.num9_button);
        context= view.getContext();
        v=view;

        ImageButton quitGame = getActivity().findViewById(R.id.quit_game_button);

        //remaininTime=getArguments().getInt("score");
        editor = getActivity().getSharedPreferences("Save", MODE_PRIVATE).edit();

        HiAnalyticsTools.enableLog();
        instance=HiAnalytics.getInstance(getActivity());
        instance.setAnalyticsCollectionEnabled(true);
        instance.setAutoCollectionEnabled(true);
        quitGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                SweetAlertDialog sD;
                sD=new SweetAlertDialog(Objects.requireNonNull(getActivity()),SweetAlertDialog.WARNING_TYPE);
                        sD.setTitleText("Attention!")
                        .setContentText("Are you sure want to quit ?")
                        .setConfirmText("Yes")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                getActivity().onBackPressed();
                                countDownTimer.cancel();
                                postScore();
                            }
                        }).setNeutralButton("Save", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        editor.putInt("score", trueAnswer);
                                        editor.apply();
                                        sweetAlertDialog.dismissWithAnimation();
                                        getActivity().onBackPressed();
                                        countDownTimer.cancel();
                                    }
                        }).setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                timer(Math.toIntExact(remaininTime[0]));
                            }
                        })
                        .show();
                        sD.setCanceledOnTouchOutside(false);
            }
        });

        setOnClickListeners();
        startGame();

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            accessToken=getArguments().getString("accessToken");
            unionId=getArguments().getString("unionId");
            /*if(getArguments().getInt("score")!=0)
            {*/
                trueAnswer=getArguments().getInt("score");
                scoreBoard.setText(Integer.toString(trueAnswer));
            //}
        }
    }

    private SkuDetailReq createSkuDetailReq() {
        SkuDetailReq skuDetailRequest = new SkuDetailReq();
        ArrayList<String> skuList = new ArrayList<>();
        skuList.add("testc");
        skuDetailRequest.skuIds = skuList;
        return skuDetailRequest;
    }

    private void beforeInitProduct(){
        Task<GetPurchasesResult> task = Iap.getIapClient(Objects.requireNonNull(getActivity()))
                .getPurchases(createGetPurchaseReq());

        task.addOnSuccessListener(new OnSuccessListener<GetPurchasesResult>() {
            @Override
            public void onSuccess(GetPurchasesResult getPurchasesResult) {
                if(getPurchasesResult.getReturnCode() == 0 && !getPurchasesResult.getInAppPurchaseDataList().isEmpty()){
                    for(String inAppPurchaseData : getPurchasesResult.getInAppPurchaseDataList()){
                        consumePurchase(getActivity(), inAppPurchaseData);
                    }
                }
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
            }
        });
    }
    private GetPurchaseReq createGetPurchaseReq(){
        GetPurchaseReq getPurchaseReq = new GetPurchaseReq();
        getPurchaseReq.priceType = Constant.PRODUCT_TYPE_CONSUMABLE;
        return getPurchaseReq;
    }


    private void initProduct() {
        beforeInitProduct();
        IapClient iapClient = Iap.getIapClient(Objects.requireNonNull(getActivity()));
        Task<SkuDetailResult> task = iapClient.getSkuDetail(createSkuDetailReq());
        task.addOnSuccessListener(new OnSuccessListener<SkuDetailResult>() {
            @Override
            public void onSuccess(SkuDetailResult result) {
                if (result != null && !result.getSkuList().isEmpty()) {
                    showProduct(result.getSkuList());
                    //getBuyIntent(getActivity(), "testc", Constant.PRODUCT_TYPE_NON_CONSUMABLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
            }
        });
    }
    private void showProduct(List<SkuDetail> skuDetailList) {
        Log.d(TAG, "showProduct: ");

        getBuyIntent(getActivity(), skuDetailList.get(0).productId);
    }


    private void getBuyIntent(final Activity activity, String skuId) {
        Log.i(TAG, "call getBuyIntent");
        IapClient mClient = Iap.getIapClient(activity);
        Task<GetBuyIntentResult> task = mClient.getBuyIntent(createGetBuyIntentReq(skuId));
        task.addOnSuccessListener(new OnSuccessListener<GetBuyIntentResult>() {
            @Override
            public void onSuccess(GetBuyIntentResult result) {
                Log.i(TAG, "getBuyIntent, onSuccess");
                if (result == null) {
                    Log.e(TAG, "result is null");
                    return;
                }
                Status status = result.getStatus();
                if (status == null) {
                    Log.e(TAG, "status is null");
                    return;
                }
                if (status.hasResolution()) {
                    try {
                        status.startResolutionForResult(activity, Constant.REQ_CODE_BUY_CONSUMABLE);
                    } catch (IntentSender.SendIntentException exp) {
                        Log.e(TAG, Objects.requireNonNull(exp.getMessage()));
                    }
                } else {
                    Log.e(TAG, "intent is null");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                if (e instanceof IapApiException) {
                    IapApiException apiException = (IapApiException)e;
                    int returnCode = apiException.getStatusCode();
                    Log.i(TAG, "getBuyIntent, returnCode: " + returnCode);
                } else {
                    Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                }

            }
        });
    }

    private GetBuyIntentReq createGetBuyIntentReq(String skuId) {
        GetBuyIntentReq request = new GetBuyIntentReq();
        request.productId = skuId;
        request.priceType = Constant.PRODUCT_TYPE_CONSUMABLE;
        request.developerPayload = "test";
        return request;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQ_CODE_BUY_CONSUMABLE) {
            if (data != null) {
                BuyResultInfo buyResultInfo = Iap.getIapClient(Objects.requireNonNull(getActivity())).getBuyResultInfoFromIntent(data);
                if(buyResultInfo.getReturnCode() == OrderStatusCode.ORDER_STATE_CANCEL){
                    Toast.makeText(getActivity(),"User cancel", Toast.LENGTH_LONG).show();
                    return;
                }
                if(buyResultInfo.getReturnCode() == OrderStatusCode.ORDER_ITEM_ALREADY_OWNED){
                    Toast.makeText(getActivity(), "You already have owned this item",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (buyResultInfo.getReturnCode() == OrderStatusCode.ORDER_STATE_SUCCESS) {
                        consumePurchase(getContext(), buyResultInfo.getInAppPurchaseData());
                        sweetAlertDialog.dismissWithAnimation();
                        startAgain();
                } else {
                    Toast.makeText(getActivity(), "Pay failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.i(TAG, "data is null");
            }
        }

    }

    private void consumePurchase(final Context context, String inAppPurchaseData){
        Log.d(TAG, "consumePurchase: ");
        IapClient iapClient = Iap.getIapClient(context);
        Task<ConsumePurchaseResult> task = iapClient.consumePurchase(createConsumePurchaseReq(inAppPurchaseData));
        task.addOnSuccessListener(new OnSuccessListener<ConsumePurchaseResult>() {
            @Override
            public void onSuccess(ConsumePurchaseResult consumePurchaseResult) {
                Log.i(TAG, "consume purchase onSuccess: ");
                Toast.makeText(context, "Pay success and the product has been delivered", Toast.LENGTH_LONG).show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                if(e instanceof IapApiException){
                    IapApiException apiException = (IapApiException)e;
                    int returnCode = apiException.getStatusCode();
                    Log.i(TAG, "onFailure: " + returnCode);
                }
                else{
                    Log.e(TAG, "onFailure: " + e.getMessage() );
                    Toast.makeText(context, "External Error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private ConsumePurchaseReq createConsumePurchaseReq(String purchaseData){
        ConsumePurchaseReq consumePurchaseReq = new ConsumePurchaseReq() ;
        String purchaseToken = "";
        try{
            JSONObject jsonObject = new JSONObject(purchaseData);
            purchaseToken = jsonObject.optString("purchaseToken");
        }
        catch (JSONException ignored){

        }
        consumePurchaseReq.purchaseToken = purchaseToken;
        return consumePurchaseReq;
    }


    private void setOnClickListeners()
    {
        num1.setOnClickListener(this);
        num2.setOnClickListener(this);
        num3.setOnClickListener(this);
        num4.setOnClickListener(this);
        num5.setOnClickListener(this);
        num6.setOnClickListener(this);
        num7.setOnClickListener(this);
        num8.setOnClickListener(this);
        num9.setOnClickListener(this);
        num0.setOnClickListener(this);
        clearButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.num0_button:
                if (addNumber("0"))
                break;
            case R.id.num1_button:
                if (addNumber("1"))
                break;
            case R.id.num2_button:
                if (addNumber("2"))
                break;
            case R.id.num3_button:
                if (addNumber("3"))
                break;
            case R.id.num4_button:
                if (addNumber("4"))
                break;
            case R.id.num5_button:
                if (addNumber("5"))
                break;
            case R.id.num6_button:
                if (addNumber("6"))
                break;
            case R.id.num7_button:
                if (addNumber("7"))
                    break;
            case R.id.num8_button:
                if (addNumber("8"))
                break;
            case R.id.num9_button:
                if (addNumber("9"))
                break;
            case R.id.clearButton:
                answerText.getText().clear();
                break;
    }

    }

    @SuppressLint("SetTextI18n")
    private boolean addNumber(String number)
    {
        answerText.setText(answerText.getText() + number);
        return true;
    }

    private void startGame(){
        generateQuestion();
        timer(5000);
    }
    private void startAgain(){
        generateQuestion();
        if((int) remaininTime[0]<5000)
        timer((int) remaininTime[0]+5000);
        else
            timer((int) remaininTime[0]);

    }

    private void timer(int time){
        final int[] countDownTime = {time};

        remaininTime = new long[1];
        countDownTimer=new CountDownTimer(countDownTime[0], 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                remaininTime[0] =millisUntilFinished;
                timer.setText("" + remaininTime[0] / 1000);
            }
            public void onFinish() {
                timer.setText("0");
                expired=true;
                if(!isFalseAnswer)
                    missed();
            }

        }.start();

        if(!expired){
            submitButton.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    if(!answerText.getText().toString().equals("")){
                    answer = Integer.parseInt(answerText.getText().toString());
                    if ((answer == result)) {
                        answerText.getText().clear();
                        generateQuestion();
                        remaininTime[0] += 2000;
                        countDownTime[0] = (int) remaininTime[0];
                        countDownTimer.cancel();
                        timer(countDownTime[0]);
                        trueAnswer++;
                        if(trueAnswer>5)
                            unlock("083254CA16B4D60FFF5F690C298B9F0AF72BF4AA317DF955AABAA9DE84DF90E5");
                        if (trueAnswer>10)
                            unlock("C441ACC4815BDF36FA26DF1A9F40BB0962B998E813C563AFB924CC374E9061B8");
                        scoreBoard.setText(Integer.toString(trueAnswer));
                    } else {
                        isFalseAnswer=true;
                        missed();
                    }
                }
                }

            });
        }
    }

    private void unlock(String achievementId) {
        level2=true;
        Games.getAchievementsClient(Objects.requireNonNull(getActivity()),
                ((MainActivity) Objects.requireNonNull(getActivity())).getSignInHuaweiId()).unlock(achievementId);
    }

    @SuppressLint("SetTextI18n")
    private void generateQuestion(){
        Random random = new Random();
        int maxNum;
        if(level2)
            maxNum=30;
        else
            maxNum =10;
        int operatorSelector = random.nextInt(4) + 1;
        if(operatorSelector==4)
            maxNum=5;
        int val1 = random.nextInt(maxNum) + 2;
        int val2 = random.nextInt(maxNum) + 2;

        switch (operatorSelector){
            case 1: result= val1 + val2;
                operation.setText(val1 +" + "+ val2);
                break;
            case 2: if(val2 > val1)
                val1 = val2 + val1;
                result= val1 - val2;
                operation.setText(val1 +" - "+ val2);
                break;
            case 3: result= val1 * val2;
                operation.setText(val1 +" x "+ val2);
                break;
            case 4:if (val2>val1)
                val1=val2+(val1);
            if(val1%val2!=0)
                val1=val2*val1;
                result= val1 / val2;
                operation.setText(val1 +" /  "+ val2);
                break;
        }
    }

    private void postScore() {
        Bundle bundle = new Bundle();
        bundle.putLong(HA_SCORE, trueAnswer);
        instance.logEvent(HA_POST_SCORE, bundle);
    }

    private void getScreenshot()  {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        try {
            mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";
            View v1 = v.getRootView();
            v1.setDrawingCacheEnabled(true);
            v1.buildDrawingCache(true);
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(String.valueOf(imageFile));
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            initDrive();
            //share();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void share(){

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        Uri screenshotUri = Uri.parse(mPath);

        sharingIntent.setType("image/jpg");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        context.startActivity(Intent.createChooser(sharingIntent, "Share image using"));
    }

    private void initDrive(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (accessToken == null) {
                        return;
                    }
                    if (drive == null){
                        int initCode = DriveManager.getInstance().init(context, unionId, accessToken, new DriveCredential.AccessMethod() {
                            @Override
                            public String refreshToken() throws IOException {
                                return accessToken;
                            }
                        });

                        if (DriveCode.SUCCESS != initCode) {
                        } else {
                            drive =  new Drive.Builder(DriveManager.getInstance().getCredential(), context).build();
                            sendScreenshot();
                        }
                    }
                } catch (Exception ex) {
                    Logger.e("init drive", ex);
                }
            }
        }).start();

    }

    private void sendScreenshot(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (accessToken == null) {
                        return;
                    }
                    if (drive == null){
                        int initCode = DriveManager.getInstance().init(Objects.requireNonNull(getActivity()), unionId, accessToken, new DriveCredential.AccessMethod() {
                            @Override
                            public String refreshToken() throws IOException {
                                return accessToken;
                            }
                        });

                        if (DriveCode.SUCCESS != initCode) {
                            return;
                        }
                        else {
                            drive =  new Drive.Builder(DriveManager.getInstance().getCredential(), getActivity()).build();
                        }
                    }

                    // create somepath directory
                    com.huawei.api.services.drive.model.File file = new com.huawei.api.services.drive.model.File();
                    file.setName("Presentation Directory" + System.currentTimeMillis()).setMimeType("application/vnd.huawei-apps.folder");
                    directoryCreated = drive.files().create(file).execute();

                    // create test.jpg on cloud
                    com.huawei.api.services.drive.model.File content = new com.huawei.api.services.drive.model.File()
                            .setName("test")
                            .setMimeType("image/jpeg")
                            .setParents(Collections.singletonList(directoryCreated.getId()));
                    java.io.File fileObject = new java.io.File(mPath);
                    drive.files().create(content, fileObject).execute();


                } catch (Exception ex) {
                    Logger.e("upload", ex);
                }
            }
        }).start();

    }

    private void missed(){
        countDownTimer.cancel();
        SweetAlertDialog sD;
        sD= new SweetAlertDialog(Objects.requireNonNull(getActivity()), SweetAlertDialog.ERROR_TYPE);
                sD.setTitleText("BUSTED!!")
                .setContentText(trueAnswer + " Answers correct Do you wanna try Again ? ")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sweetAlertDialog=sDialog;
                        initProduct();
                    }
                })
                        /*.setNeutralButton("Share", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                countDownTimer.cancel();
                                editor.clear().commit();
                                Objects.requireNonNull(getActivity()).onBackPressed();
                                getScreenshot();
                                postScore();
                            }
                        })*/
                .setCancelButton("Quit", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        countDownTimer.cancel();
                        editor.clear().commit();
                        Objects.requireNonNull(getActivity()).onBackPressed();
                        postScore();
                    }
                })
                .show();
        sD.setCanceledOnTouchOutside(false);

    }


}

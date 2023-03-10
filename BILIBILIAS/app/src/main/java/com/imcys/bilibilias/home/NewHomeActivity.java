package com.imcys.bilibilias.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.core.app.ActivityCompat;

import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;

import com.google.android.material.snackbar.Snackbar;
import com.imcys.bilibilias.AppFilePathUtils;
import com.imcys.bilibilias.BilibiliPost;
import com.imcys.bilibilias.HttpUtils;
import com.imcys.bilibilias.R;
import com.imcys.bilibilias.SetActivity;

import com.imcys.bilibilias.as.MergeVideoActivity;
import com.imcys.bilibilias.as.RankingActivity;
import com.imcys.bilibilias.as.VideoAsActivity;
import com.imcys.bilibilias.play.PlayPathActivity;

import com.imcys.bilibilias.user.CacheActivity;
import com.imcys.bilibilias.user.UserActivity;


import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class NewHomeActivity extends AppCompatActivity {

    private List<Function> functionList = new ArrayList<>();
    private Function FunctionListData;
    private GridLayoutManager layoutManager;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private Banner banner;

    String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    List<String> mPermissionList = new ArrayList<>();
    private static final int PERMISSION_REQUEST = 1;
    private static int LJ;
    private String cookie;
    private String oauthKey;
    private String toKen;
    private String csrf;
    private String URL;
    private ProgressDialog pd2;
    private String mid;
    private String GxUrl = "https://api.misakamoe.com/app/bilibilias.php?type=json&version=2.2";
    private String Version = "2.2";
    private AppFilePathUtils mAppFilePathUtils;
    private SharedPreferences sharedPreferences;
    private String ps = "??????????????????????????????????????????QQ1250422131 ???????????????????????????";
    private static final int NO_1 = 0x1;
    private String AsCookie;
    private String LoginType;
    private String access_token;
    private String AsUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newhome);

        //????????????
        checkPermission();
        //?????????????????????
        newView();
        //??????????????????
        initDrawerToggle();
        //???????????? -> ????????????????????????
        getApkMd5();
        LoginCheck();
        getNotice();

        //???????????????
        setBanner();
        //??????????????????
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(NewHomeActivity.this);
        LoginType = sharedPreferences.getString("LoginType", "BILIBILIAS");
        access_token = sharedPreferences.getString("access_token", "0");
        AsUserName = sharedPreferences.getString("AsUserName", "0");
        AsCookie = sharedPreferences.getString("AsCookie", "0");

        //??????????????????
        asUser();
        //????????????
        getCacheSize();

        //??????????????????????????????
        newVersionCheck();

        //??????????????????????????????
        //Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        //intent.setData(Uri.parse("package:" + getPackageName()));
        //startActivityForResult(intent, 41);

        //Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        //startActivityForResult(intent1, 42);

        //fileUriUtils.startFor("/storage/emulated/0/Android/data/tv.danmaku.bili/", this, 1);

        //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()) {
        //Toast.makeText(this, "????????????????????????????????????", Toast.LENGTH_SHORT).show();
        //} else {
        // Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
        //startActivity(intent);
        //}


    }



    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }


    private void asUser() {
        if (!AsUserName.equals("0")) {
            LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.Home_As_LinearLayout);
            TextView mTextView = (TextView) findViewById(R.id.Home_As_UserName);
            mTextView.setText(AsUserName);
            mLinearLayout.setVisibility(View.VISIBLE);
        }

    }

    private void newVersionCheck() {
        //??????1???????????????SharedPreferences??????
        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        String SPVersion = sharedPreferences.getString("Version", "");
        if (!SPVersion.equals(Version)) { //Version
            LinearLayout lLayout = new LinearLayout(this);
            lLayout.setOrientation(LinearLayout.VERTICAL);
            WebView Privacy = new WebView(NewHomeActivity.this);
            Privacy.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//??????js??????????????????????????????window.open()????????????false
            Privacy.getSettings().setJavaScriptEnabled(true);//??????????????????js????????????false?????????true???????????????????????????XSS??????
            Privacy.getSettings().setLoadWithOverviewMode(true);//???setUseWideViewPort(true)?????????????????????????????????
            Privacy.getSettings().setDomStorageEnabled(true);//DOM Storage ?????????????????????
            Privacy.getSettings().setAllowFileAccess(false);
            Privacy.loadUrl("https://docs.qq.com/doc/p/080e6bdd303d1b274e7802246de47bd7cc28eeb7?dver=2.1.27292865");
            LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1500);//?????????????????????????????????????????????????????????????????????????????????????????????
            lLayout.addView(Privacy, lParams);
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(NewHomeActivity.this);
            builder.setView(lLayout);
            builder.setCancelable(false);
            builder.setTitle("????????????");
            builder.setPositiveButton("????????????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //??????2??? ?????????SharedPreferences.Editor??????
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    //??????3????????????????????????????????????
                    editor.putString("Version", "2.2");
                    //??????4????????? commit????????????apply??????
                    editor.apply();
                    Intent intent = new Intent();
                    intent.setClass(NewHomeActivity.this, VersionActivity.class);
                    startActivity(intent);
                }
            });
            builder.show();
        }
    }

    private void getCacheSize() {
        mAppFilePathUtils = new AppFilePathUtils(NewHomeActivity.this, "com.imcys.bilibilias");
        final long total = AppFilePathUtils.getTotalSizeOfFilesInDir(mAppFilePathUtils.getCache());
        double cacheNc = (double) (total / 1048576);
        if ((int) cacheNc > 60) {
            androidx.appcompat.app.AlertDialog aldg;
            androidx.appcompat.app.AlertDialog.Builder adBd = new androidx.appcompat.app.AlertDialog.Builder(NewHomeActivity.this);
            adBd.setTitle("????????????");
            adBd.setMessage("???????????????????????????????????????????????????????????????????????????60MB??????????????????????????????????????????????????????????????????????????????????????????");
            adBd.setPositiveButton("????????????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AppFilePathUtils.deleteDir(getCacheDir());
                    DrawerLayout Home_DrawerLayout = (DrawerLayout) findViewById(R.id.Home_DrawerLayout);
                    Snackbar.make(Home_DrawerLayout, "????????????", Snackbar.LENGTH_LONG).show();
                }
            });
            adBd.setNegativeButton("????????????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            aldg = adBd.create();
            aldg.show();
        }
    }


    //?????????????????????
    private void newView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.Home_FunctionRecyclerView);
        drawerLayout = (DrawerLayout) findViewById(R.id.Home_DrawerLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }


    //????????????????????????
    private void initDrawerToggle() {
        toolbar = (Toolbar) findViewById(R.id.Home_Toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff")); //??????????????????
        toolbar.setSubtitle("????????????????????????????????????????????????");
        toolbar.setSubtitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        //?????????????????????
        getSupportActionBar().setHomeButtonEnabled(true);
        // ????????????????????????activity???DrawerLayout????????????toolbar????????????????????????????????????open drawer?????????close drawer
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerToggle.syncState();
        drawerLayout.setDrawerListener(mDrawerToggle);
        // ??????????????????????????????????????????????????????????????????; ????????????????????????????????????????????????????????????????????????????????????????????????
        mDrawerToggle.syncState();
        // ???????????????????????????; ????????????????????????????????????????????????????????????????????????????????????
        drawerLayout.setDrawerListener(mDrawerToggle);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //??????????????????
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                goSet();
                return true;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                String HomeItemStr = HttpUtils.doGet("https://api.misakamoe.com/app/AppFunction.php?type=homeitem", "");
                try {
                    JSONObject HomeItemJson = new JSONObject(HomeItemStr);
                    JSONArray HomeItemArray = HomeItemJson.getJSONArray("data");
                    for (int i = 0; i < HomeItemArray.length(); i++) {
                        JSONObject HomeItemData = HomeItemArray.getJSONObject(i);
                        String Title = HomeItemData.getString("Title");
                        String SrcUrl = HomeItemData.getString("SrcUrl");
                        int ViewTag = HomeItemData.getInt("ViewTag");
                        int visibility = HomeItemData.getInt("visibility");
                        if (visibility == 1) {
                            FunctionListData = new Function(Title, SrcUrl, ViewTag, NewHomeActivity.this);
                            functionList.add(FunctionListData);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.Home_FunctionRecyclerView);
                        GridLayoutManager layoutManager = new GridLayoutManager(NewHomeActivity.this, 2);
                        recyclerView.setLayoutManager(layoutManager);
                        FunctionAdapter adapter = new FunctionAdapter(functionList);
                        recyclerView.setAdapter(adapter);
                        adapter.setOnItemClickListener(new FunctionAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(int position, int tag, View v) {
                                switch (tag) {
                                    case 1:
                                        GoUser();
                                        break;
                                    case 2:
                                        GoVideoAs();
                                        break;
                                    case 3:
                                        goPlayPath();
                                        break;
                                    case 4:
                                        goRanking();
                                        break;
                                    case 5:
                                        goSet();
                                        break;
                                    case 6:
                                        UserExit();
                                        break;
                                    case 7:
                                        goMergeVideo();
                                        break;
                                    case 8:
                                        getCache();
                                        break;
                                    default:
                                        Snackbar.make(findViewById(R.id.Home_DrawerLayout), "??????????????????????????????????????????", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }


    //?????????????????? ??? ??? ???
    private void GoUser() {
        Intent intent = new Intent();
        intent.setClass(NewHomeActivity.this, UserActivity.class);
        intent.putExtra("cookie", cookie);
        intent.putExtra("csrf", csrf);
        intent.putExtra("toKen", toKen);
        intent.putExtra("mid", mid);
        startActivity(intent);
    }

    private void GoVideoAs() {
        Intent intent = new Intent();
        intent.setClass(NewHomeActivity.this, VideoAsActivity.class);
        intent.putExtra("cookie", cookie);
        intent.putExtra("csrf", csrf);
        intent.putExtra("toKen", toKen);
        intent.putExtra("mid", mid);
        startActivity(intent);
    }

    private void goSet() {
        Intent intent = new Intent();
        intent.setClass(NewHomeActivity.this, SetActivity.class);
        startActivity(intent);
    }

    private void goRanking() {
        Intent intent = new Intent();
        intent.setClass(NewHomeActivity.this, RankingActivity.class);
        startActivity(intent);
    }

    private void goPlayPath() {
        Intent intent = new Intent();
        intent.setClass(NewHomeActivity.this, PlayPathActivity.class);
        startActivity(intent);
    }

    private void goMergeVideo() {
        Intent intent = new Intent();
        intent.setClass(NewHomeActivity.this, MergeVideoActivity.class);
        startActivity(intent);
    }

    public void getCache() {
        Intent intent = new Intent();
        intent.setClass(NewHomeActivity.this, CacheActivity.class);
        intent.putExtra("cookie", cookie);
        intent.putExtra("mid", mid);
        startActivity(intent);
    }

    private void UserExit() {
        MessageDialog.build()
                .setTitle("??????")
                .setMessage("awa????????????????????????????????????????????????B???????????????????????????????????????")
                //?????????????????????????????????
                .setOkButton("?????????????????????", new OnDialogButtonClickListener<MessageDialog>() {
                    @Override
                    public boolean onClick(MessageDialog baseDialog, View v) {
                        new Thread(() -> {
                            HttpUtils.doPost("https://passport.bilibili.com/login/exit/v2", "biliCSRF=" + csrf, cookie);
                            runOnUiThread(() -> {
                                Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
                                System.exit(0);
                            });
                        }).start();
                        return false;
                    }
                })
                .setCancelButton("?????????", null).show();


    }


    //??????????????? ??? ???
    private void getApkMd5() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String StrGx = HttpUtils.doGet(GxUrl, "");
                try {
                    JSONObject jsonGxStr = new JSONObject(StrGx);
                    final String MD5 = jsonGxStr.getString("APKMD5");
                    final String CRC = jsonGxStr.getString("APKToKenCR");
                    final String SHA = jsonGxStr.getString("APKToKen");
                    final String ID = jsonGxStr.getString("ID");
                    final String StrUrl = jsonGxStr.getString("url");
                    final String sha = apkVerifyWithSHA(NewHomeActivity.this, SHA);
                    System.out.println(sha);
                    System.out.println(SHA);
                    final String md5 = apkVerifyWithMD5(NewHomeActivity.this, MD5);
                    System.out.println(md5);
                    System.out.println(MD5);
                    final String crc = apkVerifyWithCRC(NewHomeActivity.this, CRC);
                    System.out.println(crc);
                    System.out.println(CRC);
                    if (ID.equals("1")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!sha.equals(SHA)) {
                                    DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.Home_DrawerLayout);
                                    mDrawerLayout.setVisibility(View.GONE);
                                    Uri uri = Uri.parse(StrUrl);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                    System.exit(0);
                                } else if (!md5.equals(MD5)) {
                                    DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.Home_DrawerLayout);
                                    mDrawerLayout.setVisibility(View.GONE);
                                    Uri uri = Uri.parse(StrUrl);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                    System.exit(0);
                                } else if (!crc.equals(CRC)) {
                                    DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.Home_DrawerLayout);
                                    mDrawerLayout.setVisibility(View.GONE);
                                    Uri uri = Uri.parse(StrUrl);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                    System.exit(0);
                                }
                            }
                        });
                    } else if (ID.equals("0")) {
                        String fs = HttpUtils.doGet("https://api.misakamoe.com/app/bilibilias.php?type=json&version=" + Version + "&SHA=" + sha + "&MD5=" + md5 + "&CRC=" + crc + "lj=" + LJ, cookie);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String NoticeStr = HttpUtils.doGet("https://api.misakamoe.com/app/AppFunction.php?type=Notice", "");
                try {
                    JSONObject NoticeJson = new JSONObject(NoticeStr);
                    String Title = NoticeJson.getString("Title");
                    String msg = NoticeJson.getString("msg");
                    int state = NoticeJson.getInt("state");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (state == 1) {
                                MessageDialog.build()
                                        .setTitle(Title)
                                        .setMessage(msg)
                                        //?????????????????????????????????
                                        .setOkButton("????????????", null).show();

                            }

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    private void goWebUrl() {
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.Home_DrawerLayout);
        mDrawerLayout.setVisibility(View.GONE);
        Uri uri = Uri.parse("https//:api.misakamoe.bilibilias/app");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        System.exit(0);
    }


    private void getNotice() {
        //????????????
        new Thread(new Runnable() {
            @Override
            public void run() {
                String StrGx = HttpUtils.doGet(GxUrl, "");
                if (!GxUrl.equals("https://api.misakamoe.com/app/bilibilias.php?type=json&version=" + Version)) {
                } else {
                    JSONObject jsonGxStr = null;
                    String StrPd = null;
                    try {
                        jsonGxStr = new JSONObject(StrGx);
                        StrPd = jsonGxStr.getString("version");
                        final String StrNr = jsonGxStr.getString("gxnotice");
                        final String StrUrl = jsonGxStr.getString("url");
                        if (StrPd.equals(Version)) {
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MessageDialog.build()
                                            .setTitle("???????????????")
                                            .setMessage(StrNr)
                                            //?????????????????????????????????
                                            .setOkButton("???????????????", new OnDialogButtonClickListener<MessageDialog>() {
                                                @Override
                                                public boolean onClick(MessageDialog baseDialog, View v) {
                                                    Uri uri = Uri.parse(StrUrl);
                                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                    startActivity(intent);
                                                    return false;
                                                }
                                            })
                                            .setCancelButton("????????????", null).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }


    private void setBanner() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String bannerJsonStr = HttpUtils.doGet("https://api.misakamoe.com/app/bilibilias.php?type=banner", "");
                try {
                    JSONObject bannerJson = new JSONObject(bannerJsonStr);
                    JSONArray imgUrlList = bannerJson.getJSONArray("imgUrlList");
                    JSONArray textList = bannerJson.getJSONArray("textList");
                    JSONArray dataList = bannerJson.getJSONArray("dataList");
                    JSONArray typeList = bannerJson.getJSONArray("typeList");
                    JSONArray successToastList = bannerJson.getJSONArray("successToast");
                    JSONArray failToastList = bannerJson.getJSONArray("failToast");
                    JSONArray postDataList = bannerJson.getJSONArray("postData");
                    JSONArray tokenList = bannerJson.getJSONArray("token");
                    int bannerTime = bannerJson.getInt("time");
                    ArrayList<String> imagesArray = new ArrayList<>();
                    ArrayList<String> titleArray = new ArrayList<>();
                    for (int i = 0; i < imgUrlList.length(); i++) {
                        String imgUrl = imgUrlList.getString(i);
                        String title = textList.getString(i);
                        imagesArray.add(imgUrl);
                        titleArray.add(title);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            banner = (Banner) findViewById(R.id.Home_banner);
                            //?????????????????????
                            banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
                            banner.setBannerTitles(titleArray);
                            banner.isAutoPlay(true);
                            banner.setDelayTime(bannerTime);
                            banner.setImageLoader(new NewHomeActivity.GlideImageLoader());
                            //??????????????????
                            banner.setImages(imagesArray);
                            //banner?????????????????????????????????????????????
                            banner.start();
                            banner.setOnBannerListener(new OnBannerListener() {
                                @SuppressLint("IntentReset")
                                @Override
                                public void OnBannerClick(int position) {
                                    try {
                                        String data = dataList.getString(position);
                                        String type = typeList.getString(position);
                                        if (type.equals("goBilibili")) {
                                            Intent intent = new Intent();
                                            intent.setType("text/plain");
                                            intent.setData(Uri.parse(data));
                                            intent.setAction("android.intent.action.VIEW");
                                            startActivity(intent);
                                        } else if (type.equals("goAs")) {
                                            Intent intent = new Intent();
                                            intent.setClass(NewHomeActivity.this, VideoAsActivity.class);
                                            intent.putExtra("UserVideoAid", data);
                                            NewHomeActivity.this.startActivity(intent);
                                        } else if (type.equals("goUrl")) {
                                            Uri uri = Uri.parse(data);
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            startActivity(intent);
                                        } else if (type.equals("getBiliBili")) {
                                            String successToast = successToastList.getString(position);
                                            String failToast = failToastList.getString(position);
                                            getBiliBili(data, successToast, failToast);
                                        } else if (type.equals("postBiliBili")) {
                                            String successToast = successToastList.getString(position);
                                            String failToast = failToastList.getString(position);
                                            String postData = postDataList.getString(position);
                                            int token = tokenList.getInt(position);
                                            postBiliBili(data, postData, successToast, failToast, token);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    private void getBiliBili(String goUrl, String successToast, String failToast) {
        new Thread(() -> {
            String goUrlStr = HttpUtils.doGet(goUrl, cookie);
            try {
                JSONObject goUrlJson = new JSONObject(goUrlStr);
                int code = goUrlJson.getInt("code");
                runOnUiThread(() -> {
                    if (code == 0) {
                        Toast.makeText(NewHomeActivity.this, successToast, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NewHomeActivity.this, failToast, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void postBiliBili(String goUrl, String post, String successToast, String failToast, int getToken) {
        new Thread(() -> {
            String getPost = post;
            if (getToken == 1) {
                getPost = post.replace("{token}", csrf);
                System.out.println(getPost);
            }
            String goUrlStr = HttpUtils.doCardPost(goUrl, getPost, cookie);
            Log.e("CARD", goUrlStr);
            try {
                JSONObject goUrlJson = new JSONObject(goUrlStr);
                int code = goUrlJson.getInt("code");
                runOnUiThread(() -> {
                    if (code == 0) {
                        Toast.makeText(NewHomeActivity.this, successToast, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NewHomeActivity.this, failToast, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public void checkUser(View view) {
        if (AsCookie.equals("0") | AsUserName.equals("0")) {
            Intent intent = new Intent();
            intent.setClass(NewHomeActivity.this, LoginTypeActivity.class);
            startActivity(intent);
        } else {
            pd2 = ProgressDialog.show(NewHomeActivity.this, "??????", "??????????????????");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String requestUrl = "";
                    if (LoginType.equals("BILIBILIAS")) {
                        //BILIBILI AS??????
                        requestUrl = "https://api.misakamoe.com/web/";
                    } else {
                        //Profile??????
                        requestUrl = "https://api.misakamoe.com/web/testauth/";
                    }
                    String UserData = HttpUtils.doGet(requestUrl + "Account?type=userData&access_token=" + access_token, AsCookie);
                    Log.d("????????????", UserData);
                    try {
                        JSONObject UserAsJson = new JSONObject(UserData);
                        int code = UserAsJson.getInt("code");
                        String AsUser = "";
                        String AsEmail = "";
                        if (code == 0) {
                            AsUser = UserAsJson.getString("username");
                            AsEmail = UserAsJson.getString("useremail");
                        }

                        String finalAsUser = AsUser;
                        String finalAsEmail = AsEmail;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd2.cancel();
                                if (code == 0) {
                                    MessageDialog.build()
                                            .setTitle("???????????????AS????????????")
                                            .setMessage("?????????" + finalAsUser + "\n" + "?????????" + finalAsEmail)
                                            //?????????????????????????????????
                                            .setOkButton("????????????", new OnDialogButtonClickListener<MessageDialog>() {
                                                @Override
                                                public boolean onClick(MessageDialog baseDialog, View v) {

                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString("AsUserName", "0");
                                                    editor.putString("AsCookie", "0");
                                                    editor.putString("access_token", "0");
                                                    editor.apply();

                                                    String CookiePath = getExternalFilesDir("??????????????????").toString() + "/" + "cookie.txt";
                                                    String ToKenPath = getExternalFilesDir("??????????????????").toString() + "/" + "token.txt";
                                                    String csrfPath = getExternalFilesDir("??????????????????").toString() + "/" + "csrf.txt";
                                                    try {
                                                        BilibiliPost.fileWrite(CookiePath, "");
                                                        BilibiliPost.fileWrite(ToKenPath, "");
                                                        BilibiliPost.fileWrite(csrfPath, "");
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }

                                                    Intent intent = new Intent();
                                                    intent.setClass(NewHomeActivity.this, LoginTypeActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                    return false;
                                                }
                                            })
                                            .setCancelButton("??????", null).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "??????????????????", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    intent.setClass(NewHomeActivity.this, LoginTypeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    public void goServerDonation(View view) {
        Intent intent = new Intent();
        intent.setClass(NewHomeActivity.this, ServerDonationActivity.class);
        startActivity(intent);
    }

    public void feedBack(View view) {
        Uri uri = Uri.parse("https://support.qq.com/products/337496");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    public void goAbout(View view) {
        Uri uri = Uri.parse("https://support.qq.com/products/337496/team/");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void goImport(View view) {
        Uri uri = Uri.parse("https://support.qq.com/products/337496/faqs/99945");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    //????????????
    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            //Glide ????????????????????????
            Glide.with(context).load(path).into(imageView);
        }
    }


    //????????????????????????
    private void LoginCheck() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //????????????????????????
                final String ToKenPath = getExternalFilesDir("??????????????????").toString() + "/" + "token.txt";
                String csrfPath = getExternalFilesDir("??????????????????").toString() + "/" + "csrf.txt";
                String CookiePath = getExternalFilesDir("??????????????????").toString() + "/" + "cookie.txt";
                //?????????????????????????????????????????????????????????????????????????????????????????????
                try {
                    csrf = BilibiliPost.fileRead(csrfPath);
                    toKen = BilibiliPost.fileRead(ToKenPath);
                    cookie = BilibiliPost.fileRead(CookiePath);
                    System.out.println(toKen);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String pd = BilibiliPost.nav(toKen);
                if (pd.equals("0")) {
                    final String UserNavStr = HttpUtils.doGet("https://api.bilibili.com/x/web-interface/nav", cookie);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //??????????????????????????????????????????????????????????????????????????????
                            JSONObject UserNavJson = null;
                            String UserInfo = null;
                            try {
                                UserNavJson = new JSONObject(UserNavStr);
                                UserNavJson = UserNavJson.getJSONObject("data");
                                mid = UserNavJson.getString("mid");
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("BilibiliUID", mid);
                                editor.apply();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(findViewById(R.id.Home_DrawerLayout), "????????????", Snackbar.LENGTH_LONG).show();
                            Intent intent = new Intent();
                            intent.setClass(NewHomeActivity.this, LoginTypeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        }).start();

    }

    //??????????????????
    private void init(String LoginUrl) {
        WebView webView1 = (WebView) findViewById(R.id.Home_WebView1);
        //WebView??????web??????
        webView1.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//??????js??????????????????????????????window.open()????????????false

        webView1.getSettings().setJavaScriptEnabled(true);//??????????????????js????????????false?????????true???????????????????????????XSS??????

        webView1.getSettings().setSupportZoom(true);//???????????????????????????true

        webView1.getSettings().setBuiltInZoomControls(false);//?????????????????????????????????false

        webView1.getSettings().setUseWideViewPort(true);//?????????????????????????????????????????????????????????

        webView1.getSettings().setLoadWithOverviewMode(true);//???setUseWideViewPort(true)?????????????????????????????????

        webView1.getSettings().setAppCacheEnabled(true);//??????????????????

        webView1.getSettings().setDomStorageEnabled(true);//DOM Storage ?????????????????????

        webView1.getSettings().setAllowFileAccess(false);

        webView1.loadUrl(LoginUrl);
        //??????WebView?????????????????????????????????????????????????????????????????????????????????WebView??????
        webView1.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //????????????true??????????????????WebView????????????false??????????????????????????????????????????
                view.loadUrl(url);
                return true;
            }
        });
    }

    public void NewLogin(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String likeStr = HttpUtils.doGet("http://passport.bilibili.com/qrcode/getLoginUrl", "");
                JSONObject LoginQRJson = null;
                try {
                    LoginQRJson = new JSONObject(likeStr);
                    LoginQRJson = LoginQRJson.getJSONObject("data");
                    URL = LoginQRJson.getString("url");
                    oauthKey = LoginQRJson.getString("oauthKey");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println(URL);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String QRUrl = "https://api.qrserver.com/v1/create-qr-code/?data=" + URL;
                        URL = "https://api.misakamoe.com/app/BiliBiliAsLogin.php?URL=" + URL;
                        init(URL);
                        URL = QRUrl;
                        WebView webView1 = (WebView) findViewById(R.id.Home_WebView1);
                        webView1.setWebChromeClient(new WebChromeClient());
                        webView1.setWebViewClient(new NewHomeActivity.NewWebViewClient());
                        ScrollView webLayout = (ScrollView) findViewById(R.id.Home_WebLayout);
                        webLayout.setVisibility(View.VISIBLE);
                        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.Home_DrawerLayout);
                        mDrawerLayout.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

    public void UpLogin(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    cookie = HttpUtils.getCookie("oauthKey=" + oauthKey, "http://passport.bilibili.com/qrcode/getLoginInfo");
                    System.out.println(cookie);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (cookie.length() > 45) {
                    final String UserNavStr = HttpUtils.doGet("https://api.bilibili.com/x/web-interface/nav", cookie);
                    //??????????????????????????????????????????????????????mid????????????
                    JSONObject UserNavJson = null;
                    try {
                        UserNavJson = new JSONObject(UserNavStr);
                        UserNavJson = UserNavJson.getJSONObject("data");
                        mid = UserNavJson.getString("mid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toKen = "SESSDATA=" + sj(cookie, "SESSDATA=", ";");
                            csrf = sj(cookie, "bili_jct=", ";");
                            System.out.println(toKen);
                            String CookiePath = getExternalFilesDir("??????????????????").toString() + "/" + "cookie.txt";
                            String ToKenPath = getExternalFilesDir("??????????????????").toString() + "/" + "token.txt";
                            String csrfPath = getExternalFilesDir("??????????????????").toString() + "/" + "csrf.txt";
                            try {
                                BilibiliPost.fileWrite(CookiePath, cookie);
                                BilibiliPost.fileWrite(ToKenPath, toKen);
                                BilibiliPost.fileWrite(csrfPath, csrf);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ScrollView webLayout = (ScrollView) findViewById(R.id.Home_WebLayout);
                            webLayout.setVisibility(View.GONE);
                            DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.Home_DrawerLayout);
                            mDrawerLayout.setVisibility(View.VISIBLE);
                            Snackbar.make(findViewById(R.id.Home_DrawerLayout), "????????????", Snackbar.LENGTH_LONG).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "?????????", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    public void UpLoginNew(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String likeStr = HttpUtils.doGet("http://passport.bilibili.com/qrcode/getLoginUrl", "");
                JSONObject LoginQRJson = null;
                try {
                    LoginQRJson = new JSONObject(likeStr);
                    LoginQRJson = LoginQRJson.getJSONObject("data");
                    URL = LoginQRJson.getString("url");
                    oauthKey = LoginQRJson.getString("oauthKey");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println(URL);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
                        String QRUrl = "https://api.qrserver.com/v1/create-qr-code/?data=" + URL;
                        URL = "https://api.misakamoe.com/app/BiliBiliAsLogin.php?URL=" + URL;
                        init(URL);
                        URL = QRUrl;
                        WebView webView1 = (WebView) findViewById(R.id.Home_WebView1);
                        webView1.setWebChromeClient(new WebChromeClient());
                        webView1.setWebViewClient(new NewHomeActivity.NewWebViewClient());
                        ScrollView webLayout = (ScrollView) findViewById(R.id.Home_WebLayout);
                        webLayout.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }


    public void QRDownload(View view) {
        //???????????????
        pd2 = ProgressDialog.show(NewHomeActivity.this, "??????", "??????????????????");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap QRBitmap = BilibiliPost.returnBitMap("https://api.qrserver.com/v1/create-qr-code/?data=" + URL);
                savePhoto(NewHomeActivity.this, QRBitmap);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "????????????,????????????????????????", Toast.LENGTH_SHORT).show();
                        pd2.cancel();
                        SetQR();
                    }
                });
            }
        }).start();
    }

    private void SetQR() {
        try {
            Context context = NewHomeActivity.this;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("bilibili://qrscan"));
            PackageManager packageManager = context.getPackageManager();
            ComponentName componentName = intent.resolveActivity(packageManager);
            if (componentName != null) {
                context.startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "???????????????????????????B???", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //??????B???????????????
    public void SetQR(View view) {
        SetQR();
    }

    //??????????????????
    //???????????????
    public static void savePhoto(Context context, Bitmap bitmap) {
        File photoDir = new File(Environment.getExternalStorageDirectory(), "BILIBILIAS");
        if (!photoDir.exists()) {
            photoDir.mkdirs();
        }
        String fileName = "BILIBILIAS??????.jpg";
        File photo = new File(photoDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(photo);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        updatePhotoMedia(photo, context);
    }

    //????????????
    private static void updatePhotoMedia(File file, Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);
    }

    class NewWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //??????Cookie????????????
            CookieManager cookieManager = CookieManager.getInstance();
            /*
            cookie = cookieManager.getCookie(url);
            System.out.println(cookie);

             */
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //????????????true?????????WebView????????????false????????????????????????????????????????????????
            //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????webview???????????????
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

    }


    //????????????????????????
    public static String sj(String str, String start, String end) {
        if (str.contains(start) && str.contains(end)) {
            str = str.substring(str.indexOf(start) + start.length());
            return str.substring(0, str.indexOf(end));
        }
        return "";
    }


    /**
     * ??????????????????
     *
     * @param context
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private boolean isNotificationEnabled(Context context) {

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    //?????????????????????????????????????????????????????????
    //??????????????????
    private void checkPermission() {
        mPermissionList.clear();
        //???????????????????????????
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        /**
         * ??????????????????
         */
        if (mPermissionList.isEmpty()) {//?????????????????????????????????????????????
        } else {//??????????????????
            String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//???List????????????
            ActivityCompat.requestPermissions(NewHomeActivity.this, permissions, PERMISSION_REQUEST);
        }
    }


    //?????????????????? -> ????????????????????????????????????????????????????????????

    /**
     * ????????????????????????classes.dex??????????????????????????????????????????????????????
     *
     * @param orginalSHA ??????Apk??????SHA-1???
     */
    public static String apkVerifyWithSHA(Context context, String orginalSHA) {
        String apkPath = context.getPackageCodePath(); // ??????Apk???????????????
        try {
            MessageDigest dexDigest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = new byte[1024];
            int byteCount;
            FileInputStream fis = new FileInputStream(new File(apkPath)); // ??????apk??????
            while ((byteCount = fis.read(bytes)) != -1) {
                dexDigest.update(bytes, 0, byteCount);
            }
            BigInteger bigInteger = new BigInteger(1, dexDigest.digest()); // ??????apk??????????????????
            String sha = bigInteger.toString(16);
            fis.close();
            return sha;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ????????????apk??????MD5?????????????????????????????????????????????
     *
     * @param orginalMD5 ??????Apk??????MD5???
     */
    public static String apkVerifyWithMD5(Context context, String orginalMD5) {
        String apkPath = context.getPackageCodePath(); // ??????Apk???????????????
        System.out.println("????????????");
        System.out.println(apkPath.length());
        LJ = apkPath.length();
        try {
            MessageDigest dexDigest = MessageDigest.getInstance("MD5");
            byte[] bytes = new byte[1024];
            int byteCount;
            FileInputStream fis = new FileInputStream(new File(apkPath)); // ??????apk??????
            while ((byteCount = fis.read(bytes)) != -1) {
                dexDigest.update(bytes, 0, byteCount);
            }
            BigInteger bigInteger = new BigInteger(1, dexDigest.digest()); // ??????apk??????????????????
            String sha = bigInteger.toString(16);
            fis.close();
            return sha;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ????????????classes.dex?????????CRC32???????????????????????????????????????
     *
     * @param orginalCRC ??????classes.dex?????????CRC???
     */
    public static String apkVerifyWithCRC(Context context, String orginalCRC) {
        String apkPath = context.getPackageCodePath(); // ??????Apk???????????????
        try {
            ZipFile zipFile = new ZipFile(apkPath);
            ZipEntry dexEntry = zipFile.getEntry("classes.dex"); // ??????ZIP?????????classes.dex??????
            String dexCRC = String.valueOf(dexEntry.getCrc()); // ??????classes.dex?????????CRC???
            return dexCRC;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
package com.imcys.bilibilias;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.imcys.bilibilias.as.RankingActivity;
import com.imcys.bilibilias.as.VideoAsActivity;
import com.imcys.bilibilias.user.AboutActivity;
import com.imcys.bilibilias.user.UserActivity;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class HomeActivity extends AppCompatActivity {

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
    private String GxUrl = "https://api.misakaloli.com/app/bilibilias.php?type=json&edition=1.2";
    private String Edition = "1.2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        checkPermission();

        //BUGly
        //Bugly.init(getApplicationContext(), "1bb190bc7d", false);

        //??????????????????
        String DLPath = getExternalFilesDir("????????????").toString()+"/Path.txt";
        File file = new File(DLPath);
        if(!file.exists()){
            try {
                BilibiliPost.fileWrite(DLPath,getExternalFilesDir("??????????????????").toString()+"/");
                DLPath = getExternalFilesDir("????????????").toString()+"/??????????????????.txt";
                file = new File(DLPath);
                if(!file.exists()){
                    BilibiliPost.fileWrite(DLPath,"1");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String StrGx = HttpUtils.doGet(GxUrl,"");
                    try {
                        JSONObject jsonGxStr = new JSONObject(StrGx);
                        final String MD5 = jsonGxStr.getString("APKMD5");
                        final String CRC =  jsonGxStr.getString("APKToKenCR");
                        final String SHA = jsonGxStr.getString("APKToKen");
                        final String ID = jsonGxStr.getString("ID");
                        final String StrUrl = jsonGxStr.getString("url");
                        final String sha = apkVerifyWithSHA(HomeActivity.this,SHA);
                        System.out.println(sha);
                        System.out.println(SHA);
                        final String md5 = apkVerifyWithMD5(HomeActivity.this,MD5);
                        System.out.println(md5);
                        System.out.println(MD5);
                        final String crc = apkVerifyWithCRC(HomeActivity.this,CRC);
                        System.out.println(crc);
                        System.out.println(CRC);
                        if (ID.equals("1")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!sha.equals(SHA)){
                                        ScrollView ScrollView_Main = (ScrollView)findViewById(R.id.Home_MainLayout);
                                        ScrollView_Main.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), "???????????????", Toast.LENGTH_SHORT).show();
                                        Uri uri = Uri.parse(StrUrl);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);
                                    }else if(!md5.equals(MD5)){
                                        ScrollView ScrollView_Main = (ScrollView)findViewById(R.id.Home_MainLayout);
                                        ScrollView_Main.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), "???????????????", Toast.LENGTH_SHORT).show();
                                        Uri uri = Uri.parse(StrUrl);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);
                                    }else if(!crc.equals(CRC)){
                                        ScrollView ScrollView_Main = (ScrollView)findViewById(R.id.Home_MainLayout);
                                        ScrollView_Main.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), "???????????????", Toast.LENGTH_SHORT).show();
                                        Uri uri = Uri.parse(StrUrl);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }else if (ID.equals("0")){
                            String fs = HttpUtils.doGet("https://api.misakaloli.com/app/bilibilias.php?type=json&edition="+Edition+"&SHA="+sha+"&MD5="+md5+"&CRC="+crc+"lj="+LJ,cookie);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //????????????
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String StrGx = HttpUtils.doGet(GxUrl,cookie);
                    System.out.println(StrGx);
                    if (!GxUrl.equals("https://api.misakaloli.com/app/bilibilias.php?type=json&edition="+Edition)) {
                    } else {
                        JSONObject jsonGxStr = null;
                        String StrPd = null;
                        try {
                            jsonGxStr = new JSONObject(StrGx);
                            StrPd = jsonGxStr.getString("edition");
                            final String StrNr = jsonGxStr.getString("gxnotice");
                            final String StrUrl = jsonGxStr.getString("url");
                            if (StrPd.equals("1.2")) {
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this)
                                                .setTitle("???????????????")
                                                .setMessage(StrNr)
                                                .setPositiveButton("???????????????", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Uri uri = Uri.parse(StrUrl);
                                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .setNegativeButton("??????", null)
                                                .setNeutralButton(null, null)
                                                .create();
                                        dialog.setCanceledOnTouchOutside(false);
                                        dialog.show();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }catch (Exception e) {

        }
        //????????????
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String StrGx = HttpUtils.doGet(GxUrl,cookie);
                    try {
                        JSONObject jsonGxStr = new JSONObject(StrGx);
                        final String Gg = jsonGxStr.getString("notice");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this)
                                        .setTitle("????????????")
                                        .setMessage(Gg)
                                        .setPositiveButton("???????????????????????????????????????", null)
                                        .setNeutralButton(null, null)
                                        .create();
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.show();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            ).start();
        }catch (Exception e) {
        }

        //????????????????????????
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LoginCheck();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }




    //?????????????????? ??? ??? ???
    public void GoUser(View view){
        Intent intent= new Intent();
        intent.setClass(HomeActivity.this, UserActivity.class);
        intent.putExtra("cookie",cookie);
        intent.putExtra("csrf",csrf);
        intent.putExtra("toKen",toKen);
        intent.putExtra("mid",mid);
        startActivity(intent);
    }

    public void GoVideoAs(View view){
        Intent intent= new Intent();
        intent.setClass(HomeActivity.this, VideoAsActivity.class);
        intent.putExtra("cookie",cookie);
        intent.putExtra("csrf",csrf);
        intent.putExtra("toKen",toKen);
        intent.putExtra("mid",mid);
        startActivity(intent);
    }

    public void goSet(View view){
        Intent intent= new Intent();
        intent.setClass(HomeActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    public void goRanking(View view){
        Intent intent= new Intent();
        intent.setClass(HomeActivity.this, RankingActivity.class);
        startActivity(intent);
    }





    //????????????????????????
    private void LoginCheck() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //????????????????????????
                final String ToKenPath = getExternalFilesDir("??????????????????").toString()+"/"+"token.txt";
                String csrfPath = getExternalFilesDir("??????????????????").toString()+"/"+"csrf.txt";
                String CookiePath = getExternalFilesDir("??????????????????").toString()+"/"+"cookie.txt";
                String likeStr = HttpUtils.doGet("http://passport.bilibili.com/qrcode/getLoginUrl","");
                //?????????????????????????????????????????????????????????????????????????????????????????????
                try {
                    JSONObject LoginQRJson = new JSONObject(likeStr);
                    LoginQRJson = LoginQRJson.getJSONObject("data");
                    URL = LoginQRJson.getString("url");
                    oauthKey = LoginQRJson.getString("oauthKey");
                    System.out.println(URL);
                    csrf = BilibiliPost.fileRead(csrfPath);
                    toKen = BilibiliPost.fileRead(ToKenPath);
                    cookie = BilibiliPost.fileRead(CookiePath);
                    System.out.println(toKen);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String pd = BilibiliPost.nav(toKen);
                if(pd.equals("0")){
                    final String UserNavStr = HttpUtils.doGet("http://api.bilibili.com/x/web-interface/nav",cookie);
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
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            init("https://api.misakaloli.com/app/bilibiliasLogin.php?URL="+URL);
                            WebView webView1 = (WebView)findViewById(R.id.Home_WebView1);
                            webView1.setWebChromeClient(new WebChromeClient());
                            webView1.setWebViewClient(new NewWebViewClient());
                            ScrollView webLayout = (ScrollView)findViewById(R.id.Home_WebLayout);
                            ScrollView mainLayout = (ScrollView)findViewById(R.id.Home_MainLayout);
                            webLayout.setVisibility(View.VISIBLE);
                            mainLayout.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }

    //??????????????????
    private void init(String LoginUrl){
        WebView webView1 = (WebView)findViewById(R.id.Home_WebView1);
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
        webView1.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //????????????true??????????????????WebView????????????false??????????????????????????????????????????
                view.loadUrl(url);
                return true;
            }
        });
    }

    public void NewLogin(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String likeStr = HttpUtils.doGet("http://passport.bilibili.com/qrcode/getLoginUrl","");
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
                        String QRUrl = "https://api.qrserver.com/v1/create-qr-code/?data="+URL;
                        URL = "https://api.misakaloli.com/app/bilibiliasLogin.php?URL="+URL;
                        init(URL);
                        URL = QRUrl;
                        WebView webView1 = (WebView)findViewById(R.id.Home_WebView1);
                        webView1.setWebChromeClient(new WebChromeClient());
                        webView1.setWebViewClient(new NewWebViewClient());
                        ScrollView webLayout = (ScrollView)findViewById(R.id.WebLayout);
                        webLayout.setVisibility(View.VISIBLE);
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
                    cookie = HttpUtils.getCookie("oauthKey=" + oauthKey,"http://passport.bilibili.com/qrcode/getLoginInfo");
                    System.out.println(cookie);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(cookie.length()>45){
                    final String UserNavStr = HttpUtils.doGet("http://api.bilibili.com/x/web-interface/nav",cookie);
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
                            toKen ="SESSDATA="+sj(cookie,"SESSDATA=",";");
                            csrf = sj(cookie,"bili_jct=",";");
                            System.out.println(toKen);
                            String CookiePath = getExternalFilesDir("??????????????????").toString()+"/"+"cookie.txt";
                            String ToKenPath = getExternalFilesDir("??????????????????").toString()+"/"+"token.txt";
                            String csrfPath = getExternalFilesDir("??????????????????").toString()+"/"+"csrf.txt";
                            try {
                                BilibiliPost.fileWrite(CookiePath,cookie);
                                BilibiliPost.fileWrite(ToKenPath,toKen);
                                BilibiliPost.fileWrite(csrfPath,csrf);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ScrollView webLayout = (ScrollView)findViewById(R.id.Home_WebLayout);
                            webLayout.setVisibility(View.GONE);
                            ScrollView mainLayout = (ScrollView)findViewById(R.id.Home_MainLayout);
                            mainLayout.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
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
                String likeStr = HttpUtils.doGet("http://passport.bilibili.com/qrcode/getLoginUrl","");
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
                        String QRUrl = "https://api.qrserver.com/v1/create-qr-code/?data="+URL;
                        URL = "https://api.misakaloli.com/app/bilibiliasLogin.php?URL="+URL;
                        init(URL);
                        URL = QRUrl;
                        WebView webView1 = (WebView)findViewById(R.id.Home_WebView1);
                        webView1.setWebChromeClient(new WebChromeClient());
                        webView1.setWebViewClient(new NewWebViewClient());
                        ScrollView webLayout = (ScrollView)findViewById(R.id.Home_WebLayout);
                        webLayout.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }


    public void QRDownload(View view){
        //???????????????
        pd2 = ProgressDialog.show(HomeActivity.this, "??????", "??????????????????");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap QRBitmap = BilibiliPost.returnBitMap("https://api.qrserver.com/v1/create-qr-code/?data="+URL);
                savePhoto(HomeActivity.this,QRBitmap);
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

    private void SetQR(){
        try {
            Context context = HomeActivity.this;
            Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("bilibili://qrscan"));
            PackageManager packageManager= context.getPackageManager();
            ComponentName componentName=intent.resolveActivity(packageManager);
            if (componentName!=null){
                context.startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext(), "???????????????????????????B???", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //??????B???????????????
    public void SetQR(View view){
        SetQR();
    }

    //??????????????????
    //???????????????
    public static void savePhoto(Context context, Bitmap bitmap){
        File photoDir = new File(Environment.getExternalStorageDirectory(),"BILIBILIAS");
        if (!photoDir.exists()){
            photoDir.mkdirs();
        }
        String fileName = "BILIBILIAS??????.jpg";
        File photo = new File(photoDir,fileName);
        try {
            FileOutputStream fos = new FileOutputStream(photo);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
            fos.flush();
            fos.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        updatePhotoMedia(photo,context);
    }

    //????????????
    private static void updatePhotoMedia(File file ,Context context){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);
    }

    class NewWebViewClient extends WebViewClient{
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


    public static String sj(String str, String start, String end)
    {
        if (str.contains(start) && str.contains(end))
        {
            str = str.substring(str.indexOf(start) + start.length());
            return str.substring(0, str.indexOf(end));
        }
        return "";
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
            ActivityCompat.requestPermissions(HomeActivity.this, permissions, PERMISSION_REQUEST);
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
    public static String  apkVerifyWithMD5(Context context, String orginalMD5) {
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
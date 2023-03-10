package com.imcys.bilibilias.user;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.ListPreference;
import android.preference.PreferenceManager;

import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.tabs.TabLayout;
import com.imcys.bilibilias.AppFilePathUtils;

import com.imcys.bilibilias.BilibiliPost;

import com.imcys.bilibilias.R;

import com.imcys.bilibilias.fileUriUtils;
import com.kongzue.dialogx.dialogs.BottomMenu;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class CacheActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private List<Cache> CacheList = new ArrayList<>();
    private ListPreference mDLPreference;
    private TabLayout tabLayout;
    private int tabLayNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cache);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CacheActivity.this);
        newTool();
        init();
        VideoLoad();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();


    }

    private void init() {
        tabLayout = (TabLayout) findViewById(R.id.Collection_TabLayout);
        tabLayNumber = 0;
        tabLayout.addTab(tabLayout.newTab().setText("????????????"));
        tabLayout.addTab(tabLayout.newTab().setText("????????????"));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabLayNumber = tab.getPosition();
                if (tabLayNumber == 1) {
                    DMLoad();
                } else {
                    VideoLoad();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    //Tool???????????????????????????
    private void newTool() {
        androidx.appcompat.widget.Toolbar mToolbar = (Toolbar) findViewById(R.id.User_Cache_Toolbar);
        mToolbar.setTitle("????????????");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//???????????????????????????????????????
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //??????????????????
    private void VideoLoad() {
        CacheList.clear();
        ArrayList<File> mFileName = AppFilePathUtils.getAllDataFileName(sharedPreferences.getString("DownloadPath", getString(R.string.DownloadPath)));
        for (int i = 0; i < mFileName.size(); i++) {
            String FileData = mFileName.get(i).getName();
            int lengthString = FileData.length();
            if (FileData.startsWith("mp4", lengthString - 3) || FileData.startsWith("flv", lengthString - 3)) {
                if (FileData.substring(lengthString - 4, lengthString).contains(".")) {
                    double fileSize = (double) (mFileName.get(i).length() / 1048576);
                    Cache CacheListData = new Cache(FileData.substring(0, lengthString - 4), FileData.substring(lengthString - 3, lengthString), fileSize + "MB", sharedPreferences.getString("DownloadPath", getString(R.string.DownloadPath)) + FileData, CacheActivity.this);
                    CacheList.add(CacheListData);
                }
            }
        }
        //????????????
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.User_Cache_RecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CacheActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        CacheAdapter adapter = new CacheAdapter(CacheList);

        adapter.setOnItemClickListener(new CacheAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Cache cache = CacheList.get(position);

                BottomMenu.show(new String[]{"????????????", "????????????"})
                        .setMessage("????????????")
                        .setOnMenuItemClickListener(new OnMenuItemClickListener<BottomMenu>() {
                            @Override
                            public boolean onClick(BottomMenu dialog, CharSequence text, int index) {
                                switch (text.toString()) {
                                    case "????????????":
                                        share(CacheActivity.this,"????????????", FileProvider.getUriForFile(CacheActivity.this,"com.imcys.bilibilias.fileProvider",new File(cache.getPath())),"video/*");

                                        break;
                                    case "????????????":
                                        MessageDialog.build()
                                                .setTitle("????????????")
                                                .setMessage("???????????????????????????")
                                                //?????????????????????????????????
                                                .setOkButton("??????", new OnDialogButtonClickListener<MessageDialog>() {
                                                    @Override
                                                    public boolean onClick(MessageDialog baseDialog, View v) {
                                                        BilibiliPost.deleteFile(cache.getPath());
                                                        CacheList.remove(position);
                                                        adapter.notifyDataSetChanged();
                                                        return false;
                                                    }
                                                })
                                                .setCancelButton("?????????", null).show();
                                        break;
                                }
                                return false;
                            }
                        });


            }

        });

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


    private void DMLoad() {
        CacheList.clear();
        ArrayList<File> mFileName = AppFilePathUtils.getAllDataFileName(sharedPreferences.getString("DownloadPath", getString(R.string.DownloadPath)));
        for (int i = 0; i < mFileName.size(); i++) {
            String FileData = mFileName.get(i).getName();
            int lengthString = FileData.length();
            if (FileData.startsWith("xml", lengthString - 3)) {
                if (FileData.substring(lengthString - 4, lengthString).contains(".")) {
                    double fileSize = (double) (mFileName.get(i).length() / 1048576);
                    Cache CacheListData = new Cache(FileData.substring(0, lengthString - 4), FileData.substring(lengthString - 3, lengthString), fileSize + "MB", sharedPreferences.getString("DownloadPath", getString(R.string.DownloadPath)) + FileData, CacheActivity.this);
                    CacheList.add(CacheListData);
                }
            }
        }
        /**
         if (FileData.substring(lengthString - 4, lengthString).contains(".")) {
         Cache CacheListData = new Cache(FileData.substring(0, lengthString - 4), FileData.substring(lengthString - 3, lengthString), sharedPreferences.getString("DownloadPath", getString(R.string.DownloadPath)) + FileData, CacheActivity.this);
         CacheList.add(CacheListData);
         } else if (FileData.substring(lengthString - 5, lengthString).contains(".")) {
         Cache CacheListData = new Cache(FileData.substring(0, lengthString - 5), FileData.substring(lengthString - 4, lengthString), sharedPreferences.getString("DownloadPath", getString(R.string.DownloadPath)) + FileData, CacheActivity.this);
         CacheList.add(CacheListData);
         }
         */

        //????????????
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.User_Cache_RecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CacheActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        CacheAdapter adapter = new CacheAdapter(CacheList);
        adapter.setOnItemClickListener(new CacheAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Cache cache = CacheList.get(position);
                BottomMenu.show(new String[]{"????????????", "????????????"})
                        .setMessage("????????????")
                        .setOnMenuItemClickListener(new OnMenuItemClickListener<BottomMenu>() {
                            @Override
                            public boolean onClick(BottomMenu dialog, CharSequence text, int index) {
                                switch (text.toString()) {
                                    case "????????????":
                                        share(CacheActivity.this,"????????????", FileProvider.getUriForFile(CacheActivity.this,"com.imcys.bilibilias.fileProvider",new File(cache.getPath())),"text/*");
                                        break;
                                    case "????????????":
                                        MessageDialog.build()
                                                .setTitle("????????????")
                                                .setMessage("???????????????????????????")
                                                //?????????????????????????????????
                                                .setOkButton("??????", new OnDialogButtonClickListener<MessageDialog>() {
                                                    @Override
                                                    public boolean onClick(MessageDialog baseDialog, View v) {
                                                        BilibiliPost.deleteFile(cache.getPath());
                                                        CacheList.remove(position);
                                                        adapter.notifyDataSetChanged();
                                                        return false;
                                                    }
                                                })
                                                .setCancelButton("?????????", null).show();
                                        break;
                                }
                                return false;
                            }
                        });
            }

        });
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void shareFile(String filePath, String fileType) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(fileType);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "????????????"));
    }



    public static void share(Context context, String content, Uri uri ,String type) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (uri != null) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType(type);
            //??????????????????????????????sms_body????????????
            shareIntent.putExtra("sms_body", content);
        } else {
            shareIntent.setType(type);
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        //???????????????????????????
        context.startActivity(Intent.createChooser(shareIntent, "????????????"));
        //??????????????????
    }



}


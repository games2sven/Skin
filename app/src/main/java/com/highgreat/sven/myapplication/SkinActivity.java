package com.highgreat.sven.myapplication;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;


import com.highgreat.sven.myapplication.skin.Skin;
import com.highgreat.sven.myapplication.skin.SkinUtils;
import com.highgreat.sven.skin_core.SkinManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


//import com.dn_alan.skin_core1.SkinManager;

public class SkinActivity extends AppCompatActivity {


    /**
     * 从服务器拉取的皮肤表
     */
    List<Skin> skins = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin);
//
        skins.add(new Skin("e0893ca73a972d82bcfc3a5a7a83666d", "1111111.skin", "app-skin-debug" +
                ".apk"));
    }

    /**
     * 下载皮肤包
     */
    private void selectSkin(Skin skin) {
        File theme = new File(getFilesDir(), "theme");
        if (theme.exists() && theme.isFile()) {
            theme.delete();
        }
        theme.mkdirs();
        File skinFile = skin.getSkinFile(theme);
        if (skinFile.exists()) {
            Log.e("SkinActivity", "皮肤已存在,开始换肤");
            return;
        }
        Log.e("SkinActivity", "皮肤不存在,开始下载");
        FileOutputStream fos = null;
        InputStream is = null;
        //临时文件
        File tempSkin = new File(skinFile.getParentFile(), skin.name + ".temp");
        try {
            fos = new FileOutputStream(tempSkin);
            //假设下载皮肤包
            is = getAssets().open(skin.url);
            byte[] bytes = new byte[10240];
            int len;
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
            //下载成功，将皮肤包信息insert已下载数据库
            Log.e("SkinActivity", "皮肤包下载完成开始校验");
            //皮肤包的md5校验 防止下载文件损坏(但是会减慢速度。从数据库查询已下载皮肤表数据库中保留md5字段)
            if (TextUtils.equals(SkinUtils.getSkinMD5(tempSkin), skin.md5)) {
                Log.e("SkinActivity", "校验成功,修改文件名。");
                tempSkin.renameTo(skinFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tempSkin.delete();
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void change(View view) {
//        //使用第0个皮肤
        Skin skin = skins.get(0);
        selectSkin(skin);
//        //换肤
        SkinManager.getInstance().loadSkin(skin.path);
//        String path = Environment.getExternalStorageDirectory() + File.separator + "app_skin-debug.apk";
//         SkinManager.getInstance().loadSkin("/sdcard/app-skin-debug.skin");
//        SkinManager.getInstance().loadSkin(path);


        /**
         * 皮肤包 资源id  与 app 资源id 是否一样
         * 是 1   不是2
         */
    }

    public void restore(View view) {
        SkinManager.getInstance().loadSkin(null);
    }

    /**
     * 夜间模式
     * @param view
     */
    public void night(View view){
        int currentMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if(currentMode != Configuration.UI_MODE_NIGHT_YES){
            //保存夜间模式状态,Application中可以根据这个值判断是否设置夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            //ThemeConfig主题配置，这里只是保存了是否是夜间模式的boolean值
            NightModeConfig.getInstance().setNightMode(getApplicationContext(),true);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            NightModeConfig.getInstance().setNightMode(getApplicationContext(),false);
        }

        recreate();
    }

    /**
     * 日间模式
     */
//    public void day(View view){
//        //获取当前的模式，设置相反的模式，
//        int currentMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
//        if(currentMode == Configuration.UI_MODE_NIGHT_YES){
//            Log.i("Sven","设置非夜间模式");
//            getDelegate().setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//
//            //ThemeConfig主题配置 这里只是保存了是否夜间模式
//            NightModeConfig.getInstance().setNightMode(getApplicationContext(),false);
//        }else{
//            Log.i("Sven","设置夜间模式");
//           getDelegate().setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//           NightModeConfig.getInstance().setNightMode(getApplicationContext(),true);
//        }
//
//        recreate();//重新加载Activity
//    }


    private static boolean isNight=false;
    private void setNightMode() {
        //  获取当前模式
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        //  将是否为夜间模式保存到SharedPreferences
        //  切换模式
        getDelegate().setDefaultNightMode(isNight ?
                AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);

        //  重启Activity
        recreate();
    }

    public void day(View view) {
        setNightMode();
        isNight=!isNight;
    }



}

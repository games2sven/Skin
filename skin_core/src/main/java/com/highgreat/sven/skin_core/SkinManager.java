package com.highgreat.sven.skin_core;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.highgreat.sven.skin_core.utils.SkinPreference;
import com.highgreat.sven.skin_core.utils.SkinResources;

import java.lang.reflect.Method;
import java.util.Observable;

public class SkinManager extends Observable {

    Application application;
    private static  SkinManager instance;

    /**
     * Activity生命周期回调
     */
    private SkinActivityLifecycle skinActivityLifecycle;

    public static SkinManager getInstance(){
        return instance;
    }

    public static void init(Application application){
        synchronized (SkinManager.class){
            if(null == instance){
                instance = new SkinManager(application);
            }
        }
    }



    private SkinManager(Application application) {
        this.application = application;
        //共享首选项，用于记录当前使用的皮肤
        SkinPreference.init(application);
        //资源管理类  用于从app/皮肤  中加载资源
        SkinResources.init(application);
        //注册Activity生命周期
        skinActivityLifecycle = new SkinActivityLifecycle();
        application.registerActivityLifecycleCallbacks(skinActivityLifecycle);

        loadSkin(SkinPreference.getInstance().getSkin());
    }

    /**
     * 加载皮肤并应用
     * @param skinPath
     */

    public void loadSkin(String skinPath) {

        if(TextUtils.isEmpty(skinPath)){
            //记录使用默认皮肤
            SkinPreference.getInstance().setSkin("");
            //清空资源管理器，皮肤资源属性等
            SkinResources.getInstance().reset();
        }else{
            try {
                //反射创建AssetManager
                AssetManager manager = AssetManager.class.newInstance();
                // 资料路径设置 目录或者压缩包
                Method addAssetPath = manager.getClass().getMethod("addAssetPath",String.class);
                addAssetPath.invoke(manager,skinPath);

                Resources appResources = this.application.getResources();
                Resources skinResources = new Resources(manager,appResources.getDisplayMetrics(),appResources.getConfiguration());

                //记录
                SkinPreference.getInstance().setSkin(skinPath);
                //获取外部Apk(皮肤包) 包名
                PackageManager packageManager = this.application.getPackageManager();
                PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo(skinPath,PackageManager.GET_ACTIVITIES);
                String packageName = packageArchiveInfo.packageName;

                SkinResources.getInstance().applySkin(skinResources,packageName);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //采集的view 皮肤包
        setChanged();
        //通知观察者
        notifyObservers(null);

    }

    public void updateSkin(Activity activity){
        skinActivityLifecycle.updateSkin(activity);
    }

}

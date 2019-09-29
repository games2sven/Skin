package com.highgreat.sven.skin_core;

import android.app.Activity;
import android.app.Application;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;

import com.highgreat.sven.skin_core.utils.SkinThemeUtils;

import java.lang.reflect.Field;
import java.util.HashMap;


class SkinActivityLifecycle implements Application.ActivityLifecycleCallbacks {
    HashMap<Activity , SkinLayoutFactory> factoryHashMap = new HashMap<>();

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

        //更新状态栏
        SkinThemeUtils.updateStatusBarColor(activity);

        //更新字体
        Typeface typeface = SkinThemeUtils.getSkinTypeface(activity);

        LayoutInflater layoutInflater = LayoutInflater.from(activity);

        try {
            Field mFactorySet = LayoutInflater.class.getDeclaredField("mFactorySet");
            mFactorySet.setAccessible(true);
            mFactorySet.setBoolean(layoutInflater,false);
        } catch (Exception e) {
            e.printStackTrace();
        }


        SkinLayoutFactory factory = new SkinLayoutFactory(activity,typeface);
        layoutInflater.setFactory2(factory);


        //注册观察者
        SkinManager.getInstance().addObserver(factory);
        factoryHashMap.put(activity,factory);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        //删除观察者
        SkinLayoutFactory remove = factoryHashMap.remove(activity);
        SkinManager.getInstance().deleteObserver(remove);
    }

    public void updateSkin(Activity activity) {
        SkinLayoutFactory skinLayoutInflaterFactory = factoryHashMap.get(activity);
        skinLayoutInflaterFactory.update(null, null);
    }

}

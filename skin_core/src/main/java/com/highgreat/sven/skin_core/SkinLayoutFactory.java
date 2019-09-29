package com.highgreat.sven.skin_core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.highgreat.sven.skin_core.utils.SkinThemeUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;


class SkinLayoutFactory implements LayoutInflater.Factory2 , Observer {

    private static final String[] mClassPrefixlist = {
            "android.widget.",
            "android.view.",
            "android.webkit."
    };

    private static final HashMap<String,Constructor<? extends View>> mConstructor = new HashMap<>();
    private final Activity activity;

    //属性处理类
    private SkinAttribute attribute;

    public SkinLayoutFactory(Activity activity, Typeface typeface){
        this.activity = activity;
        attribute = new SkinAttribute(typeface);
    }


    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
       //用自己的方式创建view（通过反射构造方法实现）
        View view = createViewFromTag(name,context,attrs);
        //自定义view
        if(null == view){
            view = createView(name,context,attrs);
        }

        //创建view了之后筛选需要进行修改的属性
        if(null != view){
            attribute.load(view,attrs);
        }
        return view;
    }

    private View createViewFromTag(String name, Context context, AttributeSet attrs) {

        //包含自定义控件
        if(-1 != name.indexOf(".")){
            return null;
        }

        View view = null;
        for(int i = 0;i<mClassPrefixlist.length;i++){
            view = createView(mClassPrefixlist[i]+name ,context,attrs);
            if(null != view){
                break;
            }
        }
        return view;
    }

    private View createView(String name, Context context, AttributeSet attrs) {

        Constructor<? extends View> constructor = mConstructor.get(name);
        if(constructor == null){
            try {
                //通过全类名获取class
                Class<? extends View> aClass = context.getClassLoader().loadClass(name).asSubclass(View.class);
                //获取构造方法
                constructor =aClass.getConstructor(Context.class,AttributeSet.class);
                mConstructor.put(name,constructor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(null != constructor){
            try {
                //调用构造函数
                return constructor.newInstance(context,attrs);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        SkinThemeUtils.updateStatusBarColor(activity);
        Typeface typeface = SkinThemeUtils.getSkinTypeface(activity);
        attribute.setTypeface(typeface);
        attribute.applySkin();
    }
}

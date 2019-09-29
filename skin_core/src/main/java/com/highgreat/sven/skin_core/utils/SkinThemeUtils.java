package com.highgreat.sven.skin_core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;

import com.highgreat.sven.skin_core.R;


public class SkinThemeUtils {

    private static int[] APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS = {
            android.support.v7.appcompat.R.attr.colorPrimaryDark
    };
    private static int[] STATUSBAR_COLOR_ATTRS = {android.R.attr.statusBarColor,android.R.attr.navigationBarColor};

    private static int[] TYPEFACE_ATTRS = {R.attr.skinTypeface};

    public static int[] getResId(Context context, int[] attrs){
        int [] resIds = new int [attrs.length];
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        for (int i = 0; i < typedArray.length(); i++) {
            resIds[i] = typedArray.getResourceId(i,0);
        }
        typedArray.recycle();
        return resIds;
    }

    //替换状态栏
    public static void updateStatusBarColor(Activity activity){
        //5.0以上才能修改
        if(Build.VERSION.SDK_INT <Build.VERSION_CODES.LOLLIPOP){
            return ;
        }

        //获取statusBarColor与navigationBarColor  颜色值
        int[] statusBarId = getResId(activity,STATUSBAR_COLOR_ATTRS);
        //如果statusBarColor 配置颜色值 就换肤
        //如果直接在style中写入固定颜色值(而不是 @color/XXX ) 获得0
        if(statusBarId[0] != 0){
            activity.getWindow().setStatusBarColor(SkinResources.getInstance().getColor(statusBarId[0]));
        }else{
            //获取colorPrimaryDark
            int redId = getResId(activity,APPCOMPAT_COLOR_PRIMARY_DARK_ATTRS)[0];
            if(redId != 0){
                activity.getWindow().setStatusBarColor(SkinResources.getInstance().getColor(redId));
            }
        }

        if(statusBarId[1] != 0){
            activity.getWindow().setNavigationBarColor(SkinResources.getInstance().getColor(statusBarId[1]));
        }
    }

    /**
     * 字体
     */
     public static Typeface getSkinTypeface(Activity activity){
         int skinTypefaceId = getResId(activity,TYPEFACE_ATTRS)[0];
         return SkinResources.getInstance().getTypeface(skinTypefaceId);
     }

}

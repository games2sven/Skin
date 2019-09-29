package com.highgreat.sven.skin_core.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

public class SkinResources {

    private static SkinResources instance;

    private Resources mAppResources;
    private Resources mSkinResources;
    private String mSkinPkgName;
    private boolean isDefaultSkin = true;
    private SkinResources(Context context) {
        mAppResources = context.getResources();
    }

    public static void init(Context context){
        if (instance == null){
            synchronized (SkinResources.class){
                if(instance == null){
                    instance = new SkinResources(context);
                }
            }
        }
    }

    public static SkinResources getInstance(){return instance;};

    //使用默认皮肤
    public void reset() {
        mSkinResources = null;
        mSkinPkgName = "";
        isDefaultSkin = true;
    }

    public void applySkin(Resources resources,String pkgName){
        mSkinResources = resources;
        mSkinPkgName = pkgName;
        //是否使用默认皮肤
        isDefaultSkin = TextUtils.isEmpty(pkgName) || resources == null;
    }



    //根据资源名称获取资源id
    public int getIdentifier(int resId){
        //在皮肤包中不一定就是当前程序的id
        //获取对应id在当前程序中的名称

        if(isDefaultSkin){
            return resId;
        }

        String resourceEntryName = mAppResources.getResourceEntryName(resId);
        String resType = mAppResources.getResourceTypeName(resId);

        int skinId = mSkinResources.getIdentifier(resourceEntryName,resType,mSkinPkgName);
        return skinId;
    }

    public int getColor(int resId){
        if(isDefaultSkin){
            return mAppResources.getColor(resId);
        }
        int skinId = getIdentifier(resId);
        if(skinId == 0){
            return mAppResources.getColor(resId);
        }
        return mSkinResources.getColor(skinId);
    }

    //selector 颜色变色
    public ColorStateList getColorStateList(int resId){
        if(isDefaultSkin){
            return mAppResources.getColorStateList(resId);
        }
        int skinId = getIdentifier(resId);
        if(skinId == 0){
            return mAppResources.getColorStateList(resId);
        }
        return mSkinResources.getColorStateList(skinId);
    }

    public Drawable getDrawable(int resId){
        //如果有皮肤 isDefaultSkin false 没有就是true
        if(isDefaultSkin){
            return mAppResources.getDrawable(resId);
        }
        int skinId = getIdentifier(resId);
        if(skinId == 0){
            return mAppResources.getDrawable(resId);
        }
        return mSkinResources.getDrawable(skinId);
    }

    /**
     * 可能是Color 也可能是drawable
     */
    public Object getBackground(int resId){
        String resourceTypeName = mAppResources.getResourceTypeName(resId);

        if(resourceTypeName.equals("color")){
            return getColor(resId);
        }else {
            return getDrawable(resId);
        }
    }

    public String getString(int resId){
        if(isDefaultSkin){
            return mAppResources.getString(resId);
        }
        int skinId = getIdentifier(resId);
        if(skinId == 0){
            return mAppResources.getString(resId);
        }
        return mSkinResources.getString(skinId);
    }

    //字体
    public Typeface getTypeface(int resId){
        String skinTypefacePath = getString(resId);
        if(TextUtils.isEmpty(skinTypefacePath)){
            return Typeface.DEFAULT;
        }

        Typeface typeface;
        try {
            if (isDefaultSkin) {
                typeface = Typeface.createFromAsset(mAppResources.getAssets(),skinTypefacePath);
                return typeface;
            }
            return Typeface.createFromAsset(mSkinResources.getAssets(), skinTypefacePath);
        } catch (Exception e) {
        }
        return Typeface.DEFAULT;
    }

}

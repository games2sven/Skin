package com.highgreat.sven.skin_core;

import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.highgreat.sven.skin_core.utils.SkinResources;
import com.highgreat.sven.skin_core.utils.SkinThemeUtils;

import java.util.ArrayList;
import java.util.List;

class SkinAttribute {

    private static final List<String> mAttributes = new ArrayList<>();

    //跟换肤有关系的属性
    static {
        mAttributes.add("background");
        mAttributes.add("src");

        mAttributes.add("textColor");
        mAttributes.add("drawableLeft");
        mAttributes.add("drawableTop");
        mAttributes.add("drawableRight");
        mAttributes.add("drawableBottom");

        mAttributes.add("skinTypeface");
    }

    private List<SkinView> skinViews = new ArrayList();
    private Typeface typeface;

    public SkinAttribute(Typeface typeface){
        this.typeface = typeface;
    }

    public void setTypeface(Typeface typeface){
        this.typeface = typeface;
    };

    public void load(View view, AttributeSet attrs) {
        List skinPains = new ArrayList();
        for (int i = 0;i<attrs.getAttributeCount();i++){
            //获取属性名字
            String attributeName = attrs.getAttributeName(i);
            if(mAttributes.contains(attributeName)){
                //获取属性对应的值
                String attributeValue = attrs.getAttributeValue(i);
                // 如果是color的 以#开头表示写死的颜色 不可用于换肤
                if(attributeValue.startsWith("#")){
                    continue;
                }

                int resId;
                //判断前缀字符串 是否是“？”
                if(attributeValue.startsWith("?")){
                    //字符串的子字符串  从下标 1 位置开始
                    int attrId = Integer.parseInt(attributeValue.substring(1));
                    resId = SkinThemeUtils.getResId(view.getContext(),new int[]{attrId})[0];
                }else{
                    resId = Integer.parseInt(attributeValue.substring(1));
                }

                SkinPain skinPain = new SkinPain(attributeName,resId);
                skinPains.add(skinPain);
            }
        }

        if(!skinPains.isEmpty()){
            SkinView skinView = new SkinView(view,skinPains);
            skinView.applySkin(typeface);
            skinViews.add(skinView);
        }else if(view instanceof TextView || view instanceof SkinViewSupport){
            //没有属性满足，但是需要修改字体
            SkinView skinView = new SkinView(view,skinPains);
            skinView.applySkin(typeface);
            skinViews.add(skinView);
        }

    }

    //需要进行换肤的view对象
    static class SkinView{
        View view;
        List<SkinPain> skinPains;

        //一个View里面可能存在多个需要换肤的属性 比如Textview含有textcolor和backgroud两个属性
        public SkinView(View view,List<SkinPain> skinPains){
            this.view = view;
            this.skinPains = skinPains;
        }

        public void applySkin(Typeface typeface) {
            applyTypeFace(typeface);
            applySkinSupport();
            for (SkinPain skinPain : skinPains) {
                Drawable left = null, top = null, right = null, bottom = null;
                switch (skinPain.attributeName){
                    case "background":
                        Object background = SkinResources.getInstance().getBackground(skinPain.resId);
                       //Color
                        if(background instanceof Integer){
                            view.setBackgroundColor((Integer) background);
                        }else{
                            ViewCompat.setBackground(view,(Drawable)background);
                        }
                        break;
                    case "src":
                        background = SkinResources.getInstance().getBackground(skinPain.resId);
                        if(background instanceof Integer){
                            ((ImageView)view).setImageDrawable(new ColorDrawable((Integer)background));
                        }else{
                            ((ImageView)view).setImageDrawable((Drawable)background);
                        }
                        break;
                    case "textColor":
                        ((TextView) view).setTextColor(SkinResources.getInstance().getColorStateList
                                (skinPain.resId));
                        break;
                    case "drawableLeft":
                        left = SkinResources.getInstance().getDrawable(skinPain.resId);
                        break;
                    case "drawableTop":
                        top = SkinResources.getInstance().getDrawable(skinPain.resId);
                        break;
                    case "drawableRight":
                        right = SkinResources.getInstance().getDrawable(skinPain.resId);
                        break;
                    case "drawableBottom":
                        bottom = SkinResources.getInstance().getDrawable(skinPain.resId);
                        break;
                    case "skinTypeface":
                        applyTypeFace(SkinResources.getInstance().getTypeface(skinPain.resId));
                        break;
                }
                if (null != left || null != right || null != top || null != bottom) {
                    ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(left, top, right,
                            bottom);
                }
            }
        }

        private void applySkinSupport() {
            if (view instanceof SkinViewSupport) {
                ((SkinViewSupport) view).applySkin();
            }
        }

        private void applyTypeFace(Typeface typeface){
            if(view instanceof TextView){
                ((TextView)view).setTypeface(typeface);
            }
        }

    }

    //换肤需要用到的属性名称和对应的值封装成一个对象
    static class SkinPain{
        String attributeName;
        int resId;

        public SkinPain(String attributeName,int resId){
            this.attributeName = attributeName;
            this.resId = resId;
        }
    }

    public void applySkin(){
        for(SkinView mSkinView : skinViews){
            mSkinView.applySkin(typeface);
        }
    }


}

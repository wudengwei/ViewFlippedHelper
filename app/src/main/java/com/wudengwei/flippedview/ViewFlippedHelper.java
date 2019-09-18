package com.wudengwei.flippedview;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.AnimRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ViewFlipper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wudengwei
 * on 2019/4/18
 */
public class ViewFlippedHelper<T> {
    private Context mContext;
    private ViewFlipper viewFlipper;
    private List<T> dataList;
    private int mLayoutId;//布局id
    public ViewFlippedHelper(ViewFlipper viewFlipper) {
        this.viewFlipper = viewFlipper;
        mContext = viewFlipper.getContext();
        dataList = new ArrayList<>();
    }

    public void setData(@LayoutRes int layoutId,@NonNull List<T> dataList) {
        mLayoutId = layoutId;
        this.dataList = dataList;
        fillView();
    }

    public void updateData(@NonNull List<T> dataList) {
        this.dataList = dataList;
        if (dataList.size() <= 1) {
            stop();
        }
        fillView();
    }

    private void fillView() {
        if (viewFlipper != null) {
            viewFlipper.removeAllViews();
        }
        for (int i=0;i<dataList.size();i++) {
            View view = LayoutInflater.from(mContext).inflate(mLayoutId, null);
            view.setTag(i);
            if (mOnSetValueListener != null) {
                mOnSetValueListener.onSetValue(view,i);
            }
            viewFlipper.addView(view);
        }
    }

    // 设置动画切换的时间间隔
    public void setFlipInterval(int flipInterval) {
        viewFlipper.setFlipInterval(3000);
    }

    // 设置进入退出的动画
    public void setAnimation(@AnimRes int inResid,@AnimRes int outResid) {
        if (viewFlipper == null) {
            return;
        }
        // 设置进入的动画
        viewFlipper.setInAnimation(mContext, inResid);
        // 设置退出的动画
        viewFlipper.setOutAnimation(mContext, outResid);
    }

    // 启动动画，开始循环；停止循环：stopFlipping
    public void start() {
        if (viewFlipper == null) {
            return;
        }
        if (!viewFlipper.isFlipping()) {
            viewFlipper.startFlipping();
        }
    }

    //. 启动动画，开始循环；停止循环：stopFlipping
    public void stop() {
        if (viewFlipper == null) {
            return;
        }
        if (viewFlipper.isFlipping()) {
            viewFlipper.stopFlipping();
        }
    }

    private OnSetValueListener mOnSetValueListener;

    public void setOnSetValueListener(OnSetValueListener onSetValueListener) {
        this.mOnSetValueListener = onSetValueListener;
    }

    public interface OnSetValueListener {
        void onSetValue(View view, int position);
    }

    private OnClickListener mOnClickListener;

    public void setOnClickListener(OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
        if (dataList.size() == 0)
            return;
        if (dataList.size() == viewFlipper.getChildCount()) {
            for (int i=0;i<dataList.size();i++) {
                viewFlipper.getChildAt(i).setOnClickListener(vOnClickListener);
            }
        } else {
            Log.e("kkk","数量不同");
        }
    }

    public interface OnClickListener {
        void onClick(View view,int postion);
    }

    private View.OnClickListener vOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnClickListener != null) {
                int position = (int) v.getTag();
                mOnClickListener.onClick(v,position);
            }
        }
    };
}
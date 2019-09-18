package com.wudengwei.flippedview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wudengwei
 * on 2018/12/4
 */
public class TagFlowLayout extends ViewGroup {
    /*保存所有的view*/
    private List<Line> mLines;
    /*正在使用的行*/
    private Line mCurrentLine;
    /*tag之间的间隔*/
    private int tagSpace;
    /*line之间的间隔*/
    private int lineSpace;
    /*是否让所有的line的使用宽度等于最大宽度*/
    private boolean fillAllLineWidth = false;

    private boolean isAvg = true;
    private int lineItemCount = 3;

    public void setFillAllLineWidth(boolean fillAllLineWidth) {
        this.fillAllLineWidth = fillAllLineWidth;
    }
    /*tag点击事件回调*/
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public TagFlowLayout(Context context) {
        this(context,null);
    }

    public TagFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TagFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLines = new ArrayList<>();
        tagSpace = dp2px(context,10f);
        lineSpace = dp2px(context,10f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e("onMeasure",""+widthMeasureSpec);
        /*清空*/
        mLines.clear();
        mCurrentLine = null;
        /*总宽度*/
        int totalWidth = MeasureSpec.getSize(widthMeasureSpec);
        /*最大可以使用的宽度*/
        int maxWidth = totalWidth - getPaddingLeft() - getPaddingRight();
        Log.e("maxWidth",""+maxWidth);
        for (int i=0;i<getChildCount();i++) {
            View child = getChildAt(i);
            if (isAvg) {
                measureChild(child,widthMeasureSpec,heightMeasureSpec);
                int specWidth = MeasureSpec.makeMeasureSpec((maxWidth-tagSpace*(lineItemCount-1))/lineItemCount,MeasureSpec.EXACTLY);
                int specHeight = MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(),MeasureSpec.EXACTLY);
                child.measure(specWidth,specHeight);
                Log.e("child Width",""+child.getMeasuredWidth());
            } else {
                /*测量孩子的宽高*/
                measureChild(child,widthMeasureSpec,heightMeasureSpec);
            }
            /*无论如何，必须存在一个当前使用的line*/
            if (mCurrentLine == null) {
                mCurrentLine = new Line(maxWidth,tagSpace);
                mCurrentLine.fillWidth = fillAllLineWidth;
                mCurrentLine.addView(child);
                mLines.add(mCurrentLine);
            } else {
                /*当前的line可以添加view就添加，否则再创建一个line(下一行)*/
                if (mCurrentLine.canAddView(child)) {
                    mCurrentLine.addView(child);
                } else {
                    mCurrentLine = new Line(maxWidth,tagSpace);
                    mCurrentLine.fillWidth = fillAllLineWidth;
                    mCurrentLine.addView(child);
                    mLines.add(mCurrentLine);
                }
            }
        }
        /*总高度*/
        int totalHeight = getPaddingTop() + getPaddingBottom();
        for (Line line : mLines) {
            totalHeight = totalHeight + line.height;
        }
        totalHeight = totalHeight + (mLines.size()-1) * lineSpace;
        /*设置viewGroup的大小*/
        setMeasuredDimension(totalWidth,totalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.e("onLayout",""+changed);
        int marginTop = getPaddingTop();
        int marginLeft = getPaddingLeft();
        /*设置line中的所有view的位置*/
        for (int i=0;i<mLines.size();i++){
            Line line = mLines.get(i);
            /*最后一行不填满宽度*/
            if (i == mLines.size()-1) {
                line.fillWidth = false;
            }
            line.layout(marginLeft,marginTop);
            marginTop += lineSpace + line.height;
        }
        /*添加点击事件*/
        int index = 0;
        for (Line line : mLines) {
            for (int i=0;i<line.viewList.size();i++) {
                final int position = i + index;
                View view = line.viewList.get(i);
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnItemClickListener != null)
                            mOnItemClickListener.click(view,position);
                    }
                });
            }
            index = index + line.viewList.size();
        }
    }

    /**
     * 保存一行的view
     */
    private class Line {
        private List<View> viewList;
        private int usedWidth;//已经使用的宽度
        private int maxWidth;//最大可以使用的宽度
        private int viewSpace;//view之间的间隔
        private int height;//高度
        private boolean fillWidth = false;//是否设置所有view的宽度和≈maxWidth

        public Line(int maxWidth, int viewSpace) {
            this.maxWidth = maxWidth;
            this.viewSpace = viewSpace;
            viewList = new ArrayList<>();
        }

        /*在可以添加view的前提下使用*/
        public void addView(View view) {
            int viewWidth = view.getMeasuredWidth();
            int viewHeight = view.getMeasuredHeight();
            if (viewList.size() == 0) {
                /*如果view的宽度大于最大可用宽度，强制设置为最大宽度*/
                usedWidth = viewWidth > maxWidth ? maxWidth : viewWidth;
                height = viewHeight;
            } else {
                usedWidth = usedWidth + viewSpace + viewWidth;
                height = viewHeight > height ? viewHeight : height;
            }
            viewList.add(view);
        }

        /*判断viewList是否可以添加view*/
        public boolean canAddView(View view) {
            int viewWidth = view.getMeasuredWidth();
            if (viewList.size() == 0)
                return true;
            /*已经使用的宽度加上viewWidth后,小于或等于表示可以添加*/
            return viewWidth + viewSpace + usedWidth <= maxWidth;
        }

        public void layout(int marginLeft, int marginTop) {
            /*avgWidth是为了让line的view铺满maxWidth，右边不会有空白*/
            int avgWidth = (int) ((maxWidth - usedWidth) * 1f / viewList.size());
            //计算控件的上下左右的位置
            for(View view : viewList) {
                /*获取view的测量宽度*/
                int viewWidth = view.getMeasuredWidth();
                int viewHeight = view.getMeasuredHeight();
                /*重新设置view的宽度，让所有view的宽度和≈maxWidth*/
                if(avgWidth > 0 && fillWidth){
                    /*按照给定的宽高设置view的大小*/
                    int specWidth = MeasureSpec.makeMeasureSpec(viewWidth + avgWidth,MeasureSpec.EXACTLY);
                    int specHeight = MeasureSpec.makeMeasureSpec(viewHeight,MeasureSpec.EXACTLY);
                    view.measure(specWidth,specHeight);
                    // 重新获取宽度和高度
                    viewWidth = view.getMeasuredWidth();
                    viewHeight = view.getMeasuredHeight();
                }
                /*extraTop为了让view垂直居中而取的参数（因为viewHeight的大小不固定）*/
                int extraTop = (int) ((height - viewHeight)*0.5f);

                int left = marginLeft;
                int top = marginTop + extraTop;
                int right = left + viewWidth;
                int bottom = top + viewHeight;
                //摆放每一个孩子的位置
                view.layout(left,top,right,bottom);
                marginLeft += viewWidth + viewSpace;
            }
        }
    }

    public interface OnItemClickListener {
        void click(View view, int position);
    }

    /*dp转px*/
    private int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}
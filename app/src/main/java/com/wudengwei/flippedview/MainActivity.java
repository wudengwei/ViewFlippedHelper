package com.wudengwei.flippedview;

import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ViewFlipper viewFlipper;
    List<Type> typeList = new ArrayList<>();
    ViewFlippedHelper viewFlippedHelper;

    TagFlowLayout tagFlowLayout;
    String[] mDatas	= new String[] { "全部", "电子商务", "游戏", "媒体", "广告营销",
            "数据服务", "医疗健康", "生活服务", "o2o", "旅游", "分类信息", "音乐/视频/阅读", "在线教育", "社交网络",
            "人力资源服务", "企业服务", "信息安全", "智能硬件", "移动互联网", "互联网", "计算机软件", "通信/网络设备",
            "广告/公关/会展", "互联网金融", "物流/仓储", "贸易/进出口", "咨询", "工程施工", "汽车生产", "其他行业"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewFlipper = findViewById(R.id.view_flipped);

        tagFlowLayout = findViewById(R.id.tag_flow_layout);
        tagFlowLayout.setFillAllLineWidth(true);
        tagFlowLayout.setOnItemClickListener(new TagFlowLayout.OnItemClickListener() {
            @Override
            public void click(View view, int position) {
                Toast.makeText(MainActivity.this,mDatas[position]+","+position,Toast.LENGTH_LONG).show();
                if (view instanceof TextView) {
                    view.setBackgroundResource(R.drawable.bg_selected);
                    ((TextView) view).setTextColor(Color.WHITE);
                }
            }
        });
        // 动态加载数据
        for (int i = 0; i < mDatas.length; i++) {
            TextView view = new TextView(this);
            view.setText(mDatas[i]);
            view.setBackgroundResource(R.drawable.bg_unselected);
            view.setTextColor(Color.BLACK);
            view.setPadding(dp2px(7), dp2px(5), dp2px(7), dp2px(5));
            view.setGravity(Gravity.CENTER);
            view.setTextSize(15);
            tagFlowLayout.addView(view);
        }

        typeList.add(new Type("客观","之前的项目是没问题的，翻译过来就是","松开","app@debug/compileClasspath': Could not resolve"));
        typeList.add(new Type("是的","从 git 上 clone 下来的代码，直接用 Android Stu","的成","时间2019年4月15日下午6点50分左右"));
        typeList.add(new Type("客观","之前的项目是没问题的，翻译过来就是","debug","2019年4月16日凌晨3时30分左右公布了"));
        typeList.add(new Type("是的","从 git 上 clone 下来的代码，直接用 Android Stu","app","位于法国首都的巴黎圣母院15日傍晚"));

        viewFlippedHelper = new ViewFlippedHelper(viewFlipper);
        viewFlippedHelper.setAnimation(R.anim.roll_in,R.anim.roll_out);
        viewFlippedHelper.setFlipInterval(3000);
        viewFlippedHelper.setOnSetValueListener(new ViewFlippedHelper.OnSetValueListener() {
            @Override
            public void onSetValue(View view, int position) {
                Type type = typeList.get(position);
                TextView sign1 = view.findViewById(R.id.tv_sign_1);
                TextView sign2 = view.findViewById(R.id.tv_sign_2);
                TextView title1 = view.findViewById(R.id.tv_title_1);
                TextView title2 = view.findViewById(R.id.tv_title_2);

                sign1.setText(type.getSign());
                title1.setText(type.getTitle());
                sign2.setText(type.getSign1());
                title2.setText(type.getTitle1());
            }
        });
        viewFlippedHelper.setData(R.layout.viewflipped_item,typeList);
        viewFlippedHelper.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewFlippedHelper.updateData(typeList.subList(0,1));
            }
        },5000);
    }

    /*dp转px*/
    private int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }
}

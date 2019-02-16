package com.trackersurvey.happynavi;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.trackersurvey.adapter.FragmentAdapter;
import com.trackersurvey.fragment.AllGroupFragment;
import com.trackersurvey.fragment.MyGroupFragment;
import com.trackersurvey.util.AppManager;

import java.util.ArrayList;
import java.util.List;

public class MyGroupActivity extends BaseActivity {

    private TextView title;
    private TextView titleRight;

    private ViewPager groupPager;
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private FragmentAdapter mFragmentAdapter;
    private MyGroupFragment minePage;
    private AllGroupFragment allPage;
    private TextView mineTxt,allTxt;
    private LinearLayout mineLayout,allLayout;//手动点击切换fragment
    private ImageView mTabLineIv;
    private int currentIndex;
    private int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);
        StatusBarCompat.setStatusBarColor(this, Color.BLACK); // 修改状态栏颜色
        // 隐藏原始标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        AppManager.getAppManager().addActivity(this);

        init();
        initTabLineWidth();
    }
    @SuppressWarnings("deprecation")
    public void init(){
        title = (TextView) findViewById(R.id.title_text);
        title.setText(getResources().getString(R.string.mygroup));
        titleRight = findViewById(R.id.title_right_text);
        titleRight.setVisibility(View.INVISIBLE);

        allTxt = (TextView) findViewById(R.id.allgroup);
        mineTxt = (TextView) findViewById(R.id.minegroup);
        mineLayout = (LinearLayout) findViewById(R.id.id_tab_mime);
        allLayout = (LinearLayout) findViewById(R.id.id_tab_all);
        mTabLineIv = (ImageView) findViewById(R.id.tab_line_iv);
        groupPager = (ViewPager) findViewById(R.id.viewpager_group);
        minePage= new MyGroupFragment();
        allPage= new AllGroupFragment();
        mFragmentList.add(minePage);
        mFragmentList.add(allPage);
        mFragmentAdapter = new FragmentAdapter(
                this.getSupportFragmentManager(), mFragmentList);
        groupPager.setAdapter(mFragmentAdapter);
        groupPager.setCurrentItem(0);
        mineLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                groupPager.setCurrentItem(0);
            }
        });
        allLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                groupPager.setCurrentItem(1);
            }
        });
        groupPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            /**
             * state滑动中的状态 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。
             */
            @Override
            public void onPageScrollStateChanged(int state) {

            }

            /**
             * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比
             * offsetPixels:当前页面偏移的像素位置
             */
            @Override
            public void onPageScrolled(int position, float offset,
                                       int offsetPixels) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                        .getLayoutParams();

                //Log.e("offset:", offset + "");
                /**
                 * 利用currentIndex(当前所在页面)和position(下一个页面)以及offset来
                 * 设置mTabLineIv的左边距 滑动场景：
                 * 记3个页面,
                 * 从左到右分别为0,1,2
                 * 0->1; 1->2; 2->1; 1->0
                 */

                if (currentIndex == 0 && position == 0)// 0->1
                {
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 2) + currentIndex
                            * (screenWidth / 2));

                } else if (currentIndex == 1 && position == 0) // 1->0
                {
                    lp.leftMargin = (int) (-(1 - offset)
                            * (screenWidth * 1.0 / 2) + currentIndex
                            * (screenWidth / 2));

                }
                mTabLineIv.setLayoutParams(lp);
            }

            @Override
            public void onPageSelected(int position) {
                resetTextView();
                switch (position) {
                    case 0:
                        mineTxt.setTextColor(Color.BLUE);
                        break;
                    case 1:
                        allTxt.setTextColor(Color.BLUE);
                        break;

                }
                currentIndex = position;
            }
        });
    }
    /**
     * 设置滑动条的宽度为屏幕的1/3(根据Tab的个数而定)
     */
    private void initTabLineWidth() {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                .getLayoutParams();
        lp.width = screenWidth / 2;
        mTabLineIv.setLayoutParams(lp);
    }

    /**
     * 重置颜色
     */
    private void resetTextView() {
        allTxt.setTextColor(Color.BLACK);
        mineTxt.setTextColor(Color.BLACK);

    }
}

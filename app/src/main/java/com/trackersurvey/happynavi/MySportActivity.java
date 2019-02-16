package com.trackersurvey.happynavi;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.trackersurvey.bean.HealthData;
import com.trackersurvey.httpconnection.PostHealthData;
import com.trackersurvey.util.AppManager;
import com.trackersurvey.util.Common;
import com.trackersurvey.util.GsonHelper;
import com.trackersurvey.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class MySportActivity extends BaseActivity {

    private TextView title;
    private TextView titleRightTv;
    private Button refresh;
    private TextView steps;
    private TextView stepsLable;
    private TextView distance;
    private TextView duration;
    private TextView calorie;
    private LineChartView chartTop;
    private ColumnChartView chartBottom;
    private ProgressDialog proDialog;
    private LineChartData lineData;
    private ColumnChartData columnData;
    public final static String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec",};

    public final static String[] days = new String[]{"Mon", "Tue", "Wen", "Thu", "Fri", "Sat", "Sun",};

    private HealthData healdata;//测试月份判断，天数显示是否正确
    private List<HealthData> HealthDataList_Steps=new ArrayList<HealthData>();
    private List<HealthData> HealthDataList_Dis=new ArrayList<HealthData>();
    private List<HealthData> HealthDataList_Dur=new ArrayList<HealthData>();
    private List<HealthData> HealthDataList_Calorie=new ArrayList<HealthData>();
    private int currentColumn;//当前点击的列
    private int month;//当前点击的月份
    private int year;//当前点击的年

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sport);
        StatusBarCompat.setStatusBarColor(this, Color.BLACK); // 修改状态栏颜色
        // 隐藏原始标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        AppManager.getAppManager().addActivity(this);

        proDialog=new ProgressDialog(this);
        title = (TextView) findViewById(R.id.title_text);
        title.setText(getResources().getString(R.string.sportdata));
        titleRightTv = findViewById(R.id.title_right_text);
        titleRightTv.setVisibility(View.INVISIBLE);
        refresh = (Button) findViewById(R.id.refreshsport);
        steps = (TextView) findViewById(R.id.stepcount);
        stepsLable = (TextView) findViewById(R.id.steplable);
        distance = (TextView) findViewById(R.id.distancedata);
        duration = (TextView) findViewById(R.id.durationdata);
        calorie = (TextView) findViewById(R.id.caloriedata);
        initData();
        chartTop = (LineChartView) findViewById(R.id.chart_top);
        chartTop.setOnValueTouchListener(new ValueTouchListener());
        chartBottom = (ColumnChartView) findViewById(R.id.chart_bottom);
        refresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                initData();
            }
        });
    }
    private void initData(){
        if(Common.url != null && !Common.url.equals("")){

        }else{
            Common.url = getResources().getString(R.string.url);
        }
        String url_getHealthData=Common.url+"request.aspx";

        Common.showDialog(proDialog,getResources().getString(R.string.tip),getResources().getString(R.string.tips_synchronous));
        PostHealthData health=new PostHealthData(mHandler, url_getHealthData,Common.getUserId(getApplicationContext()),Common.getDeviceId(getApplicationContext()));
        health.start();

    }
    @SuppressLint("HandlerLeak")
    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch(msg.what){
                case 0:
                    Common.dismissDialog(proDialog);
                    String[] data=msg.obj.toString().trim().split("!");
                    if(data!=null&&data.length==4){
                        HealthDataList_Steps= GsonHelper.parseJsonToList(data[0], HealthData.class);
                        HealthDataList_Dis=GsonHelper.parseJsonToList(data[1], HealthData.class);
                        HealthDataList_Dur=GsonHelper.parseJsonToList(data[2], HealthData.class);
                        HealthDataList_Calorie=GsonHelper.parseJsonToList(data[3], HealthData.class);
                        if(HealthDataList_Steps.size()>0){
                            currentColumn=HealthDataList_Steps.size()-1;
                            generateInitialStepLineData();
                            generateColumnData();
                            //Log.i("trailadapter", "数量:"+HealthDataList_Steps.size());
                        }
                        chartTop.setVisibility(View.VISIBLE);
                        chartBottom.setVisibility(View.VISIBLE);
                        ToastUtil.show(getApplicationContext(),getResources().getString(R.string.tips_synchronous_succ));
                    }
                    break;
                case 1:
                    Common.dismissDialog(proDialog);
                    ToastUtil.show(getApplicationContext(),getResources().getString(R.string.tips_postfail));
                    break;
                case 10:
                    Common.dismissDialog(proDialog);
                    ToastUtil.show(getApplicationContext(),getResources().getString(R.string.tips_netdisconnect));
                    break;
            }
        }
    };
    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {//(int)value.getY()
            stepsLable.setText(year+"-"+month+"-"+(pointIndex+1)+getResources().getString(R.string.step_label));
            steps.setText(HealthDataList_Steps.get(currentColumn).getDay()[pointIndex]);
            calorie.setText(HealthDataList_Calorie.get(currentColumn).getDay()[pointIndex]+getResources().
                    getString(R.string.calorie_unit));
            distance.setText(Common.transformDistance(Double.parseDouble(HealthDataList_Dis.get(currentColumn).
                    getDay()[pointIndex]))+getResources().getString(R.string.dis_unit));
            duration.setText(Common.transformTime(Long.parseLong(HealthDataList_Dur.get(currentColumn).getDay()[pointIndex])));
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }
    private void generateColumnData() {

        int numSubcolumns = 1;//子列数，如：每个月份显示步数和卡路里两个子列
        int numColumns = HealthDataList_Steps.size();//months.length;

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;

        for (int i = 0; i < numColumns; ++i) {

            values = new ArrayList<SubcolumnValue>();
            for (int j = 0; j < numSubcolumns; ++j) {//2个子列
                //values.add(new SubcolumnValue((float) Math.random() * 50f + 5, ChartUtils.pickColor()));
                values.add(new SubcolumnValue(Integer.parseInt(HealthDataList_Steps.get(i).getTotalStep()), ChartUtils.COLOR_GREEN));
                values.add(new SubcolumnValue(Float.parseFloat(HealthDataList_Calorie.get(i).getTotalStep()), ChartUtils.COLOR_ORANGE));
            }

            //axisValues.add(new AxisValue(i).setLabel(months[i]));
            axisValues.add(new AxisValue(i).setLabel(HealthDataList_Steps.get(i).getYearMonth()));
            columns.add(new Column(values).setHasLabelsOnlyForSelected(true));
        }

        columnData = new ColumnChartData(columns);
        Axis axisX = new Axis(axisValues).setHasLines(true);
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName(getResources().getString(R.string.month));
        axisX.setMaxLabelChars(10);
        axisY.setName(getResources().getString(R.string.y_lable));
        columnData.setAxisXBottom(axisX);
        columnData.setAxisYLeft(axisY);

        chartBottom.setColumnChartData(columnData);

        // Set value touch listener that will trigger changes for chartTop.
        chartBottom.setOnValueTouchListener(new ChartBottomValueTouchListener());

        // Set selection mode to keep selected month column highlighted.
        chartBottom.setValueSelectionEnabled(true);

        chartBottom.setZoomType(ZoomType.HORIZONTAL);
        setBottomViewport(numColumns);
        // chartBottom.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // SelectedValue sv = chartBottom.getSelectedValue();
        // if (!sv.isSet()) {
        // generateInitialLineData();
        // }
        //
        // }
        // });

    }

    /**
     * Generates initial data for line chart. At the begining all Y values are equals 0. That will change when user
     * will select value on column chart.
     */
    private void generateInitialStepLineData() {
        stepsLable.setText(getResources().getString(R.string.step_label));
        int numValues = Common.currentDaynum();
        month = Common.getMonth(HealthDataList_Steps.get(HealthDataList_Steps.size()-1).getYearMonth());
        year = Common.getYear(HealthDataList_Steps.get(HealthDataList_Steps.size()-1).getYearMonth());
        String []lineValues_Step=HealthDataList_Steps.get(HealthDataList_Steps.size()-1).getDay();
        String []lineValues_Dis=HealthDataList_Dis.get(HealthDataList_Dis.size()-1).getDay();
        String []lineValues_Dur=HealthDataList_Dur.get(HealthDataList_Dur.size()-1).getDay();
        String []lineValues_Calorie=HealthDataList_Calorie.get(HealthDataList_Calorie.size()-1).getDay();
        steps.setText(lineValues_Step[numValues-1]);
        distance.setText(Common.transformDistance(Double.parseDouble(lineValues_Dis[numValues-1]))+getResources().getString(R.string.dis_unit));
        duration.setText(Common.transformTime(Long.parseLong(lineValues_Dur[numValues-1])));
        calorie.setText(lineValues_Calorie[numValues-1]+getResources().getString(R.string.calorie_unit));
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<PointValue> values_steps = new ArrayList<PointValue>();
        List<PointValue> values_calorie = new ArrayList<PointValue>();
        for (int i = 0; i < numValues; ++i) {
            try{
                values_steps.add(new PointValue(i, Integer.parseInt(lineValues_Step[i])));
                values_calorie.add(new PointValue(i, Integer.parseInt(lineValues_Calorie[i])));
                axisValues.add(new AxisValue(i).setLabel(i+1+""));
            }catch(Exception e){

            }

        }
        List<Line> lines = new ArrayList<Line>();
        Line line_step = new Line(values_steps);
        line_step.setColor(ChartUtils.COLOR_GREEN).setCubic(true);
        //line.setStrokeWidth(1);
        line_step.setHasLabels(true);
        lines.add(line_step);

        Line line_calorie = new Line(values_calorie);
        line_calorie.setColor(ChartUtils.COLOR_ORANGE).setCubic(true);
        //line.setStrokeWidth(1);
        line_calorie.setHasLabels(true);

        lines.add(line_calorie);

        lineData = new LineChartData(lines);
        Axis axisX = new Axis(axisValues).setHasLines(true);
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName(HealthDataList_Steps.get(HealthDataList_Steps.size()-1).getYearMonth());
        axisY.setName(getResources().getString(R.string.y_lable));
        lineData.setAxisXBottom(axisX);
        lineData.setAxisYLeft(axisY);

        chartTop.setLineChartData(lineData);

        // For build-up animation you have to disable viewport recalculation.
        chartTop.setViewportCalculationEnabled(true);
        chartTop.setValueSelectionEnabled(true);

        // And set initial max viewport and current viewport- remember to set viewports after data.

        //chartTop.setMaximumViewport(new Viewport(0, 100000, 31, 0));
        setViewport(numValues);
        chartTop.setZoomType(ZoomType.HORIZONTAL);
    }

    private void generateStepLineData(int color, int columnIndex) {
        // Cancel last animation if not finished.
        chartTop.cancelDataAnimation();
        int numValues=0;
        // Modify data targets
        //Line line = lineData.getLines().get(0);// For this example there is always only one line.
        month = Common.getMonth(HealthDataList_Steps.get(columnIndex).getYearMonth());
        year = Common.getYear(HealthDataList_Steps.get(columnIndex).getYearMonth());
        if(year==Common.currentYearnum()&&month==Common.currentMonthnum()){//点击的列是当前月
            numValues=Common.currentDaynum();
            //Log.i("phonelog", "当前月"+year+"-"+month+"//"+Common.currentYearnum()+"-"+Common.currentMonthnum());
        }
        else{
            switch(month){
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    numValues=31;
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    numValues=30;
                    break;
                case 2:
                    if((year%4==0&&year%100!=0)||year%400==0){
                        numValues=29;
                    }
                    else{
                        numValues=28;
                    }
                    break;
            }
            // Log.i("phonelog", "非当月"+year+"-"+month+"//"+Common.currentYearnum()+"-"+Common.currentMonthnum());

        }
        //Log.i("phonelog", "numvalues:"+numValues);
        String []lineValues_Step=HealthDataList_Steps.get(columnIndex).getDay();
        String []lineValues_Calorie=HealthDataList_Calorie.get(columnIndex).getDay();
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<PointValue> values_step = new ArrayList<PointValue>();
        List<PointValue> values_calorie = new ArrayList<PointValue>();
        for (int i = 0; i < numValues; ++i) {
            try{
                values_step.add(new PointValue(i, Integer.parseInt(lineValues_Step[i])));
                values_calorie.add(new PointValue(i, Integer.parseInt(lineValues_Calorie[i])));
                axisValues.add(new AxisValue(i).setLabel(i+1+""));
            }catch(Exception e){

            }

        }
        List<Line> lines = new ArrayList<Line>();
        Line line_step = new Line(values_step);
        line_step.setColor(ChartUtils.COLOR_GREEN);
        line_step.setHasLabels(true);

        lines.add(line_step);
        Line line_calorie = new Line(values_calorie);
        line_calorie.setColor(ChartUtils.COLOR_ORANGE);
        line_calorie.setHasLabels(true);

        lines.add(line_calorie);
        lineData = new LineChartData(lines);
        Axis axisX = new Axis(axisValues).setHasLines(true);
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName(HealthDataList_Steps.get(columnIndex).getYearMonth());
        axisY.setName(getResources().getString(R.string.y_lable));
        lineData.setAxisXBottom(axisX);
        lineData.setAxisYLeft(axisY);
        chartTop.setLineChartData(lineData);
        setViewport(numValues);
        // Start new data animation with 300ms duration;
        //chartTop.startDataAnimation(300);
    }

    private class ChartBottomValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            currentColumn=columnIndex;
            generateStepLineData(value.getColor(), columnIndex);
            //根据 subcolumIndex判断点击的是步数还是卡路里
        	/*if(subcolumnIndex==0){
        		generateStepLineData(value.getColor(), columnIndex);
        	}
        	else{
        		ToastUtil.show(PersonalSportData.this,"点的是卡路里");
        	}*/
        }

        @Override
        public void onValueDeselected() {

            //generateLineData(ChartUtils.COLOR_GREEN, 0);

        }
    }
    public void setViewport(int numValues){
        //根据点数多少设置当前显示区域
        if(numValues>8){
            chartTop.setCurrentViewport(new Viewport(numValues-8, 100000, numValues, 0));
        }
        else{
            chartTop.setCurrentViewport(new Viewport(0, 100000, numValues, 0));
        }
    }
    public void setBottomViewport(int numValues){
        //根据点数多少设置当前显示区域
        if(numValues>3){
            chartBottom.setCurrentViewport(new Viewport(numValues-3, 100000, numValues, 0));
        }
        else{
            chartBottom.setCurrentViewport(new Viewport(0, 100000, numValues, 0));
        }
    }
}

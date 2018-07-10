package com.hencoder.hencoderpracticelayout1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GestureLockView extends View {

    private int circleNum = 5;
    private Paint paint;
    private int smallRadius = 20;
    private int largeRadius = 80;
    private int gap = 200;
    private int paddingLeft = 100;
    private int paddingTop = 100;
    private Path mpath;
    private Paint ringPaint;
    private Paint pathPaint;
    private List<Integer> targetList=new ArrayList<Integer>();

    /**
     * 设置密码
     * @param defaultPwd
     */
    public void setDefaultPwd(String defaultPwd) {
        this.defaultPwd = defaultPwd;
    }

    private String defaultPwd="01456";

    public GestureLockView(Context context) {
        super(context);
        init();
    }

    public GestureLockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GestureLockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.parseColor("#B3B3B3"));
        mpath = new Path();

        ringPaint=new Paint();
        ringPaint.setColor(Color.parseColor("#B3B3B3"));
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setAntiAlias(true);
        pathPaint=new Paint();
        pathPaint.setAntiAlias(true);
        pathPaint.setColor(Color.parseColor("#B3B3B3"));
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth(20);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //计算间距
        gap = (getMeasuredWidth() - paddingLeft * 2 - largeRadius * 2) / (circleNum - 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < circleNum; i++) {
            for (int j = 0; j < circleNum; j++) {
                float x = gap * i + paddingLeft + largeRadius;
                float y = gap * j + paddingTop + largeRadius;
                //绘制圆点
                canvas.drawCircle(x, y, smallRadius, paint);
                //绘制圆
                if (targetList.contains(i+j*circleNum)){
                    canvas.drawCircle(x,y,largeRadius,ringPaint);
                }
            }
        }
        //绘制path
        canvas.drawPath(mpath, pathPaint);
    }

    /**
     * 判断触点是否在目标范围类
     * @param x
     * @param y
     * @return
     */
    private boolean isTarget(float x,float y){
        for (int i = 0; i < circleNum; i++) {
            for (int j = 0; j < circleNum; j++) {
                float deltX = gap * i + paddingLeft + largeRadius;
                float deltY = gap * j + paddingTop + largeRadius;
                if(Math.pow(deltX-x,2)+ Math.pow(deltY-y,2)<=Math.pow(largeRadius,2)){
                    addPoint(i+j*circleNum,deltX,deltY);
                    return true;
                }
            }
        }
        return false;
    }

    //添加连接点
    private void addPoint(int i,float x,float y){
        //将path起点移动到目标位置
        if(targetList.isEmpty()){
            mpath.moveTo(x,y);
        }
        //不存在则添加
        if(!targetList.contains(i)) {
            targetList.add(i);
            mpath.lineTo(x, y);
            invalidate();
        }
    }

    /**
     * 验证密码
     * @return
     */
    private boolean validPwd(){
        StringBuilder sb=new StringBuilder();
        for(int i: targetList){
            sb.append(i);
        }
        String result=sb.toString();
        if(result.equals(defaultPwd)){
            return  true;
        }
        return  false;
    }

    Runnable mrun=new Runnable() {
        @Override
        public void run() {
            pathPaint.setColor(Color.parseColor("#B3B3B3"));
            mpath.reset();
            targetList.clear();
            invalidate();
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x=event.getX();
        float y=event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                //验证密码
                if(validPwd()){
                    mpath.reset();
                    targetList.clear();
                    invalidate();
                    //todo 验证成功之后的动作
                    Toast.makeText(this.getContext(), "pwd is correct", Toast.LENGTH_SHORT).show();
                }else {
                    if(!targetList.isEmpty()) {
                        pathPaint.setColor(Color.RED);
                        invalidate();
                        //密码错误
                        Toast.makeText(this.getContext(), "password is incorrect", Toast.LENGTH_SHORT).show();
                        //1秒后清除path
                        this.postDelayed(mrun,1000);
                    }
                };
                break;
            case MotionEvent.ACTION_DOWN:
                this.removeCallbacks(mrun);
                mpath.reset();
                targetList.clear();
                pathPaint.setColor(Color.parseColor("#B3B3B3"));
                invalidate();
                //检验按下位置是否在目标区域内，如果不在，不响应后续事件
                if(isTarget(x,y)){
                    return true;
                }else{
                    return false;
                }
            case MotionEvent.ACTION_MOVE:
                isTarget(x,y);
                break;
            default:
                break;
        }
//        invalidate();
        return true;
    }
}

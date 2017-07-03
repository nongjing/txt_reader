package Page;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.BatteryManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.Activity.R;
import com.example.administrator.Activity.ReadActivity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2017/4/11.
 */
public class PageView extends View {
    Bitmap mbitmap;
    private int screenHeight, screenWidth;//实际屏幕尺寸
    private int pageWidth,pageHeight;
    private int lineNumber;//行数
    private int lineSpace;
    private static int margin;//文字显示距离屏幕实际尺寸的偏移量
    private Paint mPaint;
    private int fontSize ;
    private Canvas mCanvas;
    Context mcontext;
    PageFactory mPageFactory;
    private  OnPositionClickListener mlistener;
    float x,y,Dx,Dy;
    private SPHelper spHelper;
    long time1,time2;//time1为手指按下时的时间，time2为手指抬起时的时间。
    String time;//用于显示时间，还有添加书签时的时间。
    float progress;
    private ArrayList<String> content = new ArrayList<>();
    public void setOnPositionClickListener (OnPositionClickListener listener){
        mlistener=listener;
    }
    public interface OnPositionClickListener {
        public void onLeftClick();
        public void onRightClick();
        public void onMiddleClick();
        public void onScrollLeft();
        public void onScrollRight();

    }

    public PageView(Context context) {
        super(context);
        this.mcontext=context;
        initprivate();
    }

    public PageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mcontext=context;
        initprivate();

    }

    public PageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mcontext=context;
        initprivate();

    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        String book_name=mPageFactory.getBook().getBookName();
        int a=book_name.indexOf(".");
        book_name=book_name.substring(0,a);
        if(mPageFactory!=null) {
            content = mPageFactory.getContent();
            int x=margin+fontSize+lineSpace;
            int y =x;
            canvas.drawText("《"+book_name+"》",margin,y,mPaint);
            for (String line : content) {
                y += fontSize + lineSpace;
                canvas.drawText(line, margin, y, mPaint);

            }

            progress= (float) mPageFactory.getCurrentBegin() / mPageFactory.getFileLength() * 100;
            DecimalFormat format = new DecimalFormat("#0.00");
            String readingProgress = format.format(progress) + "%";
            int length = (int) mPaint.measureText(readingProgress);
            canvas.drawText(readingProgress, (screenWidth - length) / 2, pageHeight+2*fontSize, mPaint);

            //显示时间
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
            time = simpleDateFormat.format(new Date(System.currentTimeMillis()));
            canvas.drawText("时间:" + time, margin,pageHeight+2*fontSize, mPaint);

            //显示电量

            String batteryLevel = getBatteryLevel();
            float[] widths = new float[batteryLevel.length()];
            float batteryLevelStringWidth = 0;
            mPaint.getTextWidths(batteryLevel, widths);
            for (float f : widths) {
                batteryLevelStringWidth += f;
            }
            canvas.drawText(batteryLevel, screenWidth- margin - batteryLevelStringWidth, pageHeight+2*fontSize, mPaint);
            invalidate();
        }
    }
    public String getBatteryLevel(){
        Intent batteryIntent = mcontext.registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int scaledLevel = 0;
        if (batteryIntent != null) {
            scaledLevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
        }
        int scale = 0;
        if (batteryIntent != null) {
            scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        }
        return "电量："+String.valueOf(scaledLevel*100/scale);
    }
    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initprivate();
        mPageFactory.saveBookmark();
        mPageFactory.nextPage();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case  MotionEvent.ACTION_DOWN:
                x=event.getX();
                y=event.getY();
                time1=event.getDownTime();
                break;
            case MotionEvent.ACTION_UP:
                time2 = event.getEventTime();
                if(Math.abs(event.getX()-x)<10&&time2 - time1 < 100) {//当抬起时的坐标与按下时的坐标小于10且按下的时间小于100ms时认为触发点击事件
                        if (x < screenWidth / 3) {
                            mlistener.onLeftClick();
                            return true;
                        }
                        if (x > 2 * screenWidth / 3) {
                            mlistener.onRightClick();
                            return true;
                        }
                        if (x > screenWidth / 3 && x < 2 * screenWidth / 3) {
                            mlistener.onMiddleClick();
                            return true;
                        }
                }
                if(Math.abs(event.getX()-x)>50){//当移动的距离大于50时认为触发了滑动事件
                    if(event.getX()-x>0){
                        mlistener.onScrollRight();
                    }
                    if(event.getX()-x<0){
                        mlistener.onScrollLeft();
                    }
                }
                break;

        }





        return true;
    }
    private void initprivate(){
        DisplayMetrics metrics = new DisplayMetrics();

        lineSpace=Util.getPXWithDP(5,mcontext);
        margin=Util.getPXWithDP(5,mcontext);
        spHelper=SPHelper.getInstance(mcontext);
        ((Activity)mcontext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;
        fontSize = spHelper.getFontSize();
        pageHeight = screenHeight - margin * 4 - 3*fontSize;//减掉的一组fontsize+margin为system ui，一组为书的名字，一组为显示的时间，进度电量条。
        pageWidth = screenWidth - margin * 2;
        lineNumber = pageHeight /(fontSize+lineSpace);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(fontSize);
        Bitmap bitmap = Bitmap.createBitmap(screenWidth,screenHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(bitmap);
    }
    public void initProperties(PageFactory pageFactory){
       mPageFactory=pageFactory;
        draw(mCanvas);
    }
    public int getLineNumber() {
        return lineNumber;
    }

    public void setFontSize(int fontSize) {
        spHelper.setFontSize(fontSize);
        initprivate();
        mPageFactory.saveBookmark();
        mPageFactory.nextPage();
    }

    public int getFontSize() {
        return fontSize;
    }

    public Bitmap getMbitmap() {
        return mbitmap;
    }

    public Context getMcontext() {
        return mcontext;
    }

    public Canvas getmCanvas() {
        return mCanvas;
    }

    public String getTime() {
        return time;
    }

    public int getLineSpace() {
        return lineSpace;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }
    public Paint getmPaint(){
        return mPaint;
    }

    public int getPageHeight() {
        return pageHeight;
    }

    public int getPageWidth() {
        return pageWidth;
    }
    public float getProgress(){return progress;}
}

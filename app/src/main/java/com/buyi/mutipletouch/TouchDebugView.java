package com.buyi.mutipletouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by buyi on 15/12/25.
 */
public class TouchDebugView extends SurfaceView implements SurfaceHolder.Callback {

    private static final int MAX_TOUCHPOINTS = 10;
    private static final String START_TEXT = "请随便触摸屏幕进行测试";
    private Paint textPaint = new Paint();
    private Paint touchPaints[] = new Paint[MAX_TOUCHPOINTS];
    private int colors[] = new int[MAX_TOUCHPOINTS];

    private int width, height;
    private float scale = 1.0f;

    public TouchDebugView(Context context) {
        super(context);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        setFocusable(true); // 确保我们的View能获得输入焦点
        setFocusableInTouchMode(true); // 确保能接收到触屏事件
        init();
    }

    public TouchDebugView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        setFocusable(true); // 确保我们的View能获得输入焦点
        setFocusableInTouchMode(true); // 确保能接收到触屏事件
        init();
    }

    private void init() {
        // 初始化10个不同颜色的画笔
        textPaint.setColor(Color.WHITE);
        colors[0] = Color.BLUE;
        colors[1] = Color.RED;
        colors[2] = Color.GREEN;
        colors[3] = Color.YELLOW;
        colors[4] = Color.CYAN;
        colors[5] = Color.MAGENTA;
        colors[6] = Color.DKGRAY;
        colors[7] = Color.WHITE;
        colors[8] = Color.LTGRAY;
        colors[9] = Color.GRAY;
        for (int i = 0; i < MAX_TOUCHPOINTS; i++) {
            touchPaints[i] = new Paint();
            touchPaints[i].setColor(colors[i]);
        }
    }

    /**
     * 非常详尽的
     * @param view
     * @param event
     * @return
     */
    private void describeEvent(View view, MotionEvent event)
    {
        StringBuilder sb = new StringBuilder(300);

//        event.getHistoricalAxisValue()

        sb.append("Action: ").append(event.getAction()).append("\n");// 获取触控动作比如ACTION_DOWN
        sb.append("相对坐标: ").append(event.getX()).append("  *  ").append(event.getY()).append("   ");
        sb.append("绝对坐标: ").append(event.getRawX()).append("  *  ").append(event.getRawY()).append("\n");

        if (event.getX() < 0 || event.getX() > view.getWidth() || event.getY() < 0 || event.getY() > view.getHeight())
        {
            sb.append("未点击在View范围内");
        }

        sb.append("Edge flags: ").append(event.getEdgeFlags()).append("  ");// 边缘标记,但是看设备情况,很可能始终返回0
        sb.append("Pressure: ").append(event.getPressure()).append("  ");// 压力值,0-1之间,看情况,很可能始终返回1
        sb.append("Size: ").append(event.getSize()).append("\n");// 指压范围
        sb.append("Down time: ").append(event.getDownTime()).append("ms   ");
        sb.append("Event time: ").append(event.getEventTime()).append("ms   ");
        sb.append("Elapsed: ").append(event.getEventTime() - event.getDownTime()).append("ms\n");

        System.out.println("describeEvent:" + sb.toString());
         //sb.toString();
    }


    /**
     * index 就是所有touch事件按照发生时间顺序排序，如果有事件消失了，就往前递补
     * id 就是所有touch事件按照发生时间分配id,终其一生不变。
     * index与id在按下抬起顺序一致时一样。
     * @param event
     * @return
     */
    private void printProinterInfo (MotionEvent event) {
        int pointCount = event.getPointerCount();
        for (int count = 0; count < pointCount; count++) {
            int id = event.getPointerId(count);
            int index = event.findPointerIndex(id);
            System.out.println("id:" + id);
            System.out.println("count:" + count);
            System.out.println("index:" + index);
        }
    }


    /**
     * 打印所有MotionEvent的动作信息
     * 单指时 down  move up
     * 多指时 down pointerdown move pointerup up
     * @param event
     */
    private void printActionInfo (MotionEvent event) {
        int pointCount = event.getPointerCount();
        System.out.println(pointCount);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_POINTER_DOWN:
                System.out.println("ACTION_POINTER_DOWN");
                break;
            case MotionEvent.ACTION_POINTER_UP:
                System.out.println("ACTION_POINTER_UP");
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("ACTION_UP");
                break;
            case MotionEvent.ACTION_DOWN:
                System.out.println("ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE: {
                System.out.println("ACTION_MOVE");
                break;
            }
        }


    }

    /*
     * 处理触屏事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        printProinterInfo (event);
        printActionInfo(event);
        describeEvent (this, event);
        // 获得屏幕触点数量
        int pointerCount = event.getPointerCount();
        if (pointerCount > MAX_TOUCHPOINTS) {
            pointerCount = MAX_TOUCHPOINTS;
        }
        // 锁定Canvas,开始进行相应的界面处理
        Canvas c = getHolder().lockCanvas();
        if (c != null) {
            c.drawColor(Color.BLACK);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // 当手离开屏幕时，清屏
            } else {
                // 先在屏幕上画一个十字，然后画一个圆
                for (int i = 0; i < pointerCount; i++) {
                    // 获取一个触点的坐标，然后开始绘制
                    int id = event.getPointerId(i);
                    int x = (int) event.getX(i);
                    int y = (int) event.getY(i);
                    drawCrosshairsAndText(x, y, touchPaints[id], i, id, c);
                }
                for (int i = 0; i < pointerCount; i++) {
                    int id = event.getPointerId(i);
                    int x = (int) event.getX(i);
                    int y = (int) event.getY(i);
                    drawCircle(x, y, touchPaints[id], c);
                }
            }
            // 画完后，unlock
            getHolder().unlockCanvasAndPost(c);
        }
        return true;
    }

    /**
     * 画十字及坐标信息
     *
     * @param x
     * @param y
     * @param paint
     * @param ptr
     * @param id
     * @param c
     */
    private void drawCrosshairsAndText(int x, int y, Paint paint, int ptr,
                                       int id, Canvas c) {
        c.drawLine(0, y, width, y, paint);
        c.drawLine(x, 0, x, height, paint);
        int textY = (int) ((15 + 20 * ptr) * scale);
        c.drawText("x" + ptr + "=" + x, 10 * scale, textY, textPaint);
        c.drawText("y" + ptr + "=" + y, 70 * scale, textY, textPaint);
        c.drawText("id" + ptr + "=" + id, width - 55 * scale, textY, textPaint);
    }

    /**
     * 画圆
     *
     * @param x
     * @param y
     * @param paint
     * @param c
     */
    private void drawCircle(int x, int y, Paint paint, Canvas c) {
        c.drawCircle(x, y, 15 * scale, paint);
    }

    /*
     * 进入程序时背景画成黑色，然后把"START_TEXT"写到屏幕
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        this.width = width;
        this.height = height;
        if (width > height) {
            this.scale = width / 480f;
        } else {
            this.scale = height / 480f;
        }
        textPaint.setTextSize(14 * scale);
        Canvas c = getHolder().lockCanvas();
        if (c != null) {
            // 背景黑色
            c.drawColor(Color.BLACK);
            float tWidth = textPaint.measureText(START_TEXT);
            c.drawText(START_TEXT, width / 2 - tWidth / 2, height / 2,
                    textPaint);
            getHolder().unlockCanvasAndPost(c);
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

}


package yongcan.radarview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.BounceInterpolator;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/4/24.
 */
public class RadarView extends View {
    private double COS18 = 0.95105651629515;
    private double SIN18 = 0.30901699437495;

    private double SIN36 = 0.58778525229247;
    private double COS36 = 0.80901699437495;


    private Context context;
    private int dpRadius;
    private int Radius;//这个是px版本的半径
    private Point centralPoint;
    private float pentagonRadius;//五边形的内边长
    private int dp_d_value;
    private int d_value;//五边形各个顶点距离控件边缘的距离(px)
    private int dp_fontDistance;
    private int fontDistance;//字体距离顶点的距离
    private int dp_distanceAmongIcon;
    private int distanceAmongIcon; //图标与文字的距离

    //五个顶点
    private PointF pointF1;
    private PointF pointF2;
    private PointF pointF3;
    private PointF pointF4;
    private PointF pointF5;

    //五个区间点
    private PointF pointFInterval1;
    private PointF pointFInterval2;
    private PointF pointFInterval3;
    private PointF pointFInterval4;
    private PointF pointFInterval5;

    private Paint pathPaint;
    private Paint contentPaint;
    private Paint textPaint;

    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 11, getResources().getDisplayMetrics());

    private final float dimensionality;//每个维度的细分区间的长度

    private List<Float> data = new ArrayList();

    private float intervalLength1 = 0;
    private float intervalLength2 = 0;
    private float intervalLength3 = 0;
    private float intervalLength4 = 0;
    private float intervalLength5 = 0;
    private ValueAnimator textPaintAlpha;

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        dpRadius = 135;//dp
        Radius = dip2px(context, dpRadius);//这个是px版本的半径
        dp_d_value = 65;//dp
        d_value = dip2px(context, dp_d_value);//五边形各个顶点距离控件边缘的距离(px)
        dp_fontDistance = 12;//dp
        fontDistance = dip2px(context, dp_fontDistance);//字体距离顶点的距离
        dp_distanceAmongIcon = 8;
        distanceAmongIcon = dip2px(context, dp_distanceAmongIcon);//图标与文字的距离
        pentagonRadius = Radius - d_value;//五边形的内边长

        centralPoint = new Point(Radius, Radius);//控件的中点
        pointF1 = new PointF();
        pointF2 = new PointF();
        pointF3 = new PointF();
        pointF4 = new PointF();
        pointF5 = new PointF();

        pointFInterval1 = new PointF();
        pointFInterval2 = new PointF();
        pointFInterval3 = new PointF();
        pointFInterval4 = new PointF();
        pointFInterval5 = new PointF();

        calculateVertex();
        initPaint();
        //每个维度都有20个阶段,计算出每个区间的长度
        dimensionality = initDimensionality(20);
    }

    private void initPaint() {
        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth(1);
        pathPaint.setAntiAlias(true);
        pathPaint.setColor(Color.argb(255, 64, 193, 163));

        contentPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        contentPaint.setStyle(Paint.Style.FILL);
        contentPaint.setAntiAlias(true);
        contentPaint.setColor(Color.parseColor("#5bc2ac"));
        contentPaint.setAlpha(200);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(mTextSize);
        textPaint.setColor(Color.parseColor("#606060"));
        textPaint.setAlpha(0);

    }

    /**
     * 1
     * 5       2
     * 4   3
     */
    private void calculateVertex() {
        //第一个顶点很容易确定
        pointF1.set(Radius, d_value);
        pointF2.set((float) (pointF1.x + (COS18 * pentagonRadius)), (float) (Radius - (SIN18 * pentagonRadius)));
        pointF3.set((float) (pointF1.x + (SIN36 * pentagonRadius)), (float) (Radius + (COS36 * pentagonRadius)));
        pointF4.set((float) (pointF1.x - SIN36 * pentagonRadius), pointF3.y);
        pointF5.set((float) (pointF1.x - COS18 * pentagonRadius), pointF2.y);

    }

    /**
     * 初始化每个维度要细分num组,也就是每个维度有20种阶段
     */
    private float initDimensionality(int num) {
        return pentagonRadius / num;
    }

    /**
     * 初始化数据,传进每个维度的值
     *
     * @param data
     */
    public void setData(List<Float> data) {
        this.data = data;
        setContentData(data, 0);
    }

    private void setContentData(List<Float> data, float proportion) {
        //初始化数据完成之后,把五个维度的点计算出来
        if (data != null) {

            try {
                intervalLength1 = data.get(0) * dimensionality * proportion;
                intervalLength2 = data.get(1) * dimensionality * proportion;
                intervalLength3 = data.get(2) * dimensionality * proportion;
                intervalLength4 = data.get(3) * dimensionality * proportion;
                intervalLength5 = data.get(4) * dimensionality * proportion;
            } catch (Exception e) {
                e.printStackTrace();
            }

            pointFInterval1.set(pointF1.x, Radius - intervalLength1);
            pointFInterval2.set((float) (pointFInterval1.x + (COS18 * intervalLength2)), (float) (Radius - (SIN18 * intervalLength2)));
            pointFInterval3.set((float) (pointF1.x + (SIN36 * intervalLength3)), (float) (Radius + (COS36 * intervalLength3)));
            pointFInterval4.set((float) (pointF1.x - SIN36 * intervalLength4), (float) (Radius + (COS36 * intervalLength4)));
            pointFInterval5.set((float) (pointF1.x - COS18 * intervalLength5), (float) (Radius - (SIN18 * intervalLength5)));


        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        drawIcon(canvas);
        drawConstruction(canvas);
        drawContent(canvas);

    }

    public void startAnim() {
        ValueAnimator proportionAnim = ValueAnimator.ofFloat(0, 1);
        proportionAnim.setDuration(1700);
        proportionAnim.setInterpolator(new BounceInterpolator());
        proportionAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float proportion = (float) animation.getAnimatedValue();
                setContentData(data, proportion);
                postInvalidate();
            }
        });

        textPaintAlpha = ValueAnimator.ofInt(0, 255);
        textPaintAlpha.setDuration(800);
        textPaintAlpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                textPaint.setAlpha((Integer) textPaintAlpha.getAnimatedValue());
                postInvalidate();
            }
        });
        proportionAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                textPaintAlpha.start();
            }
        });
        proportionAnim.start();

    }


//    private void drawIcon(Canvas canvas) {
//        RectF rectF1 = new RectF(Radius - textPaint.measureText("身份特质") / 3 - 25 - distanceAmongIcon, pointF1.y - fontDistance - 25 + 3, Radius - textPaint.measureText("身份特质") / 3 - distanceAmongIcon, pointF1.y - fontDistance + 3);
//        canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.sw_shenfen), null, rectF1, textPaint);
//        canvas.drawText("身份特质", Radius - textPaint.measureText("身份特质") / 3, pointF1.y - fontDistance, textPaint);
//
//        RectF rectF2 = new RectF(pointF2.x + fontDistance, pointF2.y - 25, pointF2.x + fontDistance + 25, pointF2.y);
//        canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.sw_shejiao), null, rectF2, textPaint);
//        canvas.drawText("社交", rectF2.right + distanceAmongIcon, rectF2.bottom - 3, textPaint);
//
//        RectF rectF3 = new RectF(pointF3.x + fontDistance / 2, pointF3.y + fontDistance / 2, pointF3.x + fontDistance / 2 + 25, pointF3.y + fontDistance / 2 + 25);
//        canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.sw_huoyuedu), null, rectF3, textPaint);
//        canvas.drawText("活跃度", rectF3.right + distanceAmongIcon, rectF3.bottom - 3, textPaint);
//
//        RectF rectF4 = new RectF(pointF4.x - textPaint.measureText("资料") - fontDistance - distanceAmongIcon - 25, rectF3.bottom - 25, pointF4.x - textPaint.measureText("资料") - fontDistance - distanceAmongIcon, rectF3.bottom);
//        canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.sw_ziliao), null, rectF4, textPaint);
//        canvas.drawText("资料", pointF4.x - textPaint.measureText("资料") - fontDistance, rectF3.bottom - 3, textPaint);
//
//        RectF rectF5 = new RectF(pointF5.x - textPaint.measureText("访问") - fontDistance - distanceAmongIcon - 25, rectF2.bottom - 25, pointF5.x - textPaint.measureText("访问") - fontDistance - distanceAmongIcon, rectF2.bottom);
//        canvas.drawText("访问", pointF5.x - textPaint.measureText("访问") - fontDistance, rectF2.bottom - 3, textPaint);
//        canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.sw_yueduliang), null, rectF5, textPaint);
//    }

    /**
     * 绘制雷达图的内容
     *
     * @param canvas
     */
    private void drawContent(Canvas canvas) {
        Path path = new Path();
        path.moveTo(pointFInterval1.x, pointFInterval1.y);
        path.lineTo(pointFInterval2.x, pointFInterval2.y);
        path.lineTo(pointFInterval3.x, pointFInterval3.y);
        path.lineTo(pointFInterval4.x, pointFInterval4.y);
        path.lineTo(pointFInterval5.x, pointFInterval5.y);
        path.lineTo(pointFInterval5.x, pointFInterval5.y);
        path.close();
        canvas.drawPath(path, contentPaint);
    }


    /**
     * 绘制雷达图的主要结构
     *
     * @param canvas
     */
    private void drawConstruction(Canvas canvas) {
        Path path = new Path();
        path.moveTo(pointF1.x, pointF1.y);
        path.lineTo(pointF2.x, pointF2.y);
        path.lineTo(pointF3.x, pointF3.y);
        path.lineTo(pointF4.x, pointF4.y);
        path.lineTo(pointF5.x, pointF5.y);
        path.close();
        canvas.drawPath(path, pathPaint);

        //画5个三角形
        drawTriangle(canvas);

        canvas.save();
        for (int i = 0; i < 5; i++) {
            canvas.drawLine(pointF1.x, pointF1.y, centralPoint.x, centralPoint.y, pathPaint);
            canvas.rotate(72, centralPoint.x, centralPoint.y);
        }
    }

    private void drawTriangle(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        Path pathTriangle1 = new Path();
        pathTriangle1.moveTo(centralPoint.x, centralPoint.y);
        pathTriangle1.lineTo(pointF1.x, pointF1.y);
        pathTriangle1.lineTo(pointF2.x, pointF2.y);
        pathTriangle1.close();
        paint.setColor(Color.parseColor("#626463"));
        canvas.drawPath(pathTriangle1, paint);

        Path pathTriangle2 = new Path();
        pathTriangle2.moveTo(centralPoint.x, centralPoint.y);
        pathTriangle2.lineTo(pointF2.x, pointF2.y);
        pathTriangle2.lineTo(pointF3.x, pointF3.y);
        pathTriangle2.close();
        paint.setColor(Color.parseColor("#767877"));
        canvas.drawPath(pathTriangle2, paint);

        Path pathTriangle3 = new Path();
        pathTriangle3.moveTo(centralPoint.x, centralPoint.y);
        pathTriangle3.lineTo(pointF3.x, pointF3.y);
        pathTriangle3.lineTo(pointF4.x, pointF4.y);
        pathTriangle3.close();
        paint.setColor(Color.parseColor("#626463"));
        canvas.drawPath(pathTriangle3, paint);

        Path pathTriangle4 = new Path();
        pathTriangle4.moveTo(centralPoint.x, centralPoint.y);
        pathTriangle4.lineTo(pointF4.x, pointF4.y);
        pathTriangle4.lineTo(pointF5.x, pointF5.y);
        pathTriangle4.close();
        paint.setColor(Color.parseColor("#4f5150"));
        canvas.drawPath(pathTriangle4, paint);

        Path pathTriangle5 = new Path();
        pathTriangle5.moveTo(centralPoint.x, centralPoint.y);
        pathTriangle5.lineTo(pointF5.x, pointF5.y);
        pathTriangle5.lineTo(pointF1.x, pointF1.y);
        pathTriangle5.close();
        paint.setColor(Color.parseColor("#767877"));
        canvas.drawPath(pathTriangle5, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(Radius * 2, Radius * 2);
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);

    }
}

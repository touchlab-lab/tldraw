package co.touchlab.pdraw.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 4/19/12
 * Time: 10:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class DrawView extends View
{
    private boolean drawing = false;

    private List<Float> points = new ArrayList<Float>(100);
    private List<ColorPoints> lineList = new ArrayList<ColorPoints>();

    private long lastDraw = 0l;
    private int myColor = Color.argb(0xff, 0xff, 0xff, 0xff);

    public DrawView(Context context)
    {
        super(context);
    }

    public DrawView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    static class ColorPoints
    {
        int color;
        List<Float> points;

        ColorPoints(int color, List<Float> points)
        {
            this.color = color;
            this.points = points;
        }

        public int getColor()
        {
            return color;
        }

        public List<Float> getPoints()
        {
            return points;
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        lastDraw = System.currentTimeMillis();

        Paint paint = new Paint();
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(11f);

        for (ColorPoints linePoints : lineList)
        {
            paint.setColor(linePoints.getColor());
            drawALine(canvas, paint, linePoints.getPoints());
        }

        paint.setColor(myColor);
        drawALine(canvas, paint, points);
    }

    private void drawALine(Canvas canvas, Paint paint, List<Float> thePoints)
    {
        if(thePoints.size() > 2)
        {
            Float startX = thePoints.get(0);
            Float startY = thePoints.get(1);

            for(int i=2; i< thePoints.size();)
            {
                Float x = thePoints.get(i++);
                Float y = thePoints.get(i++);
                canvas.drawLine(startX, startY, x, y, paint);
                startX = x;
                startY = y;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int action = event.getAction();

        boolean actionDown = action == MotionEvent.ACTION_DOWN;
        boolean actionUp = action == MotionEvent.ACTION_UP;
        boolean actionMove = action == MotionEvent.ACTION_MOVE;

        Log.i(getClass().getSimpleName(), "drawing: " + drawing + "/actionDown: " + actionDown + "/actionUp: " + actionUp + "/actionMove: " + actionMove + "/x: " + event.getX() + "/y: " + event.getY());

        if (drawing)
        {

            if (actionUp)
            {
                drawing = false;
                shovePoints(event);

                lineList.add(new ColorPoints(myColor, points));

                points = new ArrayList<Float>(100);

                invalidate();
            }

            if (actionMove)
            {
                shovePoints(event);
                if ((System.currentTimeMillis() - lastDraw) > 200l)
                {
                    invalidate();
                }
            }
        }
        else
        {
            if (actionDown)
            {
                drawing = true;
                shovePoints(event);

                invalidate();
            }
        }

        return true;
    }

    private void shovePoints(MotionEvent event)
    {
        float x = event.getX();
        float y = event.getY();

        int pSize = points.size();
        if(pSize >= 2)
        {
            Float lastX = points.get(pSize - 2);
            Float lastY = points.get(pSize - 1);

            double dist = Math.sqrt(Math.pow(lastX - x, 2) + Math.pow(lastY - y, 2));

            Log.i(getClass().getSimpleName(), "dist: "+ dist);
            if(dist < 10)
                return;
        }
        points.add(x);
        points.add(y);
    }

    public void setColor(int myColor)
    {
        this.myColor = myColor;
    }
}

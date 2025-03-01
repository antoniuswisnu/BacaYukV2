package com.nara.bacayuk.ui.feat_baca_kata.quiz;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DrawView extends View {
    Paint paint = new Paint();
    private final List<Float> position1=new ArrayList<Float>();
    private List<Float> position2=new ArrayList<Float>();;

    public DrawView(Context context) {
        super(context);
        invalidate();
        Log.d("drawview","In DrawView class position1:"+position1+" position2:"+position2) ;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("on draw","IN onDraw() position1:"+position1+" position2:"+position2);

        assert position1.size() == position2.size();

        for (int i = 0; i < position1.size(); i += 2) {
            float x1 = position1.get(i);
            float y1 = position1.get(i + 1);
            float x2 = position2.get(i);
            float y2 = position2.get(i + 1);
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(3);
            canvas.drawLine(x1,y1, x2,y2, paint);
        }
    }

    public void addSourcePoint(float x1, float y1) {
        position1.add(x1);
        position1.add(y1);
    }

    public void addDestinationPoint(float x2, float y2) {
        position2.add(x2);
        position2.add(y2);
        invalidate();
    }
}
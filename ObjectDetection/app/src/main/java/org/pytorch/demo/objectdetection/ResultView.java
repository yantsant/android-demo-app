// Copyright (c) 2020 Facebook, Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.

package org.pytorch.demo.objectdetection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class ResultView extends View {

    private final static int TEXT_X = 0;
    private final static int TEXT_Y = -20;
    private final static int TEXT_WIDTH = 120;
    private final static int TEXT_HEIGHT = 20;

    private Paint mPaintRectangle;
    private Paint mPaintText;
    private ArrayList<Result> mResults;

    public ResultView(Context context) {
        super(context);
    }

    public ResultView(Context context, AttributeSet attrs){
        super(context, attrs);
        mPaintRectangle = new Paint();
        mPaintRectangle.setColor(Color.YELLOW);
        mPaintText = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        ArrayList<String> strings = PrePostProcessor.ArrayResultToStrings(mResults);
        generateNoteOnSD(this.getContext(), "data.txt", strings);

        if (mResults == null) return;
        for (Result result : mResults) {
            mPaintRectangle.setStrokeWidth(5);
            mPaintRectangle.setStyle(Paint.Style.STROKE);
            canvas.drawRect(result.rect, mPaintRectangle);
            // there may be drawn picture of tile
            int width  = result.rect.width();
            int height = result.rect.height();

           // Path mPath = new Path();
            //RectF mRectF = new RectF(result.rect.left, result.rect.top, result.rect.left + TEXT_WIDTH,  result.rect.top + TEXT_HEIGHT);
            //mPath.addRect(mRectF, Path.Direction.CW);
            //mPaintText.setColor(Color.MAGENTA);
            //canvas.drawPath(mPath, mPaintText);

            mPaintText.setColor(Color.WHITE);
            mPaintText.setStrokeWidth(2);
            mPaintText.setStyle(Paint.Style.FILL);
            mPaintText.setTextSize(48);
            String caption = String.format("%s", getTextByClass(result.classIndex));//PrePostProcessor.mClasses[result.classIndex]
            canvas.drawText(caption, result.rect.left + width/10, result.rect.top + 7*height/10, mPaintText);
        }
    }
    String getTextByClass(int index){
        //index = [0,37]
        String res = "";
        int numSuit = -1;
        int numTile = -1;
        numTile = index%10;
        if (index < 31) {
            numSuit = (index-1) / 10;
        } else{
            if (index < 35) {
                numSuit = 3;
            }else {
                numSuit = 4;
                numTile -= 4;
            }
        }
        switch (numSuit) {
            case 0: // [1,10]
                res = "P";
                break;
            case 1: // [11,20]
                res = "S";
                break;
            case 2: // [21,30]
                res = "M";
                break;
            case 3:
                res = "W";
                break;
            case 4:
                res = "D";
                break;
        }
        if (numSuit < 3){
            if (numTile == 0)
                res += Integer.toString(5)+"r";
            else if (numTile < 10)
                res += Integer.toString(numTile);
        }
        else if (numSuit == 3){
            if (numTile == 1) res += "E";
            if (numTile == 2) res += "S";
            if (numTile == 3) res += "W";
            if (numTile == 4) res += "N";
        } else if (numSuit == 4){
            if (numTile == 1) res += "R";
            if (numTile == 2) res += "W";
            if (numTile == 3) res += "G";
        }

        return res;
    }
    public void setResults(ArrayList<Result> results) {
        mResults = results;
    }

    static void generateNoteOnSD(Context context, String sFileName, ArrayList<String> sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Documents");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            for (String str : sBody) {
                writer.append(str);
            }
            //writer.append(sBody[0]);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

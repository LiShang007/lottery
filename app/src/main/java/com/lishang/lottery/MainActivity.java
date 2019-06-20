package com.lishang.lottery;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.Toast;

import com.lishang.lottery.view.LotterySurfaceView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    LotterySurfaceView lotterySurfaceView;
    List<Integer> list = new ArrayList<>();
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.img);
        initData();
        initLottery();
    }

    private void initLottery() {
        lotterySurfaceView = findViewById(R.id.lottery);

        lotterySurfaceView.setMargin(20);
        lotterySurfaceView.setOnLotteryListener(new LotterySurfaceView.OnLotteryListener() {
            @Override
            public void onStart(LotterySurfaceView view) {
                int value = (int) (Math.random() * 7);
                view.setLotteryResult(value);
            }

            @Override
            public void onLotteryResult(int result) {
                int res = list.get(result);
                Bitmap bitmap = Pool.get(res);
                if (bitmap != null) {
                    img.setImageBitmap(bitmap);
                }
            }
        });


        lotterySurfaceView.setLotteryAdapter(new LotterySurfaceView.LotteryAdapter() {


            @Override
            public void onDrawPrize(Canvas canvas, Rect rect, int position) {

                int res = list.get(position);
                Bitmap bitmap = Pool.get(res);
                if (bitmap == null) {
                    bitmap = BitmapFactory.decodeResource(MainActivity.this.getResources(), res);
                    bitmap = scaleBitmap(bitmap, rect);
                    Pool.put(res, bitmap);
                }

                Paint paint = new Paint();
                paint.setAntiAlias(true);

                Rect newRect = bitmapRect(rect, bitmap);
                canvas.drawBitmap(bitmap, null, newRect, paint);

                if (position != list.size() - 1) {
                    paint.setARGB(125, 0, 0, 0);
                    canvas.drawRect(newRect, paint);
                }

            }

            @Override
            public void onDrawLottery(Canvas canvas, Rect rect, int position) {

                int res = list.get(position);
                Bitmap bitmap = Pool.get(res);
                if (bitmap == null) {
                    bitmap = BitmapFactory.decodeResource(MainActivity.this.getResources(), res);
                    bitmap = scaleBitmap(bitmap, rect);
                    Pool.put(res, bitmap);
                }

                Paint paint = new Paint();
                paint.setAntiAlias(true);

                Rect newRect = bitmapRect(rect, bitmap);
                canvas.drawBitmap(bitmap, null, newRect, paint);

            }

            private Bitmap scaleBitmap(Bitmap bitmap, Rect rect) {
                int bw = bitmap.getWidth();
                int bh = bitmap.getHeight();
                int rw = rect.width();
                int rh = rect.height();

                float scale = (float) Math.min(1.0f * rw / bw, 1.0 * rh / bh);
                Matrix matrix = new Matrix();
                matrix.setScale(scale, scale);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bw, bh, matrix, true);
                return bitmap;
            }

            private Rect bitmapRect(Rect rect, Bitmap bitmap) {
                int bw = bitmap.getWidth();
                int bh = bitmap.getHeight();
                Rect newRect = new Rect();
                newRect.left = (rect.width() - bw) / 2 + rect.left;
                newRect.top = rect.top + (rect.height() - bh) / 2;
                newRect.right = newRect.left + bw;
                newRect.bottom = rect.top + bh;
                return newRect;
            }
        });
    }

    private void initData() {
        Resources resources = getResources();
        for (int i = 1; i < 10; i++) {
            int id = resources.getIdentifier("img_" + i, "drawable", getPackageName());
            list.add(id);
        }
    }

}

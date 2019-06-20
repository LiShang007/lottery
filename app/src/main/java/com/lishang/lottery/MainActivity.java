package com.lishang.lottery;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.lishang.lottery.view.LotterySurfaceView;

public class MainActivity extends AppCompatActivity {

    LotterySurfaceView lotterySurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Log.e("OnLotteryListener", "result:" + result);
                Toast.makeText(MainActivity.this, "当前大奖为:" + (result + 1) + "号奖品", Toast.LENGTH_SHORT).show();
            }
        });


        lotterySurfaceView.setLotteryAdapter(new LotterySurfaceView.LotteryAdapter() {
            @Override
            public void onDrawPrize(Canvas canvas, Rect rect, int position) {

                Paint paint = new Paint();
                Paint textPaint = new Paint();
                paint.setAntiAlias(true);
                textPaint.setAntiAlias(true);
                textPaint.setColor(Color.WHITE);

                int size = rect.right - rect.left;
                float textSize = size / 4.0f;
                textPaint.setTextSize(textSize);
                if (position % 2 == 0) {
                    paint.setColor(Color.RED);
                } else {
                    paint.setColor(Color.BLUE);
                }

                canvas.drawRect(rect, paint);
                if (position == 8) {
                    String str = "抽奖";
                    Rect rect1 = new Rect();
                    textPaint.getTextBounds(str, 0, str.length(), rect1);
                    canvas.drawText(str, size / 2.0f + rect.left - rect1.width() / 2.0f, rect.top + size / 2 + rect1.height() / 2, textPaint);
                } else {
                    String str = (position + 1) + "号";
                    Rect rect1 = new Rect();
                    textPaint.getTextBounds(str, 0, str.length(), rect1);
                    canvas.drawText(str, size / 2.0f + rect.left - rect1.width() / 2.0f, rect.top + size / 2 + rect1.height() / 2, textPaint);
                }

            }

            @Override
            public void onDrawLottery(Canvas canvas, Rect rect, int position) {
                Paint paint = new Paint();
                Paint textPaint = new Paint();
                paint.setAntiAlias(true);
                textPaint.setAntiAlias(true);
                textPaint.setColor(Color.WHITE);
                paint.setColor(Color.YELLOW);
                canvas.drawRect(rect, paint);
                int size = rect.right - rect.left;
                float textSize = size / 4.0f;
                textPaint.setTextSize(textSize);
                if (position == 8) {
                    String str = "抽奖";
                    Rect rect1 = new Rect();
                    textPaint.getTextBounds(str, 0, str.length(), rect1);
                    canvas.drawText(str, size / 2.0f + rect.left - rect1.width() / 2.0f, rect.top + size / 2 + rect1.height() / 2, textPaint);
                } else {
                    String str = (position + 1) + "号";
                    Rect rect1 = new Rect();
                    textPaint.getTextBounds(str, 0, str.length(), rect1);
                    canvas.drawText(str, size / 2.0f + rect.left - rect1.width() / 2.0f, rect.top + size / 2 + rect1.height() / 2, textPaint);
                }
            }
        });


    }
}

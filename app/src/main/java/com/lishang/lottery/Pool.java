package com.lishang.lottery;

import android.graphics.Bitmap;
import android.util.SparseArray;

public class Pool {

    private SparseArray<Bitmap> POOL = new SparseArray<>();

    public static void put(int key, Bitmap bitmap) {
        PoolInit.INSTANCE.POOL.put(key, bitmap);
    }

    public static Bitmap get(int key) {
        return PoolInit.INSTANCE.POOL.get(key);
    }

    private static class PoolInit {
        public static Pool INSTANCE = new Pool();
    }

}

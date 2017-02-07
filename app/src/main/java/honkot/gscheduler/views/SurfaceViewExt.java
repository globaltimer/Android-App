package honkot.gscheduler.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import honkot.gscheduler.R;

public class SurfaceViewExt extends SurfaceView
        implements SurfaceHolder.Callback, GestureDetector.OnGestureListener,
        View.OnTouchListener, Runnable {

    private final static int ONE_FRAME_TICK = 1000 / 25;    // 1フレームの時間
    private final static int MAX_FRAME_SKIPS = 5;           // 時間が余ったとき最大何回フレームをスキップするか


    private SurfaceHolder mSurfaceHolder;                   // サーフェイスホルダー
    private GestureDetector mGestureDetector;               // ジェスチャー処理
    private ImageScroller mImageScroller;                   // イメージスクロール
    private Thread mMainLoop;                               // メインのゲームループの様なモノ

    // 画像を表示するためのモノ
    final Resources mRes = this.getContext().getResources();
    List<Bitmap> mBitmapList;

    public SurfaceViewExt(Context context) {
        this(context, null, 0);
    }

    public SurfaceViewExt(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SurfaceViewExt(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

//        loadBitmap();
        initSurfaceView(context);
    }

    private void loadBitmap() {
        mBitmapList = new ArrayList<>();

        // Bitmapをロードする
        Bitmap sample = BitmapFactory.decodeResource(mRes, R.drawable.sample_image);
        mBitmapList.add(resizeBitmapToDisplaySize(sample));
//        this.mBitmapList.add(BitmapFactory.decodeResource(this.mRes, R.drawable.sample_image));

        // スクローラーを作る
        mImageScroller= new ImageScroller(
                mBitmapList.get(0).getWidth(),
                mBitmapList.size());
    }

    public Bitmap resizeBitmapToDisplaySize(Bitmap src){
        int srcWidth = src.getWidth(); // 元画像のwidth
        int srcHeight = src.getHeight(); // 元画像のheight
        Log.d("test", "srcWidth = " + String.valueOf(srcWidth)
                + " px, srcHeight = " + String.valueOf(srcHeight) + " px");

        // 画面サイズを取得する
        Matrix matrix = new Matrix();
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        float screenWidth = metrics.widthPixels;
        float screenHeight = metrics.heightPixels;
        Log.d("test", "screenWidth = " + String.valueOf(screenWidth)
                + " px, screenHeight = " + String.valueOf(screenHeight) + "px");

        float widthScale = screenWidth / srcWidth;
        float heightScale = screenHeight / srcHeight;
        Log.d("test", "widthScale = " + String.valueOf(widthScale)
                + ", heightScale = " + String.valueOf(heightScale));
        if (widthScale > heightScale) {
            matrix.postScale(heightScale, heightScale);
        } else {
            matrix.postScale(widthScale, widthScale);
        }

        // リサイズ
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, srcWidth, srcHeight, matrix, true);
        int dstWidth = dst.getWidth(); // 変更後画像のwidth
        int dstHeight = dst.getHeight(); // 変更後画像のheight
        Log.d("test", "dstWidth = " + String.valueOf(dstWidth)
                + " px, dstHeight = " + String.valueOf(dstHeight) + " px");
        src = null;
        return dst;
    }

    private void initSurfaceView(Context context) {
        // サーフェイスホルダーを取り出す
        mSurfaceHolder = this.getHolder();

        // コールバック関数を登録する
        mSurfaceHolder.addCallback(this);

        // タッチリスナーをセットする
        setOnTouchListener(this);

        // ジェスチャー処理
        mGestureDetector = new GestureDetector(getContext(), this);

        // これがないとGestureDetectorが動かないよ！
        setClickable(true);
    }

//    private float calcBitmapScale(int canvasWidth, int canvasHeight, int bmpWidth, int bmpHeight) {
//
//        // 最初は幅で調べる
//        float scale = (float)canvasWidth / (float)bmpWidth;
//        float tmp = bmpHeight * scale;
//
//        // 画像の高さがキャンバスの高さより小さい（余白ができてしまう場合）高さの方で横幅をスケールする
//        if (tmp < canvasHeight) {
//            scale = (float)canvasHeight / (float)bmpHeight;
//            return scale;
//        }
//
//        return scale;
//    }



    private float flashX, flashY, lastX, lastY, currentX, currentY;
    private float lastOffset, currentOffset, tmpOffset = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getPointerCount() == 1) {
                    flashX = event.getX();
                    flashY = event.getY();
                    currentX = event.getX();
                    currentY = event.getY();
                    lastOffset = currentOffset;
                    Log.e("onTouch", "### ACTION_DOWN");
                }
                break;
            case MotionEvent.ACTION_MOVE:
                lastX = currentX;
                lastY = currentY;
                currentX = event.getX();
                currentY = event.getY();
                tmpOffset = (event.getX() - flashX);
                currentOffset = lastOffset + tmpOffset;
                Log.e("onTouch", "### ACTION_MOVE ");
                break;
            case MotionEvent.ACTION_UP:
                if (event.getPointerCount() == 1) {
                    tmpOffset = (event.getX() - flashX);
                    currentOffset = lastOffset + tmpOffset;
                    Log.e("onTouch", "### ACTION_UP " + currentOffset);

                    flashX = 0;
                    flashY = 0;
                    lastX = 0;
                    lastY = 0;
                    currentX = 0;
                    currentY = 0;
                }
                break;
        }
        return this.mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // Here is called after onTouch with ACTION_DOWN event.
        // So, Nothing to do here. Handle the onDown in onTouch.
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.e("onFling", "### Fling! " + velocityX + "/" + velocityY);
        this.mImageScroller.setVelocity(-(velocityX / 10.f));
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // nothing to do
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // TODO
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // nothing to do
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // nothing to do
        return false;
    }

    float height = 0;

    @Override
    public void draw(final Canvas canvas) {
        if (height == 0) {
            height = canvas.getHeight();
            Log.e("test", "### height " + height);
//            loadBitmap();
//            initSurfaceView(getContext());
        }

        List<ImageOffset> imageOffsetList = mImageScroller.getImageOffsetList();
        // Bitmapを表示する
        Paint paint = new Paint();

        // 順番にオフセットをとって表示する
        for (int i = 0, len = imageOffsetList.size(); i < len; i++) {
            ImageOffset currOffset = imageOffsetList.get(i);
            canvas.drawBitmap(this.mBitmapList.get(currOffset.imageIndex), currOffset.screenOffsetW, 0, paint);
        }
    }

    float width;

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        this.height = height;
        this.width = width;

        loadBitmap();
//        initSurfaceView(getContext());

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.mMainLoop = new Thread(this);
        this.mMainLoop.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        this.mMainLoop = null;
    }


    /**
     * Called in every flame.
     */
    public void move() {
        if (mImageScroller != null) {
            this.mImageScroller.move();
        }
    }

    /**
     * One of main called override method in SurfaceView.
     * It is called when the SurfaceView runs drawing in every flame.
     */
    @Override
    public void run() {
        Canvas canvas;
        long beginTime;    // 処理開始時間
        long pastTick;    // 経過時間
        int sleep = 0;
        int frameSkipped;    // 何フレーム分スキップしたか

        // フレームレート関連
        int frameCount = 0;
        long beforeTick = 0;
        long currTime = 0;
        String tmp = "";

        // 文字書いたり
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setTextSize(60);

        // スレッドが消滅していない間はずっと処理し続ける
        while (this.mMainLoop != null) {

            canvas = null;

            // フレームレートの表示
            frameCount++;
            currTime = System.currentTimeMillis();
            if (beforeTick + 1000 < currTime) {
                beforeTick = currTime;
                tmp = "" + frameCount;
                frameCount = 0;
            }

            try {
                canvas = this.mSurfaceHolder.lockCanvas();
                canvas.drawColor(Color.WHITE);

                synchronized (this.mSurfaceHolder) {
                    // 現在時刻
                    beginTime = System.currentTimeMillis();
                    frameSkipped = 0;

                    // ////////////////////////////////////////////////////////////
                    // ↓アップデートやら描画やら
                    this.move();
                    this.draw(canvas);
                    // ////////////////////////////////////////////////////////////

                    // 経過時間
                    pastTick = System.currentTimeMillis() - beginTime;

                    // 余っちゃった時間
                    sleep = (int)(ONE_FRAME_TICK - pastTick);

                    // 余った時間があるときは待たせる
                    if (0 < sleep) {
                        try {
                            Thread.sleep(sleep);
                        } catch (Exception e) {}
                    }

                    // 描画に時間係過ぎちゃった場合は更新だけ回す
                    while (sleep < 0 && frameSkipped < MAX_FRAME_SKIPS) {
                        // ////////////////////////////////////////////////////////////
                        // 遅れた分だけ更新をかける
                        this.move();
                        // ////////////////////////////////////////////////////////////
                        sleep += ONE_FRAME_TICK;
                        frameSkipped++;
                    }
                    canvas.drawText("FPS:" + tmp, 1, 100, paint);
                }
            } finally {
                // キャンバスの解放し忘れに注意
                if (canvas != null) {
                    this.mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    // ////////////////////////////////////////////////////////////
// 画像のオフセット
    private class ImageOffset {
        int imageIndex;         // 画像のインデックス
        int screenOffsetW;      // スクリーンオフセット

    }

    private enum ScrollProcess {
        SCROLL_MOVE,
        SCROLL_SNAP,
        SCROLL_STOP,
    }

    private class ImageScroller {

        private float mCursor = 0.0f;       // 今の位置
        private float mVelocity = 0.0f;     // 加速度
        private int mImageWidth;            // 画像幅
//        private int mScreenWidth;           // スクリーン幅
        private int mImageCount;            // 画像の枚数
        private int mTotalImageWidth;       // 画像全部合わせた画像のサイズ
        private int mInScreenImageCount;    // スクリーン内に入る画像の枚数

        private ScrollProcess mCurrProcess; // 処理番号
        private float mSnapPos;             // スナップする位置

        private ImageScroller(int imageWidth, int imageCount) {
            this.mCursor = 0;
            this.mVelocity = 0.0f;
            this.mImageWidth = imageWidth;
//            this.mScreenWidth = 0;
            this.mImageCount = imageCount;
            this.mTotalImageWidth = this.mImageCount * this.mImageWidth;
            this.mInScreenImageCount = (this.mScreenWidth / this.mImageCount) + 2;
            this.mCurrProcess = ScrollProcess.SCROLL_STOP;
        }

        private void setScreenWidth(int width) {
//            this.mScreenWidth = width;
        }

        private void move() {
            this.mCursor += this.mVelocity;

            switch (this.mCurrProcess) {
                case SCROLL_MOVE:
                {
                    // だんだん減速
                    this.mVelocity *= 0.78f;

                    // 速度が適当に落ち着いたらスナップ処理へ
//                    if (Math.abs(this.mVelocity) < 20.0f) {
//
//                        // スナップする位置を決める
//                        int nearSnapPosLeft = (int)(this.mCursor / this.mImageWidth) * this.mImageWidth;
//                        int nearSnapPosRight = (int)((this.mCursor / this.mImageWidth) + 1) * this.mImageWidth;
//
//                        // 近い方へスナップする
//                        if (Math.abs(nearSnapPosLeft - this.mCursor) < Math.abs(nearSnapPosRight - this.mCursor)) {
//                            this.mSnapPos = nearSnapPosLeft;
//                        } else {
//                            this.mSnapPos = nearSnapPosRight;
//                        }
//
//                        // スナップ速度を設定
//                        this.mVelocity += (this.mSnapPos - this.mCursor) / 10;
//
////                        this.mCurrProcess = ScrollProcess.SCROLL_SNAP;
//                    }

                    // スクリーンがすべてマイナス範囲に入っているときは右端にカーソルを移動させる
                    if (this.mCursor + this.mScreenWidth < 0) {
                        this.mCursor += this.mTotalImageWidth;
                    }

                    // スクリーンが全部の画像のサイズを合わせた位置より大きくなってる場合は左端に戻す
                    if (this.mTotalImageWidth < this.mCursor) {
                        this.mCursor -= this.mTotalImageWidth;
                    }
                }
                break;
                case SCROLL_SNAP:
                {
//                    // 減速
//                    this.mVelocity *= 0.94f;
//                    // 速度を作る
//                    this.mVelocity += (this.mSnapPos - this.mCursor) / 10;

                    // ほとんど動かなくなったらぴったりくっつけて終了
//                    if (Math.abs(this.mVelocity) < 2.0f && Math.abs(this.mSnapPos - this.mCursor) < 2.0f) {
//                        this.mCursor = this.mSnapPos;
//                        this.mVelocity = 0.0f;
//                        this.mCurrProcess = ScrollProcess.SCROLL_STOP;
//                    }
                }
                break;
                case SCROLL_STOP:
                {
                    // 何もしない
                }
                break;
            }
        }

        private int getFirstImageIndex() {
            // 画面の開始位置
            int screenStartPos = (int)this.mCursor;

            // カーソルが左端を越えている(スクロールして一番最後のイメージのほうへ)
            if (screenStartPos < 0) {
                screenStartPos += this.mTotalImageWidth;
            }

            return (int)(screenStartPos / this.mImageWidth);
        }


        private List<ImageOffset> getImageOffsetList() {
            List<ImageOffset> imageOffsetList = new ArrayList<ImageOffset>();

            // 最初のイメージのインデックス番号を取り出す
            int firstImageIndex = this.getFirstImageIndex();
            int imageCount = this.mImageCount;
            int offsetBase = (int)this.mCursor;

            // カーソルがマイナス、後ろへスクロールしている場合は全部プラスとして考える
            if (offsetBase < 0)
                offsetBase += this.mTotalImageWidth;
            offsetBase = (offsetBase % this.mImageWidth) * -1;

            // 画面内の入っている画像のオフセット値を表示する
            inScreenImageCount = (this.mScreenWidth / this.mImageCount) + 2;
            for (int i = 0, len = this.mInScreenImageCount; i < len; i++) {

                ImageOffset    offset = new ImageOffset();

                offset.imageIndex = (firstImageIndex + i) % imageCount;
                offset.screenOffsetW = offsetBase + (this.mImageWidth * i);

                // 画像のインデックス番号を取り出す
                imageOffsetList.add(offset);
            }

            return imageOffsetList;
        }

        // ////////////////////////////////////////////////////////////
        // 速度を設定する
        private void setVelocity(float speed) {
            this.mVelocity += speed;
            this.mCurrProcess = ScrollProcess.SCROLL_MOVE;
        }
    }
}

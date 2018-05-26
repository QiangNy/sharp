package com.cydroid.tpicture;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cydroid.tpicture.bean.PictureListenner;
import com.cydroid.tpicture.service.AdjvService;
import com.cydroid.tpicture.service.PictureService;
import com.cydroid.tpicture.utils.BigUtil;
import com.cydroid.tpicture.utils.DswLog;
import com.cydroid.tpicture.utils.FileChooser;
import com.cydroid.tpicture.utils.Singleton;
import java.io.File;
import java.nio.ByteBuffer;

public class MainActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener,PictureListenner {

    private static String TAG = "MainActivity";
    private RadioButton frontCamera;
    private ImageView mImageView;
    private TextView mTxtResult;
    private SeekBar seekBar;
    private TextView textView;
    private File mSrcFile;
    private float threshold = 0.5f;
    // The sample includes the 20 patterns.
    static final int PATTERN_NUMBERS = 20;
    static final int PATTERN_NUMBERS1 = 16;
    // These matrices will be used to scale points of the image5
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;

    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;

    int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    int g_nPatterns;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DswLog.setMMILogFile();

        initViews();

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        textView = (TextView) findViewById(R.id.textView);
        final int max = seekBar.getMax();
        seekBar.setProgress((int)(threshold * max));
        textView.setText(String.format(Float.toString(threshold)+ "/" + Integer.toString(1)));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int val = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                val = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                threshold = (float) val / max;
                textView.setText(String.format(Float.toString(threshold)+ "/" + Integer.toString(1)));
            }
        });

        initData();

        DswLog.e(TAG, "onCreate");
    }

    private void initData() {
        setMetric4Camera(BigUtil.getfMetricLabels(), PATTERN_NUMBERS);
        g_nPatterns = PATTERN_NUMBERS;
        //regist listen
        Singleton.getInstance().setmPictureListenner(this);

        keepScreenLight();

        startAdjvService();

        startMtklog();
    }

    private void keepScreenLight() {
        mPowerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Sharp_Picture");
        mWakeLock.setReferenceCounted(false);
        mWakeLock.acquire();
    }

    private void initViews() {
        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setOnTouchListener(this);

        mTxtResult = (TextView) findViewById(R.id.txt_result);
        ((Button) findViewById(R.id.btn_open)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_rearcamera)).setOnClickListener(this);

        frontCamera = (RadioButton) findViewById(R.id.btn_frontcamera);
        frontCamera.setOnClickListener(this);
        frontCamera.setChecked(true);
    }




    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        DswLog.d(TAG, "onDestroy");
        stopService(new Intent(this, PictureService.class));
        Singleton.getInstance().unPictureListenner();

        stopAdjvService();

        try {
            mWakeLock.release();
        }catch (Exception e) {
            DswLog.d(TAG, e.getMessage());
        }

        stopMtklog();

    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_frontcamera:
                setMetric4Camera(BigUtil.getfMetricLabels(),PATTERN_NUMBERS);
                g_nPatterns = PATTERN_NUMBERS;
                break;
            case R.id.btn_rearcamera:
                setMetric4Camera(BigUtil.getrMetricLabels(),PATTERN_NUMBERS1);
                g_nPatterns = PATTERN_NUMBERS1;
                break;
            case R.id.btn_open:
                FileChooser fileChooser = new FileChooser(MainActivity.this, "Select img file", FileChooser.DialogType.SELECT_FILE, mSrcFile);
                FileChooser.FileSelectionCallback callback = new FileChooser.FileSelectionCallback() {

                    @Override
                    public void onSelect(File file) {
                        //Do something with the selected file
                        DswLog.e(TAG, "file path: " + file.getPath());
                        mSrcFile = file;
                        try {
                            matrix.reset();
                            savedMatrix.set(matrix);
                            mImageView.setImageMatrix(matrix);
                            DswLog.e(TAG, "### 1");

                            BitmapFactory.Options op = new BitmapFactory.Options();
                            op.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            Bitmap bitmapSrc = BitmapFactory.decodeFile(file.getPath(), op);
                            DswLog.e(TAG, "### 2");
                            int[] degree = new int[PATTERN_NUMBERS];
                            int[] xPos = new int[PATTERN_NUMBERS];
                            int[] yPos = new int[PATTERN_NUMBERS];

                            int success = SharpnessEngine.getSharpnessValues(bitmapSrc, threshold, degree, xPos, yPos);

                            drawBitmap(bitmapSrc, xPos, yPos, g_nPatterns, success);
                            if (success == 0)
                            {
                                mTxtResult.setText(String.format(""));
                                Toast.makeText(getApplicationContext(), "Fail to parse the image context.", Toast.LENGTH_LONG).show();
                                return;
                            }

                            String result = "";
                            for (int i = 0; i < g_nPatterns; i++) {
                                result += Integer.toString(degree[i]) + ", ";
                            }
                            mTxtResult.setText(result);

                        } catch (Exception e) {
                            e.printStackTrace();
                            DswLog.e(TAG, "### 5");
                        }
                    }
                };
                DswLog.e(TAG, "### 6");
                fileChooser.show(callback);
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;
        DswLog.e(TAG, "### 7");
        dumpEvent(event);
        // Handle touch events here...

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:   // first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG"); // write to LogCat
                mode = DRAG;
                break;

            case MotionEvent.ACTION_UP: // first finger lifted

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                } else if (mode == ZOOM) {
                    // pinch zooming
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f) {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist; // setting the scaling of the
                        // matrix...if scale > 1 means
                        // zoom in...if scale < 1 means
                        // zoom out
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix); // display the transformation on screen

        return true; // indicate event was handled
    }

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */

    private float spacing(MotionEvent event)
    {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */

    private void midPoint(PointF point, MotionEvent event)
    {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * Show an event in the LogCat view, for debugging
     */
    private void dumpEvent(MotionEvent event)
    {
        String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }

        sb.append("]");
        Log.d("Touch Events ---------", sb.toString());
    }

    private void drawBitmap(Bitmap src, int[] xPos, int[] yPos, int nPatterns, int ret) {
        Bitmap bitmapDst = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapDst);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.RED);
        p.setStrokeWidth(2);
        p.setStyle(Paint.Style.STROKE);
        canvas.drawBitmap(src, new Matrix(), null);
        if (ret == 1) {
            for (int i = 0; i < nPatterns; i++) {
                canvas.drawCircle(xPos[i], yPos[i], 10, p);
            }
        }
        DswLog.e(TAG, "### B3");
        mImageView.setImageBitmap(bitmapDst);
    }

    private void stopAdjvService() {
        Intent intent = new Intent(MainActivity.this,
                AdjvService.class);
        stopService(intent);
    }

    private void startAdjvService() {
        Intent intent = new Intent(MainActivity.this,
                AdjvService.class);
        startService(intent);
    }

    private void setMetric4Camera(int[] label, int nPatterns)
    {
        int fSuccess = SharpnessEngine.setMetricLabels(label, nPatterns);
        if ( fSuccess == 0)
        {
            Toast.makeText(getApplicationContext(), "Fail to set metric label.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPicture(boolean isPass) {
        Toast.makeText(getApplicationContext(), "wirte test flag " + isPass, Toast.LENGTH_LONG).show();
    }

    /**
     * Start MTKLog
     */
    private void startMtklog() {
        Intent starti = new Intent();
        starti.setAction("com.mediatek.mtklogger.ADB_CMD");
        Bundle bundle = new Bundle();
        bundle.putString("cmd_name", "start");
        bundle.putInt("cmd_target", 1);
        starti.putExtras(bundle);
        sendBroadcast(starti);
        DswLog.e(TAG, "start mtk mmi logcat ");
    }

    /**
     * Stop MTKLog
     */
    private void stopMtklog() {
        Intent stopi = new Intent();
        stopi.setAction("com.mediatek.mtklogger.ADB_CMD");
        Bundle bundle = new Bundle();
        bundle.putString("cmd_name", "stop");
        //Gionee zhangke 20151030 modify for CR01577329 start
        bundle.putInt("cmd_target", 1);
        //Gionee zhangke 20151030 modify for CR01577329 end
        stopi.putExtras(bundle);
        sendBroadcast(stopi);
    }



}

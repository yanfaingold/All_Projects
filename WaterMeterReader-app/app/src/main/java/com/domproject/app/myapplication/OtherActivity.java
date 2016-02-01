package com.domproject.app.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.AsyncServiceHelper;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.opencv.android.AsyncServiceHelper.InstallService;

public class OtherActivity extends Activity implements OnTouchListener {
    //load
    final String TAG = "Hello World";

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Create and set View
                    setContentView(v);
                }
                break;

                default: {

                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
    String rtext0 = "",rtext1 = "",rtext2 = "",rtext3 = "",rtext4 = "",rtext5 = "",rtext6 = "";
    static Uri capturedImageUri = null;
    String fiveorsixnumbers, domwanted;
    OurView v;
    int loop = 0, changepic = 0, size = 1;
    Canvas c;
    Bitmap original, originalgray, ex1,ex1_2, ex2, ex3, meterPic, photo;
    int cheight, cwidth;
    int counttochange = 0;
    long startcount = 0, endcount = 0;
    float pressedx = 0, pressedy = 0, movex = 0, movey = 0, savex = 0, savey = 0;
    boolean linesfound = true, success = false, captured = false, photosaved = false;
    //can change in gui settings
    int MAX_PICTURE_SIZE,PRE_DILATE_SIZE,PRE_ERODE_SIZE,NUM_OF_CANDIDATES,LINES_GAP, DILATE_SIZE, ERODE_SIZE, LINES_FOUND_DRAW_SIZE,CLEAN_LOWER_GAP_PRECENTAGE, CLEAN_HIGHER_GAP_PRECENTAGE, CLEAN_LOWER_TOTAL_PIXEL_PRECENTAGE, CLEAN_HIGHER_TOTAL_PIXEL_PRECENTAGE,OCR_PICTURE_SIZE;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SharedPreferences prefs;
        //default settings
        defaultsettings();
        //add saved settings if been changed

        File fpicsize = new File("/data/data/com.domproject.app.myapplication/shared_prefs/picsize.xml");
        if (fpicsize.exists()) {
            prefs = getApplicationContext().getSharedPreferences("picsize", 1);

            MAX_PICTURE_SIZE = prefs.getInt("savedpicsize", -1);

        }

        File fpredilate = new File("/data/data/com.domproject.app.myapplication/shared_prefs/predilate.xml");
        if (fpredilate.exists()) {
            prefs = getApplicationContext().getSharedPreferences("predilate", 1);

            PRE_DILATE_SIZE = prefs.getInt("savedpredilate", -1);

        }

        File fpreerode = new File("/data/data/com.domproject.app.myapplication/shared_prefs/preerode.xml");
        if (fpreerode.exists()) {
            prefs = getApplicationContext().getSharedPreferences("preerode", 1);

            PRE_ERODE_SIZE = prefs.getInt("savedpreerode", -1);

        }

        File fnumofcandidates = new File("/data/data/com.domproject.app.myapplication/shared_prefs/numofcandidates.xml");
        if (fnumofcandidates.exists()) {
            prefs = getApplicationContext().getSharedPreferences("numofcandidates", 1);

            NUM_OF_CANDIDATES = prefs.getInt("savednumofcandidates", -1);

        }

        File flinesgap = new File("/data/data/com.domproject.app.myapplication/shared_prefs/linesgap.xml");
        if (flinesgap.exists()) {
            prefs = getApplicationContext().getSharedPreferences("linesgap", 1);

            LINES_GAP = prefs.getInt("savedlinesgap", -1);

        }

        File fdilate = new File("/data/data/com.domproject.app.myapplication/shared_prefs/dilate.xml");
        if (fdilate.exists()) {
            prefs = getApplicationContext().getSharedPreferences("dilate", 1);

            DILATE_SIZE = prefs.getInt("saveddilate", -1);

        }

        File ferode = new File("/data/data/com.domproject.app.myapplication/shared_prefs/erode.xml");
        if (ferode.exists()) {
            prefs = getApplicationContext().getSharedPreferences("erode", 1);

            ERODE_SIZE = prefs.getInt("savederode", -1);

        }


        File flinesfounddrawsize = new File("/data/data/com.domproject.app.myapplication/shared_prefs/linesfounddrawsize.xml");
        if (flinesfounddrawsize.exists()) {
            prefs = getApplicationContext().getSharedPreferences("linesfounddrawsize", 1);

            LINES_FOUND_DRAW_SIZE = prefs.getInt("savedlinesfounddrawsize", -1);

        }

        File flowergapremove = new File("/data/data/com.domproject.app.myapplication/shared_prefs/lowergapremove.xml");
        if (flowergapremove.exists()) {
            prefs = getApplicationContext().getSharedPreferences("lowergapremove", 1);

            CLEAN_LOWER_GAP_PRECENTAGE = prefs.getInt("savedlowergapremove", -1);

        }

        File fhighergapremove = new File("/data/data/com.domproject.app.myapplication/shared_prefs/highergapremove.xml");
        if (fhighergapremove.exists()) {
            prefs = getApplicationContext().getSharedPreferences("highergapremove", 1);

            CLEAN_HIGHER_GAP_PRECENTAGE = prefs.getInt("savedhighergapremove", -1);

        }

        File flowertotalremove = new File("/data/data/com.domproject.app.myapplication/shared_prefs/lowertotalremove.xml");
        if (flowertotalremove.exists()) {
            prefs = getApplicationContext().getSharedPreferences("lowertotalremove", 1);

            CLEAN_LOWER_TOTAL_PIXEL_PRECENTAGE = prefs.getInt("savedlowertotalremove", -1);

        }

        File fhighertotalremove = new File("/data/data/com.domproject.app.myapplication/shared_prefs/highertotalremove.xml");
        if (fhighertotalremove.exists()) {
            prefs = getApplicationContext().getSharedPreferences("highertotalremove", 1);

            CLEAN_HIGHER_TOTAL_PIXEL_PRECENTAGE = prefs.getInt("savedhighertotalremove", -1);

        }

        File focrpicsize = new File("/data/data/com.domproject.app.myapplication/shared_prefs/ocrpicsize.xml");
        if (focrpicsize.exists()) {
            prefs = getApplicationContext().getSharedPreferences("ocrpicsize", 1);

            OCR_PICTURE_SIZE = prefs.getInt("savedocrpicsize", -1);

        }

        //create folders
        File folder = new File(Environment.getExternalStorageDirectory() + "/OCRfiles/tessdata");
        while (!folder.exists()) {
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
                Log.i("writed", "writed");
            }
            if (success) {

            } else {
                Log.i("didntmanagedtowrite", "notwritedfolders");
            }
        }
        //create file in storage
        try {
            importfile("org.opencv.engine.apk");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //check if installed
        boolean installed = appInstalledOrNot("org.opencv.engine");
        //install it
        if (installed == false) {
            //startLockTask();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/OCRfiles/tessdata/" + "org.opencv.engine.apk")), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            while (!installed) {
                installed = appInstalledOrNot("org.opencv.engine");
            }

            //restart
            Intent i = new Intent(this, OtherActivity.class);
            finish();
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);

        }
        Log.i(TAG, "Trying to load OpenCV library");
        if (!initOpenCV(OpenCVLoader.OPENCV_VERSION_2_4_2, this, mOpenCVCallBack)) {
            Log.e(TAG, "Cannot connect to OpenCV Manager");
        }

        v = new OurView(this);
        v.setOnTouchListener(this);


        //Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        String fname = Environment.getExternalStorageDirectory() + "/temp.png";
        while (success == false) {
            if (size > 30)
                System.exit(1);
            options.inSampleSize = size;//should make a functions that increae it by 1 if its an error(too large picture)
            try {//if pic too big
                //    meterPic = BitmapFactory.decodeResource(getResources(), R.drawable.temp, options);


                meterPic = BitmapFactory.decodeFile(fname, options);
                Log.i("w", Integer.toString(meterPic.getWidth()));
                Log.i("h", Integer.toString(meterPic.getHeight()));
                if (meterPic.getWidth() * meterPic.getHeight() < MAX_PICTURE_SIZE)//max size check
                    success = true;
                else
                    size++;//if too big for hough lines
            } catch (Exception e) {
                size++;//take smaller sample
            }
        }

    }

    private void defaultsettings() {
        //set default settings
        MAX_PICTURE_SIZE = 2500000;
        PRE_DILATE_SIZE = 3;
        PRE_ERODE_SIZE = 2;
        NUM_OF_CANDIDATES=20;
        LINES_GAP=2;
        DILATE_SIZE = 2;
        ERODE_SIZE = 2;
        LINES_FOUND_DRAW_SIZE = 10;
        //aftercutsettings
        CLEAN_LOWER_GAP_PRECENTAGE = 55;
        CLEAN_HIGHER_GAP_PRECENTAGE = 90;
        CLEAN_LOWER_TOTAL_PIXEL_PRECENTAGE = 13;
        CLEAN_HIGHER_TOTAL_PIXEL_PRECENTAGE = 70;
        OCR_PICTURE_SIZE=2;
    }

    public Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static boolean initOpenCV(String Version, final Context AppContext,
                                     final LoaderCallbackInterface Callback) {
        AsyncServiceHelper helper = new AsyncServiceHelper(Version, AppContext,
                Callback);
        Intent intent = new Intent("org.opencv.engine.BIND");
        intent.setPackage("org.opencv.engine");
        if (AppContext.bindService(intent, helper.mServiceConnection,
                Context.BIND_AUTO_CREATE)) {
            return true;
        } else {
            AppContext.unbindService(helper.mServiceConnection);
            InstallService(AppContext, Callback);
            return false;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int CAMERA_REQUEST = 1888;
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            try {
                meterPic = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), capturedImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        v.pause();
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        v.resume();
    }


    public class OurView extends SurfaceView implements Runnable {

        Thread t = null;
        SurfaceHolder holder;
        boolean isItOK = false;

        public OurView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
            holder = getHolder();


        }

        public void run() {

            while (isItOK) {
                if (!holder.getSurface().isValid()) {
                    continue;
                }
                c = holder.lockCanvas();
                cheight = c.getHeight();
                cwidth = c.getWidth();
                try {
                    algoritm();
                } catch (Exception e) {
                }

                try {
                    drawing(c);
                } catch (Exception e) {

                }

                holder.unlockCanvasAndPost(c);
            }
        }


        private void algoritm() {
            // TODO Auto-generated method stub
            if (loop == 0) {

                meterPic = meterPic.copy(Bitmap.Config.ARGB_8888, true);
                original = meterPic.copy(Bitmap.Config.ARGB_8888, true);
                originalgray = meterPic.copy(Bitmap.Config.ARGB_8888, true);
                ex1_2=meterPic.copy(Bitmap.Config.ARGB_8888, true);

                Mat lines = new Mat();
                Mat myMat = new Mat();
                Mat temp = new Mat();
                Mat forex1_2=new Mat();

                Utils.bitmapToMat(meterPic, myMat);
                Imgproc.cvtColor(myMat, myMat, Imgproc.COLOR_RGB2GRAY);
                double otsu_thresh_val = Imgproc.threshold(myMat, temp, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
                double high_thresh_val = otsu_thresh_val,
                        lower_thresh_val = otsu_thresh_val * 0.5;
                Utils.matToBitmap(myMat, originalgray);
                Imgproc.Canny(myMat, myMat, lower_thresh_val, high_thresh_val);
                Imgproc.dilate(myMat, myMat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(PRE_DILATE_SIZE, PRE_DILATE_SIZE)));
                Imgproc.erode(myMat, myMat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(PRE_ERODE_SIZE, PRE_ERODE_SIZE)));
                Utils.matToBitmap(myMat, meterPic);

                boolean foundlines=true,foundcondidates=false;
                int stopwhenchange=0;
                int counterupanddown=0;
                while (!foundcondidates && foundlines) {
                    Log.i("currenthough",Integer.toString(300+counterupanddown));
                    try {
                        Imgproc.HoughLines(myMat, lines, 1, Math.PI / 180, 400+counterupanddown);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "didnt find lines",
                                Toast.LENGTH_LONG).show();
                        foundlines = false;

                    }
                    if(lines.cols()<NUM_OF_CANDIDATES) {
                        if(stopwhenchange==-1)
                            foundcondidates=true;
                        stopwhenchange=1;
                        counterupanddown -= 50;
                    }
                    else {
                        if(stopwhenchange==1)
                            foundcondidates=true;
                        stopwhenchange=-1;
                        counterupanddown += 50;
                    }
                }
                double finalangle = 0;

                int[] twoioffoundlines = new int[2];

                double[] allslopes = new double[lines.cols()];

                Point[] pt1 = new Point[lines.cols()];
                Point[] pt2 = new Point[lines.cols()];
                if(foundlines) {
                    //save points and slopes
                    savepointsandslopes(allslopes, pt1, pt2, lines, myMat);


                    getcutpoints(allslopes, pt1, pt2, lines, myMat, twoioffoundlines, meterPic);

                    forex1_2=myMat.clone();
                    //draw all lines to check
                    for(int i=0;i<pt1.length;i++) {
                        Scalar s = new Scalar(127, 127, 127);
                        Core.line(myMat, new Point(pt1[i].x, pt1[i].y), new Point(pt2[i].x, pt2[i].y), s, (int) (1));
                    }
                    Utils.matToBitmap(myMat, meterPic);
                    int inttwoioffoundlines = twoioffoundlines[0];
                    finalangle = Math.toDegrees(Math.atan2(pt2[inttwoioffoundlines].y - pt1[inttwoioffoundlines].y, pt2[inttwoioffoundlines].x - pt1[inttwoioffoundlines].x));

                    ex1 = Bitmap.createScaledBitmap(meterPic,  (meterPic.getWidth() * OCR_PICTURE_SIZE),  (meterPic.getHeight() * OCR_PICTURE_SIZE), false);
                    //chosen before rotation
                    Scalar sforex1_2 = new Scalar(127, 127, 127);
                    Core.line(forex1_2, new Point(pt1[twoioffoundlines[0]].x, pt1[twoioffoundlines[0]].y), new Point(pt2[twoioffoundlines[0]].x, pt2[twoioffoundlines[0]].y), sforex1_2, (int) (LINES_FOUND_DRAW_SIZE));
                    Core.line(forex1_2, new Point(pt1[twoioffoundlines[1]].x, pt1[twoioffoundlines[1]].y), new Point(pt2[twoioffoundlines[1]].x, pt2[twoioffoundlines[1]].y), sforex1_2, (int) (LINES_FOUND_DRAW_SIZE));
                    Utils.matToBitmap(forex1_2, ex1_2);
                    ex1_2 = Bitmap.createScaledBitmap(ex1_2,  (ex1_2.getWidth() * OCR_PICTURE_SIZE),  (ex1_2.getHeight() * OCR_PICTURE_SIZE), false);

                    Log.i("xp1", Double.toString(pt1[(twoioffoundlines[0])].x));
                    Log.i("yp1", Double.toString(pt1[(twoioffoundlines[0])].y));
                    Log.i("xp2", Double.toString(pt2[(twoioffoundlines[0])].x));
                    Log.i("yp2", Double.toString(pt2[(twoioffoundlines[0])].y));
                    double y1fromzeroxp1 = allslopes[(twoioffoundlines[0])] * (0 - pt1[(twoioffoundlines[0])].x) + pt1[(twoioffoundlines[0])].y;
                    double y2fromendpicxp1 = allslopes[(twoioffoundlines[0])] * (meterPic.getWidth() - pt2[(twoioffoundlines[0])].x) + pt2[(twoioffoundlines[0])].y;
                    Log.i("y1start", Double.toString(y1fromzeroxp1));
                    Log.i("y1fin", Double.toString(y2fromendpicxp1));
                    double y1fromzeroxp2 = allslopes[(twoioffoundlines[1])] * (0 - pt1[(twoioffoundlines[1])].x) + pt1[(twoioffoundlines[1])].y;
                    double y2fromendpicxp2 = allslopes[(twoioffoundlines[1])] * (meterPic.getWidth() - pt2[(twoioffoundlines[1])].x) + pt2[(twoioffoundlines[1])].y;
                    Log.i("y1start", Double.toString(y1fromzeroxp2));
                    Log.i("y1fin", Double.toString(y2fromendpicxp2));
                    //find center points of lines
                    double middleyp1;
                    if (y1fromzeroxp1 > y2fromendpicxp1)
                        middleyp1 = (y1fromzeroxp1 - y2fromendpicxp1) / 2 + y2fromendpicxp1;
                    else
                        middleyp1 = (y2fromendpicxp1 - y1fromzeroxp1) / 2 + y1fromzeroxp1;
                    Log.i("middleyp1", Double.toString(middleyp1));
                    double middleyp2;
                    if (y1fromzeroxp2 > y2fromendpicxp2)
                        middleyp2 = (y1fromzeroxp2 - y2fromendpicxp2) / 2 + y2fromendpicxp2;
                    else
                        middleyp2 = (y2fromendpicxp2 - y1fromzeroxp2) / 2 + y1fromzeroxp2;
                    Log.i("middleyp2", Double.toString(middleyp2));

                    //rotate
                    int heightneedtoadd = meterPic.getHeight();
                    meterPic = RotateBitmap(original, (float) finalangle * -1);
                    heightneedtoadd = meterPic.getHeight() - heightneedtoadd;
                    //copy rotated meterpic
                    ex2 = meterPic.copy(Bitmap.Config.ARGB_8888, true);
                    //canny again
                    Utils.bitmapToMat(meterPic, myMat);
                    Imgproc.cvtColor(myMat, myMat, Imgproc.COLOR_RGB2GRAY);
                    otsu_thresh_val = Imgproc.threshold(myMat, temp, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
                    high_thresh_val = otsu_thresh_val;
                    lower_thresh_val = otsu_thresh_val * 0.5;

                    Imgproc.Canny(myMat, myMat, lower_thresh_val, high_thresh_val);
                    Imgproc.dilate(myMat, myMat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(DILATE_SIZE, DILATE_SIZE)));
                    Imgproc.erode(myMat, myMat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(ERODE_SIZE, ERODE_SIZE)));
                    //draw two lines chosen to show
                    Scalar schosen = new Scalar(127, 127, 127);
                    Core.line(myMat, new Point(0, middleyp1 + heightneedtoadd / 2), new Point(meterPic.getWidth(), middleyp1 + heightneedtoadd / 2), schosen, (int) (LINES_FOUND_DRAW_SIZE));
                    Core.line(myMat, new Point(0, middleyp2 + heightneedtoadd / 2), new Point(meterPic.getWidth(), middleyp2 + heightneedtoadd / 2), schosen, (int) (LINES_FOUND_DRAW_SIZE));

                    Utils.matToBitmap(myMat, ex2);
                    ex2 = Bitmap.createScaledBitmap(ex2,  (ex2.getWidth() * OCR_PICTURE_SIZE), (ex2.getHeight() * OCR_PICTURE_SIZE), false);
                    //draw black line for floodfill
                    Scalar s = new Scalar(0, 0, 0);
                    Core.line(myMat, new Point(0, middleyp1 + heightneedtoadd / 2), new Point(meterPic.getWidth(), middleyp1 + heightneedtoadd / 2), s, (int) (LINES_FOUND_DRAW_SIZE));
                    Core.line(myMat, new Point(0, middleyp2 + heightneedtoadd / 2), new Point(meterPic.getWidth(), middleyp2 + heightneedtoadd / 2), s, (int) (LINES_FOUND_DRAW_SIZE));
                    Utils.matToBitmap(myMat, meterPic);


                    //cut
                    if (middleyp2 > middleyp1)
                        meterPic = Bitmap.createBitmap(meterPic, (int) (0), (int) (middleyp1 + heightneedtoadd / 2), meterPic.getWidth(), (int) (middleyp2 - middleyp1));
                    else
                        meterPic = Bitmap.createBitmap(meterPic, (int) (0), (int) (middleyp2 + heightneedtoadd / 2), meterPic.getWidth(), (int) (middleyp1 - middleyp2));
                    //fill with white
                    floodFill(meterPic, new Point(0, 0), Color.BLACK, Color.WHITE);


                    ex3 = Bitmap.createScaledBitmap(meterPic, (int) (meterPic.getWidth() * OCR_PICTURE_SIZE), (int) (meterPic.getHeight() * OCR_PICTURE_SIZE), false);
                    try {
                        //cleaning not needed dots and parts pixelwise
                        List<objectleftrighttopdown> listofstartfin = new ArrayList<objectleftrighttopdown>();
                        List<objectleftrighttopdown> listofstartfincleaning = new ArrayList<objectleftrighttopdown>();
                        Point cutpoint = cutsides(meterPic, listofstartfin, listofstartfincleaning);

                        //fill list to clean numbers
                        List<Point> listforfloodfill;
                        Bitmap copy;
                        for (int i = 0; i < listofstartfin.size(); i++) {
                            listforfloodfill = new ArrayList<Point>();
                            copy = meterPic.copy(Bitmap.Config.ARGB_8888, true);
                            //fill
                            fillfloodfilllist(listofstartfin.get(i).left, listofstartfin.get(i).right, listofstartfin.get(i).top, listofstartfin.get(i).down, listofstartfin.get(i).pixels, copy, listforfloodfill);
                            //clean
                            for (int cleani = 0; cleani < listforfloodfill.size(); cleani++) {
                                floodFill(meterPic, listforfloodfill.get(cleani), Color.BLACK, Color.WHITE);
                            }
                        }
                        //cut sides again to update info
                        cutpoint = cutsides(meterPic, listofstartfin, listofstartfincleaning);
                        //cut the sides after
                        meterPic = Bitmap.createBitmap(meterPic, (int) (cutpoint.x), (int) (0), (int) (cutpoint.y - cutpoint.x), (int) (meterPic.getHeight()));
                    } catch (Exception e) {

                    }
                    //bigger
                    meterPic = Bitmap.createScaledBitmap(meterPic,  (meterPic.getWidth() * OCR_PICTURE_SIZE),  (meterPic.getHeight() * OCR_PICTURE_SIZE), false);

                }
            }
        }



        private void fillfloodfilllist(double left, double right, double top, double down, double pixels, Bitmap copy, List<Point> listforfloodfill) {
            double xsize = right - left + 1;
            double ysize = down - top + 1;
            List<Double> pixelssize = new ArrayList<Double>();
            //Log.i("inside", "gotin");


            //pass on all the object
            for (int y = (int) top; y < top + ysize; y++) {

                for (int x = (int) left; x < left + xsize; x++) {

                    if (copy.getPixel(x, y) == Color.BLACK) {
                        //save point of floodfill
                        listforfloodfill.add(new Point(x, y));
                        floodFill(copy, new Point(x, y), Color.BLACK, Color.WHITE);

                        //save size after floodfill
                        double countpixels = 0;
                        for (int y2 = (int) top; y2 < top + ysize; y2++) {
                            for (int x2 = (int) left; x2 < left + xsize; x2++) {
                                if (copy.getPixel(x2, y2) == Color.BLACK)
                                    countpixels++;
                            }
                        }
                        //save and change overall pixels
                        Log.i("countpixels", Double.toString(countpixels));
                        Log.i("pixels", Double.toString(pixels));
                        pixelssize.add(pixels - countpixels);
                        pixels = countpixels;
                        //init loop
                        y = (int) top;
                        x = (int) left - 1;
                    }

                }
            }
            //delete highest pixel size(what need to be left) from the list
            double maxpixels = 0;
            int itodelete = 0;
            for (int i = 0; i < pixelssize.size(); i++) {
                if (pixelssize.get(i) > maxpixels) {
                    maxpixels = pixelssize.get(i);
                    itodelete = i;
                }
            }
            listforfloodfill.remove(itodelete);


        }

        private Point cutsides(Bitmap meterPic, List<objectleftrighttopdown> listofstartfin, List<objectleftrighttopdown> listofstartfincleaning) {

            objectleftrighttopdown startfin = null; // change here

//fill rows if even got 1 pixel in black
            int height = meterPic.getHeight();
            boolean blackandwhite[] = new boolean[meterPic.getWidth()];
            for (int i = 0; i < meterPic.getWidth(); i++) {

                for (int j = 0; j < height; j++) {
                    if (meterPic.getPixel(i, j) == Color.BLACK) {
                        blackandwhite[i] = true;
                        break;
                    }

                }
            }
            //fill list
            for (int i = 0; i < blackandwhite.length; i++) {
                //if white black or start of pic black(start)
                if (i == 0) {
                    if (blackandwhite[i] == true) {
                        startfin = new objectleftrighttopdown();
                        startfin.left = i;
                    }
                } else if (blackandwhite[i] == true && blackandwhite[i - 1] == false) {
                    startfin = new objectleftrighttopdown();
                    startfin.left = i;
                }
                //if black white or end of pic black(fin)
                if (i == blackandwhite.length - 1) {
                    if (blackandwhite[i] == true) {
                        startfin.right = i;
                        Log.i("x", Double.toString(startfin.left));
                        Log.i("y", Double.toString(startfin.right));
                        listofstartfin.add(startfin);
                    }
                } else if (i != 0) {
                    if (blackandwhite[i] == false && blackandwhite[i - 1] == true) {
                        startfin.right = i;
                        Log.i("x", Double.toString(startfin.left));
                        Log.i("y", Double.toString(startfin.right));
                        listofstartfin.add(startfin);
                    }
                }

            }
//ELIMINATE NOT NUMBERS using % of black pixels

            for (int listi = 0; listi < listofstartfin.size(); listi++) {
                int width = (int) (listofstartfin.get(listi).right - listofstartfin.get(listi).left);
                int miny = height;
                int maxy = 0;
                double allpixels = height * width;
                double blackpixels = 0;
                for (int i = 0; i < height; i++) {
                    for (int j = (int) (listofstartfin.get(listi).left); j < listofstartfin.get(listi).right; j++) {
                        if (meterPic.getPixel(j, i) == Color.BLACK) {
                            blackpixels++;
                            if (i < miny)
                                miny = i;
                            if (i > maxy)
                                maxy = i;
                        }
                    }
                }
                //set top down
                listofstartfin.get(listi).top = miny;
                listofstartfin.get(listi).down = maxy;
                listofstartfin.get(listi).pixels = blackpixels;
                //end of pic scan
                //  Log.i("pixels",Double.toString(blackpixels));
                //  Log.i("min",Integer.toString(miny));
                //  Log.i("max",Integer.toString(maxy));
                // Log.i("start",Double.toString(listofstartfin.get(listi).x));
                // Log.i("fin",Double.toString(listofstartfin.get(listi).y));
//requirments for delete
                if (maxy - miny < height * ((double)(CLEAN_LOWER_GAP_PRECENTAGE) / 100) || maxy - miny > height * ( (double)(CLEAN_HIGHER_GAP_PRECENTAGE) / 100) || blackpixels / allpixels < ( (double)(CLEAN_LOWER_TOTAL_PIXEL_PRECENTAGE) / 100) || blackpixels / allpixels > ( (double)(CLEAN_HIGHER_TOTAL_PIXEL_PRECENTAGE) / 100)) {
                    listofstartfincleaning.add(listofstartfin.get(listi));
                    listofstartfin.remove(listi);
                    if (listofstartfin.size() != listi)
                        listi--;
                }

            }
            //remove whats not in pic
            //  Log.i("listsize",Integer.toString(listofstartfincleaning.size()));
            if (listofstartfincleaning.size() != 0 && listofstartfin.size() != 0) {
                for (int listi = 0; listi < listofstartfincleaning.size(); listi++) {
                    double startpoint = listofstartfincleaning.get(listi).left;
                    if (!(startpoint > listofstartfin.get(0).left && startpoint < listofstartfin.get(listofstartfin.size() - 1).right))
                        listofstartfincleaning.remove(listi);
                }
            }
            //clean whats not numbers inside of pic
            if (listofstartfincleaning.size() != 0 && listofstartfin.size() != 0) {
                for (int listi = 0; listi < listofstartfincleaning.size(); listi++) {
                    for (int i = 0; i < height; i++) {
                        for (int j = (int) (listofstartfincleaning.get(listi).left); j < listofstartfincleaning.get(listi).right; j++) {
                            if (meterPic.getPixel(j, i) == Color.BLACK) {
                                meterPic.setPixel(j, i, Color.WHITE);
                            }
                        }
                    }
                }

            }
//check
            Log.i("lengthofthearray", Integer.toString(listofstartfin.size()));
            //  if(listofstartfin.size()>0)
            //     meterPic = Bitmap.createBitmap(meterPic, (int) (listofstartfin.get(0).left), (int) (0),(int) (listofstartfin.get(listofstartfin.size()-1).right-listofstartfin.get(0).left), (int) (meterPic.getHeight()));

            return new Point(listofstartfin.get(0).left, listofstartfin.get(listofstartfin.size() - 1).right);
        }

        private void getcutpoints(double[] allslopes, Point[] pt1, Point[] pt2, Mat lines, Mat myMat, int[] return2ioffoundlines, Bitmap meterPic) {
            int[] linelength = new int[lines.cols()];
            int biggestnogapline = 0, count = 0;
            double ypos = 0;
            for (int iobj = 0; iobj < lines.cols(); iobj++) {
                for (int xpos = 0; xpos < meterPic.getWidth(); xpos++) {
                    ypos = allslopes[iobj] * (xpos - pt2[iobj].x) + pt2[iobj].y;
                    try {
                        if (meterPic.getPixel(xpos, (int) ypos) == Color.BLACK) {//if black pixel
                            if (count > biggestnogapline)
                                biggestnogapline = count;
                            count = 0;
                        } else {//if white pixel
                            count++;
                        }
                    } catch (Exception e) {//if take pixel outside of meterpic X that obj and go to next obj
                        biggestnogapline = 0;//X object cause he wont be selected as max
                        //go next obj
                        xpos = meterPic.getWidth();
                    }

                }
                //save final result of gapline for this obj
                linelength[iobj] = biggestnogapline;
                //reset
                biggestnogapline=0;
            }
            //select 2 highest gaplines as found lines and not the same line
            boolean sameline=true;
            while(sameline){
                return2ioffoundlines[0] = 0;
                return2ioffoundlines[1] = 0;
                int max1,max2;
                max1=0;
                max2=0;
                for (int i = 0; i < linelength.length; i++) {
                    if (linelength[i] > max1) {
                        return2ioffoundlines[1] = return2ioffoundlines[0];
                        max2=max1;
                        return2ioffoundlines[0] = i;
                        max1 = linelength[i];
                    } else if (linelength[i] > max2) {
                        return2ioffoundlines[1] = i;
                        max2 = linelength[i];
                    }
                }
                //after found check ylength so wont be same line
                double ypos1,ypos2,thegap;
                ypos1 = allslopes[return2ioffoundlines[0]] * (meterPic.getWidth()/2 - pt2[return2ioffoundlines[0]].x) + pt2[return2ioffoundlines[0]].y;
                ypos2 = allslopes[return2ioffoundlines[1]] * (meterPic.getWidth()/2 - pt2[return2ioffoundlines[1]].x) + pt2[return2ioffoundlines[1]].y;
                thegap=ypos1-ypos2;
                if(thegap<0)//get gap not as minus if pos2 is bigger
                    thegap*=-1;
                if(thegap>meterPic.getHeight()*((double)(LINES_GAP)/100))
                    sameline=false;
                else {//delete 1 of copies
                    //no lines with required gap
                    if(linelength[return2ioffoundlines[0]]==0 || linelength[return2ioffoundlines[1]]==0){
                        Toast.makeText(getApplicationContext(), "cant find lines with such gap",
                                Toast.LENGTH_LONG).show();
                        sameline=false;
                    }
                    linelength[return2ioffoundlines[1]] = 0;
                }

            }

        }

        public void floodFill(Bitmap image, Point node, int targetColor,
                              int replacementColor) {
            int width = image.getWidth();
            int height = image.getHeight();
            int target = targetColor;
            int replacement = replacementColor;
            if (target != replacement) {
                Queue<Point> queue = new LinkedList<Point>();
                do {
                    int x = (int) node.x;
                    int y = (int) node.y;
                    while (x > 0 && image.getPixel(x - 1, y) == target) {
                        x--;
                    }
                    boolean spanUp = false;
                    boolean spanDown = false;
                    while (x < width && image.getPixel(x, y) == target) {
                        image.setPixel(x, y, replacement);
                        if (!spanUp && y > 0 && image.getPixel(x, y - 1) == target) {
                            queue.add(new Point(x, y - 1));
                            spanUp = true;
                        } else if (spanUp && y > 0
                                && image.getPixel(x, y - 1) != target) {
                            spanUp = false;
                        }
                        if (!spanDown && y < height - 1
                                && image.getPixel(x, y + 1) == target) {
                            queue.add(new Point(x, y + 1));
                            spanDown = true;
                        } else if (spanDown && y < height - 1
                                && image.getPixel(x, y + 1) != target) {
                            spanDown = false;
                        }
                        x++;
                    }
                } while ((node = queue.poll()) != null);
            }
        }


        private void savepointsandslopes(double[] allslopes, Point[] pt1, Point[] pt2, Mat lines, Mat myMat) {
            double[] data;
            double rho, theta;
            for (int i = 0; i < lines.cols(); i++) {
                pt1[i] = new Point();
                pt2[i] = new Point();
            }
            double a, b;
            double x0, y0;
            double slope = 0;

            for (int i = 0; i < lines.cols(); i++) {
                data = lines.get(0, i);
                rho = data[0];
                theta = data[1];
                a = Math.cos(theta);
                b = Math.sin(theta);
                x0 = a * rho;
                y0 = b * rho;
                pt1[i].x = Math.round(x0 + 1000 * (-b));
                pt1[i].y = Math.round(y0 + 1000 * a);
                pt2[i].x = Math.round(x0 - 1000 * (-b));
                pt2[i].y = Math.round(y0 - 1000 * a);
                double x = pt2[i].x - pt1[i].x;
                double y = pt2[i].y - pt1[i].y;
                slope = y / x;
                Log.i("slopes", Double.toString(slope));
                allslopes[i] = slope;

            }
        }


        private void recyclefunc(Bitmap bitmap) {
            // TODO Auto-generated method stub
            bitmap.recycle();
            bitmap = null;
        }


        protected void drawing(Canvas canvas) throws IOException {
            if (loop == 10) {
//change to tiff
            /*    TIFFImageWriterSpi spi = new TIFFImageWriterSpi();
                ImageWriter writer = spi.createWriterInstance();
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_DISABLED);

                ImageOutputStream ios = ImageIO.createImageOutputStream(new File("output.tif"));
                writer.setOutput(ios);
                writer.write(null, new IIOImage(bmp, null, null), param);*/


                //import all files
                importfile("eng.cube.bigrams");
                importfile("eng.cube.fold");
                importfile("eng.cube.lm");
                importfile("eng.cube.nn");
                importfile("eng.cube.params");
                importfile("eng.cube.size");
                importfile("eng.cube.word-freq");
                importfile("eng.tesseract_cube.nn");
                importfile("eng.traineddata");
            }


            int numofpicplus1 = 7;

            if (loop == 12) {
                String DATA_PATH = "";



                DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/OCRfiles/";




                TessBaseAPI baseApi5 = new TessBaseAPI();
                baseApi5.init(DATA_PATH, "eng", 3);
                baseApi5.setImage(ex3);
                rtext5 = baseApi5.getUTF8Text();
                baseApi5.end();

                TessBaseAPI baseApi6 = new TessBaseAPI();
                baseApi6.init(DATA_PATH, "eng", 3);
                baseApi6.setImage(meterPic);
                rtext6 = baseApi6.getUTF8Text();
                baseApi6.end();



            }
            if (loop > 12) {

                if (changepic % numofpicplus1 == 0) {
                    canvas.drawColor(Color.BLACK);
                    canvas.drawBitmap(original, cwidth / 2 - original.getWidth() / 2 + movex + savex, cheight / 2 - original.getHeight() / 2 + movey + savey, null);

                }
                if (changepic % numofpicplus1 == 1) {
                    canvas.drawColor(Color.BLACK);
                    canvas.drawBitmap(originalgray, cwidth / 2 - originalgray.getWidth() / 2 + movex + savex, cheight / 2 - originalgray.getHeight() / 2 + movey + savey, null);

                }
                if (changepic % numofpicplus1 == 2) {
                    canvas.drawColor(Color.BLACK);
                    canvas.drawBitmap(ex1, cwidth / 2 - ex1.getWidth() / 2 + movex + savex, cheight / 2 - ex1.getHeight() / 2 + movey + savey, null);

                }
                if (changepic % numofpicplus1 == 3) {
                    canvas.drawColor(Color.BLACK);
                    canvas.drawBitmap(ex1_2, cwidth / 2 - ex1_2.getWidth() / 2 + movex + savex, cheight / 2 - ex1_2.getHeight() / 2 + movey + savey, null);

                }
                if (changepic % numofpicplus1 == 4 && linesfound) {
                    canvas.drawColor(Color.BLACK);
                    canvas.drawBitmap(ex2, cwidth / 2 - ex2.getWidth() / 2 + movex + savex, cheight / 2 - ex2.getHeight() / 2 + movey + savey, null);

                }
                if (changepic % numofpicplus1 == 5 && linesfound) {
                    canvas.drawColor(Color.BLACK);
                    canvas.drawBitmap(ex3, cwidth / 2 - ex3.getWidth() / 2 + movex + savex, cheight / 2 - ex3.getHeight() / 2 + movey + savey, null);
                    domwanted = rtext5;
                    TextPaint p = new TextPaint();

                    p.setTextSize((float) (cwidth*0.05));
                    p.setARGB(255, 126, 255, 249);
                    canvas.drawText(domwanted, (float) (cwidth*0.1), (float) (cheight*0.1), p);
                }
                if (changepic % numofpicplus1 == 6) {
                    canvas.drawColor(Color.BLACK);
                    canvas.drawBitmap(meterPic, cwidth / 2 - meterPic.getWidth() / 2 + movex + savex, cheight / 2 - meterPic.getHeight() / 2 + movey + savey, null);
                    domwanted = rtext6;
                    TextPaint p = new TextPaint();

                    p.setTextSize((float) (cwidth*0.05));
                    p.setARGB(255, 126, 255, 249);
                    canvas.drawText(domwanted, (float) (cwidth*0.1), (float) (cheight*0.1), p);
                }

                // Log.i("lastoutput", fiveorsixnumbers);


            }
            loop++;


        }

        private void sendSMS(String phoneNumber, String message) {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, null, null);
        }


        public void pause() {
            isItOK = false;
            while (true) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            }
            t = null;
        }

        public void resume() {
            isItOK = true;
            t = new Thread(this);
            t.start();
        }
    }

    public void copyfiles(InputStream in, File dst) throws IOException {
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    private void importfile(String name) throws IOException {
        //create empty file with right name to write to
        File new_file = new File(Environment.getExternalStorageDirectory() + "/OCRfiles/tessdata" + File.separator + name);
        try {
            new_file.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d("Create File", "File exists?" + new_file.exists());


        //copy data from file in proj
        AssetManager am = getAssets();
        InputStream inputstream = am.open(name);

        try {
            if (new_file.length() == 0)
                copyfiles(inputstream, new_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:


                pressedx = event.getX();
                pressedy = event.getY();
                //double tap, double quick move also work fix latter
                if (counttochange == 0)
                    startcount = System.currentTimeMillis();

                if (counttochange >= 1) {
                    endcount = System.currentTimeMillis();
                    if (endcount - startcount < 360) {
                        changepic++;
                        counttochange = -1;
                    } else {
                        counttochange = 0;
                        startcount = System.currentTimeMillis();
                    }
                }

                counttochange++;

                break;
            case MotionEvent.ACTION_UP:


                savex += movex;
                savey += movey;
                movex = 0;
                movey = 0;
                break;
            case MotionEvent.ACTION_MOVE:

                movex = event.getX() - pressedx;
                movey = event.getY() - pressedy;
                break;
        }
        return true;
    }


}

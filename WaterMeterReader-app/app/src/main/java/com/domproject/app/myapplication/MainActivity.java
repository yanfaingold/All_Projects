package com.domproject.app.myapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
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
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.opencv.android.AsyncServiceHelper.InstallService;

public class MainActivity extends Activity implements View.OnClickListener {


    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {


                }
                break;

                default: {

                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    //what i need
    static Uri capturedImageUri = null;
    boolean phototaken = false,opencvloaded = false,dialogactive = false;
    Bitmap cameraphoto = null, originalcopy = null,idphoto=null;
    //can change in gui settings
    int MAX_PICTURE_SIZE,PRE_DILATE_SIZE,PRE_ERODE_SIZE,NUM_OF_CANDIDATES,LINES_GAP, DILATE_SIZE, ERODE_SIZE, LINES_FOUND_DRAW_SIZE, CLEAN_LOWER_GAP_PRECENTAGE, CLEAN_HIGHER_GAP_PRECENTAGE, CLEAN_LOWER_TOTAL_PIXEL_PRECENTAGE, CLEAN_HIGHER_TOTAL_PIXEL_PRECENTAGE,OCR_PICTURE_SIZE;
    int ID_LINES_SIZE,ID_PICTURE_SIZE;
    float ID_DILATE,ID_ERODE;
    int language;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        SharedPreferences prefs;
        //default settings
        defaultsettings();

        //get init location
        String locale=getResources().getConfiguration().locale.getISO3Country();
        if(locale.equals("ENG"))
            language=1;
        else if(locale.equals("ISR"))
            language=2;
        else if(locale.equals("RUS"))
            language=3;
        else
            language=1;
        //setlanguage
        File file = new File("/data/data/com.domproject.app.myapplication/shared_prefs/Mylanguage.xml");
        if(file.exists()) {
            prefs = getApplicationContext().getSharedPreferences("Mylanguage", 1);

            language = prefs.getInt("currentlanguage", -1);
        }

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

        //id loads
        File fidlinessize = new File("/data/data/com.domproject.app.myapplication/shared_prefs/idlinessize.xml");
        if (fidlinessize.exists()) {
            prefs = getApplicationContext().getSharedPreferences("idlinessize", 1);

            ID_LINES_SIZE = prefs.getInt("savedidlinessize", -1);

        }
        File fiddilate = new File("/data/data/com.domproject.app.myapplication/shared_prefs/iddilate.xml");
        if (fiddilate.exists()) {
            prefs = getApplicationContext().getSharedPreferences("iddilate", 1);

            ID_DILATE = prefs.getFloat("savediddilate", -1);

        }
        File fiderode = new File("/data/data/com.domproject.app.myapplication/shared_prefs/iderode.xml");
        if (fiderode.exists()) {
            prefs = getApplicationContext().getSharedPreferences("iderode", 1);

            ID_ERODE = prefs.getFloat("savediderode", -1);

        }
        File fidpicsize = new File("/data/data/com.domproject.app.myapplication/shared_prefs/idpicsize.xml");
        if (fidpicsize.exists()) {
            prefs = getApplicationContext().getSharedPreferences("idpicsize", 1);

            ID_PICTURE_SIZE = prefs.getInt("savedidpicsize", -1);

        }
        //create folders
        createmainfolders();

        //create opencv manager file and ask user to install it if not on phone
        opencvloaded = installopencvmanagerifnotexist();

        //import all OCR files
        try {
            importfile("eng.cube.bigrams");
            importfile("eng.cube.fold");
            importfile("eng.cube.lm");
            importfile("eng.cube.nn");
            importfile("eng.cube.params");
            importfile("eng.cube.size");
            importfile("eng.cube.word-freq");
            importfile("eng.tesseract_cube.nn");
            importfile("eng.traineddata");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "some OCR files failed to be added",
                    Toast.LENGTH_LONG).show();
        }
        //Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        Button one = (Button) findViewById(R.id.button1);
        one.setOnClickListener(this);
        Button two = (Button) findViewById(R.id.button2);
        two.setOnClickListener(this);
        Button three = (Button) findViewById(R.id.button3);
        three.setOnClickListener(this);
        Button four = (Button) findViewById(R.id.button4);
        four.setOnClickListener(this);
        Button five = (Button) findViewById(R.id.button5);
        five.setOnClickListener(this);
        Button six = (Button) findViewById(R.id.button6);
        six.setOnClickListener(this);

        ImageButton ione = (ImageButton) findViewById(R.id.imageButton1);
        ione.setOnClickListener(this);
        ImageButton itwo = (ImageButton) findViewById(R.id.imageButton2);
        itwo.setOnClickListener(this);

        ImageButton ithree = (ImageButton) findViewById(R.id.imageButton3);
        ithree.setOnClickListener(this);

        ImageButton ifour = (ImageButton) findViewById(R.id.imageButton4);
        ifour.setOnClickListener(this);



        EditText edit = (EditText) findViewById(R.id.editText);
        //save main layout language strings
        switch(language) {
            case 1:
                //languageimageset
                ithree.setImageResource(R.drawable.united_kingdom);
                //in layout
                edit.setHint(R.string.eng_phonehint);
                one.setText(R.string.eng_takephoto);
                two.setText(R.string.eng_redandsend);
                three.setText(R.string.eng_choosefile);
                four.setText(R.string.eng_advancedsettings);
                five.setText(R.string.eng_idcrop);
                six.setText(R.string.eng_stepbystep);

                break;

            case 2:
                //languageimageset
                ithree.setImageResource(R.drawable.israel);
                //in layout
                edit.setHint(R.string.heb_phonehint);
                one.setText(R.string.heb_takephoto);
                two.setText(R.string.heb_redandsend);
                three.setText(R.string.heb_choosefile);
                four.setText(R.string.heb_advancedsettings);
                five.setText(R.string.heb_idcrop);
                float sizeidcropheb=five.getTextSize();
                five.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeidcropheb - 10);
                six.setText(R.string.heb_stepbystep);

                break;

            case 3:
                //languageimageset
                ithree.setImageResource(R.drawable.russia);
                //in layout
                edit.setHint(R.string.rus_phonehint);
                one.setText(R.string.rus_takephoto);
                float sizetakephoto=one.getTextSize();
                one.setTextSize(TypedValue.COMPLEX_UNIT_PX,sizetakephoto-13);
                two.setText(R.string.rus_redandsend);
                three.setText(R.string.rus_choosefile);
                float sizechoosefile=three.getTextSize();
                three.setTextSize(TypedValue.COMPLEX_UNIT_PX,sizechoosefile-10);
                four.setText(R.string.rus_advancedsettings);
                five.setText(R.string.rus_idcrop);
                float sizeidcroprus=five.getTextSize();
                five.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeidcroprus - 13);
                six.setText(R.string.rus_stepbystep);

                break;

            default:

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
        //id
        ID_LINES_SIZE=5;
        ID_DILATE=2;
        ID_ERODE=2;
        ID_PICTURE_SIZE=2;
    }

    private void takephotowithcamera() {
        File file = new File(Environment.getExternalStorageDirectory(), "temp.png");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        capturedImageUri = Uri.fromFile(file);
        Intent cameraIntent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                capturedImageUri);
        int TAKE_PHOTO = 0;
        startActivityForResult(cameraIntent, TAKE_PHOTO);
    }

    private boolean installopencvmanagerifnotexist() {
        boolean opencvappinstalled = false;
        int countstuck = 0;
        try {
            importfile("org.opencv.engine.apk");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //check if installed
        boolean installed = appInstalledOrNot("org.opencv.engine");
        //install it
        if (installed == false) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/OCRfiles/tessdata/" + "org.opencv.engine.apk")), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            while (!installed) {
                installed = appInstalledOrNot("org.opencv.engine");
                countstuck++;
                if (countstuck > 200) {
                    Toast.makeText(getApplicationContext(), "cant install opencv manager",
                            Toast.LENGTH_LONG).show();
                }
            }

            //installation confirmed
            opencvappinstalled = true;

        } else
            opencvappinstalled = true;

        //load opencv
        if (opencvappinstalled)
            if (!initOpenCV(OpenCVLoader.OPENCV_VERSION_2_4_2, this, mOpenCVCallBack)) {

                return false;
            }
        return opencvappinstalled;
    }

    private void createmainfolders() {
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

        int CAMERA_REQUEST = 0;
        int FILE_REQUEST = 1;
        int CUSTOM_CROP = 2;
        int ID_CROP=3;
        int size = 1;
        boolean success = false;

        InputStream imageStream = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        //take photo
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            //set bitmap took from camera to max pixels

            while (success == false) {
                options.inSampleSize = size;//should make a functions that increae it by 1 if its an error(too large picture)
                try {//if pic too big
                    cameraphoto = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/temp.png", options);

                    if (cameraphoto.getWidth() * cameraphoto.getHeight() < MAX_PICTURE_SIZE)//max size check
                        success = true;
                    else
                        size++;//if too big for hough lines
                } catch (Exception e) {
                    size++;//take smaller sample
                }
            }


        }
        //choose file
        if (requestCode == FILE_REQUEST && resultCode == RESULT_OK) {


            Uri selectedImage = data.getData();

            try {
                imageStream = getContentResolver().openInputStream(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("error", "error");
            }
            //set bitmap took from file to max pixels
            Bitmap temp = BitmapFactory.decodeStream(imageStream);
            saveToInternalSorage(temp, "temp.png");
            while (success == false) {
                options.inSampleSize = size;//should make a functions that increae it by 1 if its an error(too large picture)
                try {//if pic too big
                    cameraphoto = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/temp.png", options);
                    ;

                    if (cameraphoto.getWidth() * cameraphoto.getHeight() < MAX_PICTURE_SIZE)//max size check
                        success = true;
                    else
                        size++;//if too big for hough lines
                } catch (Exception e) {
                    size++;//take smaller sample
                }
            }


        }
        if (resultCode == RESULT_OK && requestCode!=ID_CROP) {
            if (requestCode != CUSTOM_CROP) {
                savepicandshowrotatebuttons();
                bitmapcrop();
            } else {//do after custom crop returned
                cameraphoto = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/temp.png");
                savepicandshowrotatebuttons();
            }
        }
        //after crop id
        if (resultCode == RESULT_OK && requestCode==ID_CROP) {


        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void bitmapcrop() {
        Intent i = new Intent(this, CropActivity.class);
        i.putExtra("whichfiletosave", 0);
        startActivityForResult(i, 2);
    }



    private void savepicandshowrotatebuttons() {
        // createoriginalcopy
        originalcopy = cameraphoto;
        //save to file too
        savepic();//dont need when custom crop returns change latter mybe
        //set visualy
        ImageView theimage = (ImageView) findViewById(R.id.imageView);
        theimage.setImageBitmap(originalcopy);
        makeallvisible();
        refreshgallary();

    }

    private void refreshgallary() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File mypath = new File(Environment.getExternalStorageDirectory(),"temp.png");
            Uri contentUri = Uri.fromFile(mypath);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        } else {
            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://"
                            + Environment.getExternalStorageDirectory())));
        }
    }

    private String saveToInternalSorage(Bitmap bitmapImage,String path) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        // File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(Environment.getExternalStorageDirectory(),path);
        if (mypath.exists())
            mypath.delete();
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Environment.getExternalStorageDirectory().getAbsolutePath();
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

    //ndk
   /* public native String HelloJNI();
    static
    {
        System.loadLibrary("HelloJNI");
    }*/

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if(!dialogactive)
            switch (v.getId()) {

                case R.id.button1:
                    dialogactive=true;
                    //take pic
                    takephotowithcamera();
                    phototaken = true;
                    dialogactive=false;
                    break;

                case R.id.button2:
                    dialogactive=true;
                    //get the number input right
                    boolean validphonenumber = false;
                    EditText edit = (EditText) findViewById(R.id.editText);
                    final String phonenumber = edit.getText().toString();
                    try {
                        Integer.parseInt(phonenumber);
                        validphonenumber = true;
                    } catch (Exception e) {
                        validphonenumber = false;
                    }
                    if (phonenumber == "")
                        validphonenumber = false;

                    if (cameraphoto != null && opencvloaded && phototaken && validphonenumber) {
                        //create the loading
                        final ProgressDialog loading;
                        loading = ProgressDialog.show(MainActivity.this, "Reading and Sending SMS",
                                "Please Wait", true);
                        new Thread(new Runnable() {
                            public void run() {
                                cameraphoto = originalcopy;
                                //get bitmap
                                try {
                                    idphoto = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/id.png");
                                }catch(Exception e){

                                }
                                inputvalidstartalgoritm();
                                if(idphoto!=null)
                                idcleanalgoritm();

                                sendOCRtoread(phonenumber);
                                loading.dismiss();
                                return;
                            }
                        }).start();
                    } else
                        Toast.makeText(getApplicationContext(), "cant do,something is missing",
                                Toast.LENGTH_LONG).show();
                    dialogactive=false;
                    break;
                case R.id.button3:
                    dialogactive=true;
                    int SELECT_PHOTO = 1;
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                    phototaken = true;
                    dialogactive=false;
                    break;

                case R.id.button4:
                    dialogactive=true;
                    openalertsettings();

                    break;
                case R.id.button5:
                    dialogactive=true;
                    Intent i = new Intent(this, CropActivity.class);
                    i.putExtra("whichfiletosave", 1);
                    startActivityForResult(i, 3);
                    dialogactive=false;
                    break;
                case R.id.button6:
                    dialogactive=true;
                    Intent ishow = new Intent(this, OtherActivity.class);
                    startActivity(ishow);
                    dialogactive=false;
                    break;
                case R.id.imageButton1:
                    dialogactive=true;
                    if (phototaken) {
                        originalcopy = RotateBitmap(originalcopy, (float) 90);
                        savepic();
                        //set visualy
                        ImageView theimage = (ImageView) findViewById(R.id.imageView);
                        theimage.setImageBitmap(originalcopy);
                    }
                    dialogactive=false;
                    break;
                case R.id.imageButton2:
                    dialogactive=true;
                    if (phototaken) {
                        originalcopy = RotateBitmap(originalcopy, (float) -90);
                        savepic();
                        //set visualy
                        ImageView theimage = (ImageView) findViewById(R.id.imageView);
                        theimage.setImageBitmap(originalcopy);
                    }
                    dialogactive=false;
                    break;
                case R.id.imageButton3:
                    dialogactive=true;
                    openlanguagealert();
                    dialogactive=false;
                    break;
                case R.id.imageButton4:
                    dialogactive=true;
                    //exit button activate
                    System.exit(1);
                    dialogactive=false;
                    break;
                default:
                    break;
            }
    }



    private void idcleanalgoritm() {

        idphoto=idphoto.copy(Bitmap.Config.ARGB_8888, true);
        //canny
        Mat idmat=new Mat();
        Mat temp = new Mat();
        Utils.bitmapToMat(idphoto, idmat);
        Imgproc.cvtColor(idmat, idmat, Imgproc.COLOR_RGB2GRAY);
        double otsu_thresh_val = Imgproc.threshold(idmat, temp, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        double high_thresh_val = otsu_thresh_val;
        double lower_thresh_val = otsu_thresh_val * 0.5;

        Imgproc.Canny(idmat, idmat, lower_thresh_val, high_thresh_val);
        Imgproc.dilate(idmat, idmat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(ID_DILATE, ID_DILATE)));
        Imgproc.erode(idmat, idmat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(ID_ERODE, ID_ERODE)));
        //draw black lines for floodfill
        Scalar s = new Scalar(0, 0, 0);
        Core.line(idmat, new Point(0, 0), new Point(idphoto.getWidth(),0), s,  (ID_LINES_SIZE));
        Core.line(idmat, new Point(0,idphoto.getHeight()-1), new Point(idphoto.getWidth(),idphoto.getHeight()-1), s,  (ID_LINES_SIZE));
        Utils.matToBitmap(idmat,idphoto);

        //fill with white
        try {

             floodFill(idphoto, new Point(0, 0), Color.BLACK, Color.WHITE);

        } catch (Exception e) {

        }
        //bigger
        idphoto = Bitmap.createScaledBitmap(idphoto, (idphoto.getWidth() * ID_PICTURE_SIZE), (idphoto.getHeight() * ID_PICTURE_SIZE), false);
        //for check
        saveToInternalSorage(idphoto,"idsendedtoocr.png");
    }

    SharedPreferences prefs;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void openlanguagealert() {


        View v=this.findViewById(android.R.id.content).getRootView();

        final AlertDialog alert = new AlertDialog.Builder(this,android.R.style.Theme_Holo).create();

        //centered title
        TextView title = new TextView(this);

        title.setBackgroundColor(Color.rgb(41, 41, 41));
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.rgb(102, 205, 248));
        title.setTextSize(20);






        final LinearLayout ll=new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        //english button
        final Button en=new Button(this);

        en.setTypeface(null, Typeface.BOLD);
        en.setTextColor(Color.WHITE);



        en.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                //shared prefernces save
                if (v == en && language != 1) {

                    prefs = getApplicationContext().getSharedPreferences("Mylanguage", 1);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("currentlanguage", 1);
                    editor.commit();


                    Intent firstintent = new Intent(v.getContext(), MainActivity.class);
                    finish();
                    System.gc();
                    startActivity(firstintent);
                    alert.dismiss();
                }
            }

        });

        //russian button
        final Button ru=new Button(this);

        ru.setTypeface(null, Typeface.BOLD);
        ru.setTextColor(Color.WHITE);


        ru.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                //shared prefernces save
                if (v == ru && language != 3) {

                    prefs = getApplicationContext().getSharedPreferences("Mylanguage", 1);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("currentlanguage", 3);
                    editor.commit();

                    //reopen

                    Intent firstintent = new Intent(v.getContext(), MainActivity.class);
                    finish();
                    System.gc();
                    startActivity(firstintent);
                    alert.dismiss();
                }
            }
        });

        //hebrew button
        final Button he=new Button(this);

        he.setTypeface(null, Typeface.BOLD);
        he.setTextColor(Color.WHITE);


        he.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                //shared prefernces save
                if (v == he && language != 2) {

                    prefs = getApplicationContext().getSharedPreferences("Mylanguage", 1);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("currentlanguage", 2);
                    editor.commit();
                    //reopen

                    Intent firstintent = new Intent(v.getContext(), MainActivity.class);
                    finish();
                    System.gc();
                    startActivity(firstintent);

                    alert.dismiss();
                }

            }
        });
        switch (language){
            case 1:
                title.setText(R.string.eng_languagetitle);
                en.setText(R.string.eng_english);
                ru.setText(R.string.eng_russian);
                he.setText(R.string.eng_hebrew);
                break;
            case 2:
                title.setText(R.string.heb_languagetitle);
                en.setText(R.string.heb_english);
                ru.setText(R.string.heb_russian);
                he.setText(R.string.heb_hebrew);
                break;
            case 3:
                title.setText(R.string.rus_languagetitle);
                en.setText(R.string.rus_english);
                ru.setText(R.string.rus_russian);
                he.setText(R.string.rus_hebrew);
                break;
            default:
        }
        alert.setCustomTitle(title);
        ll.addView(en);
        ll.addView(ru);
        ll.addView(he);
        alert.setView(ll);

        alert.show();
        //adjust size of languge alert
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(alert.getWindow().getAttributes());
        lp.width = v.getWidth();
        lp.height = v.getHeight();
        lp.x=0;
        lp.y= (int) (v.getHeight()*0.01);
        alert.getWindow().setAttributes(lp);
    }

    private void savepic() {
        //save picture
        if (originalcopy != null)
            saveToInternalSorage(originalcopy,"temp.png");

    }

    private void makeallvisible() {
        ImageButton ione = (ImageButton) findViewById(R.id.imageButton1);
        ImageButton itwo = (ImageButton) findViewById(R.id.imageButton2);
        ione.setVisibility(View.VISIBLE);
        itwo.setVisibility(View.VISIBLE);
        Button five = (Button) findViewById(R.id.button5);
        five.setVisibility(View.VISIBLE);
        Button six = (Button) findViewById(R.id.button6);
        six.setVisibility(View.VISIBLE);
    }

    EditText editpicsize, editpredilate, editpreerode, editdilate, editerode, editnumofcandidates,editlinesgap, editlinesfounddrawsize, editlowergapremove, edithighergapremove, editlowertotalremove, edithighertotalremove,editocrpicsize,editidlinessize,editiddilate,editiderode,editidpicsize;

    private void openalertsettings() {

        AlertDialog.Builder alertd = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo));
        //centered title
        TextView title = new TextView(this);
        title.setBackgroundColor(Color.rgb(41, 41, 41));
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.rgb(102, 205, 248));
        title.setTextSize(20);


        //linear layout
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        //pre rotate

        TextView pre_rotate = new TextView(this);
        pre_rotate.setGravity(Gravity.CENTER);
        pre_rotate.setTextColor(Color.WHITE);

        pre_rotate.setBackgroundColor(Color.GRAY);
        float size=pre_rotate.getTextSize();
        pre_rotate.setTextSize(TypedValue.COMPLEX_UNIT_PX, size + 15);


        TextView picsize = new TextView(this);
        picsize.setGravity(Gravity.CENTER);
        picsize.setTextColor(Color.WHITE);




        editpicsize = new EditText(this);
        editpicsize.setGravity(Gravity.CENTER);
        editpicsize.setTextColor(Color.parseColor("#FFFFFF"));
        editpicsize.setHintTextColor(Color.parseColor("#32FFFFFF"));
        editpicsize.setHint(Integer.toString(MAX_PICTURE_SIZE));



        TextView predilate = new TextView(this);
        predilate.setGravity(Gravity.CENTER);
        predilate.setTextColor(Color.WHITE);




        editpredilate = new EditText(this);
        editpredilate.setGravity(Gravity.CENTER);
        editpredilate.setTextColor(Color.parseColor("#FFFFFF"));
        editpredilate.setHintTextColor(Color.parseColor("#32FFFFFF"));
        editpredilate.setHint(Integer.toString(PRE_DILATE_SIZE));



        TextView preerode = new TextView(this);
        preerode.setGravity(Gravity.CENTER);
        preerode.setTextColor(Color.WHITE);




        editpreerode = new EditText(this);
        editpreerode.setGravity(Gravity.CENTER);
        editpreerode.setTextColor(Color.parseColor("#FFFFFF"));
        editpreerode.setHintTextColor(Color.parseColor("#32FFFFFF"));
        editpreerode.setHint(Integer.toString(PRE_ERODE_SIZE));



        TextView numofcandidates = new TextView(this);
        numofcandidates.setGravity(Gravity.CENTER);
        numofcandidates.setTextColor(Color.WHITE);




        editnumofcandidates = new EditText(this);
        editnumofcandidates.setGravity(Gravity.CENTER);
        editnumofcandidates.setTextColor(Color.parseColor("#FFFFFF"));
        editnumofcandidates.setHintTextColor(Color.parseColor("#32FFFFFF"));
        editnumofcandidates.setHint(Integer.toString(NUM_OF_CANDIDATES));



        TextView linesgap = new TextView(this);
        linesgap.setGravity(Gravity.CENTER);
        linesgap.setTextColor(Color.WHITE);




        editlinesgap = new EditText(this);
        editlinesgap.setGravity(Gravity.CENTER);
        editlinesgap.setTextColor(Color.parseColor("#FFFFFF"));
        editlinesgap.setHintTextColor(Color.parseColor("#32FFFFFF"));
        editlinesgap.setHint(Integer.toString(LINES_GAP)+ "%");



        //post rotate

        TextView post_rotate = new TextView(this);
        post_rotate.setGravity(Gravity.CENTER);
        post_rotate.setTextColor(Color.WHITE);

        post_rotate.setBackgroundColor(Color.GRAY);
        size=post_rotate.getTextSize();
        post_rotate.setTextSize(TypedValue.COMPLEX_UNIT_PX, size + 15);



        TextView dilate = new TextView(this);
        dilate.setGravity(Gravity.CENTER);
        dilate.setTextColor(Color.WHITE);




        editdilate = new EditText(this);
        editdilate.setGravity(Gravity.CENTER);
        editdilate.setTextColor(Color.parseColor("#FFFFFF"));
        editdilate.setHintTextColor(Color.parseColor("#32FFFFFF"));
        editdilate.setHint(Integer.toString(DILATE_SIZE));



        TextView erode = new TextView(this);
        erode.setGravity(Gravity.CENTER);
        erode.setTextColor(Color.WHITE);




        editerode = new EditText(this);
        editerode.setGravity(Gravity.CENTER);
        editerode.setTextColor(Color.parseColor("#FFFFFF"));
        editerode.setHintTextColor(Color.parseColor("#32FFFFFF"));
        editerode.setHint(Integer.toString(ERODE_SIZE));




        TextView linesfounddrawsize = new TextView(this);
        linesfounddrawsize.setGravity(Gravity.CENTER);
        linesfounddrawsize.setTextColor(Color.WHITE);




        editlinesfounddrawsize = new EditText(this);
        editlinesfounddrawsize.setGravity(Gravity.CENTER);
        editlinesfounddrawsize.setTextColor(Color.parseColor("#FFFFFF"));
        editlinesfounddrawsize.setHintTextColor(Color.parseColor("#32FFFFFF"));
        editlinesfounddrawsize.setHint(Integer.toString(LINES_FOUND_DRAW_SIZE));



        //post cut
        TextView post_cut = new TextView(this);
        post_cut.setGravity(Gravity.CENTER);
        post_cut.setTextColor(Color.WHITE);

        post_cut.setBackgroundColor(Color.GRAY);
        size=post_cut.getTextSize();
        post_cut.setTextSize(TypedValue.COMPLEX_UNIT_PX, size + 15);



        TextView lowergapremove = new TextView(this);
        lowergapremove.setGravity(Gravity.CENTER);
        lowergapremove.setTextColor(Color.WHITE);




        editlowergapremove = new EditText(this);
        editlowergapremove.setGravity(Gravity.CENTER);
        editlowergapremove.setTextColor(Color.parseColor("#FFFFFF"));
        editlowergapremove.setHintTextColor(Color.parseColor("#32FFFFFF"));
        editlowergapremove.setHint(Integer.toString(CLEAN_LOWER_GAP_PRECENTAGE) + "%");



        TextView highergapremove = new TextView(this);
        highergapremove.setGravity(Gravity.CENTER);
        highergapremove.setTextColor(Color.WHITE);



        edithighergapremove = new EditText(this);
        edithighergapremove.setGravity(Gravity.CENTER);
        edithighergapremove.setTextColor(Color.parseColor("#FFFFFF"));
        edithighergapremove.setHintTextColor(Color.parseColor("#32FFFFFF"));
        edithighergapremove.setHint(Integer.toString(CLEAN_HIGHER_GAP_PRECENTAGE) + "%");



        TextView lowertotalremove = new TextView(this);
        lowertotalremove.setGravity(Gravity.CENTER);
        lowertotalremove.setTextColor(Color.WHITE);




        editlowertotalremove = new EditText(this);
        editlowertotalremove.setGravity(Gravity.CENTER);
        editlowertotalremove.setTextColor(Color.parseColor("#FFFFFF"));
        editlowertotalremove.setHintTextColor(Color.parseColor("#32FFFFFF"));
        editlowertotalremove.setHint(Integer.toString(CLEAN_LOWER_TOTAL_PIXEL_PRECENTAGE) + "%");



        TextView highertotalremove = new TextView(this);
        highertotalremove.setGravity(Gravity.CENTER);
        highertotalremove.setTextColor(Color.WHITE);




        edithighertotalremove = new EditText(this);
        edithighertotalremove.setGravity(Gravity.CENTER);
        edithighertotalremove.setTextColor(Color.parseColor("#FFFFFF"));
        edithighertotalremove.setHintTextColor(Color.parseColor("#32FFFFFF"));
        edithighertotalremove.setHint(Integer.toString(CLEAN_HIGHER_TOTAL_PIXEL_PRECENTAGE) + "%");

        TextView ocrpicsize = new TextView(this);
        ocrpicsize.setGravity(Gravity.CENTER);
        ocrpicsize.setTextColor(Color.WHITE);


        editocrpicsize = new EditText(this);
        editocrpicsize.setGravity(Gravity.CENTER);
        editocrpicsize.setTextColor(Color.parseColor("#FFFFFF"));
        editocrpicsize.setHintTextColor(Color.parseColor("#32FFFFFF"));
        editocrpicsize.setHint(Integer.toString(OCR_PICTURE_SIZE));

        //id settings
        TextView id_settings = new TextView(this);
        id_settings.setGravity(Gravity.CENTER);
        id_settings.setTextColor(Color.WHITE);

        id_settings.setBackgroundColor(Color.GRAY);
        size=id_settings.getTextSize();
        id_settings.setTextSize(TypedValue.COMPLEX_UNIT_PX, size + 15);

        TextView idlinessize = new TextView(this);
        idlinessize.setGravity(Gravity.CENTER);
        idlinessize.setTextColor(Color.WHITE);


        editidlinessize = new EditText(this);
        editidlinessize.setGravity(Gravity.CENTER);
        editidlinessize.setTextColor(Color.parseColor("#FFFFFF"));
        editidlinessize.setHintTextColor(Color.parseColor("#32FFFFFF"));
        editidlinessize.setHint(Integer.toString(ID_LINES_SIZE));

        TextView iddilate = new TextView(this);
        iddilate.setGravity(Gravity.CENTER);
        iddilate.setTextColor(Color.WHITE);


        editiddilate = new EditText(this);
        editiddilate.setGravity(Gravity.CENTER);
        editiddilate.setTextColor(Color.parseColor("#FFFFFF"));
        editiddilate.setHintTextColor(Color.parseColor("#32FFFFFF"));
        editiddilate.setHint(Float.toString(ID_DILATE));

        TextView iderode = new TextView(this);
        iderode.setGravity(Gravity.CENTER);
        iderode.setTextColor(Color.WHITE);


        editiderode = new EditText(this);
        editiderode.setGravity(Gravity.CENTER);
        editiderode.setTextColor(Color.parseColor("#FFFFFF"));
        editiderode.setHintTextColor(Color.parseColor("#32FFFFFF"));
        editiderode.setHint(Float.toString(ID_ERODE));

        TextView idpicsize = new TextView(this);
        idpicsize.setGravity(Gravity.CENTER);
        idpicsize.setTextColor(Color.WHITE);


        editidpicsize = new EditText(this);
        editidpicsize.setGravity(Gravity.CENTER);
        editidpicsize.setTextColor(Color.parseColor("#FFFFFF"));
        editidpicsize.setHintTextColor(Color.parseColor("#32FFFFFF"));
        editidpicsize.setHint(Integer.toString(ID_PICTURE_SIZE));

        //set all strings for this alert
        String negetiveb = null,positiveb = null;
        switch (language) {
            case 1:
                title.setText(R.string.eng_advancedsettings);
                pre_rotate.setText(R.string.eng_prerotate);
                picsize.setText(R.string.eng_picsize);
                predilate.setText(R.string.eng_dilate);
                preerode.setText(R.string.eng_erode);
                numofcandidates.setText(R.string.eng_numofcandidates);
                linesgap.setText(R.string.eng_linesgap);
                post_rotate.setText(R.string.eng_postrotate);
                dilate.setText(R.string.eng_dilate);
                erode.setText(R.string.eng_erode);
                linesfounddrawsize.setText(R.string.eng_linefill);
                post_cut.setText(R.string.eng_postcut);
                lowergapremove.setText(R.string.eng_mingapprec);
                highergapremove.setText(R.string.eng_maxgapprec);
                lowertotalremove.setText(R.string.eng_minpixelprec);
                highertotalremove.setText(R.string.eng_maxpixelprec);
                ocrpicsize.setText(R.string.eng_ocrpicsize);
                idlinessize.setText(R.string.eng_idlinessize);
                iddilate.setText(R.string.eng_dilate);
                iderode.setText(R.string.eng_erode);
                idpicsize.setText(R.string.eng_ocrpicsize);
                //id
                id_settings.setText(R.string.eng_idsettings);

                negetiveb = getResources().getString(R.string.eng_restoredefault);
                positiveb = getResources().getString(R.string.eng_savesetting);

                break;

            case 2:
                title.setText(R.string.heb_advancedsettings);
                pre_rotate.setText(R.string.heb_prerotate);
                picsize.setText(R.string.heb_picsize);
                predilate.setText(R.string.heb_dilate);
                preerode.setText(R.string.heb_erode);
                numofcandidates.setText(R.string.heb_numofcandidates);
                linesgap.setText(R.string.heb_linesgap);
                post_rotate.setText(R.string.heb_postrotate);
                dilate.setText(R.string.heb_dilate);
                erode.setText(R.string.heb_erode);
                linesfounddrawsize.setText(R.string.heb_linefill);
                post_cut.setText(R.string.heb_postcut);
                lowergapremove.setText(R.string.heb_mingapprec);
                highergapremove.setText(R.string.heb_maxgapprec);
                lowertotalremove.setText(R.string.heb_minpixelprec);
                highertotalremove.setText(R.string.heb_maxpixelprec);
                ocrpicsize.setText(R.string.heb_ocrpicsize);
                idlinessize.setText(R.string.heb_idlinessize);
                iddilate.setText(R.string.heb_dilate);
                iderode.setText(R.string.heb_erode);
                idpicsize.setText(R.string.heb_ocrpicsize);
                //id
                id_settings.setText(R.string.heb_idsettings);

                negetiveb = getResources().getString(R.string.heb_restoredefault);
                positiveb = getResources().getString(R.string.heb_savesetting);

                break;

            case 3:
                title.setText(R.string.rus_advancedsettings);
                pre_rotate.setText(R.string.rus_prerotate);
                picsize.setText(R.string.rus_picsize);
                predilate.setText(R.string.rus_dilate);
                preerode.setText(R.string.rus_erode);
                numofcandidates.setText(R.string.rus_numofcandidates);
                linesgap.setText(R.string.rus_linesgap);
                post_rotate.setText(R.string.rus_postrotate);
                dilate.setText(R.string.rus_dilate);
                erode.setText(R.string.rus_erode);
                linesfounddrawsize.setText(R.string.rus_linefill);
                post_cut.setText(R.string.rus_postcut);
                lowergapremove.setText(R.string.rus_mingapprec);
                highergapremove.setText(R.string.rus_maxgapprec);
                lowertotalremove.setText(R.string.rus_minpixelprec);
                highertotalremove.setText(R.string.rus_maxpixelprec);
                ocrpicsize.setText(R.string.rus_ocrpicsize);
                idlinessize.setText(R.string.rus_idlinessize);
                iddilate.setText(R.string.rus_dilate);
                iderode.setText(R.string.rus_erode);
                idpicsize.setText(R.string.rus_ocrpicsize);
                //id
                id_settings.setText(R.string.rus_idsettings);

                negetiveb = getResources().getString(R.string.rus_restoredefault);
                positiveb = getResources().getString(R.string.rus_savesetting);

                break;

            default:
        }
        //add all
        alertd.setCustomTitle(title);
        ll.addView(pre_rotate);
        ll.addView(picsize);
        ll.addView(editpicsize);
        ll.addView(predilate);
        ll.addView(editpredilate);
        ll.addView(preerode);
        ll.addView(editpreerode);
        ll.addView(numofcandidates);
        ll.addView(editnumofcandidates);
        ll.addView(linesgap);
        ll.addView(editlinesgap);
        ll.addView(post_rotate);
        ll.addView(dilate);
        ll.addView(editdilate);
        ll.addView(erode);
        ll.addView(editerode);
        ll.addView(linesfounddrawsize);
        ll.addView(editlinesfounddrawsize);
        ll.addView(post_cut);
        ll.addView(lowergapremove);
        ll.addView(editlowergapremove);
        ll.addView(highergapremove);
        ll.addView(edithighergapremove);
        ll.addView(lowertotalremove);
        ll.addView(editlowertotalremove);
        ll.addView(highertotalremove);
        ll.addView(edithighertotalremove);
        ll.addView(ocrpicsize);
        ll.addView(editocrpicsize);
        ll.addView(id_settings);
        ll.addView(idlinessize);
        ll.addView(editidlinessize);
        ll.addView(iddilate);
        ll.addView(editiddilate);
        ll.addView(iderode);
        ll.addView(editiderode);
        ll.addView(idpicsize);
        ll.addView(editidpicsize);

        //ScrollView sv = new ScrollView(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.scrollview, null);

        ScrollView sv =(ScrollView) v.findViewById(R.id.thescrollView);


        sv.addView(ll);


        alertd.setView(v);

        alertd.setPositiveButton(positiveb, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //remove button lock
                dialogactive=false;
                //save changes
                try {
                    MAX_PICTURE_SIZE = Integer.parseInt(editpicsize.getText().toString());
                } catch (Exception e) {
                }
                try {
                    PRE_DILATE_SIZE = Integer.parseInt(editpredilate.getText().toString());
                } catch (Exception e) {
                }
                try {
                    PRE_ERODE_SIZE = Integer.parseInt(editpreerode.getText().toString());
                } catch (Exception e) {
                }
                try {
                    DILATE_SIZE = Integer.parseInt(editdilate.getText().toString());
                } catch (Exception e) {
                }
                try {
                    ERODE_SIZE = Integer.parseInt(editerode.getText().toString());
                } catch (Exception e) {
                }
                try {
                    NUM_OF_CANDIDATES = Integer.parseInt(editnumofcandidates.getText().toString());
                } catch (Exception e) {
                }
                try {
                    LINES_GAP = Integer.parseInt(editlinesgap.getText().toString());
                } catch (Exception e) {
                }
                try {
                    LINES_FOUND_DRAW_SIZE = Integer.parseInt(editlinesfounddrawsize.getText().toString());
                } catch (Exception e) {
                }
                try {
                    CLEAN_LOWER_GAP_PRECENTAGE = Integer.parseInt(editlowergapremove.getText().toString());
                } catch (Exception e) {
                }
                try {
                    CLEAN_HIGHER_GAP_PRECENTAGE = Integer.parseInt(edithighergapremove.getText().toString());
                } catch (Exception e) {
                }
                try {
                    CLEAN_LOWER_TOTAL_PIXEL_PRECENTAGE = Integer.parseInt(editlowertotalremove.getText().toString());
                } catch (Exception e) {
                }
                try {
                    CLEAN_HIGHER_TOTAL_PIXEL_PRECENTAGE = Integer.parseInt(edithighertotalremove.getText().toString());
                } catch (Exception e) {
                }
                try {
                    OCR_PICTURE_SIZE = Integer.parseInt(editocrpicsize.getText().toString());
                } catch (Exception e) {
                }
                try {
                    ID_LINES_SIZE = Integer.parseInt(editidlinessize.getText().toString());
                } catch (Exception e) {
                }
                try {
                    ID_DILATE = Float.parseFloat(editiddilate.getText().toString());
                } catch (Exception e) {
                }
                try {
                    ID_ERODE = Float.parseFloat(editiderode.getText().toString());
                } catch (Exception e) {
                }
                try {
                    ID_PICTURE_SIZE = Integer.parseInt(editidpicsize.getText().toString());
                } catch (Exception e) {
                }

                savesettingsintofiles();
            }
        });
        alertd.setNegativeButton(negetiveb, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //remove button lock
                dialogactive=false;
                defaultsettings();
                savesettingsintofiles();
            }
        });
        alertd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //remove button lock
                dialogactive=false;
            }
        });
        alertd.show();

    }

    private void savesettingsintofiles() {
        //save settings
        SharedPreferences prefs;

        prefs = getApplicationContext().getSharedPreferences("picsize", 1);
        SharedPreferences.Editor picsizeeditor = prefs.edit();
        picsizeeditor.putInt("savedpicsize", MAX_PICTURE_SIZE);
        picsizeeditor.commit();

        prefs = getApplicationContext().getSharedPreferences("predilate", 1);
        SharedPreferences.Editor predilateeditor = prefs.edit();
        predilateeditor.putInt("savedpredilate", PRE_DILATE_SIZE);
        predilateeditor.commit();

        prefs = getApplicationContext().getSharedPreferences("preerode", 1);
        SharedPreferences.Editor preerodeeditor = prefs.edit();
        preerodeeditor.putInt("savedpreerode", PRE_ERODE_SIZE);
        preerodeeditor.commit();

        prefs = getApplicationContext().getSharedPreferences("dilate", 1);
        SharedPreferences.Editor dilateeditor = prefs.edit();
        dilateeditor.putInt("saveddilate", DILATE_SIZE);
        dilateeditor.commit();

        prefs = getApplicationContext().getSharedPreferences("erode", 1);
        SharedPreferences.Editor erodeeditor = prefs.edit();
        erodeeditor.putInt("savederode", ERODE_SIZE);
        erodeeditor.commit();

        prefs = getApplicationContext().getSharedPreferences("numofcandidates", 1);
        SharedPreferences.Editor numofcandidateseditor = prefs.edit();
        numofcandidateseditor.putInt("savednumofcandidates", NUM_OF_CANDIDATES);
        numofcandidateseditor.commit();

        prefs = getApplicationContext().getSharedPreferences("linesgap", 1);
        SharedPreferences.Editor linesgapeditor = prefs.edit();
        linesgapeditor.putInt("savedlinesgap", LINES_GAP);
        linesgapeditor.commit();

        prefs = getApplicationContext().getSharedPreferences("linesfounddrawsize", 1);
        SharedPreferences.Editor linesfounddrawsizeeditor = prefs.edit();
        linesfounddrawsizeeditor.putInt("savedlinesfounddrawsize", LINES_FOUND_DRAW_SIZE);
        linesfounddrawsizeeditor.commit();

        prefs = getApplicationContext().getSharedPreferences("lowergapremove", 1);
        SharedPreferences.Editor lowergapremoveeditor = prefs.edit();
        lowergapremoveeditor.putInt("savedlowergapremove", CLEAN_LOWER_GAP_PRECENTAGE);
        lowergapremoveeditor.commit();

        prefs = getApplicationContext().getSharedPreferences("highergapremove", 1);
        SharedPreferences.Editor highergapremoveeditor = prefs.edit();
        highergapremoveeditor.putInt("savedhighergapremove", CLEAN_HIGHER_GAP_PRECENTAGE);
        highergapremoveeditor.commit();

        prefs = getApplicationContext().getSharedPreferences("lowertotalremove", 1);
        SharedPreferences.Editor lowertotalremoveeditor = prefs.edit();
        lowertotalremoveeditor.putInt("savedlowertotalremove", CLEAN_LOWER_TOTAL_PIXEL_PRECENTAGE);
        lowertotalremoveeditor.commit();

        prefs = getApplicationContext().getSharedPreferences("highertotalremove", 1);
        SharedPreferences.Editor highertotalremoveeditor = prefs.edit();
        highertotalremoveeditor.putInt("savedhighertotalremove", CLEAN_HIGHER_TOTAL_PIXEL_PRECENTAGE);
        highertotalremoveeditor.commit();

        prefs = getApplicationContext().getSharedPreferences("ocrpicsize", 1);
        SharedPreferences.Editor ocrpicsizeeditor = prefs.edit();
        ocrpicsizeeditor.putInt("savedocrpicsize", OCR_PICTURE_SIZE);
        ocrpicsizeeditor.commit();

        prefs = getApplicationContext().getSharedPreferences("idlinessize", 1);
        SharedPreferences.Editor idlinessizeeditor = prefs.edit();
        idlinessizeeditor.putInt("savedidlinessize", ID_LINES_SIZE);
        idlinessizeeditor.commit();

        prefs = getApplicationContext().getSharedPreferences("iddilate", 1);
        SharedPreferences.Editor iddilateeditor = prefs.edit();
        iddilateeditor.putFloat("savediddilate", ID_DILATE);
        iddilateeditor.commit();

        prefs = getApplicationContext().getSharedPreferences("iderode", 1);
        SharedPreferences.Editor iderodeeditor = prefs.edit();
        iderodeeditor.putFloat("savediderode", ID_ERODE);
        iderodeeditor.commit();

        prefs = getApplicationContext().getSharedPreferences("idpicsize", 1);
        SharedPreferences.Editor idpicsizeeditor = prefs.edit();
        idpicsizeeditor.putInt("savedidpicsize", ID_PICTURE_SIZE);
        idpicsizeeditor.commit();
    }

    private void sendOCRtoread(String phonenumber) {
        String DATA_PATH = "";
        String recognizedText = "";


        DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/OCRfiles/";
        //read main pic
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.init(DATA_PATH, "eng", 3);
        baseApi.setImage(cameraphoto);
        baseApi.setVariable("tessedit_char_whitelist", "0123456789");
        recognizedText = baseApi.getUTF8Text();
        baseApi.end();

        //read id pic
        TessBaseAPI baseApi2 = new TessBaseAPI();
        baseApi2.init(DATA_PATH, "eng", 3);
        baseApi2.setImage(idphoto);
        String idtextfound = baseApi2.getUTF8Text();
        baseApi2.end();
        //if id too long dont write it
        if(idtextfound.length()>9)
            idtextfound="";
        //send sms
        Calendar c = Calendar.getInstance();

        String day = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
        String month = Integer.toString(c.get(Calendar.MONTH));
        String year = Integer.toString(c.get(Calendar.YEAR));

        String hour = Integer.toString(c.get(Calendar.HOUR));
        String minute = Integer.toString(c.get(Calendar.MINUTE));
        String second = Integer.toString(c.get(Calendar.SECOND));
        //editlanguage string
        String meteridstring = null,datestring = null,execttimestring = null,ocroutputstring = null;
        switch (language){
            case 1:
                meteridstring=getResources().getString(R.string.eng_sms_meterid);
                datestring=getResources().getString(R.string.eng_sms_date);
                execttimestring=getResources().getString(R.string.eng_sms_sent);
                ocroutputstring=getResources().getString(R.string.eng_sms_whatreaded);
                break;
            case 2:
                meteridstring=getResources().getString(R.string.heb_sms_meterid);
                datestring=getResources().getString(R.string.heb_sms_date);
                execttimestring=getResources().getString(R.string.heb_sms_sent);
                ocroutputstring=getResources().getString(R.string.heb_sms_whatreaded);
                break;
            case 3:
                meteridstring=getResources().getString(R.string.rus_sms_meterid);
                datestring=getResources().getString(R.string.rus_sms_date);
                execttimestring=getResources().getString(R.string.rus_sms_sent);
                ocroutputstring=getResources().getString(R.string.rus_sms_whatreaded);
                break;

            default:
        }
        //before joining all parts
        String watermeterid=meteridstring+" "+idtextfound;
        String fulldate=datestring+" "+day+"/"+month+"/"+year;
        String execttime=execttimestring+" "+hour+":"+minute+":"+second;
        String readed=ocroutputstring+" "+recognizedText;

        String sendedsms =watermeterid+"\n"+fulldate+"\n"+execttime+"\n"+readed;

        sendSMS(phonenumber, sendedsms);
    }

    private void inputvalidstartalgoritm() {
        cameraphoto = cameraphoto.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap original = cameraphoto.copy(Bitmap.Config.ARGB_8888, true);
        Mat lines = new Mat();
        Mat myMat = new Mat();
        Mat temp = new Mat();


        Utils.bitmapToMat(cameraphoto, myMat);
        Imgproc.cvtColor(myMat, myMat, Imgproc.COLOR_RGB2GRAY);

        double otsu_thresh_val = Imgproc.threshold(myMat, temp, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        double high_thresh_val = otsu_thresh_val,
                lower_thresh_val = otsu_thresh_val * 0.5;

        Imgproc.Canny(myMat, myMat, lower_thresh_val, high_thresh_val);
        Imgproc.dilate(myMat, myMat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(PRE_DILATE_SIZE, PRE_DILATE_SIZE)));
        Imgproc.erode(myMat, myMat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(PRE_ERODE_SIZE, PRE_ERODE_SIZE)));
        Utils.matToBitmap(myMat, cameraphoto);

        boolean foundlines=true,foundcondidates=false;
        int stopwhenchange=0;
        int counterupanddown=0;
        while (!foundcondidates && foundlines) {
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

            getcutpoints(allslopes, pt1, pt2, lines, myMat, twoioffoundlines, cameraphoto);
            int inttwoioffoundlines = twoioffoundlines[0];
            finalangle = Math.toDegrees(Math.atan2(pt2[inttwoioffoundlines].y - pt1[inttwoioffoundlines].y, pt2[inttwoioffoundlines].x - pt1[inttwoioffoundlines].x));

            Utils.matToBitmap(myMat, cameraphoto);

            double y1fromzeroxp1 = allslopes[(twoioffoundlines[0])] * (0 - pt1[(twoioffoundlines[0])].x) + pt1[(twoioffoundlines[0])].y;
            double y2fromendpicxp1 = allslopes[(twoioffoundlines[0])] * (cameraphoto.getWidth() - pt2[(twoioffoundlines[0])].x) + pt2[(twoioffoundlines[0])].y;
            double y1fromzeroxp2 = allslopes[(twoioffoundlines[1])] * (0 - pt1[(twoioffoundlines[1])].x) + pt1[(twoioffoundlines[1])].y;
            double y2fromendpicxp2 = allslopes[(twoioffoundlines[1])] * (cameraphoto.getWidth() - pt2[(twoioffoundlines[1])].x) + pt2[(twoioffoundlines[1])].y;
            //find center points of lines
            double middleyp1;
            if (y1fromzeroxp1 > y2fromendpicxp1)
                middleyp1 = (y1fromzeroxp1 - y2fromendpicxp1) / 2 + y2fromendpicxp1;
            else
                middleyp1 = (y2fromendpicxp1 - y1fromzeroxp1) / 2 + y1fromzeroxp1;
            double middleyp2;
            if (y1fromzeroxp2 > y2fromendpicxp2)
                middleyp2 = (y1fromzeroxp2 - y2fromendpicxp2) / 2 + y2fromendpicxp2;
            else
                middleyp2 = (y2fromendpicxp2 - y1fromzeroxp2) / 2 + y1fromzeroxp2;

            //rotate
            int heightneedtoadd = cameraphoto.getHeight();
            cameraphoto = RotateBitmap(original, (float) finalangle * -1);
            idphoto = RotateBitmap(idphoto, (float) finalangle * -1);
            heightneedtoadd = cameraphoto.getHeight() - heightneedtoadd;

            //canny again
            Utils.bitmapToMat(cameraphoto, myMat);
            Imgproc.cvtColor(myMat, myMat, Imgproc.COLOR_RGB2GRAY);
            otsu_thresh_val = Imgproc.threshold(myMat, temp, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
            high_thresh_val = otsu_thresh_val;
            lower_thresh_val = otsu_thresh_val * 0.5;

            Imgproc.Canny(myMat, myMat, lower_thresh_val, high_thresh_val);
            Imgproc.dilate(myMat, myMat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(DILATE_SIZE, DILATE_SIZE)));
            Imgproc.erode(myMat, myMat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(ERODE_SIZE, ERODE_SIZE)));

            //draw black line for floodfill
            Scalar s = new Scalar(0, 0, 0);
            Core.line(myMat, new Point(0, middleyp1 + heightneedtoadd / 2), new Point(cameraphoto.getWidth(), middleyp1 + heightneedtoadd / 2), s, (int) (LINES_FOUND_DRAW_SIZE));
            Core.line(myMat, new Point(0, middleyp2 + heightneedtoadd / 2), new Point(cameraphoto.getWidth(), middleyp2 + heightneedtoadd / 2), s, (int) (LINES_FOUND_DRAW_SIZE));
            Utils.matToBitmap(myMat, cameraphoto);


            //cut
            if (middleyp2 > middleyp1)
                cameraphoto = Bitmap.createBitmap(cameraphoto, (int) (0), (int) (middleyp1 + heightneedtoadd / 2), cameraphoto.getWidth(), (int) (middleyp2 - middleyp1));
            else
                cameraphoto = Bitmap.createBitmap(cameraphoto, (int) (0), (int) (middleyp2 + heightneedtoadd / 2), cameraphoto.getWidth(), (int) (middleyp1 - middleyp2));
            //fill with white
            floodFill(cameraphoto, new Point(0, 0), Color.BLACK, Color.WHITE);


            try {
                //cleaning unneeded dots and parts pixelwise
                List<objectleftrighttopdown> listofstartfin = new ArrayList<objectleftrighttopdown>();
                List<objectleftrighttopdown> listofstartfincleaning = new ArrayList<objectleftrighttopdown>();
                Point cutpoint = cutsides(cameraphoto, listofstartfin, listofstartfincleaning);

                //fill list to clean numbers
                List<Point> listforfloodfill;
                Bitmap copy;
                for (int i = 0; i < listofstartfin.size(); i++) {
                    listforfloodfill = new ArrayList<Point>();
                    copy = cameraphoto.copy(Bitmap.Config.ARGB_8888, true);
                    //fill
                    fillfloodfilllist(listofstartfin.get(i).left, listofstartfin.get(i).right, listofstartfin.get(i).top, listofstartfin.get(i).down, listofstartfin.get(i).pixels, copy, listforfloodfill);
                    //clean
                    for (int cleani = 0; cleani < listforfloodfill.size(); cleani++) {
                        floodFill(cameraphoto, listforfloodfill.get(cleani), Color.BLACK, Color.WHITE);
                    }
                }
                //cut sides again to update info
                cutpoint = cutsides(cameraphoto, listofstartfin, listofstartfincleaning);
                //cut the sides after
                cameraphoto = Bitmap.createBitmap(cameraphoto, (int) (cutpoint.x), (int) (0), (int) (cutpoint.y - cutpoint.x), (int) (cameraphoto.getHeight()));
            } catch (Exception e) {

            }
            //bigger
            cameraphoto = Bitmap.createScaledBitmap(cameraphoto, (cameraphoto.getWidth() * OCR_PICTURE_SIZE),(cameraphoto.getHeight() * OCR_PICTURE_SIZE), false);

        }

    }


    private void fillfloodfilllist(double left, double right, double top, double down, double pixels, Bitmap copy, List<Point> listforfloodfill) {
        double xsize = right - left + 1;
        double ysize = down - top + 1;
        List<Double> pixelssize = new ArrayList<Double>();

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

    private Point cutsides(Bitmap cameraphoto, List<objectleftrighttopdown> listofstartfin, List<objectleftrighttopdown> listofstartfincleaning) {

        objectleftrighttopdown startfin = null;
        int height = cameraphoto.getHeight();
        //fill rows if even got 1 pixel in black
        boolean blackandwhite[] = new boolean[cameraphoto.getWidth()];
        for (int i = 0; i < cameraphoto.getWidth(); i++) {

            for (int j = 0; j < height; j++) {
                if (cameraphoto.getPixel(i, j) == Color.BLACK) {
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
                    listofstartfin.add(startfin);
                }
            } else if (i != 0) {
                if (blackandwhite[i] == false && blackandwhite[i - 1] == true) {
                    startfin.right = i;
                    listofstartfin.add(startfin);
                }
            }

        }
//ELIMINATE NOT NUMBERS using % of black pixels and certain height

        for (int listi = 0; listi < listofstartfin.size(); listi++) {

            int width = (int) (listofstartfin.get(listi).right - listofstartfin.get(listi).left);
            int miny = height;
            int maxy = 0;
            double allpixels = height * width;
            double blackpixels = 0;
            for (int i = 0; i < height; i++) {
                for (int j = (int) (listofstartfin.get(listi).left); j < listofstartfin.get(listi).right; j++) {
                    if (cameraphoto.getPixel(j, i) == Color.BLACK) {
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
            //requirments for delete

            if (maxy - miny < height * ((double)(CLEAN_LOWER_GAP_PRECENTAGE) / 100) || maxy - miny > height * ( (double)(CLEAN_HIGHER_GAP_PRECENTAGE) / 100) || blackpixels / allpixels < ( (double)(CLEAN_LOWER_TOTAL_PIXEL_PRECENTAGE) / 100) || blackpixels / allpixels > ( (double)(CLEAN_HIGHER_TOTAL_PIXEL_PRECENTAGE) / 100)) {
                listofstartfincleaning.add(listofstartfin.get(listi));
                listofstartfin.remove(listi);
                if (listofstartfin.size() != listi)
                    listi--;
            }

        }
        //remove whats not in pic
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
                        if (cameraphoto.getPixel(j, i) == Color.BLACK) {
                            cameraphoto.setPixel(j, i, Color.WHITE);
                        }
                    }
                }
            }

        }

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


    public Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        PendingIntent sentPI;
        String SENT = "SMS_SENT";

        sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        sms.sendTextMessage(phoneNumber, null, message, sentPI, null);
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


}

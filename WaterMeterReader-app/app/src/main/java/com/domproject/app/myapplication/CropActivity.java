package com.domproject.app.myapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CropActivity extends Activity implements OnTouchListener {

    Canvas c;
    OurView v;
    int loop,width,height,originalphotowidth,originalphotoheight;
    float moveleftx=0.001f,moverightx=0.001f,moveupy=0.001f,movedowny=0.001f;
    Bitmap thephoto,larrow,rarrow,uarrow,darrow,vbitmap,xbitmap,rotateleftside,rotaterightside;
    Paint transperentpaint;
    boolean larrowclicked=false,rarrowclicked=false,uarrowclicked=false,darrowclicked=false;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        v=new OurView(this);
        v.setOnTouchListener(this);

        //Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //get photo to crop
        String fname = Environment.getExternalStorageDirectory() + "/temp.png";
        thephoto=BitmapFactory.decodeFile(fname);
        originalphotowidth=thephoto.getWidth();
        originalphotoheight=thephoto.getHeight();

        //get bitmaps from the program
        larrow=BitmapFactory.decodeResource(getResources(), R.drawable.leftarrow);
        rarrow=BitmapFactory.decodeResource(getResources(), R.drawable.rightarrow);
        uarrow=BitmapFactory.decodeResource(getResources(), R.drawable.uparrow);
        darrow=BitmapFactory.decodeResource(getResources(), R.drawable.downarrow);
        vbitmap=BitmapFactory.decodeResource(getResources(), R.drawable.myvi);
        xbitmap=BitmapFactory.decodeResource(getResources(), R.drawable.myx);
        rotateleftside=BitmapFactory.decodeResource(getResources(), R.drawable.right);
        rotaterightside=BitmapFactory.decodeResource(getResources(), R.drawable.left);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        //resize thephoto if bigger than screen
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        boolean success=false;
        int bitmapsize=1;
            while (success == false) {
                options.inSampleSize = bitmapsize;
                try {//if pic too big
                    thephoto = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/temp.png", options);

                    if (thephoto.getWidth()< width && thephoto.getHeight()<height) {//max size check
                        success = true;
                    }
                    else
                        bitmapsize++;//if too big for screen
                } catch (Exception e) {
                    bitmapsize++;//take smaller sample if too big for load
                }
            }


        //set size of arrows
        larrow=Bitmap.createScaledBitmap(larrow,100, 100,false);
        rarrow=Bitmap.createScaledBitmap(rarrow,100, 100,false);
        uarrow=Bitmap.createScaledBitmap(uarrow,100, 100,false);
        darrow=Bitmap.createScaledBitmap(darrow,100, 100,false);
        //set size of v and x
        vbitmap=Bitmap.createScaledBitmap(vbitmap, (int) (height*0.1175), (int) (height*0.1175),false);
        xbitmap=Bitmap.createScaledBitmap(xbitmap, (int) (height*0.09), (int) (height*0.09),false);

        transperentpaint=new Paint();
        transperentpaint.setColor(Color.WHITE);
        transperentpaint.setAlpha(127);
        setContentView(v);
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


        }


















        protected void drawing(Canvas c) throws IOException {
            //redraw black
            c.drawColor(Color.BLACK);

            //draw photo
            c.drawBitmap(thephoto, width / 2 - thephoto.getWidth() / 2, height / 2 - thephoto.getHeight() / 2, null);

                //draw crop square
                c.drawRect(width / 2 - thephoto.getWidth() / 2 + moveleftx, height / 2 - thephoto.getHeight() / 2 + moveupy, width / 2 + thephoto.getWidth() / 2 + moverightx, height / 2 + thephoto.getHeight() / 2 + movedowny, transperentpaint);

                //draw arrows
                c.drawBitmap(larrow, width / 2 - thephoto.getWidth() / 2 - larrow.getWidth() + moveleftx, height / 2 - larrow.getHeight() / 2 + moveupy / 2 + movedowny / 2, transperentpaint);

                c.drawBitmap(rarrow, width / 2 + thephoto.getWidth() / 2 + moverightx, height / 2 - rarrow.getHeight() / 2 + moveupy / 2 + movedowny / 2, transperentpaint);

                c.drawBitmap(uarrow, width / 2 - uarrow.getWidth() / 2 + moveleftx / 2 + moverightx / 2, height / 2 - thephoto.getHeight() / 2 - uarrow.getHeight() + moveupy, transperentpaint);

                c.drawBitmap(darrow, width / 2 - darrow.getWidth() / 2 + moveleftx / 2 + moverightx / 2, height / 2 + thephoto.getHeight() / 2 + movedowny, transperentpaint);

                c.drawBitmap(vbitmap, 0, height - vbitmap.getHeight(), null);

                c.drawBitmap(xbitmap, width - xbitmap.getWidth(), height - xbitmap.getHeight(), null);

            loop++;


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






    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                    //left arrow clicked
                    if (event.getX() >= (width / 2 - thephoto.getWidth() / 2 - larrow.getWidth() + moveleftx) && event.getX() <= (width / 2 - thephoto.getWidth() / 2 + moveleftx) && event.getY() >= (height / 2 - larrow.getHeight() / 2 + moveupy / 2 + movedowny / 2) && event.getY() <= (height / 2 + larrow.getHeight() / 2 + moveupy / 2 + movedowny / 2)) {
                        larrowclicked = true;
                        v.playSoundEffect(SoundEffectConstants.CLICK);
                    }
                    //right arrow clicked
                    if (event.getX() >= (width / 2 + thephoto.getWidth() / 2 + moverightx) && event.getX() <= (width / 2 + thephoto.getWidth() / 2 + rarrow.getWidth() + moverightx) && event.getY() >= (height / 2 - rarrow.getHeight() / 2 + moveupy / 2 + movedowny / 2) && event.getY() <= (height / 2 + rarrow.getHeight() / 2 + moveupy / 2 + movedowny / 2)) {
                        rarrowclicked = true;
                        v.playSoundEffect(SoundEffectConstants.CLICK);
                    }
                    //up arrow clicked
                    if (event.getX() >= (width / 2 - uarrow.getWidth() / 2 + moveleftx / 2 + moverightx / 2) && event.getX() <= (width / 2 + uarrow.getWidth() / 2 + moveleftx / 2 + moverightx / 2) && event.getY() >= (height / 2 - thephoto.getHeight() / 2 - uarrow.getHeight() + moveupy) && event.getY() <= (height / 2 - thephoto.getHeight() / 2 + moveupy)) {
                        uarrowclicked = true;
                        v.playSoundEffect(SoundEffectConstants.CLICK);
                    }
                    //down arrow clicked
                    if (event.getX() >= (width / 2 - darrow.getWidth() / 2 + moveleftx / 2 + moverightx / 2) && event.getX() <= (width / 2 + darrow.getWidth() / 2 + moveleftx / 2 + moverightx / 2) && event.getY() >= (height / 2 + thephoto.getHeight() / 2 + movedowny) && event.getY() <= (height / 2 + thephoto.getHeight() / 2 + darrow.getHeight() + movedowny)) {
                        darrowclicked = true;
                        v.playSoundEffect(SoundEffectConstants.CLICK);
                    }
                    //x pressed
                    if (event.getX() >= (width - xbitmap.getWidth()) && event.getX() <= (width) && event.getY() >= (height - xbitmap.getHeight()) && event.getY() <= (height)) {
                        v.playSoundEffect(SoundEffectConstants.CLICK);
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_CANCELED, returnIntent);
                        finish();
                    }
                    //y pressed
                    if (event.getX() >= (0) && event.getX() <= (vbitmap.getWidth()) && event.getY() >= (height - vbitmap.getHeight()) && event.getY() <= (height)) {
                        v.playSoundEffect(SoundEffectConstants.CLICK);
                        //if out of screen  load original and scale data for crop
                        int tempphotowidth, tempphotoheight;
                        float scalewidth, scaleheight;
                        tempphotowidth = thephoto.getWidth();
                        tempphotoheight = thephoto.getHeight();
                        scalewidth = originalphotowidth / tempphotowidth;
                        scaleheight = originalphotoheight / tempphotoheight;
                        moveleftx = moveleftx * scalewidth;
                        moverightx = moverightx * scalewidth;
                        moveupy = moveupy * scaleheight;
                        movedowny = movedowny * scaleheight;

                        thephoto = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/temp.png");
                        //crop by user
                        thephoto = Bitmap.createBitmap(thephoto, (int) (moveleftx + 1), (int) (moveupy + 1), (int) (thephoto.getWidth() + moverightx - moveleftx - 1), (int) (thephoto.getHeight() + movedowny - moveupy - 1));

                        //getwheretosave
                        int saveto = getIntent().getIntExtra("whichfiletosave", 1);
                        //save to file
                        if (thephoto != null) {
                            if(saveto==0)
                                saveToInternalSorage(thephoto, "temp.png");
                            else
                                saveToInternalSorage(thephoto, "id.png");
                        }
                        //return intent
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }

                break;

            case MotionEvent.ACTION_UP:
                larrowclicked=false;
                rarrowclicked=false;
                uarrowclicked=false;
                darrowclicked=false;
                break;
            case MotionEvent.ACTION_MOVE:

                //move after clicked

                if (larrowclicked) {
                    float savepreviousmovex;
                    savepreviousmovex = moveleftx;
                    moveleftx = (width / 2 - thephoto.getWidth() / 2 - event.getX()) * -1;

                    //minimal size adjustment
                    float rectleftx = width / 2 - thephoto.getWidth() / 2 + moveleftx;
                    float rectrightx = width / 2 + thephoto.getWidth() / 2 + moverightx;
                    //out of pic
                    if(rectleftx < width / 2 - thephoto.getWidth() / 2)
                        moveleftx=0.001f;
                    if (rectrightx - rectleftx < 100)
                        moveleftx = savepreviousmovex;
                }
                if (rarrowclicked) {
                    float savepreviousmovex;
                    savepreviousmovex = moverightx;
                    moverightx = (width / 2 + thephoto.getWidth() / 2 - event.getX()) * -1;

                    //minimal size adjustment
                    float rectleftx = width / 2 - thephoto.getWidth() / 2 + moveleftx;
                    float rectrightx = width / 2 + thephoto.getWidth() / 2 + moverightx;
                    //out of pic
                    if(rectrightx > width / 2 + thephoto.getWidth() / 2)
                        moverightx=0.001f;
                    if (rectrightx - rectleftx < 100)
                        moverightx = savepreviousmovex;
                }
                if (uarrowclicked) {
                    float savepreviousmovey;
                    savepreviousmovey = moveupy;
                    moveupy = (height / 2 - thephoto.getHeight() / 2 - event.getY()) * -1;

                    //minimal size adjustment
                    float rectupy = height / 2 - thephoto.getHeight() / 2 + moveupy;
                    float rectdowny = height / 2 + thephoto.getHeight() / 2 + movedowny;
                    //out of pic
                    if(rectupy < height / 2 - thephoto.getHeight() / 2)
                        moveupy=0.001f;
                    if (rectdowny - rectupy < 100)
                        moveupy = savepreviousmovey;
                }
                if (darrowclicked) {
                    float savepreviousmovey;
                    savepreviousmovey = movedowny;
                    movedowny = (height / 2 + thephoto.getHeight() / 2 - event.getY()) * -1;

                    //minimal size adjustment
                    float rectupy = height / 2 - thephoto.getHeight() / 2 + moveupy;
                    float rectdowny = height / 2 + thephoto.getHeight() / 2 + movedowny;
                    //out of pic
                    if(rectdowny > height / 2 + thephoto.getHeight() / 2)
                        movedowny=0.001f;
                    if (rectdowny - rectupy < 100)
                        movedowny = savepreviousmovey;
                }



                break;
        }
        return true;
    }




    private String saveToInternalSorage(Bitmap bitmapImage,String path) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        File mypath = new File(Environment.getExternalStorageDirectory(),path);
        if (mypath.exists())
            mypath.delete();
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);


            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }


}

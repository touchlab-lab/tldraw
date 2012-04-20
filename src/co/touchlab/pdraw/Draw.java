package co.touchlab.pdraw;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import co.touchlab.pdraw.views.DrawView;
import yuku.ambilwarna.AmbilWarnaDialog;

import java.io.*;

public class Draw extends Activity {
    private int myColor = Color.argb(0xff, 0xff, 0xff, 0xff);
    ;
    private View colorBox;
    private DrawView drawView;
    private Button lineWidth;
    private int width = 7;
    private Button clear;
    private Long time;
    private TextView timer;
    private Handler handler;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        time = System.currentTimeMillis();

        setContentView(R.layout.main);

        drawView = (DrawView) findViewById(R.id.drawView);

        drawView.setColor(myColor);
        drawView.setChosenWidth((float) width);

        timer = (TextView) findViewById(R.id.timeLeft);

        colorBox = findViewById(R.id.color);
        colorBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(Draw.this, myColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    public void onCancel(AmbilWarnaDialog dialog) {

                    }

                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        myColor = color;
                        drawView.setColor(myColor);
                        showColor();
                    }
                });

                dialog.show();
            }
        });

        lineWidth = (Button) findViewById(R.id.lineWidthButton);
        lineWidth.setText(width + "");
        lineWidth.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                //To change body of implemented methods use File | Settings | File Templates.
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setTitle("Choose a Line Width");
                alert.setMessage("Enter a Number from 0-100 :");

                // Set an EditText view to get user input
                final EditText input = new EditText(view.getContext());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setText(width + "");
                input.selectAll();
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        width = Integer.parseInt(input.getText().toString());
                        drawView.setChosenWidth((float) width);
                        lineWidth.setText(width + "");

                        return;
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        return;
                    }
                });
                alert.show();
            }
        });

        clear = (Button) findViewById(R.id.clearButton);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setTitle("Are you sure you want to \n clear your drawing?");


                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        drawView.clearAll();

                        return;
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        return;
                    }
                });
                alert.show();


            }
        });

        handler = new Handler();
        new Thread() {
            @Override
            public void run() {
                long timeLeft = 30;
                do{
                    timeLeft = findDiff();

                    final long finalTimeLeft = timeLeft;

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            refreshTimer(finalTimeLeft);
                        }
                    });


                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                    }
                }while(timeLeft > 0);

                stopDrawing();
                saveViewScreenshot(drawView);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder alert = new AlertDialog.Builder(drawView.getContext());
                        alert.setTitle("Your drawing has been saved!");

                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                drawView.clearAll();
                            }
                        });

                        alert.show();
                    }
                });
            }
        }.start();


    }

    private void stopDrawing()
    {
        drawView.setDrawable(false);
    }

    private void showColor() {
        colorBox.setBackgroundColor(myColor);
    }

    private void refreshTimer(long timeLeft) {
        timer.setText(timeLeft + "");
    }

    private long findDiff() {
        long diff = (System.currentTimeMillis() - time) / 1000;
        return 30 - diff;
    }

    public static void saveViewScreenshot( View view)
    {
        Bitmap b;
        if(view.isDrawingCacheEnabled())
        {
            b = view.getDrawingCache();
            saveBitmap(b);
        }
        else
        {
            view.buildDrawingCache();
            b = view.getDrawingCache();
            saveBitmap(b);
            view.destroyDrawingCache();
        }
    }

    public static void saveBitmap( Bitmap bitmap)
    {
        try
        {
            File imageFile = new File( Environment.getExternalStorageDirectory().getPath(), "masterpiece_"+ System.currentTimeMillis() +".png");
            FileOutputStream imgOut = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, imgOut);
            
            imgOut.close();

        }
        catch (Exception e)
        {
            Log.e(Draw.class.getSimpleName(), null, e);
        }
    }
}

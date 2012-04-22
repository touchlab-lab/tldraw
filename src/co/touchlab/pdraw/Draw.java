package co.touchlab.pdraw;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import co.touchlab.pdraw.service.NotifyComplete;
import co.touchlab.pdraw.service.PDrawUploadService;
import co.touchlab.pdraw.service.UploadImage;
import co.touchlab.pdraw.views.DrawView;
import yuku.ambilwarna.AmbilWarnaDialog;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Draw extends Activity
{
    public static final String QR_KEY = "QR_KEY";
    private int myColor = Color.argb(0xff, 0xff, 0xff, 0xff);

    private View colorBox;
    private DrawView drawView;
    private int width = 7;
    private Long time;
    private TextView timer;
    private Handler handler;

    private String qrKey;

    public int getMyColor()
    {
        return myColor;
    }

    public int getWidth()
    {
        return width;
    }

    public boolean isPractice()
    {
        return qrKey == null;
    }

    public void setMyColor(int myColor)
    {
        this.myColor = myColor;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        qrKey = getIntent().getStringExtra(QR_KEY);

        time = System.currentTimeMillis();

        setContentView(R.layout.main);

        drawView = (DrawView) findViewById(R.id.drawView);

        drawView.setDrawActivity(this);

        timer = (TextView) findViewById(R.id.timeLeft);

        colorBox = findViewById(R.id.color);
        colorBox.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(Draw.this, myColor, new AmbilWarnaDialog.OnAmbilWarnaListener()
                {
                    public void onCancel(AmbilWarnaDialog dialog)
                    {

                    }

                    public void onOk(AmbilWarnaDialog dialog, int color)
                    {
                        myColor = color;
                        showColor();
                    }
                });

                dialog.show();
            }
        });

        final Spinner lineWidth = (Spinner) findViewById(R.id.lineWidth);
        List<String> lineWidths = new ArrayList<String>();
        for (int i = 1; i < 50; i += 2)
        {
            lineWidths.add(Integer.toString(i));
        }
        ArrayAdapter<String> aa = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                lineWidths);

        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        lineWidth.setAdapter(aa);
        lineWidth.setSelection(4); //Default to 7. Probably should be more careful here.

        lineWidth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l)
            {
                String widthString = ((ArrayAdapter<String>) lineWidth.getAdapter()).getItem(position);
                width = Integer.parseInt(widthString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

        Button clear = (Button) findViewById(R.id.clearButton);
        clear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setTitle("Are you sure you want to \n clear your drawing?");


                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        drawView.clearAll();

                        return;
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener()
                {

                    public void onClick(DialogInterface dialog, int which)
                    {
                        // TODO Auto-generated method stub
                        return;
                    }
                });
                alert.show();


            }
        });

        handler = new Handler();

        View doneButton = findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                andWereDone();
            }
        });

        if (isPractice())
        {
            findViewById(R.id.timeLeftLabel).setVisibility(View.GONE);
            timer.setVisibility(View.GONE);
            doneButton.setVisibility(View.VISIBLE);
        }
        else
        {
            doneButton.setVisibility(View.GONE);
            new Thread()
            {
                @Override
                public void run()
                {
                    long timeLeft = 30;
                    do
                    {
                        timeLeft = findDiff();

                        final long finalTimeLeft = timeLeft;

                        handler.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                refreshTimer(finalTimeLeft);
                            }
                        });

                        try
                        {
                            sleep(500);
                        }
                        catch (InterruptedException e)
                        {
                        }
                    }
                    while (timeLeft > 0);

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            andWereDone();
                        }
                    });
                }
            }.start();
        }


    }

    private void andWereDone()
    {
        stopDrawing();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving image...");
        new AsyncTask()
        {
            @Override
            protected Object doInBackground(Object... objects)
            {
                File bitmapFile = saveViewScreenshot(drawView);
                PDrawUploadService.startMe(Draw.this, new UploadImage(bitmapFile.getPath()));
                return bitmapFile;
            }

            @Override
            protected void onPostExecute(Object o)
            {
                progressDialog.dismiss();
                finish();
                ShowResult.callMe(Draw.this, (File) o);
            }
        }.execute();
    }

    private void stopDrawing()
    {
        drawView.setDrawable(false);
        if(!isPractice())
            PDrawUploadService.startMe(this, new NotifyComplete(qrKey));
    }

    private void showColor()
    {
        colorBox.setBackgroundColor(myColor);
    }

    private void refreshTimer(long timeLeft)
    {
        timer.setText(timeLeft + "");
    }

    private long findDiff()
    {
        long diff = (System.currentTimeMillis() - time) / 1000;
        return 30 - diff;
    }

    public File saveViewScreenshot(View view)
    {
        Bitmap b;
        File bitmapFile;
        if (view.isDrawingCacheEnabled())
        {
            b = view.getDrawingCache();
            bitmapFile = saveBitmap(b);
        }
        else
        {
            view.buildDrawingCache();
            b = view.getDrawingCache();
            bitmapFile = saveBitmap(b);
            view.destroyDrawingCache();
        }

        return bitmapFile;
    }

    public File saveBitmap(Bitmap bitmap)
    {
        try
        {
            File imageFile = new File(getFilesDir(), "masterpiece_" + UUID.randomUUID().toString() + ".png");
            FileOutputStream imgOut = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, imgOut);

            imgOut.close();

            return imageFile;
        }
        catch (Exception e)
        {
            Log.e(Draw.class.getSimpleName(), null, e);
        }

        return null;
    }

    public static void callMe(Context c, String key)
    {
        Intent intent = new Intent(c, Draw.class);
        intent.putExtra(QR_KEY, key);
        c.startActivity(intent);
    }
}

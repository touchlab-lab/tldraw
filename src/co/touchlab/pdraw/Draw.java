package co.touchlab.pdraw;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import co.touchlab.pdraw.views.ColorPickerDialog;
import co.touchlab.pdraw.views.DrawView;
import yuku.ambilwarna.AmbilWarnaDialog;

public class Draw extends Activity
{
    private int myColor;
    private View colorBox;
    private DrawView drawView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);

        drawView = (DrawView) findViewById(R.id.drawView);

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
                        drawView.setColor(myColor);
                        showColor();
                    }
                });

                dialog.show();
            }
        });
    }

    private void showColor()
    {
        colorBox.setBackgroundColor(myColor);
    }
}

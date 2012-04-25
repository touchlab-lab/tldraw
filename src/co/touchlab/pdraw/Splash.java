package co.touchlab.pdraw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 4/22/12
 * Time: 10:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class Splash extends Activity
{

    private String passedUrl;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        final Intent intent = getIntent();

        if (intent != null && Intent.ACTION_VIEW.equals(intent.getAction()))
        {
            passedUrl = intent.getData().toString();
        }

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                finish();
                Intro.callMe(Splash.this, passedUrl);
            }
        }, 1500);
    }
}
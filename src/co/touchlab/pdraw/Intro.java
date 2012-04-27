package co.touchlab.pdraw;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import co.touchlab.appdebug.proto.Appdebug;
import co.touchlab.ir.process.UploadManagerService;
import co.touchlab.pdraw.utils.Const;
import co.touchlab.pdraw.views.IntentIntegrator;
import co.touchlab.pdraw.views.IntentResult;
import com.google.zxing.client.android.CaptureActivity;
import org.apache.commons.lang3.StringUtils;
import twitter4j.ProfileImage;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: touchlab
 * Date: 4/20/12
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class Intro extends Activity
{

    public static final int OAUTH_REQUEST = 125;
    public static final String SPLASH_URL = "SPLASH_URL";
    private Twitter mTwitter;
    private RequestToken mRequestToken;
    private String twitterHandle;
    private String twitterIcon;
    private Button mLoginButton;
    private Button goButton;
    private String splashUrl;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.intro);

        loadPassedUrl();

        TextView welcome = (TextView) findViewById(R.id.welcome);
        welcome.setText("Touch Lab Remote Draw!");

        TextView intro = (TextView) findViewById(R.id.intro);
        intro.setText("\nCome decorate (or deface) our website.  You have 60 seconds to create" +
                " your masterpiece! Watch it draw dynamically on the website! \n\n" +
                "How to get started: \n\n" +
                "1. Go to www.touchlab.co \t(on a different device). \n\n" +
                "2. Authenticate with twitter (we don't read your tweets or do anything). \n\n" +
                "3. Scan the QR code on screen. \n\n" +
                "4. Draw! \n\n" +
                "You can practice drawing first if you'd like.");

        goButton = (Button) findViewById(R.id.go);
        goButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //runQRScanner();
                Intent intent = new Intent(Intro.this, CaptureActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.practice).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                practiceDrawing();
            }
        });

        mLoginButton = (Button) findViewById(R.id.openAuth);
        mLoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ConfigurationBuilder confbuilder = new ConfigurationBuilder();
                Configuration conf = confbuilder
                        .setOAuthConsumerKey(Const.CONSUMER_KEY)
                        .setOAuthConsumerSecret(Const.CONSUMER_SECRET)
                        .build();
                mTwitter = new TwitterFactory(conf).getInstance();
                mTwitter.setOAuthAccessToken(null);
                try
                {
                    mRequestToken = mTwitter.getOAuthRequestToken(Const.CALLBACK_URL);
                    Intent intent = new Intent(Intro.this, TwitterLogin.class);
                    intent.putExtra(Const.IEXTRA_AUTH_URL, mRequestToken.getAuthorizationURL());
                    startActivityForResult(intent, OAUTH_REQUEST);
                }
                catch (TwitterException e)
                {
                    e.printStackTrace();
                }
            }
        });

        refreshTwitterHandle();
    }

    private void loadPassedUrl()
    {
        Intent intent = getIntent();
        if(intent != null)
            splashUrl = intent.getStringExtra(SPLASH_URL);
    }

    private void runQRScanner()
    {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    @SuppressWarnings("unchecked")
    private void grabConch(final String qrUrl) throws IOException
    {
        final String qrCodeValue = StringUtils.substring(qrUrl, qrUrl.lastIndexOf('/') + 1);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Grabbing Conch...");
        progressDialog.show();
        new AsyncTask()
        {
            @Override
            protected Object doInBackground(Object... objects)
            {
                Appdebug.IssueReportResponseTO checkReturnTO = null;
                try
                {
                    InputStream result = UploadManagerService.callServerWithPayload("drawOnHomepage", null, qrCodeValue, twitterHandle);
                    checkReturnTO = Appdebug.IssueReportResponseTO.parseFrom(result);
                }
                catch (IOException e)
                {
                    Log.e(getClass().getSimpleName(), null, e);
                }

                return checkReturnTO;
            }

            @Override
            protected void onPostExecute(Object o)
            {
                progressDialog.dismiss();
                Appdebug.IssueReportResponseTO checkReturnTO = (Appdebug.IssueReportResponseTO) o;
                if (checkReturnTO.getSuccess())
                {
                    startDrawing(qrCodeValue);
                }
                else
                {
                    new AlertDialog.Builder(Intro.this)
                            .setMessage("Sorry. Looks like somebody else is drawing. " +
                                    "Try agian in a couple minutes.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialogInterface, int i)
                                {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                }
            }
        }.execute();
    }

    @SuppressWarnings("unchecked")
    private void refreshTwitterHandle()
    {
        new AsyncTask()
        {
            @Override
            protected Object doInBackground(Object... objects)
            {
                try
                {
                    twitterHandle = findTwitterHandle();
                    if(twitterHandle != null)
                    {
                        twitterIcon = findTwitterIcon(twitterHandle);
                    }
                }
                catch (TwitterException e)
                {
                    Log.e(getClass().getSimpleName(), null, e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o)
            {
                ((TextView)findViewById(R.id.twitterHandle)).setText(twitterHandle);
                if(twitterIcon != null)
                {
                    try
                    {
                        Drawable thumb_d = Drawable.createFromStream(new URL(twitterIcon).openStream(), "src");
                        ((ImageView)findViewById(R.id.twitterIcon)).setImageDrawable(thumb_d);
                    }
                    catch (IOException e)
                    {
                        Log.e(getClass().getSimpleName(), null, e);
                    }
                }

                if(twitterHandle == null)
                {
                    mLoginButton.setVisibility(View.VISIBLE);
                    goButton.setEnabled(false);
                    if(splashUrl != null)
                    {
                        splashUrl = null;
                        Toast.makeText(Intro.this, "You must log into twitter first", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    mLoginButton.setVisibility(View.GONE);
                    goButton.setEnabled(true);
                    if(splashUrl != null)
                    {
                        String passingUrl = splashUrl;
                        splashUrl = null;
                        try
                        {
                            grabConch(passingUrl);
                        }
                        catch (IOException e)
                        {
                            Log.e(Intro.class.getSimpleName(), null, e);
                        }
                    }
                }
            }
        }.execute();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if (requestCode == OAUTH_REQUEST)
        {
            if (resultCode == RESULT_OK)
            {
                AccessToken accessToken = null;
                try
                {
                    String oauthVerifier = intent.getExtras().getString(Const.IEXTRA_OAUTH_VERIFIER);
                    accessToken = mTwitter.getOAuthAccessToken(mRequestToken, oauthVerifier);
                    TwitterPrefs.setAccessToken(this, accessToken.getToken(), accessToken.getTokenSecret());

                    Toast.makeText(this, "authorized", Toast.LENGTH_SHORT).show();
                }
                catch (TwitterException e)
                {
                    e.printStackTrace();
                }
                refreshTwitterHandle();
            }
            else if (resultCode == RESULT_CANCELED)
            {
                Log.w(getClass().getSimpleName(), "Twitter auth canceled.");
            }
        }
        else
        {
            if(resultCode == RESULT_OK)
            {
                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
                if (scanResult != null)
                {
                    try
                    {
                        grabConch(scanResult.getContents());
                    }
                    catch (IOException e)
                    {
                        Log.e(getClass().getSimpleName(), null, e);
                    }
                }
            }
        }
    }

    private String findTwitterIcon(String twitterHandle) throws TwitterException
    {
        Twitter twitter = initTwitter();
        if(twitter == null)
            return null;
        return twitter.getProfileImage(twitterHandle, ProfileImage.NORMAL).getURL();
    }

    private String findTwitterHandle() throws TwitterException
    {
        String handle = TwitterPrefs.getTwitterHandle(this);
        if(handle != null)
            return handle;

        Twitter twitter = initTwitter();
        if(twitter == null)
            return null;

        String remoteHandle = twitter.getScreenName();
        TwitterPrefs.setTwitterHandle(this, remoteHandle);

        return remoteHandle;
    }

    private Twitter initTwitter()
    {
        if (mTwitter == null) {
            ConfigurationBuilder confbuilder = new ConfigurationBuilder();
            Configuration conf = confbuilder
                    .setOAuthConsumerKey(Const.CONSUMER_KEY)
                    .setOAuthConsumerSecret(Const.CONSUMER_SECRET)
                    .build();
            mTwitter = new TwitterFactory(conf).getInstance();
        }

        SharedPreferences pref = getSharedPreferences(Const.PREF_NAME, MODE_PRIVATE);
        String accessToken = pref.getString(Const.PREF_KEY_ACCESS_TOKEN, null);
        String accessTokenSecret = pref.getString(Const.PREF_KEY_ACCESS_TOKEN_SECRET, null);
        if (accessToken == null || accessTokenSecret == null) {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(Intro.this, "not authorized yet", Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }
        mTwitter.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret));
        return mTwitter;
    }

    private void practiceDrawing()
    {
        Draw.callMe(this, null);
    }

    private void startDrawing(final String qrKey)
    {
        new AlertDialog.Builder(this)
                .setMessage("OK! Get ready to draw! You have 60 seconds to create your masterpiece." +
                        " Go!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        Draw.callMe(Intro.this, qrKey);
                    }
                })
                .show();
    }

    public static void callMe(Context c, String splashUrl)
    {
        Intent intent = new Intent(c, Intro.class);
        intent.putExtra(SPLASH_URL, splashUrl);
        c.startActivity(intent);
    }
}
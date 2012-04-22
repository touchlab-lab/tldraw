package co.touchlab.pdraw;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import co.touchlab.appdebug.proto.Appdebug;
import co.touchlab.ir.process.UploadManagerService;
import co.touchlab.pdraw.views.DrawView;
import co.touchlab.pdraw.views.IntentIntegrator;
import co.touchlab.pdraw.views.IntentResult;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: touchlab
 * Date: 4/20/12
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class Intro extends Activity
{

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.intro);

        TextView welcome = (TextView) findViewById(R.id.welcome);
        welcome.setText("Welcome to Touch Lab");

        TextView intro = (TextView) findViewById(R.id.intro);
        intro.setText("\nYou have 30 seconds to create" +
                "        your own masterpiece to be featured on the company website! \n\n" +
                "How to get started: \n\n" +
                "1. Go to www.touchlab.co \t(on a different device). \n\n" +
                "2. Enter the text code on the \tscreen in the box below. \n\n" +
                "3. Don't draw anything \noffensive.");

        Button goButton;
        goButton = (Button) findViewById(R.id.go);
        goButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                runQRScanner();
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
    }

    private void runQRScanner()
    {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    @SuppressWarnings("unchecked")
    private void grabConch(final String qrCodeValue) throws IOException
    {
        final EditText codeText = (EditText) findViewById(R.id.twitterHandle);
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
                    InputStream result = UploadManagerService.callServerWithPayload("drawOnHomepage", null, qrCodeValue, codeText.getText().toString());
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

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
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

    private void practiceDrawing()
    {
        Draw.callMe(this, null);
    }

    private void startDrawing(final String qrKey)
    {
        new AlertDialog.Builder(this)
                .setMessage("OK! Get ready to draw! You have 30 seconds to create your masterpiece." +
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
}
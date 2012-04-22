package co.touchlab.pdraw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import co.touchlab.ir.util.NetUtils;
import co.touchlab.pdraw.utils.ShareUtils;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 4/21/12
 * Time: 5:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class ShowResult extends Activity
{
    public static final String IMAGE_PATH = "IMAGE_PATH";
    private String picPath;
    private ImageView showPicView;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showresult);

        picPath = getIntent().getStringExtra(IMAGE_PATH);
        showPicView = (ImageView) findViewById(R.id.showPic);

        findViewById(R.id.sharePic).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String imageLink = NetUtils.BASE_URL + "/s3d/" + picPath.substring(picPath.lastIndexOf("/") + 1);
                String message = "I decorated (or defaced) Touch Lab's website! Check out my work! " +
                        imageLink +
                        " http://touchlab.co";
                ShareUtils.callShare(ShowResult.this, message);
            }
        });

        showPic();
    }

    private void showPic()
    {
        new AsyncTask()
        {
            @Override
            protected Object doInBackground(Object... objects)
            {
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bm = BitmapFactory.decodeFile(picPath, options);
                return Bitmap.createScaledBitmap(bm, bm.getWidth()/2, bm.getHeight()/2, true);
            }

            @Override
            protected void onPostExecute(Object o)
            {
                Bitmap imageBitmap = (Bitmap) o;

                showPicView.setImageBitmap(imageBitmap);
            }
        }.execute();
    }

    public static void callMe(Context c, File imagePath)
    {
        Intent intent = new Intent(c, ShowResult.class);
        intent.putExtra(IMAGE_PATH, imagePath.getPath());
        c.startActivity(intent);
    }
}
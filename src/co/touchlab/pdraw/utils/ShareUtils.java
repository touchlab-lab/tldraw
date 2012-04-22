package co.touchlab.pdraw.utils;

import android.app.Activity;
import android.content.Intent;
import co.touchlab.ir.util.NetUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 4/21/12
 * Time: 6:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class ShareUtils
{
    public static void callShare(Activity activity, String body)
    {
        Intent intent=new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        intent.putExtra(Intent.EXTRA_SUBJECT, "I'm a super artist!");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        activity.startActivity(Intent.createChooser(intent, "How do you want to share?"));
    }

    public static void pushPic(File path) throws IOException
    {

        HttpPost method = new HttpPost(NetUtils.BASE_URL + "/s3u/" + path.getName());

        try
        {
            method.setEntity(new InputStreamEntity(new BufferedInputStream(new FileInputStream(path), 2048), path.length()));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        DefaultHttpClient client = new DefaultHttpClient();

        client.execute(method);
    }
}

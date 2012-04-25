package co.touchlab.pdraw;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import co.touchlab.pdraw.utils.Const;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 4/24/12
 * Time: 8:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class TwitterPrefs
{
    private static SharedPreferences findPrefs(Context context)
    {
        return context.getSharedPreferences(Const.PREF_NAME, context.MODE_PRIVATE);
    }

    public static String getAccessToken(Context context)
    {
        return findPrefs(context).getString(Const.PREF_KEY_ACCESS_TOKEN, null);
    }

    public static void setAccessToken(Context context, String token, String tokenSecret)
    {
        SharedPreferences.Editor editor = findPrefs(context).edit();
        editor.putString(Const.PREF_KEY_ACCESS_TOKEN, token);
        editor.putString(Const.PREF_KEY_ACCESS_TOKEN_SECRET, tokenSecret);
        editor.commit();
    }

    public static String getTwitterHandle(Context context)
    {
        return findPrefs(context).getString(Const.PREF_KEY_TWITTER_HANDLE, null);
    }

    public static void setTwitterHandle(Context context, String handle)
    {
        SharedPreferences.Editor editor = findPrefs(context).edit();
        editor.putString(Const.PREF_KEY_TWITTER_HANDLE, handle);
        editor.commit();
    }

    public static File getTwitterIcon(Context context)
    {
        File file = theTwitterPic(context);
        return file.exists() ? file : null;
    }

    private static File theTwitterPic(Context context)
    {
        return new File(context.getFilesDir(), "twitter_pic.png");
    }

    public static void setTwitterHandle(Context context, byte[] pic)
    {
        try
        {
            File file = theTwitterPic(context);
            FileOutputStream outputStream = new FileOutputStream(file);
            IOUtils.copy(new ByteArrayInputStream(pic), outputStream);
            outputStream.close();
        }
        catch (IOException e)
        {
            Log.e(TwitterPrefs.class.getSimpleName(), null, e);
        }
    }
}

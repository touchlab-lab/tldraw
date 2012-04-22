package co.touchlab.pdraw.service;

import android.content.Context;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.PermanentException;
import co.touchlab.android.superbus.TransientException;
import co.touchlab.android.superbus.localfile.JsonFileCommand;
import co.touchlab.pdraw.utils.ShareUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 4/21/12
 * Time: 4:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class UploadImage extends JsonFileCommand
{
    public static final String PATH = "path";
    private String path;

    public UploadImage(String path)
    {
        this.path = path;
    }

    public String getPath()
    {
        return path;
    }

    @Override
    public void writeToStorage(JSONObject jsonObject) throws JSONException
    {
        jsonObject.put(PATH, path);
    }

    @Override
    public void readFromStorage(JSONObject jsonObject) throws JSONException
    {
        path = jsonObject.getString(PATH);
    }

    @Override
    public String logSummary()
    {
        return path;
    }

    @Override
    public boolean same(Command command)
    {
        return command instanceof UploadImage && path.equals(((UploadImage)command).getPath());
    }

    @Override
    public void callCommand(Context context) throws TransientException, PermanentException
    {
        try
        {
            ShareUtils.pushPic(new File(path));
        }
        catch (IOException e)
        {
            throw new TransientException(e);
        }
    }
}

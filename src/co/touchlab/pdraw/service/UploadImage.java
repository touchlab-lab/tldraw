package co.touchlab.pdraw.service;

import android.content.Context;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.PermanentException;
import co.touchlab.android.superbus.TransientException;
import co.touchlab.android.superbus.localfile.JsonFileCommand;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 4/21/12
 * Time: 4:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class UploadImage extends JsonFileCommand
{
    @Override
    public void writeToStorage(JSONObject jsonObject) throws JSONException
    {

    }

    @Override
    public void readFromStorage(JSONObject jsonObject) throws JSONException
    {

    }

    @Override
    public String logSummary()
    {
        return null;
    }

    @Override
    public boolean same(Command command)
    {
        return false;
    }

    @Override
    public void callCommand(Context context) throws TransientException, PermanentException
    {

    }
}

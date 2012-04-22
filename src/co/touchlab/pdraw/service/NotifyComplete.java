package co.touchlab.pdraw.service;

import android.content.Context;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.PermanentException;
import co.touchlab.android.superbus.TransientException;
import co.touchlab.android.superbus.localfile.JsonFileCommand;
import co.touchlab.ir.process.UploadManagerService;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 4/21/12
 * Time: 4:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class NotifyComplete extends JsonFileCommand
{
    private String conchKey;

    public NotifyComplete(String conchKey)
    {
        this.conchKey = conchKey;
    }

    @Override
    public void writeToStorage(JSONObject jsonObject) throws JSONException
    {
        jsonObject.put("key", conchKey);
    }

    @Override
    public void readFromStorage(JSONObject jsonObject) throws JSONException
    {
        conchKey = jsonObject.getString("key");
    }

    @Override
    public String logSummary()
    {
        return "endDrawing: "+ conchKey;
    }

    @Override
    public boolean same(Command command)
    {
        return false;
    }

    @Override
    public void callCommand(Context context) throws TransientException, PermanentException
    {
        try
        {
            UploadManagerService.callServerWithPayload(
                    "endDrawing",
                    null,
                    conchKey
            );
        }
        catch (IOException e)
        {
            throw new TransientException(e);
        }
    }
}

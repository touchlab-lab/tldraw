package co.touchlab.pdraw.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.localfile.LocalFileBusService;
import co.touchlab.android.superbus.localfile.LocalFileCommand;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 4/21/12
 * Time: 4:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class PDrawUploadService extends LocalFileBusService
{
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public LocalFileCommand createCommand(File file)
    {
        return null;
    }

    public static void startMe(Context c, Command sc)
    {
        Intent intent = new Intent(c, PDrawUploadService.class);
        if(sc != null)
            intent.putExtra(SERVICE_COMMAND, sc);
        c.startService(intent);
    }
}

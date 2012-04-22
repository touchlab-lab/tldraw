package co.touchlab.pdraw.service;

import android.content.Context;
import co.touchlab.android.superbus.Command;
import co.touchlab.android.superbus.PermanentException;
import co.touchlab.android.superbus.TransientException;
import co.touchlab.android.superbus.localfile.JsonFileCommand;
import co.touchlab.appdebug.proto.Appdebug;
import co.touchlab.ir.process.UploadManagerService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 4/21/12
 * Time: 4:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class UploadStroke extends JsonFileCommand
{
    private String color;
    private float width;
    private List<Float> points;
    private int canvasWidth;
    private int canvasHeight;

    public UploadStroke(String color, float width, List<Float> points, int canvasWidth, int canvasHeight)
    {
        this.color = color;
        this.width = width;
        this.points = points;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    @Override
    public void writeToStorage(JSONObject jsonObject) throws JSONException
    {
        jsonObject.put("color", color);
        jsonObject.put("width", width);
        jsonObject.put("points", new JSONArray(points));
        jsonObject.put("canvasWidth", canvasWidth);
        jsonObject.put("canvasHeight", canvasHeight);
    }

    @Override
    public void readFromStorage(JSONObject jsonObject) throws JSONException
    {
        color = jsonObject.getString("color");
        width = (float) jsonObject.getDouble("width");
        points = new ArrayList<Float>();
        JSONArray jsonArray = jsonObject.getJSONArray("points");
        for(int i=0; i<jsonArray.length(); i++)
        {
            points.add((float)jsonArray.getDouble(i));
        }

        canvasWidth = (int) jsonObject.getInt("canvasWidth");
        canvasHeight = (int) jsonObject.getInt("canvasHeight");
    }

    @Override
    public String logSummary()
    {
        return "{color: "+ color +", width: "+ width +", "+ points +"}";
    }

    @Override
    public boolean same(Command command)
    {
        return false;
    }

    @Override
    public void callCommand(Context context) throws TransientException, PermanentException
    {
        Appdebug.DrawStrokeTO drawStrokeTO = Appdebug.DrawStrokeTO.newBuilder()
                .setColorhex(color)
                .setWidth((int) width)
                .addAllPoints(points)
                .setCanvasWidth(canvasWidth)
                .setCanvasHeight(canvasHeight)
                .build();
        try
        {
            UploadManagerService.callServerWithPayload(
                    "drawStroke",
                    drawStrokeTO.toByteArray()
            );
        }
        catch (IOException e)
        {
            throw new TransientException(e);
        }
    }
}

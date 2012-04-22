package co.touchlab.pdraw.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created with IntelliJ IDEA.
 * User: kgalligan
 * Date: 4/22/12
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ViewUtils
{
    public static float dipToPixels(Context c, float dip)
    {
        Resources r = c.getResources();

        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
    }
}

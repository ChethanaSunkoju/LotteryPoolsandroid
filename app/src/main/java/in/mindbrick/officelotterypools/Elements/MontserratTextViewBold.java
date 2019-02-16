package in.mindbrick.officelotterypools.Elements;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by Chethana on 4/06/2018.
 */

public class MontserratTextViewBold extends AppCompatTextView {


    public MontserratTextViewBold(Context context) {
        super(context);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "fonts/Jaapokki_Regular.otf");
        this.setTypeface(face);
    }

    public MontserratTextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "fonts/Jaapokki_Regular.otf");
        this.setTypeface(face);
    }

    public MontserratTextViewBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "fonts/Jaapokki_Regular.otf");
        this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }

}

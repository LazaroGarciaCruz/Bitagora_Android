package com.lazarogarciacruz.bitagora.utilidades;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.support.v7.widget.AppCompatTextView;
import com.lazarogarciacruz.bitagora.R;

/**
 * Created by lazarogarciacruz on 1/6/17.
 */

public class CustomFontText extends AppCompatTextView {

    public CustomFontText(Context context) {
        this(context, null);
    }

    public CustomFontText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomFontText(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

        if (isInEditMode())
            return;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomFontText);

        if (ta != null) {

            String fontAsset = ta.getString(R.styleable.CustomFontText_typefaceAsset);

            if (fontAsset != null && !fontAsset.isEmpty()) {
                Typeface tf = FontManager.getInstance().getFont(fontAsset);
                int style = Typeface.NORMAL;
                float size = getTextSize();

                if (getTypeface() != null)
                    style = getTypeface().getStyle();

                if (tf != null)
                    setTypeface(tf, style);
                else
                    Log.d("FontText", String.format("Could not create a font from asset: %s", fontAsset));
            }

        }

    }

}

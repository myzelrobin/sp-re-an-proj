/**
 * 
 */
package com.srandroid.overflow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 *
 */
public class CustomizedPreferenceCategory extends PreferenceCategory 
{
    public CustomizedPreferenceCategory(Context context) {
        super(context);
    }

    public CustomizedPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomizedPreferenceCategory(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onBindView(View view) 
    {
        super.onBindView(view);
        TextView titleView = (TextView) view.findViewById(android.R.id.title);
        titleView.setTextColor(Color.GRAY);
        titleView.setTypeface(null, Typeface.ITALIC);
    }
}

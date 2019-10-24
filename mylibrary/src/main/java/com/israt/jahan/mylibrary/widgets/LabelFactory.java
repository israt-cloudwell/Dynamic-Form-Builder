package com.israt.jahan.mylibrary.widgets;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;


import com.israt.jahan.mylibrary.R;
import com.israt.jahan.mylibrary.interfaces.CommonListener;
import com.israt.jahan.mylibrary.interfaces.FormWidgetFactory;

import static com.israt.jahan.mylibrary.utils.FormUtils.FONT_BOLD_PATH;
import static com.israt.jahan.mylibrary.utils.FormUtils.WRAP_CONTENT;
import static com.israt.jahan.mylibrary.utils.FormUtils.getLayoutParams;
import static com.israt.jahan.mylibrary.utils.FormUtils.getTextViewWith;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vijay on 24-05-2015.
 */
public class LabelFactory implements FormWidgetFactory {
    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject jsonObject, CommonListener listener) throws Exception {
        List<View> views = new ArrayList<>(1);
        LinearLayout.LayoutParams layoutParams = getLayoutParams(WRAP_CONTENT, WRAP_CONTENT, 0, 0, 0, (int) context
                .getResources().getDimension(R.dimen.default_bottom_margin));
        views.add(getTextViewWith(context, 16, jsonObject.getString("text"), jsonObject.getString("key"),
                jsonObject.getString("type"), layoutParams, FONT_BOLD_PATH));
        return views;
    }

}

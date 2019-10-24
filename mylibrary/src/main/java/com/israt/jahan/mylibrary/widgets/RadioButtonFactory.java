package com.israt.jahan.mylibrary.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;


import com.israt.jahan.mylibrary.R;
import com.israt.jahan.mylibrary.constants.JsonFormConstants;
import com.israt.jahan.mylibrary.customviews.RadioButton;
import com.israt.jahan.mylibrary.interfaces.CommonListener;
import com.israt.jahan.mylibrary.interfaces.FormWidgetFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.israt.jahan.mylibrary.utils.FormUtils.FONT_BOLD_PATH;
import static com.israt.jahan.mylibrary.utils.FormUtils.FONT_REGULAR_PATH;
import static com.israt.jahan.mylibrary.utils.FormUtils.MATCH_PARENT;
import static com.israt.jahan.mylibrary.utils.FormUtils.WRAP_CONTENT;
import static com.israt.jahan.mylibrary.utils.FormUtils.getLayoutParams;
import static com.israt.jahan.mylibrary.utils.FormUtils.getTextViewWith;


/**
 * Created by vijay on 24-05-2015.
 */
public class RadioButtonFactory implements FormWidgetFactory {
    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject jsonObject, CommonListener listener) throws Exception {
        List<View> views = new ArrayList<>(1);
        views.add(getTextViewWith(context, 16, jsonObject.getString("label"), jsonObject.getString("key"),
                jsonObject.getString("type"), getLayoutParams(MATCH_PARENT, WRAP_CONTENT, 0, 0, 0, 0),
                FONT_BOLD_PATH));
        JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        for (int i = 0; i < options.length(); i++) {
            JSONObject item = options.getJSONObject(i);
            RadioButton radioButton = (RadioButton) LayoutInflater.from(context).inflate(R.layout.item_radiobutton,
                    null);
            radioButton.setText(item.getString("text"));
            radioButton.setTag(R.id.key, jsonObject.getString("key"));
            radioButton.setTag(R.id.type, jsonObject.getString("type"));
            radioButton.setTag(R.id.childKey, item.getString("key"));
            radioButton.setGravity(Gravity.CENTER_VERTICAL);
            radioButton.setTextSize(16);
            radioButton.setTypeface(Typeface.createFromAsset(context.getAssets(), FONT_REGULAR_PATH));
            radioButton.setOnCheckedChangeListener(listener);
            if (!TextUtils.isEmpty(jsonObject.optString("value"))
                    && jsonObject.optString("value").equals(item.getString("key"))) {
                radioButton.setChecked(true);
            }
            if (i == options.length() - 1) {
                radioButton.setLayoutParams(getLayoutParams(MATCH_PARENT, WRAP_CONTENT, 0, 0, 0, (int) context
                        .getResources().getDimension(R.dimen.extra_bottom_margin)));
            }
            views.add(radioButton);
        }
        return views;
    }
}

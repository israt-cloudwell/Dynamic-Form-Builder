package com.israt.jahan.mylibrary.customviews;

import org.json.JSONException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.widget.TintContextWrapper;

import com.israt.jahan.mylibrary.R;
import com.israt.jahan.mylibrary.interfaces.JsonApi;


public class GenericTextWatcher implements TextWatcher {

    private View   mView;
    private String mStepName;

    public GenericTextWatcher(String stepName, View view) {
        mView = view;
        mStepName = stepName;
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void afterTextChanged(Editable editable) {
        String text = editable.toString();
        JsonApi api = null;
        if(mView.getContext() instanceof JsonApi) {
            api = (JsonApi) mView.getContext();
        } else if(mView.getContext() instanceof TintContextWrapper) {
            TintContextWrapper tintContextWrapper = (TintContextWrapper) mView.getContext();
            api = (JsonApi) tintContextWrapper.getBaseContext();
        } else {
            throw new RuntimeException("Could not fetch context");
        }

        String key = (String) mView.getTag(R.id.key);
        try {
            api.writeValue(mStepName, key, text);
        } catch (JSONException e) {
            // TODO- handle
            e.printStackTrace();
        }
    }
}
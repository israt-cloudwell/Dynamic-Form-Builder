package com.vijay.jsonwizard.presenters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.israt.jahan.mylibrary.R;
import com.israt.jahan.mylibrary.constants.JsonFormConstants;
import com.israt.jahan.mylibrary.customviews.RadioButton;
import com.israt.jahan.mylibrary.fragments.JsonFormFragment;
import com.israt.jahan.mylibrary.interactors.JsonFormInteractor;
import com.israt.jahan.mylibrary.mvp.MvpBasePresenter;
import com.israt.jahan.mylibrary.utils.ImageUtils;
import com.israt.jahan.mylibrary.utils.ValidationStatus;
import com.israt.jahan.mylibrary.views.JsonFormFragmentView;
import com.israt.jahan.mylibrary.viewstates.JsonFormFragmentViewState;
import com.israt.jahan.mylibrary.widgets.EditTextFactory;
import com.israt.jahan.mylibrary.widgets.ImagePickerFactory;
import com.israt.jahan.mylibrary.widgets.SpinnerFactory;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

import static com.israt.jahan.mylibrary.utils.FormUtils.dpToPixels;


/**
 * Created by vijay on 5/14/15.
 */
public class JsonFormFragmentPresenter extends MvpBasePresenter<JsonFormFragmentView<JsonFormFragmentViewState>> {
    private static final String TAG = "FormFragmentPresenter";
    private static final int RESULT_LOAD_IMG = 1;
    private String mStepName;
    private JSONObject mStepDetails;
    private String mCurrentKey;
    private JsonFormInteractor mJsonFormInteractor = JsonFormInteractor.getInstance();

    public void addFormElements() {
        mStepName = getView().getArguments().getString("stepName");
        JSONObject step = getView().getStep(mStepName);
        try {
            mStepDetails = new JSONObject(step.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        List<View> views = mJsonFormInteractor.fetchFormElements(mStepName, getView().getContext(), mStepDetails,
                getView().getCommonListener());
        getView().addFormElements(views);
    }

    @SuppressLint("ResourceAsColor")
    public void setUpToolBar() {
        if (!mStepName.equals(JsonFormConstants.FIRST_STEP_NAME)) {
            getView().setUpBackButton();
        }
        getView().setActionBarTitle(mStepDetails.optString("title"));
        if (mStepDetails.has("next")) {
            getView().updateVisibilityOfNextAndSave(true, false);
        } else {
            getView().updateVisibilityOfNextAndSave(false, true);
        }
        getView().setToolbarTitleColor(R.color.white);
    }

    public void onBackClick() {
        getView().hideKeyBoard();
        getView().backClick();
    }

    public void onNextClick(LinearLayout mainView) {
        ValidationStatus validationStatus = writeValuesAndValidate(mainView);
        if (validationStatus.isValid()) {
            JsonFormFragment next = (JsonFormFragment) JsonFormFragment.getFormFragment(mStepDetails.optString("next"));
            getView().hideKeyBoard();
            getView().transactThis(next);
        } else {
            getView().showToast(validationStatus.getErrorMessage());
        }
    }

    public ValidationStatus writeValuesAndValidate(LinearLayout mainView) {
        int childCount = mainView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = mainView.getChildAt(i);
            String key = (String) childAt.getTag(R.id.key);
            if (childAt instanceof MaterialEditText) {
                MaterialEditText editText = (MaterialEditText) childAt;
                ValidationStatus validationStatus = EditTextFactory.validate(editText);
                if (!validationStatus.isValid()) {
                    return validationStatus;
                }
                getView().writeValue(mStepName, key, editText.getText().toString());
            } else if (childAt instanceof ImageView) {
                ValidationStatus validationStatus = ImagePickerFactory.validate((ImageView) childAt);
                if (!validationStatus.isValid()) {
                    return validationStatus;
                }
                Object path = childAt.getTag(R.id.imagePath);
                if (path instanceof String) {
                    getView().writeValue(mStepName, key, (String) path);
                }
            } else if (childAt instanceof CheckBox) {
                String parentKey = (String) childAt.getTag(R.id.key);
                String childKey = (String) childAt.getTag(R.id.childKey);
                getView().writeValue(mStepName, parentKey, JsonFormConstants.OPTIONS_FIELD_NAME, childKey,
                        String.valueOf(((CheckBox) childAt).isChecked()));
            } else if (childAt instanceof RadioButton) {
                String parentKey = (String) childAt.getTag(R.id.key);
                String childKey = (String) childAt.getTag(R.id.childKey);
                if (((RadioButton) childAt).isChecked()) {
                    getView().writeValue(mStepName, parentKey, childKey);
                }
            } else if (childAt instanceof MaterialSpinner) {
                MaterialSpinner spinner = (MaterialSpinner) childAt;
                ValidationStatus validationStatus = SpinnerFactory.validate(spinner);
                if (!validationStatus.isValid()) {
                    spinner.setError(validationStatus.getErrorMessage());
                    return validationStatus;
                } else {
                    spinner.setError(null);
                }
            }
        }
        return new ValidationStatus(true, null);
    }

    public void onSaveClick(LinearLayout mainView) {
        ValidationStatus validationStatus = writeValuesAndValidate(mainView);
        if (validationStatus.isValid()) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("json", getView().getCurrentJsonState());
            getView().finishWithResult(returnIntent);
        } else {
            Toast.makeText(getView().getContext(), validationStatus.getErrorMessage(), Toast.LENGTH_LONG);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            // No need for null check on cursor
            Cursor cursor = getView().getContext().getContentResolver()
                    .query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            getView().updateRelevantImageView(ImageUtils.loadBitmapFromFile(imagePath, ImageUtils.getDeviceWidth(getView().getContext()), dpToPixels(getView().getContext(), 200)), imagePath, mCurrentKey);
            cursor.close();
        }
    }

    public void onClick(View v) {
        String key = (String) v.getTag(R.id.key);
        String type = (String) v.getTag(R.id.type);
        if (JsonFormConstants.CHOOSE_IMAGE.equals(type)) {
            getView().hideKeyBoard();
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            mCurrentKey = key;
            getView().startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
        }
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (compoundButton instanceof CheckBox) {
            String parentKey = (String) compoundButton.getTag(R.id.key);
            String childKey = (String) compoundButton.getTag(R.id.childKey);
            getView().writeValue(mStepName, parentKey, JsonFormConstants.OPTIONS_FIELD_NAME, childKey,
                    String.valueOf(((CheckBox) compoundButton).isChecked()));
        } else if (compoundButton instanceof RadioButton) {
            if (isChecked) {
                String parentKey = (String) compoundButton.getTag(R.id.key);
                String childKey = (String) compoundButton.getTag(R.id.childKey);
                getView().unCheckAllExcept(parentKey, childKey);
                getView().writeValue(mStepName, parentKey, childKey);
            }
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String parentKey = (String) parent.getTag(R.id.key);
        if (position >= 0) {
            String value = (String) parent.getItemAtPosition(position + 1);
            getView().writeValue(mStepName, parentKey, value);
        }
    }
}

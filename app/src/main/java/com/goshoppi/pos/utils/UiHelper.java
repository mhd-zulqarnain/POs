package com.goshoppi.pos.utils;

import android.app.Activity;
import android.content.Context;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.goshoppi.pos.R;

import java.util.ArrayList;
import java.util.List;

public class UiHelper {
    public static EditText focusedEditext = null;

    /**
     * Validate if edittext is empty
     *
     * @param editText
     * @param errorMessage
     * @return
     */

    public static boolean isValidEditText(EditText editText, String errorMessage) {
        try {
            boolean isValid = true;

            if (editText.getText().length() == 0)
                isValid = false;

            if (isValid)
                setTextInputError(editText, null);
            else
                setTextInputError(editText, errorMessage);

            return isValid;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean islenghtEqualOrGreater(EditText editText, String errorMessage, int length) {
        try {
            boolean isValid = true;

            if (editText.getText().length() < length)
                isValid = false;

            if (isValid)
                setTextInputError(editText, null);
            else
                setTextInputError(editText, errorMessage);

            return isValid;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean islenghtEqualOrLess(EditText editText, String errorMessage, int length) {
        try {
            boolean isValid = true;

            if (editText.getText().length() > length)
                isValid = false;

            if (isValid)
                setTextInputError(editText, null);
            else
                setTextInputError(editText, errorMessage);

            return isValid;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean islenghtEqual(EditText editText, String errorMessage, int length) {
        try {
            boolean isValid = true;

            if (editText.getText().length() != length)
                isValid = false;

            if (isValid)
                setTextInputError(editText, null);
            else
                setTextInputError(editText, errorMessage);

            return isValid;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isAlphaOrNumOrAlphaNum(EditText editText, String errorMessage) {
        boolean isValid = editText.getText().toString().matches("^[a-zA-Z0-9]*$");
        if (isValid)
            setTextInputError(editText, null);
        else
            setTextInputError(editText, errorMessage);

        return isValid;

    }

    public static boolean isAlphaNumeric(EditText editText, String errorMessage) {
        String str = editText.getText().toString();
        boolean isValid = true;
        try {
            Double.parseDouble(str);
            isValid = false;
        } catch (Exception ex) {
        }
        isValid = str.matches(".*\\d.*");

        if (isValid)
            setTextInputError(editText, null);
        else
            setTextInputError(editText, errorMessage);

        return isValid;
    }

    /**
     * Validate if number is valid uae mobile number
     *
     * @param editText
     * @param errorMessage
     * @return
     */

    public static boolean isValidUaeMobileNo(EditText editText, String errorMessage) {
        try {
            boolean isValid = true;
            //TextInputLayout textInputLayout = (TextInputLayout) editText.getParent();

            if (editText.getText().toString().length() != 10)
                isValid = false;

            if (!editText.getText().toString().startsWith("05"))
                isValid = false;

            if (isValid)
                setTextInputError(editText, null);
            else
                setTextInputError(editText, errorMessage);

            return isValid;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Validate if emailId is valid
     *
     * @param editText
     * @param errorMessage
     * @return
     */
    public static boolean isValidEmail(EditText editText, String errorMessage) {
        try {
            boolean isValid = true;

            if (TextUtils.isEmpty(editText.getText().toString()))
                isValid = false;
            else
                isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches();

            if (isValid)
                setTextInputError(editText, null);
            else
                setTextInputError(editText, errorMessage);

            return isValid;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Set error text for textinputlayout and focus first invalid editext
     *
     * @param editText
     * @param errorMessage
     */
    public static void setTextInputError(EditText editText, String errorMessage) {
        try {
            TextInputLayout textInputLayout = null;
            if (editText.getParent() instanceof TextInputLayout)
                textInputLayout = (TextInputLayout) editText.getParent();
            else if (editText.getParent().getParent() instanceof TextInputLayout)
                textInputLayout = (TextInputLayout) editText.getParent().getParent();

            if (textInputLayout != null)
                textInputLayout.setError(errorMessage);
            else {
                editText.setError(errorMessage);
            }

            //Focus editext if this is the first invalid editext and if the error text set is not null
            if (focusedEditext == null && errorMessage != null) {
                editText.clearFocus();
                editText.requestFocus();
                focusedEditext = editText;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTextViewError(TextView textview, String errorMessage) {
        try {
            TextInputLayout textInputLayout = null;

            if (textview.getParent() instanceof TextInputLayout)
                textInputLayout = (TextInputLayout) textview.getParent();
            else if (textview.getParent().getParent() instanceof TextInputLayout)
                textInputLayout = (TextInputLayout) textview.getParent().getParent();


            if (textInputLayout != null)
                textInputLayout.setError(errorMessage);

            //Focus editext if this is the first invalid editext and if the error text set is not null
            if (focusedEditext == null && errorMessage != null) {
                textview.clearFocus();
                textview.requestFocus();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Show a editext as mandatory field
     *
     * @param editText
     */
    public static void setMandatoryField(EditText editText) {
        try {
            TextInputLayout textInputLayout = null;
            if (editText.getParent() instanceof TextInputLayout)
                textInputLayout = (TextInputLayout) editText.getParent();
            else if (editText.getParent().getParent() instanceof TextInputLayout)
                textInputLayout = (TextInputLayout) editText.getParent().getParent();

            if (textInputLayout != null) {
                if (!textInputLayout.getHint().toString().contains("*"))
                    textInputLayout.setHint(textInputLayout.getHint() + "*");
            } else {
                if (!editText.getHint().toString().contains("*"))
                    editText.setHint(editText.getHint() + "*");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeMandatoryField(EditText editText) {
        try {
            TextInputLayout textInputLayout = null;
            if (editText.getParent() instanceof TextInputLayout)
                textInputLayout = (TextInputLayout) editText.getParent();
            else if (editText.getParent().getParent() instanceof TextInputLayout)
                textInputLayout = (TextInputLayout) editText.getParent().getParent();

            if (textInputLayout != null) {
                String strHint = textInputLayout.getHint().toString();
                if (strHint.contains("*"))
                    textInputLayout.setHint(strHint.substring(0, strHint.length() - 1));
            } else {
                String strHint = editText.getHint().toString();
                if (strHint.contains("*"))
                    editText.setHint(strHint.substring(0, strHint.length() - 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface SpinnerListener<T> {
        void onItemSelected(T selectedItem, int selectedIndex);
    }



    public static <T> void setupFloatingSpinner(final TextInputEditText editText, final List<T> list, String selected, final SpinnerListener<T> listener, final Context context) {
        try {

            editText.setText(selected);
            editText.setInputType(InputType.TYPE_NULL);
            editText.setFocusableInTouchMode(false);
            editText.setCursorVisible(false);
            editText.setTag("-1"); // This to prevent null tag value being accessed before an item is selected

            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard((Activity) context);
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        hideSoftKeyboard((Activity) context);
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        View layout = inflater.inflate(R.layout.spinner_list, null);
                        final PopupWindow popupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                        //popupWindow.showAtLocation(anchorView, Gravity.CENTER, (int) anchorView.getX(), (int) anchorView.getY());
                        //popupWindow.showAsDropDown(editText, (int) editText.getX(), (int) editText.getY());
                        popupWindow.update(0, 0, editText.getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
                        popupWindow.showAsDropDown(editText, 0, 0);

                        ArrayList<String> menuNames = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            menuNames.add(list.get(i).toString());
                        }
                        ListView listView = (ListView) layout.findViewById(R.id.lvMenu);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, menuNames);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                editText.setText(list.get(position).toString());
                                editText.setTag(position);

                                if (listener != null)
                                    listener.onItemSelected(list.get(position), position);

                                popupWindow.dismiss();
                            }
                        });

                        if (list.size() == 1)
                            listView.performItemClick(listView.getChildAt(0), 0, listView.getAdapter().getItemId(0));

                    }
                    return false;
                }
            });

            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        hideSoftKeyboard((Activity) context);
                    }
                }
            });

            if (list.size() == 1) {
                editText.setText(list.get(0).toString());
                editText.setTag(0);
                editText.dispatchTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 100, 100, 0.5f, 5, 0, 1, 1, 0, 0));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {

            if (activity == null) return;

            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

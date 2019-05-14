import android.app.Service;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomView extends FrameLayout {
    private int num;
    private LinearLayout lin;
    private EditText edit;
    private int textSize;
    private int boxBgId;
    private int textColor;
    private int childWidth;
    private int childHeight;
    private String inputType;
    private int childVPadding;
    private int childHPadding;
    private final static String TYPE_NUMBER = "number";
    private final static String TYPE_TEXT = "text";
    private final static String TYPE_PASSWORD = "password";
    private final String Tag = CustomView.class.getSimpleName();
    private String pwdStr;
    private OnCompleteLinster mlistener;

    public CustomView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomView);
        num = a.getInt(R.styleable.CustomView_num, 4);
        childHPadding = (int) a.getDimension(R.styleable.CustomView_child_h_padding, 0);
        childVPadding = (int) a.getDimension(R.styleable.CustomView_child_v_padding, 0);
        boxBgId = a.getResourceId(R.styleable.CustomView_boxBackground, 0);
        inputType = a.getString(R.styleable.CustomView_inputType);
        pwdStr = a.getString(R.styleable.CustomView_pwdStr);
        setPwdStr(pwdStr);
        childWidth = (int) a.getDimension(R.styleable.CustomView_child_width, 120);
        childHeight = (int) a.getDimension(R.styleable.CustomView_child_height, 120);
        textColor = a.getInt(R.styleable.CustomView_textColor, Color.BLACK);
        textSize = px2sp(a.getDimensionPixelSize(R.styleable.CustomView_textSize, 20));

        a.recycle();
        initView();
    }

    private void setPwdStr(String s) {
        if (TextUtils.isEmpty(s)) {
            pwdStr = "*";
            return;
        } else {
            if (s.length() > 1) {
                throw new IllegalArgumentException("pwdStr length must be 1!!!");
            }
        }
    }

    public String getText() {
        return edit.getText().toString();
    }

    private void initView() {
        edit = new EditText(getContext());
        edit.setTextColor(Color.TRANSPARENT);
        edit.setBackgroundColor(Color.TRANSPARENT);
        edit.setLongClickable(false);
        edit.setTextIsSelectable(false);
        edit.setCursorVisible(false);
        edit.setSingleLine();
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                for (int i = 0; i < num; i++) {
                    String str = "";
                    if (i < s.length()) {
                        str = s.subSequence(i, i + 1).toString();
                    }
                    if (lin != null) {
                        TextView text = (TextView) lin.getChildAt(i);
                        if (TYPE_PASSWORD.equals(inputType) && !TextUtils.isEmpty(str)) {
                            text.setText(pwdStr);
                        } else {
                            text.setText(str);
                        }
                    }
                }

                if (s != null && s.length() == num) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    if (mlistener != null) {
                        mlistener.onComplete(s.toString());
                    }
                }
            }
        });
        if (TYPE_PASSWORD.equals(inputType)) {
            edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
            edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else if (TYPE_TEXT.equals(inputType)) {
            edit.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            edit.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        edit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(num)});
        edit.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(edit);
        lin = new LinearLayout(getContext());
        lin.setGravity(Gravity.CENTER);
        lin.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(childWidth, childHeight);
        params.leftMargin = childHPadding;
        params.rightMargin = childHPadding;
        params.topMargin = childVPadding;
        params.bottomMargin = childVPadding;
        for (int i = 0; i < num; i++) {
            TextView text = new TextView(getContext());
            text.setBackgroundDrawable(getResources().getDrawable(boxBgId));
            text.setGravity(Gravity.CENTER);
            text.setLayoutParams(params);
            text.setTextSize(textSize);
            text.setTextColor(textColor);
            lin.addView(text);
        }
        addView(lin);
    }

    public void setCompleteListener(OnCompleteLinster listener) {
        this.mlistener = listener;
    }

    private int px2sp(float pxValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public interface OnCompleteLinster {
        void onComplete(String str);
    }
}

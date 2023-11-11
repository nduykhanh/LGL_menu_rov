//Please don't replace listeners with lambda!

package com.android.support;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.widget.RelativeLayout.ALIGN_PARENT_LEFT;
import static android.widget.RelativeLayout.ALIGN_PARENT_RIGHT;

import org.xml.sax.ErrorHandler;

import com.android.support.esp.EspView;
import android.graphics.Canvas;

public class Menu {
    //********** Here you can easly change the menu appearance **********//

    //region Variable
    public static final String TAG = "Mod_Menu"; //Tag for logcat

    int TEXT_COLOR = Color.parseColor("#8e9297");
    int TEXT_COLOR_2 = Color.parseColor("#cdced0");
    int BTN_COLOR = Color.parseColor("#36393f");
    int MENU_BG_COLOR = Color.parseColor("#202225"); //#AARRGGBB
    int MENU_FEATURE_BG_COLOR = Color.parseColor("#2f3136"); //#AARRGGBB
    int MENU_WIDTH = 280;
    int MENU_HEIGHT = 210;
    int POS_X = 0;
    int POS_Y = 100;

    float MENU_CORNER = 40f;
    int ICON_SIZE = 45; //Change both width and height of image
    float ICON_ALPHA = 200.7f; //Transparent
    int ToggleON = Color.parseColor("#3ba55d");
    int ToggleOFF = Color.parseColor("#72767d");
    int BtnON = Color.parseColor("#1b5e20");
    int BtnOFF = Color.parseColor("#7f0000");
    int CategoryBG = Color.parseColor("#292b2f");
    int SeekBarColor = Color.parseColor("#ffffff");
    int SeekBarProgressColor = Color.parseColor("#5865f2");
    int CheckBoxColor = Color.parseColor("#949cf7");
    int RadioColor = Color.parseColor("#FFFFFF");
    String NumberTxtColor = "#4dd279";
    //********************************************************************//

    RelativeLayout mCollapsed, mRootContainer;
    LinearLayout mExpanded, mods, mSettings, mCollapse;
    LinearLayout.LayoutParams scrlLLExpanded, scrlLL;
    WindowManager mWindowManager;
    WindowManager.LayoutParams vmParams;
    ImageView startimage;
    FrameLayout rootFrame;
    ScrollView scrollView;
    boolean stopChecking, overlayRequired;
    Context getContext;

    //initialize methods from the native library
    native void Init(Context context, TextView title, TextView subTitle);

    native String Icon();

    native String IconWebViewData();

    native String[] GetFeatureList();

    native String[] SettingsList();

    native boolean IsGameLibLoaded();

	public static native void DrawEsp(EspView espView, Canvas canvas);
	
    WindowManager.LayoutParams espParams;
	
	EspView overlayView;

	private int getLayoutType() {
		if (Build.VERSION.SDK_INT >= 26) {
			return 2038;
		}
		if (Build.VERSION.SDK_INT >= 24) {
			return 2002;
		}
		if (Build.VERSION.SDK_INT >= 23) {
			return 2005;
		}
		return 2003;
	}

	public void DrawCanvas() {
		WindowManager.LayoutParams layoutParams;
		this.espParams = layoutParams = new WindowManager.LayoutParams(
			WindowManager.LayoutParams.MATCH_PARENT,
			WindowManager.LayoutParams.MATCH_PARENT,
			this.getLayoutType(),
			WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
			WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
			PixelFormat.TRANSLUCENT);
		layoutParams.gravity = Gravity.TOP | Gravity.START;
		this.espParams.x = 0;
		this.espParams.y = 100;
		this.mWindowManager.addView((View) this.overlayView, (ViewGroup.LayoutParams) this.espParams);
	}
	
	void CreateCanvas(){
		
		overlayView = new EspView(getContext);
		DrawCanvas();
		
	}
	
    //Here we write the code for our Menu
    // Reference: https://www.androidhive.info/2016/11/android-floating-widget-like-facebook-chat-head/
    public Menu(Context context) {

        getContext = context;
        Preferences.context = context;
        rootFrame = new FrameLayout(context); // Global markup
        rootFrame.setOnTouchListener(onTouchListener());
        mRootContainer = new RelativeLayout(context); // Markup on which two markups of the icon and the menu itself will be placed
        mCollapsed = new RelativeLayout(context); // Markup of the icon (when the menu is minimized)
        mCollapsed.setVisibility(View.VISIBLE);
        mCollapsed.setAlpha(ICON_ALPHA);

        //********** The box of the mod menu **********
        mExpanded = new LinearLayout(context); // Menu markup (when the menu is expanded)
        mExpanded.setVisibility(View.GONE);
        mExpanded.setBackgroundColor(MENU_BG_COLOR);
        mExpanded.setOrientation(LinearLayout.VERTICAL);
        // mExpanded.setPadding(1, 1, 1, 1); //So borders would be visible
        mExpanded.setLayoutParams(new LinearLayout.LayoutParams(dp(MENU_WIDTH), WRAP_CONTENT));
        GradientDrawable gdMenuBody = new GradientDrawable();
        gdMenuBody.setCornerRadius(MENU_CORNER); //Set corner
        gdMenuBody.setColor(MENU_BG_COLOR); //Set background color
        gdMenuBody.setStroke(1, Color.parseColor("#32cb00")); //Set border
        //mExpanded.setBackground(gdMenuBody); //Apply GradientDrawable to it

        //********** The icon to open mod menu **********
        startimage = new ImageView(context);
        startimage.setLayoutParams(new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        int applyDimension = (int) TypedValue.applyDimension(1, ICON_SIZE, context.getResources().getDisplayMetrics()); //Icon size
        startimage.getLayoutParams().height = applyDimension;
        startimage.getLayoutParams().width = applyDimension;
        //startimage.requestLayout();
        startimage.setScaleType(ImageView.ScaleType.FIT_XY);
        byte[] decode = Base64.decode("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", 0);
        startimage.setImageBitmap(BitmapFactory.decodeByteArray(decode, 0, decode.length));
        ((ViewGroup.MarginLayoutParams) startimage.getLayoutParams()).topMargin = convertDipToPixels(10);
        //Initialize event handlers for buttons, etc.
        startimage.setOnTouchListener(onTouchListener());
        startimage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mCollapsed.setVisibility(View.GONE);
                mExpanded.setVisibility(View.VISIBLE);
            }
        });

        //********** The icon in Webview to open mod menu **********
        WebView wView = new WebView(context); //Icon size width=\"50\" height=\"50\"
        wView.setLayoutParams(new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        int applyDimension2 = (int) TypedValue.applyDimension(1, ICON_SIZE, context.getResources().getDisplayMetrics()); //Icon size
        wView.getLayoutParams().height = applyDimension2;
        wView.getLayoutParams().width = applyDimension2;
        wView.loadData("<html>" +
                "<head></head>" +
                "<body style=\"margin: 0; padding: 0\">" +
                "<img src=\"" + IconWebViewData() + "\" width=\"" + ICON_SIZE + "\" height=\"" + ICON_SIZE + "\" >" +
                "</body>" +
                "</html>", "text/html", "utf-8");
        wView.setBackgroundColor(0x00000000); //Transparent
        wView.setAlpha(ICON_ALPHA);
        wView.getSettings().setAppCacheEnabled(true);
        wView.setOnTouchListener(onTouchListener());

        //********** Settings icon **********
        TextView settings = new TextView(context); //Android 5 can't show ⚙, instead show other icon instead
        settings.setText(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M ? "⚙" : "\uD83D\uDD27");
        settings.setTextColor(TEXT_COLOR);
        settings.setTypeface(Typeface.DEFAULT_BOLD);
        settings.setTextSize(20.0f);
        RelativeLayout.LayoutParams rlsettings = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        rlsettings.addRule(ALIGN_PARENT_RIGHT);
        settings.setLayoutParams(rlsettings);
        settings.setOnClickListener(new View.OnClickListener() {
            boolean settingsOpen;

            @Override
            public void onClick(View v) {
                try {
                    settingsOpen = !settingsOpen;
                    if (settingsOpen) {
                        scrollView.removeView(mods);
                        scrollView.addView(mSettings);
                        scrollView.scrollTo(0, 0);
                    } else {
                        scrollView.removeView(mSettings);
                        scrollView.addView(mods);
                    }
                } catch (IllegalStateException e) {
                }
            }
        });

        //********** Settings **********
        mSettings = new LinearLayout(context);
        mSettings.setOrientation(LinearLayout.VERTICAL);
        featureList(SettingsList(), mSettings);

        //********** Title **********
        RelativeLayout titleText = new RelativeLayout(context);
        titleText.setPadding(10, 5, 10, 5);
        titleText.setVerticalGravity(16);

        TextView title = new TextView(context);
        title.setTextColor(TEXT_COLOR);
        title.setTextSize(18.0f);
        title.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        rl.addRule(RelativeLayout.CENTER_HORIZONTAL);
        title.setLayoutParams(rl);

        //********** Sub title **********
        TextView subTitle = new TextView(context);
        subTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        subTitle.setMarqueeRepeatLimit(-1);
        subTitle.setSingleLine(true);
        subTitle.setSelected(true);
        subTitle.setTextColor(TEXT_COLOR);
        subTitle.setTextSize(10.0f);
        subTitle.setGravity(Gravity.CENTER);
        subTitle.setPadding(0, 0, 0, 5);

        //********** Mod menu feature list **********
        scrollView = new ScrollView(context);
        //Auto size. To set size manually, change the width and height example 500, 500
        scrlLL = new LinearLayout.LayoutParams(MATCH_PARENT, dp(MENU_HEIGHT));
        scrlLLExpanded = new LinearLayout.LayoutParams(mExpanded.getLayoutParams());
        scrlLLExpanded.weight = 1.0f;
        scrollView.setLayoutParams(Preferences.isExpanded ? scrlLLExpanded : scrlLL);
        scrollView.setBackgroundColor(MENU_FEATURE_BG_COLOR);
        mods = new LinearLayout(context);
        mods.setOrientation(LinearLayout.VERTICAL);

        //********** RelativeLayout for buttons **********
        RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setPadding(10, 3, 10, 3);
        relativeLayout.setVerticalGravity(Gravity.CENTER);

        //**********  Hide/Kill button **********
        RelativeLayout.LayoutParams lParamsHideBtn = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lParamsHideBtn.addRule(ALIGN_PARENT_LEFT);

        Button hideBtn = new Button(context);
        hideBtn.setLayoutParams(lParamsHideBtn);
        hideBtn.setBackgroundColor(Color.TRANSPARENT);
        hideBtn.setText("HIDE/KILL (Hold)");
        hideBtn.setTextColor(TEXT_COLOR);
        hideBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mCollapsed.setVisibility(View.VISIBLE);
                mCollapsed.setAlpha(0);
                mExpanded.setVisibility(View.GONE);
                Toast.makeText(view.getContext(), "Icon hidden. Remember the hidden icon position", Toast.LENGTH_LONG).show();
            }
        });
        hideBtn.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                Toast.makeText(view.getContext(), "Menu killed", Toast.LENGTH_LONG).show();
                rootFrame.removeView(mRootContainer);
                mWindowManager.removeView(rootFrame);
                return false;
            }
        });

        //********** Close button **********
        RelativeLayout.LayoutParams lParamsCloseBtn = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lParamsCloseBtn.addRule(ALIGN_PARENT_RIGHT);

        Button closeBtn = new Button(context);
        closeBtn.setLayoutParams(lParamsCloseBtn);
        closeBtn.setBackgroundColor(Color.TRANSPARENT);
        closeBtn.setText("MINIMIZE");
        closeBtn.setTextColor(TEXT_COLOR);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mCollapsed.setVisibility(View.VISIBLE);
                mCollapsed.setAlpha(ICON_ALPHA);
                mExpanded.setVisibility(View.GONE);
            }
        });

        //********** Adding view components **********
        mRootContainer.addView(mCollapsed);
        mRootContainer.addView(mExpanded);
        if (IconWebViewData() != null) {
            mCollapsed.addView(wView);
        } else {
            mCollapsed.addView(startimage);
        }
        titleText.addView(title);
        titleText.addView(settings);
        mExpanded.addView(titleText);
        mExpanded.addView(subTitle);
        scrollView.addView(mods);
        mExpanded.addView(scrollView);
        relativeLayout.addView(hideBtn);
        relativeLayout.addView(closeBtn);
        mExpanded.addView(relativeLayout);

        Init(context, title, subTitle);
    }

    public void ShowMenu() {
        rootFrame.addView(mRootContainer);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            boolean viewLoaded = false;

            @Override
            public void run() {
                //If the save preferences is enabled, it will check if game lib is loaded before starting menu
                //Comment the if-else code out except startService if you want to run the app and test preferences
                if (Preferences.loadPref && !IsGameLibLoaded() && !stopChecking) {
                    if (!viewLoaded) {
                        Category(mods, "Save preferences was been enabled. Waiting for game lib to be loaded...\n\nForce load menu may not apply mods instantly. You would need to reactivate them again");
                        Button(mods, -100, "Force load menu");
                        viewLoaded = true;
                    }
                    handler.postDelayed(this, 600);
                } else {
                    mods.removeAllViews();
                    featureList(GetFeatureList(), mods);
                }
            }
        }, 500);
    }

    @SuppressLint("WrongConstant")
    public void SetWindowManagerWindowService() {
        //Variable to check later if the phone supports Draw over other apps permission
        int iparams = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ? 2038 : 2002;
        vmParams = new WindowManager.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, iparams, 8, -3);
        //params = new WindowManager.LayoutParams(WindowManager.LayoutParams.LAST_APPLICATION_WINDOW, 8, -3);
        vmParams.gravity = 51;
        vmParams.x = POS_X;
        vmParams.y = POS_Y;

        mWindowManager = (WindowManager) getContext.getSystemService(getContext.WINDOW_SERVICE);
        mWindowManager.addView(rootFrame, vmParams);

        overlayRequired = true;
    }

    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {
            final View collapsedView = mCollapsed;
            final View expandedView = mExpanded;
            private float initialTouchX, initialTouchY;
            private int initialX, initialY;

            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = vmParams.x;
                        initialY = vmParams.y;
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int rawX = (int) (motionEvent.getRawX() - initialTouchX);
                        int rawY = (int) (motionEvent.getRawY() - initialTouchY);
                        mExpanded.setAlpha(1f);
                        mCollapsed.setAlpha(1f);
                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (rawX < 10 && rawY < 10 && isViewCollapsed()) {
                            //When user clicks on the image view of the collapsed layout,
                            //visibility of the collapsed layout will be changed to "View.GONE"
                            //and expanded view will become visible.
                            try {
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            } catch (NullPointerException e) {

                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        mExpanded.setAlpha(0.5f);
                        mCollapsed.setAlpha(0.5f);
                        //Calculate the X and Y coordinates of the view.
                        vmParams.x = initialX + ((int) (motionEvent.getRawX() - initialTouchX));
                        vmParams.y = initialY + ((int) (motionEvent.getRawY() - initialTouchY));
                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(rootFrame, vmParams);
                        return true;
                    default:
                        return false;
                }
            }
        };
    }

    private void featureList(String[] listFT, LinearLayout linearLayout) {
        //Currently looks messy right now. Let me know if you have improvements
        int featNum, subFeat = 0;
        LinearLayout llBak = linearLayout;

        for (int i = 0; i < listFT.length; i++) {
            boolean switchedOn = false;
            //Log.i("featureList", listFT[i]);
            String feature = listFT[i];
            if (feature.contains("_True")) {
                switchedOn = true;
                feature = feature.replaceFirst("_True", "");
            }

            linearLayout = llBak;
            if (feature.contains("CollapseAdd_")) {
                //if (collapse != null)
                linearLayout = mCollapse;
                feature = feature.replaceFirst("CollapseAdd_", "");
            }
            String[] str = feature.split("_");

            //Assign feature number
            if (TextUtils.isDigitsOnly(str[0]) || str[0].matches("-[0-9]*")) {
                featNum = Integer.parseInt(str[0]);
                feature = feature.replaceFirst(str[0] + "_", "");
                subFeat++;
            } else {
                //Subtract feature number. We don't want to count ButtonLink, Category, RichTextView and RichWebView
                featNum = i - subFeat;
            }
            String[] strSplit = feature.split("_");
            switch (strSplit[0]) {
                case "Toggle":
                    Switch(linearLayout, featNum, strSplit[1], switchedOn);
                    break;
                case "SeekBar":
                    SeekBar(linearLayout, featNum, strSplit[1], Integer.parseInt(strSplit[2]), Integer.parseInt(strSplit[3]));
                    break;
                case "Button":
                    Button(linearLayout, featNum, strSplit[1]);
                    break;
                case "ButtonOnOff":
                    ButtonOnOff(linearLayout, featNum, strSplit[1], switchedOn);
                    break;
                case "Spinner":
                    TextView(linearLayout, strSplit[1]);
                    Spinner(linearLayout, featNum, strSplit[1], strSplit[2]);
                    break;
                case "InputText":
                    InputText(linearLayout, featNum, strSplit[1]);
                    break;
                case "InputValue":
                    if (strSplit.length == 3)
                        InputNum(linearLayout, featNum, strSplit[2], Integer.parseInt(strSplit[1]));
                    if (strSplit.length == 2)
                        InputNum(linearLayout, featNum, strSplit[1], 0);
                    break;
                case "CheckBox":
                    CheckBox(linearLayout, featNum, strSplit[1], switchedOn);
                    break;
                case "RadioButton":
                    RadioButton(linearLayout, featNum, strSplit[1], strSplit[2]);
                    break;
                case "Collapse":
                    Collapse(linearLayout, strSplit[1], switchedOn);
                    subFeat++;
                    break;
                case "ButtonLink":
                    subFeat++;
                    ButtonLink(linearLayout, strSplit[1], strSplit[2]);
                    break;
                case "Category":
                    subFeat++;
                    Category(linearLayout, strSplit[1]);
                    break;
                case "RichTextView":
                    subFeat++;
                    TextView(linearLayout, strSplit[1]);
                    break;
                case "RichWebView":
                    subFeat++;
                    WebTextView(linearLayout, strSplit[1]);
                    break;
            }
        }
    }

    private void Switch(LinearLayout linLayout, final int featNum, final String featName, boolean swiOn) {
        final Switch switchR = new Switch(getContext);
        ColorStateList buttonStates = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        Color.BLUE,
                        ToggleON, // ON
                        ToggleOFF // OFF
                }
        );
        //Set colors of the switch. Comment out if you don't like it
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                switchR.getThumbDrawable().setTintList(buttonStates);
                switchR.getTrackDrawable().setTintList(buttonStates);
            } catch (NullPointerException ex) {
                Log.d(TAG, String.valueOf(ex));
            }
        }
        switchR.setText(featName);
        switchR.setTextColor(TEXT_COLOR_2);
        switchR.setPadding(10, 5, 0, 5);
        switchR.setChecked(Preferences.loadPrefBool(featName, featNum, swiOn));
        switchR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean bool) {
                Preferences.changeFeatureBool(featName, featNum, bool);
                switch (featNum) {
                    case -1: //Save perferences
                        Preferences.with(switchR.getContext()).writeBoolean(-1, bool);
                        if (bool == false)
                            Preferences.with(switchR.getContext()).clear(); //Clear perferences if switched off
                        break;
                    case -3:
                        Preferences.isExpanded = bool;
                        scrollView.setLayoutParams(bool ? scrlLLExpanded : scrlLL);
                        break;
                }
            }
        });

        linLayout.addView(switchR);
    }

    private void SeekBar(LinearLayout linLayout, final int featNum, final String featName, final int min, int max) {
        int loadedProg = Preferences.loadPrefInt(featName, featNum);
        LinearLayout linearLayout = new LinearLayout(getContext);
        linearLayout.setPadding(10, 5, 0, 5);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);

        final TextView textView = new TextView(getContext);
        textView.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + ((loadedProg == 0) ? min : loadedProg)));
        textView.setTextColor(TEXT_COLOR_2);

        SeekBar seekBar = new SeekBar(getContext);
        seekBar.setPadding(25, 10, 35, 10);
        seekBar.setMax(max);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            seekBar.setMin(min); //setMin for Oreo and above
        seekBar.setProgress((loadedProg == 0) ? min : loadedProg);
        seekBar.getThumb().setColorFilter(SeekBarColor, PorterDuff.Mode.SRC_ATOP);
        seekBar.getProgressDrawable().setColorFilter(SeekBarProgressColor, PorterDuff.Mode.SRC_ATOP);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                //if progress is greater than minimum, don't go below. Else, set progress
                seekBar.setProgress(i < min ? min : i);
                Preferences.changeFeatureInt(featName, featNum, i < min ? min : i);
                textView.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + (i < min ? min : i)));
            }
        });
        linearLayout.addView(textView);
        linearLayout.addView(seekBar);

        linLayout.addView(linearLayout);
    }

    private void Button(LinearLayout linLayout, final int featNum, final String featName) {
        final Button button = new Button(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(7, 5, 7, 5);
        button.setLayoutParams(layoutParams);
        button.setTextColor(TEXT_COLOR_2);
        button.setAllCaps(false); //Disable caps to support html
        button.setText(Html.fromHtml(featName));
        button.setBackgroundColor(BTN_COLOR);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (featNum) {

                    case -6:
                        scrollView.removeView(mSettings);
                        scrollView.addView(mods);
                        break;
                    case -100:
                        stopChecking = true;
                        break;
                }
                Preferences.changeFeatureInt(featName, featNum, 0);
            }
        });

        linLayout.addView(button);
    }

    private void ButtonLink(LinearLayout linLayout, final String featName, final String url) {
        final Button button = new Button(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(7, 5, 7, 5);
        button.setLayoutParams(layoutParams);
        button.setAllCaps(false); //Disable caps to support html
        button.setTextColor(TEXT_COLOR_2);
        button.setText(Html.fromHtml(featName));
        button.setBackgroundColor(BTN_COLOR);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(url));
                getContext.startActivity(intent);
            }
        });
        linLayout.addView(button);
    }

    private void ButtonOnOff(LinearLayout linLayout, final int featNum, String featName, boolean switchedOn) {
        final Button button = new Button(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(7, 5, 7, 5);
        button.setLayoutParams(layoutParams);
        button.setTextColor(TEXT_COLOR_2);
        button.setAllCaps(false); //Disable caps to support html

        final String finalfeatName = featName.replace("OnOff_", "");
        boolean isOn = Preferences.loadPrefBool(featName, featNum, switchedOn);
        if (isOn) {
            button.setText(Html.fromHtml(finalfeatName + ": ON"));
            button.setBackgroundColor(BtnON);
            isOn = false;
        } else {
            button.setText(Html.fromHtml(finalfeatName + ": OFF"));
            button.setBackgroundColor(BtnOFF);
            isOn = true;
        }
        final boolean finalIsOn = isOn;
        button.setOnClickListener(new View.OnClickListener() {
            boolean isOn = finalIsOn;

            public void onClick(View v) {
                Preferences.changeFeatureBool(finalfeatName, featNum, isOn);
                //Log.d(TAG, finalfeatName + " " + featNum + " " + isActive2);
                if (isOn) {
                    button.setText(Html.fromHtml(finalfeatName + ": ON"));
                    button.setBackgroundColor(BtnON);
                    isOn = false;
                } else {
                    button.setText(Html.fromHtml(finalfeatName + ": OFF"));
                    button.setBackgroundColor(BtnOFF);
                    isOn = true;
                }
            }
        });
        linLayout.addView(button);
    }

    private void Spinner(LinearLayout linLayout, final int featNum, final String featName, final String list) {
        Log.d(TAG, "spinner " + featNum + " " + featName + " " + list);
        final List<String> lists = new LinkedList<>(Arrays.asList(list.split(",")));

        // Create another LinearLayout as a workaround to use it as a background
        // to keep the down arrow symbol. No arrow symbol if setBackgroundColor set
        LinearLayout linearLayout2 = new LinearLayout(getContext);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        layoutParams2.setMargins(7, 2, 7, 2);
        linearLayout2.setOrientation(LinearLayout.VERTICAL);
        linearLayout2.setBackgroundColor(BTN_COLOR);
        linearLayout2.setLayoutParams(layoutParams2);

        final Spinner spinner = new Spinner(getContext, Spinner.MODE_DROPDOWN);
        spinner.setLayoutParams(layoutParams2);
        spinner.getBackground().setColorFilter(1, PorterDuff.Mode.SRC_ATOP); //trick to show white down arrow color
        //Creating the ArrayAdapter instance having the list
        ArrayAdapter aa = new ArrayAdapter(getContext, android.R.layout.simple_spinner_dropdown_item, lists);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner'
        spinner.setAdapter(aa);
        spinner.setSelection(Preferences.loadPrefInt(featName, featNum));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Preferences.changeFeatureInt(spinner.getSelectedItem().toString(), featNum, position);
                ((TextView) parentView.getChildAt(0)).setTextColor(TEXT_COLOR_2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        linearLayout2.addView(spinner);
        linLayout.addView(linearLayout2);
    }

    private void InputNum(LinearLayout linLayout, final int featNum, final String featName, final int maxValue) {
        LinearLayout linearLayout = new LinearLayout(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(7, 5, 7, 5);

        final Button button = new Button(getContext);
        int num = Preferences.loadPrefInt(featName, featNum);
        button.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + ((num == 0) ? 1 : num) + "</font>"));
        button.setAllCaps(false);
        button.setLayoutParams(layoutParams);
        button.setBackgroundColor(BTN_COLOR);
        button.setTextColor(TEXT_COLOR_2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertName = new AlertDialog.Builder(getContext);
                final EditText editText = new EditText(getContext);
                if (maxValue != 0)
                    editText.setHint("Max value: " + maxValue);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setKeyListener(DigitsKeyListener.getInstance("0123456789-"));
                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(10);
                editText.setFilters(FilterArray);
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        InputMethodManager imm = (InputMethodManager) getContext.getSystemService(getContext.INPUT_METHOD_SERVICE);
                        if (hasFocus) {
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        } else {
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        }
                    }
                });
                editText.requestFocus();

                alertName.setTitle("Input number");
                alertName.setView(editText);
                LinearLayout layoutName = new LinearLayout(getContext);
                layoutName.setOrientation(LinearLayout.VERTICAL);
                layoutName.addView(editText); // displays the user input bar
                alertName.setView(layoutName);

                alertName.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        int num;
                        try {
                            num = Integer.parseInt(TextUtils.isEmpty(editText.getText().toString()) ? "0" : editText.getText().toString());
                            if (maxValue != 0 && num >= maxValue)
                                num = maxValue;
                        } catch (NumberFormatException ex) {
                            if (maxValue != 0)
                                num = maxValue;
                            else
                                num = 2147483640;
                        }

                        button.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + num + "</font>"));
                        Preferences.changeFeatureInt(featName, featNum, num);

                        editText.setFocusable(false);
                    }
                });

                alertName.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // dialog.cancel(); // closes dialog
                        InputMethodManager imm = (InputMethodManager) getContext.getSystemService(getContext.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });

                if (overlayRequired) {
                    AlertDialog dialog = alertName.create(); // display the dialog
                    dialog.getWindow().setType(Build.VERSION.SDK_INT >= 26 ? 2038 : 2002);
                    dialog.show();
                } else {
                    alertName.show();
                }
            }
        });

        linearLayout.addView(button);
        linLayout.addView(linearLayout);
    }

    private void InputText(LinearLayout linLayout, final int featNum, final String featName) {
        LinearLayout linearLayout = new LinearLayout(getContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParams.setMargins(7, 5, 7, 5);

        final Button button = new Button(getContext);

        String string = Preferences.loadPrefString(featName, featNum);
        button.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + string + "</font>"));

        button.setAllCaps(false);
        button.setLayoutParams(layoutParams);
        button.setBackgroundColor(BTN_COLOR);
        button.setTextColor(TEXT_COLOR_2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertName = new AlertDialog.Builder(getContext);

                final EditText editText = new EditText(getContext);
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        InputMethodManager imm = (InputMethodManager) getContext.getSystemService(getContext.INPUT_METHOD_SERVICE);
                        if (hasFocus) {
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        } else {
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        }
                    }
                });
                editText.requestFocus();

                alertName.setTitle("Input text");
                alertName.setView(editText);
                LinearLayout layoutName = new LinearLayout(getContext);
                layoutName.setOrientation(LinearLayout.VERTICAL);
                layoutName.addView(editText); // displays the user input bar
                alertName.setView(layoutName);

                alertName.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String str = editText.getText().toString();
                        button.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + str + "</font>"));
                        Preferences.changeFeatureString(featName, featNum, str);
                        editText.setFocusable(false);
                    }
                });

                alertName.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //dialog.cancel(); // closes dialog
                        InputMethodManager imm = (InputMethodManager) getContext.getSystemService(getContext.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });


                if (overlayRequired) {
                    AlertDialog dialog = alertName.create(); // display the dialog
                    dialog.getWindow().setType(Build.VERSION.SDK_INT >= 26 ? 2038 : 2002);
                    dialog.show();
                } else {
                    alertName.show();
                }
            }
        });

        linearLayout.addView(button);
        linLayout.addView(linearLayout);
    }

    private void CheckBox(LinearLayout linLayout, final int featNum, final String featName, boolean switchedOn) {
        final CheckBox checkBox = new CheckBox(getContext);
        checkBox.setText(featName);
        checkBox.setTextColor(TEXT_COLOR_2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            checkBox.setButtonTintList(ColorStateList.valueOf(CheckBoxColor));
        checkBox.setChecked(Preferences.loadPrefBool(featName, featNum, switchedOn));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox.isChecked()) {
                    Preferences.changeFeatureBool(featName, featNum, isChecked);
                } else {
                    Preferences.changeFeatureBool(featName, featNum, isChecked);
                }
            }
        });
        linLayout.addView(checkBox);
    }

    private void RadioButton(LinearLayout linLayout, final int featNum, String featName, final String list) {
        //Credit: LoraZalora
        final List<String> lists = new LinkedList<>(Arrays.asList(list.split(",")));

        final TextView textView = new TextView(getContext);
        textView.setText(featName + ":");
        textView.setTextColor(TEXT_COLOR_2);

        final RadioGroup radioGroup = new RadioGroup(getContext);
        radioGroup.setPadding(10, 5, 10, 5);
        radioGroup.setOrientation(LinearLayout.VERTICAL);
        radioGroup.addView(textView);

        for (int i = 0; i < lists.size(); i++) {
            final RadioButton Radioo = new RadioButton(getContext);
            final String finalfeatName = featName, radioName = lists.get(i);
            View.OnClickListener first_radio_listener = new View.OnClickListener() {
                public void onClick(View v) {
                    textView.setText(Html.fromHtml(finalfeatName + ": <font color='" + NumberTxtColor + "'>" + radioName));
                    Preferences.changeFeatureInt(finalfeatName, featNum, radioGroup.indexOfChild(Radioo));
                }
            };
            System.out.println(lists.get(i));
            Radioo.setText(lists.get(i));
            Radioo.setTextColor(Color.LTGRAY);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                Radioo.setButtonTintList(ColorStateList.valueOf(RadioColor));
            Radioo.setOnClickListener(first_radio_listener);
            radioGroup.addView(Radioo);
        }

        int index = Preferences.loadPrefInt(featName, featNum);
        if (index > 0) { //Preventing it to get an index less than 1. below 1 = null = crash
            textView.setText(Html.fromHtml(featName + ": <font color='" + NumberTxtColor + "'>" + lists.get(index - 1)));
            ((RadioButton) radioGroup.getChildAt(index)).setChecked(true);
        }
        linLayout.addView(radioGroup);
    }

    private void Collapse(LinearLayout linLayout, final String text, final boolean expanded) {
        LinearLayout.LayoutParams layoutParamsLL = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layoutParamsLL.setMargins(0, 5, 0, 0);

        LinearLayout collapse = new LinearLayout(getContext);
        collapse.setLayoutParams(layoutParamsLL);
        collapse.setVerticalGravity(16);
        collapse.setOrientation(LinearLayout.VERTICAL);

        final LinearLayout collapseSub = new LinearLayout(getContext);
        collapseSub.setVerticalGravity(16);
        collapseSub.setPadding(0, 5, 0, 5);
        collapseSub.setOrientation(LinearLayout.VERTICAL);
        collapseSub.setBackgroundColor(MENU_FEATURE_BG_COLOR);
        collapseSub.setVisibility(View.GONE);
        mCollapse = collapseSub;

        final TextView textView = new TextView(getContext);
        textView.setBackgroundColor(CategoryBG);
        textView.setText("▽ " + text + " ▽");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(TEXT_COLOR_2);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(0, 20, 0, 20);

        if (expanded) {
            collapseSub.setVisibility(View.VISIBLE);
            textView.setText("△ " + text + " △");
        }

        textView.setOnClickListener(new View.OnClickListener() {
            boolean isChecked = expanded;

            @Override
            public void onClick(View v) {

                boolean z = !isChecked;
                isChecked = z;
                if (z) {
                    collapseSub.setVisibility(View.VISIBLE);
                    textView.setText("△ " + text + " △");
                    return;
                }
                collapseSub.setVisibility(View.GONE);
                textView.setText("▽ " + text + " ▽");
            }
        });
        collapse.addView(textView);
        collapse.addView(collapseSub);
        linLayout.addView(collapse);
    }

    private void Category(LinearLayout linLayout, String text) {
        TextView textView = new TextView(getContext);
        textView.setBackgroundColor(CategoryBG);
        textView.setText(Html.fromHtml(text));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(TEXT_COLOR_2);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(0, 5, 0, 5);
        linLayout.addView(textView);
    }

    private void TextView(LinearLayout linLayout, String text) {
        TextView textView = new TextView(getContext);
        textView.setText(Html.fromHtml(text));
        textView.setTextColor(TEXT_COLOR_2);
        textView.setPadding(10, 5, 10, 5);
        linLayout.addView(textView);
    }

    private void WebTextView(LinearLayout linLayout, String text) {
        WebView wView = new WebView(getContext);
        wView.loadData(text, "text/html", "utf-8");
        wView.setBackgroundColor(0x00000000); //Transparent
        wView.setPadding(0, 5, 0, 5);
        wView.getSettings().setAppCacheEnabled(false);
        linLayout.addView(wView);
    }

    private boolean isViewCollapsed() {
        return rootFrame == null || mCollapsed.getVisibility() == View.VISIBLE;
    }

    //For our image a little converter
    private int convertDipToPixels(int i) {
        return (int) ((((float) i) * getContext.getResources().getDisplayMetrics().density) + 0.5f);
    }

    private int dp(int i) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) i, getContext.getResources().getDisplayMetrics());
    }

    public void setVisibility(int view) {
        if (rootFrame != null) {
            rootFrame.setVisibility(view);
        }
    }

    public void onDestroy() {
        if (rootFrame != null) {
            mWindowManager.removeView(rootFrame);
        }
    }
}
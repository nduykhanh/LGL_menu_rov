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

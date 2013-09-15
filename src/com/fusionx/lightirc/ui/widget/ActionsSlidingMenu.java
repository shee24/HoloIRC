package com.fusionx.lightirc.ui.widget;

import android.content.Context;

import com.fusionx.lightirc.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class ActionsSlidingMenu extends SlidingMenu {
    public ActionsSlidingMenu(Context context) {
        super(context);
        setShadowDrawable(R.drawable.shadow);
        setMode(SlidingMenu.LEFT);
        setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        setTouchmodeMarginThreshold(10);
        setMenu(R.layout.sliding_menu_fragment_actions);
        setBehindWidthRes(R.dimen.user_menu_sliding_width);
    }
}
package trikita.talalarmo.ui;

import android.graphics.drawable.Drawable;

import trikita.anvil.Anvil;
import trikita.talalarmo.R;

import static trikita.anvil.DSL.*;

public enum Theme {

    LIGHT(0xffffffff, 0x0f607d88, 0xff607d8b, 0xff455a64, 0xffcfd8dc, 0xff00bcd4,
            0xde000000, 0x8a000000, 0x42000000, 0x1f000000,
            R.drawable.popup_background_light,
            R.drawable.popup_menu_item_light_selector),
    DARK(0xff212121, 0x4c666666, 0xffe0e0e0, 0xff424242, 0xff757575, 0xffff5252,
            0xffffffff, 0xb2ffffff, 0x4cffffff, 0x1fffffff,
            R.drawable.popup_background_dark,
            R.drawable.popup_menu_item_dark_selector);

    public final int backgroundColor;
    public final int backgroundTranscluentColor;
    public final int primaryColor;
    public final int primaryDarkColor;
    public final int primaryLightColor;
    public final int accentColor;
    public final int primaryTextColor;
    public final int secondaryTextColor;
    public final int disabledColor;
    public final int dividerColor;
    public final int popupMenuBackground;
    public final int popupMenuItemSelector;

    Theme(int backgroundColor, int backgroundTranscluentColor, int primaryColor,
          int primaryDarkColor, int primaryLightColor, int accentColor, int primaryTextColor,
          int secondaryTextColor, int disabledColor, int dividerColor,
          int popupMenuBackground, int popupMenuItemSelector) {
        this.backgroundColor = backgroundColor;
        this.backgroundTranscluentColor = backgroundTranscluentColor;
        this.primaryColor = primaryColor;
        this.primaryDarkColor = primaryDarkColor;
        this.primaryLightColor = primaryLightColor;
        this.accentColor = accentColor;
        this.primaryTextColor = primaryTextColor;
        this.secondaryTextColor = secondaryTextColor;
        this.disabledColor = disabledColor;
        this.dividerColor = dividerColor;
        this.popupMenuBackground = popupMenuBackground;
        this.popupMenuItemSelector = popupMenuItemSelector;
    }

    public static Theme get(int index) {
        if (index == 0) {
            return LIGHT;
        } else {
            return DARK;
        }
    }

    public static void materialIcon(Anvil.Renderable r) {
        textView(() -> {
            size(WRAP, WRAP);
            typeface("fonts/MaterialIcons-Regular.ttf");
            gravity(CENTER);
            r.view();
        });
    }
}

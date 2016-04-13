package trikita.talalarmo.ui;

import trikita.anvil.Anvil;

import static trikita.anvil.DSL.*;

public enum Theme {
    LIGHT(0xffffffff, 0x0f555555, 0xff666666, 0xff0097a7, 0xff00bcd4,
            0xff212121, 0xff727272, true),
    DARK(0xff002b36, 0x40002129, 0xffcbd2d2, 0xff002129, 0xffe91e63,
            0xddfdf6e3, 0xff93a1a1, true);

    public final int backgroundColor;
    public final int backgroundTranslucentColor;
    public final int primaryColor;
    public final int primaryDarkColor;
    public final int accentColor;
    public final int primaryTextColor;
    public final int secondaryTextColor;
    public final boolean light;

    Theme(int backgroundColor, int backgroundTranslucentColor, int primaryColor,
          int primaryDarkColor, int accentColor, int primaryTextColor,
          int secondaryTextColor, boolean light) {
        this.backgroundColor = backgroundColor;
        this.backgroundTranslucentColor = backgroundTranslucentColor;
        this.primaryColor = primaryColor;
        this.primaryDarkColor = primaryDarkColor;
        this.accentColor = accentColor;
        this.primaryTextColor = primaryTextColor;
        this.secondaryTextColor = secondaryTextColor;
        this.light = light;
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

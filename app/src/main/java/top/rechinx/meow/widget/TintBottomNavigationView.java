package top.rechinx.meow.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import top.rechinx.meow.R;
import top.rechinx.rikka.theme.utils.ThemeUtils;
import top.rechinx.rikka.theme.widgets.Tintable;

public class TintBottomNavigationView extends BottomNavigationView implements Tintable {

    public TintBottomNavigationView(Context context) {
        this(context, null);
    }

    public TintBottomNavigationView(Context context, AttributeSet attrs) {
        this(context, attrs, com.google.android.material.R.attr.bottomNavigationStyle);
    }

    public TintBottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setItemIconTintList(ThemeUtils.getThemeColorStateList(getContext(), R.color.bottom_navigation_colors));
        setItemTextColor(ThemeUtils.getThemeColorStateList(getContext(), R.color.bottom_navigation_colors));
    }

    @Override
    public void tint() {
        setItemIconTintList(ThemeUtils.getThemeColorStateList(getContext(), R.color.bottom_navigation_colors));
        setItemTextColor(ThemeUtils.getThemeColorStateList(getContext(), R.color.bottom_navigation_colors));
    }
}

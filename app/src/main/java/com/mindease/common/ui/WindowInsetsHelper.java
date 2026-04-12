package com.mindease.common.ui;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public final class WindowInsetsHelper {
    private WindowInsetsHelper() {
    }

    public static void enableEdgeToEdge(@NonNull androidx.appcompat.app.AppCompatActivity activity) {
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
    }

    public static void applyTopPadding(@NonNull View view) {
        final int initialTop = view.getPaddingTop();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    v.getPaddingLeft(),
                    initialTop + systemBars.top,
                    v.getPaddingRight(),
                    v.getPaddingBottom()
            );
            return insets;
        });
        requestInsets(view);
    }

    public static void applyTopAndBottomPadding(@NonNull View view) {
        final int initialTop = view.getPaddingTop();
        final int initialBottom = view.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    v.getPaddingLeft(),
                    initialTop + systemBars.top,
                    v.getPaddingRight(),
                    initialBottom + systemBars.bottom
            );
            return insets;
        });
        requestInsets(view);
    }

    public static void applyBottomPadding(@NonNull View view) {
        final int initialBottom = view.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    v.getPaddingLeft(),
                    v.getPaddingTop(),
                    v.getPaddingRight(),
                    initialBottom + systemBars.bottom
            );
            return insets;
        });
        requestInsets(view);
    }

    public static void applyImeAwareBottomMargin(@NonNull View view) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        final int initialBottom = layoutParams.bottomMargin;
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.bottomMargin = initialBottom + Math.max(systemBars.bottom, ime.bottom);
            v.setLayoutParams(params);
            return insets;
        });
        requestInsets(view);
    }

    private static void requestInsets(View view) {
        ViewCompat.requestApplyInsets(view);
    }
}

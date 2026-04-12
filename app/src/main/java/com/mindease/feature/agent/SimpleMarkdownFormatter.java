package com.mindease.feature.agent;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BulletSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.mindease.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SimpleMarkdownFormatter {
    private static final Pattern HEADING = Pattern.compile("(?m)^#{1,3}\\s+(.+)$");
    private static final Pattern BULLET = Pattern.compile("(?m)^[-*]\\s+(.+)$");
    private static final Pattern BOLD = Pattern.compile("\\*\\*(.+?)\\*\\*");
    private static final Pattern ITALIC = Pattern.compile("(?<!\\*)\\*(?!\\*)(.+?)(?<!\\*)\\*(?!\\*)");
    private static final Pattern INLINE_CODE = Pattern.compile("`([^`]+)`");

    private SimpleMarkdownFormatter() {
    }

    public static void apply(TextView textView, String text) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text == null ? "" : text);
        applyHeading(builder);
        applyBullet(builder);
        applyPattern(builder, BOLD, Typeface.BOLD, false);
        applyPattern(builder, ITALIC, Typeface.ITALIC, false);
        applyCode(builder, textView);
        textView.setText(builder);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private static void applyHeading(SpannableStringBuilder builder) {
        Matcher matcher = HEADING.matcher(builder.toString());
        while (matcher.find()) {
            int start = matcher.start(1);
            int end = matcher.end(1);
            builder.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new AbsoluteSizeSpan(17, true), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private static void applyBullet(SpannableStringBuilder builder) {
        Matcher matcher = BULLET.matcher(builder.toString());
        while (matcher.find()) {
            int symbolStart = matcher.start();
            int contentStart = matcher.start(1);
            int end = matcher.end(1);
            builder.replace(symbolStart, contentStart, "");
            builder.setSpan(new BulletSpan(20), symbolStart, end - (contentStart - symbolStart), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private static void applyPattern(SpannableStringBuilder builder, Pattern pattern, int typeface, boolean monospace) {
        Matcher matcher = pattern.matcher(builder.toString());
        while (matcher.find()) {
            int markerLength = matcher.group().length() - matcher.group(1).length();
            int start = matcher.start();
            int innerStart = matcher.start(1);
            int innerEnd = matcher.end(1);
            builder.replace(innerEnd, matcher.end(), "");
            builder.replace(start, innerStart, "");
            int end = start + matcher.group(1).length();
            builder.setSpan(new StyleSpan(typeface), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (monospace) {
                builder.setSpan(new TypefaceSpan("monospace"), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            matcher = pattern.matcher(builder.toString());
        }
    }

    private static void applyCode(SpannableStringBuilder builder, TextView textView) {
        Matcher matcher = INLINE_CODE.matcher(builder.toString());
        while (matcher.find()) {
            int start = matcher.start();
            int innerStart = matcher.start(1);
            int innerEnd = matcher.end(1);
            builder.replace(innerEnd, matcher.end(), "");
            builder.replace(start, innerStart, "");
            int end = start + matcher.group(1).length();
            builder.setSpan(new TypefaceSpan("monospace"), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(
                    new ForegroundColorSpan(ContextCompat.getColor(textView.getContext(), R.color.brand_cloud_text)),
                    start,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            matcher = INLINE_CODE.matcher(builder.toString());
        }
    }
}

package com.example.financialmanagement.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentParser {

    private static final Pattern CHECKLIST_PATTERN = Pattern.compile("@\\[([^\\]]+)\\]\\(checklist:([^)]+)\\)");
    private static final Pattern TASK_PATTERN = Pattern.compile("@\\[([^\\]]+)\\]\\(task:([^)]+)\\)");
    private static final Pattern USER_PATTERN = Pattern.compile("@([a-zA-Z0-9_\\s\\u00C0-\\u1EF9]+)");

    public static void parseAndSet(@NonNull TextView textView, String text, boolean isOwnMessage) {
        if (text == null || text.isEmpty()) {
            textView.setText("");
            return;
        }

        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        List<MatchResult> matches = new ArrayList<>();

        // Match Checklist
        Matcher checklistMatcher = CHECKLIST_PATTERN.matcher(text);
        while (checklistMatcher.find()) {
            matches.add(new MatchResult("checklist", checklistMatcher.start(), checklistMatcher.end(), checklistMatcher.group(1), checklistMatcher.group(2)));
        }

        // Match Task
        Matcher taskMatcher = TASK_PATTERN.matcher(text);
        while (taskMatcher.find()) {
            matches.add(new MatchResult("task", taskMatcher.start(), taskMatcher.end(), taskMatcher.group(1), taskMatcher.group(2)));
        }

        // Sort by start position descending to replace without messing up indices
        Collections.sort(matches, (a, b) -> b.start - a.start);

        for (MatchResult match : matches) {
            String displayName = match.name;
            int start = match.start;
            int end = match.end;

            // Replace full match with just the display name
            ssb.replace(start, end, displayName);
            int newEnd = start + displayName.length();

            // Apply style
            int color = isOwnMessage ? Color.WHITE : Color.parseColor("#0075FF");
            if ("checklist".equals(match.type)) {
                color = isOwnMessage ? Color.WHITE : Color.parseColor("#34C759");
            } else if ("task".equals(match.type)) {
                color = isOwnMessage ? Color.WHITE : Color.parseColor("#AF52DE");
            }

            ssb.setSpan(new ForegroundColorSpan(color), start, newEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, newEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Add ClickableSpan for task navigation if needed
            if ("task".equals(match.type)) {
                final String taskId = match.id;
                ssb.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        // Forward request to activity if possible, or just open task
                        // For now just toast
                        Toast.makeText(widget.getContext(), "Mở nhiệm vụ: " + match.name, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }
                }, start, newEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        // Finally match simple User mentions (those not already part of complex mentions)
        // This is tricky with SpannableStringBuilder. Let's do it on the original text first then map?
        // Actually, let's just do it on the remaining text where spans aren't present.
        
        textView.setText(ssb);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private static class MatchResult {
        String type;
        int start;
        int end;
        String name;
        String id;

        MatchResult(String type, int start, int end, String name, String id) {
            this.type = type;
            this.start = start;
            this.end = end;
            this.name = name;
            this.id = id;
        }
    }
}

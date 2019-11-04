package com.zingtv.logshowjava.parser;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.graphics.Typeface.BOLD;

public class ZingTVHtmlParser implements HtmlIParser {
    @Override
    public synchronized Spanned read(String raw, String filterString, String filterPriority) {
        String[] listPtag = raw.split("</p>");
//        String newP="";
        String priority = "";
        String textColor;
        int currentIndex = 0;
        int startIndex = 0;
//        Spannable outputSpanned;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (filterString.length() > 0) {
            for (int i = 0; i < listPtag.length; i++) {
//                newP = "<p ";

                Pattern priorityPattern = Pattern.compile("(priority=\")([0-9])(\")");
                Matcher matcher = priorityPattern.matcher(listPtag[i]);
                if (matcher.find()) {
                    priority = matcher.group(2);
                }

                if (TextUtils.isEmpty(priority)) {
                    priority = "2";
                }
//                newP += "priority=\"" + priority + "\">";

                switch (Integer.parseInt(priority)) {
                    case Log.ERROR:
                        textColor = "#E74C3C";
                        break;
                    case Log.DEBUG:
                        textColor = "#B7950B";
                        break;
                    default:
                        textColor = "#FFFFFF";
                        break;
                }
//                newP += "<font color=\"" + textColor + "\">";

                priorityPattern = Pattern.compile("(<strong(.+)\">)(.*)(</strong><strong>)(.*)(</strong>)");
                matcher = priorityPattern.matcher(listPtag[i]);
                if (matcher.find()) {

//                    newP += "<small>" + matcher.group(3).replace("&nbsp&nbsp", " &nbsp ")
//                            + "</small><" + matcher.group(5).replace("&nbsp&nbsp", " &nbsp ");

                    if (priority.equals(filterPriority) && matcher.group(5).contains(filterString)){
                        String time = matcher.group(3).replace("&nbsp&nbsp", " ");
                        String content = matcher.group(5).replace("&nbsp&nbsp", " ");



                        spannableStringBuilder.append(time);
                        spannableStringBuilder.setSpan(new RelativeSizeSpan(0.7f),currentIndex, currentIndex + time.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        currentIndex += time.length();
                        spannableStringBuilder.append(content);
                        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor(textColor)),startIndex, currentIndex + content.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        currentIndex += content.length();
                        startIndex = currentIndex;
                    } else {
                        continue;
                    }

                } else {
                    Log.d("ZINGLOGSHOW", "not match");

                }

//                newP += "</font>";
//
//                listPtag[i] = newP;

            }
        } else {
            for (int i = 0; i < listPtag.length; i++) {
//                newP = "<p ";

                Pattern priorityPattern = Pattern.compile("(priority=\")([0-9])(\")");
                Matcher matcher = priorityPattern.matcher(listPtag[i]);
                if (matcher.find()) {
                    priority = matcher.group(2);
                }

                if (TextUtils.isEmpty(priority)) {
                    priority = "2";
                }
//                newP += "priority=\"" + priority + "\">";

                switch (Integer.parseInt(priority)) {
                    case Log.ERROR:
                        textColor = "#E74C3C";
                        break;
                    case Log.DEBUG:
                        textColor = "#B7950B";
                        break;
                    default:
                        textColor = "#B7950B";
                        break;
                }
//                newP += "<font color=\"" + textColor + "\">";

                priorityPattern = Pattern.compile("(<strong(.+)\">)(.*)(</strong><strong>)(.*)(</strong>)(.*)");

                matcher = priorityPattern.matcher(listPtag[i]);
                if (matcher.find()) {

//                    newP += "<small>" + matcher.group(3).replace("&nbsp&nbsp", " &nbsp ")
//                            + "</small><" + matcher.group(5).replace("&nbsp&nbsp", " &nbsp ");

                    if (priority.equals(filterPriority)){
                        String time = matcher.group(3).replace("&nbsp&nbsp", " ");
                        String tag = matcher.group(5).replace("&nbsp&nbsp", " ");
                        String content = matcher.group(7)+"\n";
                        Log.i("FLoatingLogView", "current index = "+ currentIndex );

                        spannableStringBuilder.append(time);
                        spannableStringBuilder.setSpan(new RelativeSizeSpan(0.4f),currentIndex, currentIndex + time.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        currentIndex += time.length();

                        spannableStringBuilder.append(tag);
                        spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),currentIndex, currentIndex + tag.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        currentIndex += tag.length();

                        spannableStringBuilder.append(content);
                        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor(textColor)),startIndex, currentIndex + content.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        currentIndex += content.length();
                        startIndex = currentIndex;
                    } else {
                        continue;
                    }

                } else {
                    Log.d("ZINGLOGSHOW", "not match");

                }

//                newP += "</font>";
//
//                listPtag[i] = newP;

            }

        }

//        return TextUtils.join("</p>", listPtag);
            return new SpannableString(spannableStringBuilder);
    }
}

package com.zingtv.logshowjava.parser;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.graphics.Typeface.BOLD;

public class ZingTVHtmlParser implements HtmlIParser {

    @Override
    public synchronized List<Spanned> read(String raw, String filterString, String filterPriority) {
        String[] listPtag = raw.split("</p>");
        List<Spanned> spannedList = new ArrayList<>();

        String priority = "";
        String textColor;
        String shortTag = "";
        int currentIndex = 0;
        int startIndex = 0;

        if (filterString.length() > 0) {
            for (int i = 0; i < listPtag.length; i++) {


                Pattern priorityPattern = Pattern.compile("(priority=\")([0-9])(\")");
                Matcher matcher = priorityPattern.matcher(listPtag[i]);
                if (matcher.find()) {
                    priority = matcher.group(2);
                }

                if (TextUtils.isEmpty(priority)) {
                    priority = "2";
                }

                switch (Integer.parseInt(priority)) {
                    case Log.ERROR:
                        textColor = "#E74C3C";
                        shortTag = "E/";
                        break;
                    case Log.DEBUG:
                        textColor = "#B7950B";
                        shortTag = "D/";
                        break;
                    case Log.VERBOSE:
                        shortTag = "V/";
                        textColor = "#FFFFFF";
                        break;
                    case Log.INFO:
                        shortTag = "I/";
                        textColor = "#FFFFFF";
                        break;
                    case Log.ASSERT:
                        shortTag = "A/";
                        textColor = "#FFFFFF";
                        break;
                    default:
                        shortTag = "W/";
                        textColor = "#FFFFFF";
                        break;
                }


                priorityPattern = Pattern.compile("(<strong(.+)\">)(.*)(</strong><strong>)(.*)(</strong>)(.*)");
                matcher = priorityPattern.matcher(listPtag[i]);
                if (matcher.find()) {

                    if (priority.equals(filterPriority) && (matcher.group(7).contains(filterString) || matcher.group(5).contains(filterString))) {

                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

                        String time = matcher.group(3).replace("&nbsp&nbsp", " ");
                        String tag = shortTag + matcher.group(5).replace("&nbsp&nbsp", "") + ": ";
                        String content = matcher.group(7) + "\n";

                        spannableStringBuilder.append(time);
                        spannableStringBuilder.setSpan(new RelativeSizeSpan(0.7f), currentIndex, currentIndex + time.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        currentIndex += time.length();

                        spannableStringBuilder.append(tag);
                        spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), currentIndex, currentIndex + tag.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        currentIndex += tag.length();

                        spannableStringBuilder.append(content);
                        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor(textColor)), 0, currentIndex + content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        currentIndex = 0;
                        spannedList.add(spannableStringBuilder);

                        int index = TextUtils.indexOf(spannableStringBuilder, filterString);


                        while (index >= 0) {
                            spannableStringBuilder.setSpan(new BackgroundColorSpan(Color.parseColor("#9dd6f9")), index, index
                                    + filterString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            index = TextUtils.indexOf(spannableStringBuilder, filterString, index + filterString.length());
                        }
//                        startIndex = currentIndex;
                    } else {
                        continue;
                    }

                } else {
                    Log.d("ZINGLOGSHOW", "not match");

                }


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


                switch (Integer.parseInt(priority)) {
                    case Log.ERROR:
                        textColor = "#E74C3C";
                        shortTag = "E/";
                        break;
                    case Log.DEBUG:
                        textColor = "#B7950B";
                        shortTag = "D/";
                        break;
                    case Log.VERBOSE:
                        shortTag = "V/";
                        textColor = "#FFFFFF";
                        break;
                    case Log.INFO:
                        shortTag = "I/";
                        textColor = "#FFFFFF";
                        break;
                    case Log.ASSERT:
                        shortTag = "A/";
                        textColor = "#FFFFFF";
                        break;
                    default:
                        shortTag = "W/";
                        textColor = "#FFFFFF";
                        break;
                }


                priorityPattern = Pattern.compile("(<strong(.+)\">)(.*)(</strong><strong>)(.*)(</strong>)(.*)");

                matcher = priorityPattern.matcher(listPtag[i]);
                if (matcher.find()) {


                    if (priority.equals(filterPriority)) {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

                        String time = matcher.group(3).replace("&nbsp&nbsp", " ");
                        String tag = shortTag + matcher.group(5).replace("&nbsp&nbsp", "") + ": ";
                        String content = matcher.group(7) + "\n";


                        spannableStringBuilder.append(time);
                        spannableStringBuilder.setSpan(new RelativeSizeSpan(0.7f), currentIndex, currentIndex + time.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        currentIndex += time.length();

                        spannableStringBuilder.append(tag);
                        spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), currentIndex, currentIndex + tag.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        currentIndex += tag.length();

                        spannableStringBuilder.append(content);
                        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor(textColor)), 0, currentIndex + content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        currentIndex = 0;
                        spannedList.add(spannableStringBuilder);

//                        startIndex = currentIndex;
                    } else {
                        continue;
                    }

                } else {
                    Log.d("ZINGLOGSHOW", "not match");

                }


            }

        }

        return spannedList;
    }
}

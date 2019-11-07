package com.zingtv.logshowjava.parser;

import android.text.Spanned;

import java.util.List;

public interface HtmlIParser {
    List<Spanned> read(String raw, String filterString, String priority);
}

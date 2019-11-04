package com.zingtv.logshowjava.parser;

import android.text.Spanned;

public interface HtmlIParser {
    Spanned read(String raw, String filterString, String priority);
}

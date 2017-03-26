package com.xtracteddev.newsreader.nntp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class NNTPFromFieldFormatter {

    private static final String decodePatternMail = "([<]?[A-Za-zäöüÄÖÜ0-9.!#$%&'*+\\-/=?^_`{|}~]*@[A-Za-zäöüÄÖÜ\\.0-9\\-]+\\.[A-Za-z]{2,}[>]?)";
    private String fullName = "";
    private String email = "";

    public NNTPFromFieldFormatter(String fromField) {
        split(fromField);
    }

    private void split(String text) {
        Pattern pattern = Pattern.compile(decodePatternMail);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            email = matcher.group(0);
            text = text.replace(email, "");
            email = email.replace("<","").replace(">","");
        }
        text = text.replaceAll("[_\"\'()]", " ");
        String[] nameArray = text.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String s : nameArray) {
            sb.append(s).append(" ");
        }
        fullName = sb.toString().trim();
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }
}

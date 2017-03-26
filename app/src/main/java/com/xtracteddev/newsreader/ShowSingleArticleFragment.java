package com.xtracteddev.newsreader;

import android.app.Fragment;
import android.os.Bundle;

public class ShowSingleArticleFragment extends Fragment {

    private boolean extended;
    private String fromName;
    private String subject;
    private String prettyDate;
    private String messageBody;
    private String messageHeader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public boolean isExtended() {
        return extended;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPrettyDate() {
        return prettyDate;
    }

    public void setPrettyDate(String prettyDate) {
        this.prettyDate = prettyDate;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getMessageHeader() {
        return messageHeader;
    }

    public void setMessageHeader(String messageHeader) {
        this.messageHeader = messageHeader;
    }
}

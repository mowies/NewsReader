package com.xtracteddev.newsreader;

import android.app.Fragment;
import android.os.Bundle;

public class WriteMessageFragment extends Fragment {

    private String fromEmail;
    private String fromName;
    private String fromSignature;

    private String fromSubject;
    private String fromMessage;

    private String fromNewsgroup;
    private String references;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromSignature() {
        return fromSignature;
    }

    public void setFromSignature(String fromSignature) {
        this.fromSignature = fromSignature;
    }

    public String getFromSubject() {
        return fromSubject;
    }

    public void setFromSubject(String fromSubject) {
        this.fromSubject = fromSubject;
    }

    public String getFromMessage() {
        return fromMessage;
    }

    public void setFromMessage(String fromMessage) {
        this.fromMessage = fromMessage;
    }

    public String getFromNewsgroup() {
        return fromNewsgroup;
    }

    public void setFromNewsgroup(String fromNewsgroup) {
        this.fromNewsgroup = fromNewsgroup;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String refIds) {
        this.references = refIds;
    }
}

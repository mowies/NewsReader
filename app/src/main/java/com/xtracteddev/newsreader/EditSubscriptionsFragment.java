package com.xtracteddev.newsreader;

import android.app.Fragment;
import android.os.Bundle;

import java.util.ArrayList;

public class EditSubscriptionsFragment extends Fragment{

    private ArrayList<EditSubscriptionsActivity.NewsGroupItem> newsGroupItems;
    private int checkedSelection;
    private int currentDetailView;
    private long msgLoadDefaultInterval;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public ArrayList<EditSubscriptionsActivity.NewsGroupItem> getNewsGroupItems() {
        return newsGroupItems;
    }

    public void setNewsGroupItems(ArrayList<EditSubscriptionsActivity.NewsGroupItem> newsGroupItems) {
        this.newsGroupItems = newsGroupItems;
    }

    public int getCheckedSelection() {
        return checkedSelection;
    }

    public void setCheckedSelection(int checkedSelection) {
        this.checkedSelection = checkedSelection;
    }

    public int getCurrentDetailView() {
        return currentDetailView;
    }

    public void setCurrentDetailView(int currentDetailView) {
        this.currentDetailView = currentDetailView;
    }

    public long getMsgLoadDefaultInterval() {
        return msgLoadDefaultInterval;
    }

    public void setMsgLoadDefaultInterval(long msgLoadDefaultInterval) {
        this.msgLoadDefaultInterval = msgLoadDefaultInterval;
    }
}

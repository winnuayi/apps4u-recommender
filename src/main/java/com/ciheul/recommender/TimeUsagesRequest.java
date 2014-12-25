package com.ciheul.recommender;

import java.util.ArrayList;

public class TimeUsagesRequest {

    String androidID;
    ArrayList<TimeUsageModel> timeUsages;

    static class TimeUsageModel {
        int startEpoch;
        int endEpoch;
        String appName;

        public TimeUsageModel() {}

        public int getStartEpoch() { return startEpoch; }
        public int getEndEpoch() { return endEpoch; }
        public String getAppName() { return appName; }

        public void setStartEpoch(int start) { startEpoch = start; }
        public void setEndEpoch(int end) { endEpoch = end; }
        public void setAppName(String name) { appName = name; }
    }

    public TimeUsagesRequest() {}

    public ArrayList<TimeUsageModel> getTimeUsages() { return timeUsages; }
    public String getAndroidID() { return androidID; }

    public void setTimeUsages(ArrayList<TimeUsageModel> timeUsages) {
        this.timeUsages = timeUsages;
    }

    public void setAndroidID(String androidID) {
        this.androidID = androidID;
    }

}

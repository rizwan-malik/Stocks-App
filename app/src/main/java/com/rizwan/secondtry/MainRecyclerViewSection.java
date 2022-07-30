package com.rizwan.secondtry;

import java.util.List;

public class MainRecyclerViewSection {

    private String sectionName;

    private String networthHeading;
    private String networthAmount;

    private List<FavoriteRecyclerItem> sectionItems;

    public MainRecyclerViewSection(String sectionName, String networthHeading, String networthAmount, List<FavoriteRecyclerItem> sectionItems) {
        this.sectionName = sectionName;
        this.networthHeading = networthHeading;
        this.networthAmount = networthAmount;
        this.sectionItems = sectionItems;
    }

    public String getSectionName() {
        return sectionName;
    }

    public List<FavoriteRecyclerItem> getSectionItems() {
        return sectionItems;
    }

    public String getNetworthHeading() {
        return networthHeading;
    }

    public String getNetworthAmount() {
        return networthAmount;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public void setSectionItems(List<FavoriteRecyclerItem> sectionItems) {
        this.sectionItems = sectionItems;
    }
}

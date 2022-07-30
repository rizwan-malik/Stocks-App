package com.rizwan.secondtry;

import java.util.List;

public class NewsSection {

    private DetailsInterfaces.NewsData sectionName;
    private List<DetailsInterfaces.NewsData> sectionItem;

    public NewsSection(DetailsInterfaces.NewsData sectionName, List<DetailsInterfaces.NewsData> sectionItem) {
        this.sectionName = sectionName;
        this.sectionItem = sectionItem;
    }

    public DetailsInterfaces.NewsData getSectionName() {
        return sectionName;
    }

    public void setSectionName(DetailsInterfaces.NewsData sectionName) {
        this.sectionName = sectionName;
    }

    public List<DetailsInterfaces.NewsData> getSectionItem() {
        return sectionItem;
    }

    public void setSectionItem(List<DetailsInterfaces.NewsData> sectionItem) {
        this.sectionItem = sectionItem;
    }
}

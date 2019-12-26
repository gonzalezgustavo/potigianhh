package com.example.potigianhh.model;

import java.util.Map;

public class CloseRequestPayload {
    private Map<Integer, Integer> articleCount;
    private int printer;
    private int bags;

    public CloseRequestPayload(Map<Integer, Integer> articleCount, int printer, int bags) {
        this.articleCount = articleCount;
        this.printer = printer;
        this.bags = bags;
    }

    public Map<Integer, Integer> getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(Map<Integer, Integer> articleCount) {
        this.articleCount = articleCount;
    }

    public int getPrinter() {
        return printer;
    }

    public void setPrinter(int printer) {
        this.printer = printer;
    }

    public int getBags() {
        return bags;
    }

    public void setBags(int bags) {
        this.bags = bags;
    }
}

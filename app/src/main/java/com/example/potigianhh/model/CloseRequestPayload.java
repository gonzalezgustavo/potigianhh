package com.example.potigianhh.model;

import java.util.Map;

public class CloseRequestPayload {
    private Map<Integer, Integer> articleCount;
    private int printer;

    public CloseRequestPayload(Map<Integer, Integer> articleCount, int printer) {
        this.articleCount = articleCount;
        this.printer = printer;
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
}

package com.example.MADPROJECT;

public class NewsItem {
    private String headline;
    private String link;
    private String pubDate;
    private String content;

    public NewsItem(String headline, String link, String pubDate, String content) {
        this.headline = headline;
        this.link = link;
        this.pubDate = pubDate;
        this.content = content;
    }

    public String getHeadline() {
        return headline;
    }

    public String getLink() {
        return link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

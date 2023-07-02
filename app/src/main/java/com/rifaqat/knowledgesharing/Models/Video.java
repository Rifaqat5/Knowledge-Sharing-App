package com.rifaqat.knowledgesharing.Models;

public class Video {
    private String postVideoId,postVideo,descriptionVideo,postedVideoBy;
    private long postedVideoAt;

    public Video() {
    }

    public Video(String postVideoId, String postVideo, String descriptionVideo, String postedVideoBy, long postedVideoAt) {
        this.postVideoId = postVideoId;
        this.postVideo = postVideo;
        this.descriptionVideo = descriptionVideo;
        this.postedVideoBy = postedVideoBy;
        this.postedVideoAt = postedVideoAt;
    }

    public String getPostVideoId() {
        return postVideoId;
    }

    public void setPostVideoId(String postVideoId) {
        this.postVideoId = postVideoId;
    }

    public String getPostVideo() {
        return postVideo;
    }

    public void setPostVideo(String postVideo) {
        this.postVideo = postVideo;
    }

    public String getDescriptionVideo() {
        return descriptionVideo;
    }

    public void setDescriptionVideo(String descriptionVideo) {
        this.descriptionVideo = descriptionVideo;
    }

    public String getPostedVideoBy() {
        return postedVideoBy;
    }

    public void setPostedVideoBy(String postedVideoBy) {
        this.postedVideoBy = postedVideoBy;
    }

    public long getPostedVideoAt() {
        return postedVideoAt;
    }

    public void setPostedVideoAt(long postedVideoAt) {
        this.postedVideoAt = postedVideoAt;
    }

}

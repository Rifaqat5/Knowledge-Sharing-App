package com.rifaqat.knowledgesharing.Models;

public class Document {
    String title, postDocument, postDocumentId,postedDocumentBy;
    private long postedVideoAt;

    public Document() {
    }

    public Document(String title, String postDocument, String postDocumentId, String postedDocumentBy, long postedVideoAt) {
        this.title = title;
        this.postDocument = postDocument;
        this.postDocumentId = postDocumentId;
        this.postedDocumentBy = postedDocumentBy;
        this.postedVideoAt = postedVideoAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostDocument() {
        return postDocument;
    }

    public void setPostDocument(String postDocument) {
        this.postDocument = postDocument;
    }

    public String getPostDocumentId() {
        return postDocumentId;
    }

    public void setPostDocumentId(String postDocumentId) {
        this.postDocumentId = postDocumentId;
    }

    public String getPostedDocumentBy() {
        return postedDocumentBy;
    }

    public void setPostedDocumentBy(String postedDocumentBy) {
        this.postedDocumentBy = postedDocumentBy;
    }

    public long getPostedVideoAt() {
        return postedVideoAt;
    }

    public void setPostedVideoAt(long postedVideoAt) {
        this.postedVideoAt = postedVideoAt;
    }
}

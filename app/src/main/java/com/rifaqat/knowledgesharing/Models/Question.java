package com.rifaqat.knowledgesharing.Models;

public class Question {
    private String questionId,questionDescription,questionBy;
    private long questionAt;

    public Question() {
    }

    public Question(String questionId, String questionDescription, String questionBy, long questionAt) {
        this.questionId = questionId;
        this.questionDescription = questionDescription;
        this.questionBy = questionBy;
        this.questionAt = questionAt;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }

    public String getQuestionBy() {
        return questionBy;
    }

    public void setQuestionBy(String questionBy) {
        this.questionBy = questionBy;
    }

    public long getQuestionAt() {
        return questionAt;
    }

    public void setQuestionAt(long questionAt) {
        this.questionAt = questionAt;
    }
}

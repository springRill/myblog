package com.myblog.dto;

import java.util.List;

public class PostDto {
    Long id;
    String title;
    String text;
    List<String> tags;
    String imagePath;
    Integer likesCount;

    List<CommentDto> comments;
    String tagsAsText;
    String textPreview;
    List<String> textParts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }

    public String getTagsAsText() {
        return tagsAsText;
    }

    public void setTagsAsText(String tagsAsText) {
        this.tagsAsText = tagsAsText;
    }

    public String getTextPreview() {
        return textPreview;
    }

    public void setTextPreview(String textPreview) {
        this.textPreview = textPreview;
    }

    public List<String> getTextParts() {
        return textParts;
    }

    public void setTextParts(List<String> textParts) {
        this.textParts = textParts;
    }

    //////////////////////////////////////////////////
/*
    public String getTextPreview() {
        if(text.length()<=100){
            return text;
        }
        return text.substring(0, 100) + " ...";

    }
*/

/*
    public List<String> getTextParts() {
        return List.of(text.split("\r\n"));
    }
*/

/*
    public String getTagsAsText() {
        return "post.getTagsAsText()" + tags;
    }
*/

}

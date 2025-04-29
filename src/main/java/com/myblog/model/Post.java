package com.myblog.model;

import java.util.List;

public class Post {

    private Long id;
    private String title;
    private String text;
    private String tags;

    private String imagePath;
    private Integer likesCount = 0;
    private Integer comments;

    public Post(Long id, String title, String text, String tags, String imagePath) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.tags = tags;
        this.imagePath = imagePath;
    }

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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTagsAsText() {
        return tags;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTextPreview() {
        if(text.length()<=3){
            return text;
        }
        return text.substring(0, 3);

    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public List getComments() {
        return List.of("", "");
    }

    public List<String> getTextParts() {
        List<String> splitedPost = List.of(text.split("\r\n"));
        return splitedPost;
    }
}

package com.myblog.model;

import java.util.ArrayList;
import java.util.List;

public class Post {

    private Long id;
    private String title;
    private String text;
    private String tags;
    private String image;

    public Post(Long id, String title, String text, String tags, String image) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.tags = tags;
        this.image = image;
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

    public String getTextPreview() {
        return text.substring(0,4);
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public int getLikesCount(){
        return 10;
    }

    public List getComments(){
        return List.of("", "");
    }

    public List<String> getTextParts(){
        List<String> splitedPost = List.of(text.split("/n"));
        return splitedPost;
    }
}

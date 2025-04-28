package com.myblog.model;

public record Paging(int pageSize, boolean hasPrevious, boolean hasNext, int pageNumber) {
}
/*
public class Paging {

    private int pageSize = 5;

    public int pageSize(){
        return pageSize;
    }

    public boolean hasPrevious(){
        return true;
    }

    public boolean hasNext(){
        return true;
    }
    public int pageNumber(){
        return 10;
    }
}
*/

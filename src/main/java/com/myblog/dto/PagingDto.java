package com.myblog.dto;

public class PagingDto {

    private int pageSize;
    private boolean hasPrevious;
    private boolean hasNext;
    private int pageNumber;

    public PagingDto(int pageSize, boolean hasPrevious, boolean hasNext, int pageNumber) {
        this.pageSize = pageSize;
        this.hasPrevious = hasPrevious;
        this.hasNext = hasNext;
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
package com.myblog.model;

public record Paging(int pageSize, boolean hasPrevious, boolean hasNext, int pageNumber) {
}
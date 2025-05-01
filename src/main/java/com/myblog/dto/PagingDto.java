package com.myblog.dto;

public record PagingDto(int pageSize, boolean hasPrevious, boolean hasNext, int pageNumber) {
}
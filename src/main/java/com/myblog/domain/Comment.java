package com.myblog.domain;

public record Comment(Long id, Long postId, String text) {
}

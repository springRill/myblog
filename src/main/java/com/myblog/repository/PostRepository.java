package com.myblog.repository;

import com.myblog.model.Post;

import java.util.List;

public interface PostRepository {

    List<Post> findAllPosts();

    Post getPostById(Long id);

    Long addPost(Post post);

    void likePost(Long id, boolean like);

    void editPost(Post post);

    void addComment(Long postId, String text);

    void editComment(Long postId, Long commentId, String text);

    void deleteComment(Long postId, Long commentId);

    void deletePost(Long postId);

}

package com.myblog.repository;

import com.myblog.domain.Comment;
import com.myblog.domain.Post;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PostRepository {

    List<Post> findPosts(String search);

    Post getPostById(Long postId);

    List<Comment> getCommentsByPostId(Long postId);

    Long addPost(Post post);

    void likePost(Long id, boolean like);

    void editPost(Post post);

    void addComment(Comment comment);

    void editComment(Comment comment);

    void deleteComment(Long commentId);

    void deleteCommentsByPostId(Long postId);

    void deletePost(Long postId);
}

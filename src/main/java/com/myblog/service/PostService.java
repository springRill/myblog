package com.myblog.service;

import com.myblog.model.Comment;
import com.myblog.model.Post;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PostService {

    private static long postSequence = 1;
    private List<Post> postList;

    public PostService() {
        postList = Stream.iterate(1l, i -> i + 1).limit(3).
                map(i -> new Post(postSequence++, "title_" + i, "asdafsd", null, "empty_image.png")).
                collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Post> findAllPosts() {
        return postList;
    }

    public Post getPostById(Long id) {
        return postList.stream().filter(post -> post.getId().equals(id)).toList().getFirst();
    }

    public Post addPost(Post post) {
        post.setId(postSequence++);
        postList.add(post);
        return post;
    }

    public void likePost(Long id, boolean like) {
        Post post = getPostById(id);
        if (like) {
            post.setLikesCount(post.getLikesCount() + 1);
        } else {
            post.setLikesCount(post.getLikesCount() - 1);
        }
    }

    public void editPost(Post post) {
        Post existingPost = getPostById(post.getId());
        existingPost.setTitle(post.getTitle());
        existingPost.setText(post.getText());
        existingPost.setTags(post.getTags());
        if (post.getImagePath() != null) {
            existingPost.setImagePath(post.getImagePath());
        }
    }

    public void addComment(Long postId, String text) {
        Post post = getPostById(postId);
        List<Comment> commentList = post.getComments();
        Long maxCommentId = commentList.stream().map(comment -> comment.getId()).max(Long::compareTo).orElse(0L);
        Comment comment = new Comment(++maxCommentId, text);
        commentList.add(comment);
    }

    public void editComment(Long postId, Long commentId, String text) {
        Post post = getPostById(postId);
        List<Comment> commentList = post.getComments();
        Comment existingComment = commentList.stream().filter(comment -> comment.getId().equals(commentId)).toList().getFirst();
        existingComment.setText(text);
    }

    public void deleteComment(Long postId, Long commentId) {
        Post post = getPostById(postId);
        List<Comment> commentList = post.getComments();
        Comment existingComment = commentList.stream().filter(comment -> comment.getId().equals(commentId)).toList().getFirst();
        commentList.remove(existingComment);
    }

    public void deletePost(Long postId) {
        Post post = getPostById(postId);
        postList.remove(post);
    }

}

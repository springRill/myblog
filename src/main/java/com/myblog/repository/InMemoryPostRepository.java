package com.myblog.repository;

import com.myblog.model.Comment;
import com.myblog.model.Post;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Primary
@Repository
public class InMemoryPostRepository implements PostRepository {

    private static long postSequence = 1;
    private final List<Post> postList;

    public InMemoryPostRepository() {
        postList = Stream.iterate(1L, i -> i + 1).limit(3).
                map(i -> new Post(postSequence++, "title_" + i, "asdafsd", "", "empty_image.png", 0)).
                collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<Post> findPosts(String search) {
        return postList.stream().filter(post -> {
            if (post.getTagsAsText().contains(search)) {
                return true;
            }
            return false;
        }).toList();
    }

    @Override
    public Post getPostById(Long id) {
        return postList.stream().filter(post -> post.getId().equals(id)).toList().getFirst();
    }

    @Override
    public Long addPost(Post post) {
        post.setId(postSequence++);
        postList.add(post);
        return post.getId();
    }

    @Override
    public void likePost(Long id, boolean like) {
        Post post = getPostById(id);
        if (like) {
            post.setLikesCount(post.getLikesCount() + 1);
        } else {
            post.setLikesCount(post.getLikesCount() - 1);
        }
    }

    @Override
    public void editPost(Post post) {
        Post existingPost = getPostById(post.getId());
        existingPost.setTitle(post.getTitle());
        existingPost.setText(post.getText());
        existingPost.setTags(post.getTagsAsText());
        if (post.getImagePath() != null) {
            existingPost.setImagePath(post.getImagePath());
        }
    }

    @Override
    public void addComment(Long postId, String text) {
        Post post = getPostById(postId);
        List<Comment> commentList = post.getComments();
        Long maxCommentId = commentList.stream().map(Comment::getId).max(Long::compareTo).orElse(0L);
        Comment comment = new Comment(++maxCommentId, text);
        commentList.add(comment);
    }

    @Override
    public void editComment(Long postId, Long commentId, String text) {
        Post post = getPostById(postId);
        List<Comment> commentList = post.getComments();
        Comment existingComment = commentList.stream().filter(comment -> comment.getId().equals(commentId)).toList().getFirst();
        existingComment.setText(text);
    }

    @Override
    public void deleteComment(Long postId, Long commentId) {
        Post post = getPostById(postId);
        List<Comment> commentList = post.getComments();
        Comment existingComment = commentList.stream().filter(comment -> comment.getId().equals(commentId)).toList().getFirst();
        commentList.remove(existingComment);
    }

    @Override
    public void deletePost(Long postId) {
        Post post = getPostById(postId);
        postList.remove(post);
    }
}

package com.myblog.service;

import com.myblog.model.Post;
import com.myblog.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> findPosts(String search) {
        if (search == null) {
            search = "";
        }
        return this.postRepository.findPosts(search);
    }

    public Post getPostById(Long id) {
        return this.postRepository.getPostById(id);
    }

    public Long addPost(Post post) {
        post.setTags(post.getTags().trim().replaceAll("\\s{2,}", " "));
        return this.postRepository.addPost(post);
    }

    public void likePost(Long id, boolean like) {
        this.postRepository.likePost(id, like);
    }

    public void editPost(Post post) {
        post.setTags(post.getTagsAsText().trim().replaceAll("\\s{2,}", " "));
        this.postRepository.editPost(post);
    }

    public void addComment(Long postId, String text) {
        this.postRepository.addComment(postId, text);
    }

    public void editComment(Long postId, Long commentId, String text) {
        this.postRepository.editComment(postId, commentId, text);
    }

    public void deleteComment(Long postId, Long commentId) {
        this.postRepository.deleteComment(postId, commentId);
    }

    public void deletePost(Long postId) {
        this.postRepository.deletePost(postId);
    }

}

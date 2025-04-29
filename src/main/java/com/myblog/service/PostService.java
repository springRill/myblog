package com.myblog.service;

import com.myblog.model.Post;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

}

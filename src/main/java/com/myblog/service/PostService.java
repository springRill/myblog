package com.myblog.service;

import com.myblog.model.Post;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PostService {

    private static long postSequence = 0;
    private List<Post> postList;

    public PostService() {
        postList = Stream.iterate(1l, i -> i + 1).limit(3).
                map(i -> new Post(postSequence ++, "title_" + i, "asdafsd", null, "empty_image.png")).
                collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Post> findAll(){
        return postList;
    }

    public Post getById(Long id) {
        return postList.stream().filter(post -> post.getId().equals(id)).toList().getFirst();
    }

    public void addPost(Post post){
        post.setId(postSequence ++);
        postList.add(post);
    }
}

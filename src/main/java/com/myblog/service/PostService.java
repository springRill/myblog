package com.myblog.service;

import com.myblog.model.Post;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class PostService {

    List<Post> postList = new ArrayList<>();

    public PostService() {
        this.postList = Stream.iterate(1l, i -> i + 1).limit(3).
                map(i -> new Post(i,"title_" + i,"text_" + i + "/n text_" + i + ".2", null, null)).
                toList();
    }

    public List<Post> findAll(){
        return postList;
    }

    public Post getById(Long id) {
        return postList.stream().filter(post -> post.getId().equals(id)).toList().getFirst();
    }
}

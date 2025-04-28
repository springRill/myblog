package com.myblog.controller;

import com.myblog.model.Post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostsController {

    @GetMapping
    public String getPosts(Model model){
        List postList = List.of(
                new Post(1, "tit1", "txt1"),
                new Post(2, "tit2", "txt2")
        );
        model.addAttribute("posts", postList);
        return "posts";
    }
}

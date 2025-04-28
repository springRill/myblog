package com.myblog.controller;

import com.myblog.model.Paging;
import com.myblog.model.Post;
import com.myblog.service.PostService;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
//@RequestMapping("/posts")
public class PostsController {

    PostService postService;

    public PostsController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping()
    public String homePage() {
        return "redirect:/posts";
    }


    @GetMapping("/posts")
    public String getPosts(Model model,
                           @RequestParam(name = "search") @Nullable String search,
                           @RequestParam(name = "pageSize", defaultValue = "10") @Nullable int pageSize,
                           @RequestParam(name = "pageNumber", defaultValue = "1") @Nullable int pageNumber
    ) {
        List<Post> postList = postService.findAll();
        List<Post> displayPosts = postList.stream().skip(pageSize * (pageNumber - 1)).limit(pageSize).toList();

        model.addAttribute("paging", new Paging(pageSize, pageNumber > 1, pageSize * pageNumber < postList.size(), pageNumber));
        model.addAttribute("posts", displayPosts);

        return "posts";
    }

    @GetMapping(value = "/posts/{id}")
    public String getPost(Model model, @PathVariable(name = "id") Long id) {
        Post post = postService.getById(id);

        model.addAttribute("post", post);

        return "post";
    }

}

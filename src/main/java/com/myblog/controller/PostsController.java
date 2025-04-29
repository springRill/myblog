package com.myblog.controller;

import com.myblog.model.Paging;
import com.myblog.model.Post;
import com.myblog.service.ImageService;
import com.myblog.service.PostService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class PostsController {

    private final ResourceLoader resourceLoader;
    private final ImageService imageService;
    private final PostService postService;

    public PostsController(ResourceLoader resourceLoader, ImageService imageService, PostService postService) {
        this.resourceLoader = resourceLoader;
        this.imageService = imageService;
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

    @GetMapping(value = "/posts/add")
    public String addPostPage() {
        return "add-post";
    }

    @PostMapping(value = "/posts", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String createPost(@RequestParam(name = "title") String title,
                             @RequestParam(name = "image") MultipartFile image,
                             @RequestParam(name = "tags") String tags,
                             @RequestParam(name = "text") String text)  throws IOException {

        String fileName = imageService.uploadImage(image);
        Post post = new Post(null, title, text, tags, fileName);
        postService.addPost(post);
        return "redirect:/posts";
    }

    @GetMapping("/images/{id}")
    @ResponseBody
    public Resource getImage(@PathVariable(name = "id") Long id) {
        return imageService.getImage(id);
    }

}

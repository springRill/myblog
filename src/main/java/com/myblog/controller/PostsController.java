package com.myblog.controller;

import com.myblog.model.Paging;
import com.myblog.model.Post;
import com.myblog.service.ImageService;
import com.myblog.service.PostService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class PostsController {

    private final ImageService imageService;
    private final PostService postService;

    public PostsController(ImageService imageService, PostService postService) {
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
        List<Post> postList = postService.findAllPosts();
        List<Post> displayPosts = postList.stream().skip(pageSize * (pageNumber - 1)).limit(pageSize).toList();

        model.addAttribute("paging", new Paging(pageSize, pageNumber > 1, pageSize * pageNumber < postList.size(), pageNumber));
        model.addAttribute("posts", displayPosts);

        return "posts";
    }

    @GetMapping(value = "/posts/{id}")
    public String getPost(Model model, @PathVariable(name = "id") Long id) {
        Post post = postService.getPostById(id);

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
                             @RequestParam(name = "text") String text) {

        String fileName = imageService.uploadImage(image);
        Post post = new Post(null, title, text, tags, fileName, 0);
        Long id = postService.addPost(post);
        return "redirect:/posts/%d".formatted(id);
    }

    @GetMapping("/images/{id}")
    @ResponseBody
    public Resource getImage(@PathVariable(name = "id") Long id) {
        return imageService.getImage(id);
    }

    @PostMapping(value = "/posts/{id}/like")
    public String likePost(@PathVariable(name = "id") Long id,
                           @RequestParam(name = "like") boolean like) {
        postService.likePost(id, like);
        return "redirect:/posts/%d".formatted(id);
    }

    @GetMapping(value = "/posts/{id}/edit")
    public String editPostPage(@PathVariable(name = "id") Long id, Model model) {
        model.addAttribute("post", postService.getPostById(id));
        return "add-post";
    }

    @PostMapping(value = "/posts/{id}")
    public String editPost(@PathVariable(name = "id") Long id,
                           @RequestParam(name = "title") String title,
                           @RequestParam(name = "image") MultipartFile image,
                           @RequestParam(name = "tags") String tags,
                           @RequestParam(name = "text") String text) {

        String fileName = imageService.uploadImage(image);
        Post post = new Post(id, title, text, tags, fileName, null);
        postService.editPost(post);
        return "redirect:/posts/%d".formatted(id);
    }

    @PostMapping(value = "/posts/{id}/comments")
    public String addComment(@PathVariable(name = "id") Long postId,
                             @RequestParam(name = "text") String text) {
        postService.addComment(postId, text);
        return "redirect:/posts/%d".formatted(postId);
    }

    @PostMapping(value = "/posts/{id}/comments/{commentId}")
    public String editComment(@PathVariable(name = "id") Long postId,
                              @PathVariable(name = "commentId") Long commentId,
                              @RequestParam(name = "text") String text) {
        postService.editComment(postId, commentId, text);
        return "redirect:/posts/%d".formatted(postId);
    }

    @PostMapping(value = "/posts/{id}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable(name = "id") Long postId,
                                @PathVariable(name = "commentId") Long commentId) {
        postService.deleteComment(postId, commentId);
        return "redirect:/posts/%d".formatted(postId);
    }

    @PostMapping(value = "/posts/{id}/delete")
    public String deletePost(@PathVariable(name = "id") Long postId) {
        postService.deletePost(postId);
        return "redirect:/posts";
    }

}

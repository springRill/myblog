package com.myblog.controller;

import com.myblog.dto.CommentDto;
import com.myblog.dto.PostDto;
import com.myblog.mapper.PostMapper;
import com.myblog.service.ImageService;
import com.myblog.service.PostService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@WebMvcTest(PostsController.class)
@ActiveProfiles("test")
public class PostsControllerIntegrationMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private ImageService imageService;

    @Test
    void homePage() throws Exception {
        mockMvc.perform(get(""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));
    }

    @Test
    void getPosts() throws Exception {
        PostDto postDto_1 = new PostDto();
        postDto_1.setId(1L);
        postDto_1.setComments(List.of());
        postDto_1.setTextPreview("text_1");

        PostDto postDto_2 = new PostDto();
        postDto_2.setId(2L);
        postDto_2.setComments(List.of());
        postDto_2.setTextPreview("text_2");

        when(postService.findPosts("")).thenReturn(List.of(postDto_1, postDto_2));

        mockMvc.perform(get("/posts")
                        .param("search", ""))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("posts"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(xpath("//table/tr").nodeCount(3))
                .andExpect(xpath("//table/tr[2]/td[1]/p[2]").string("text_1"))
                .andExpect(status().isOk());
    }

    @Test
    void likePost() throws Exception {
        Long postId = 1L;

        PostDto postDto_1 = new PostDto();
        postDto_1.setId(postId);
        postDto_1.setLikesCount(0);
        postDto_1.setComments(List.of());
        postDto_1.setTextPreview("text_1");

        when(postService.getPostById(postId)).thenReturn(postDto_1);
        doAnswer(invocation -> {postDto_1.setLikesCount(postDto_1.getLikesCount()+1); return null;}).when(postService).likePost(postId, true);
        doAnswer(invocation -> {postDto_1.setLikesCount(postDto_1.getLikesCount()-1); return null;}).when(postService).likePost(postId, false);

        int likesCount = postService.getPostById(postId).getLikesCount();

        mockMvc.perform(post("/posts/%d/like".formatted(postId))
                        .param("like", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/%d".formatted(postId)));
        assertEquals(likesCount + 1, postService.getPostById(postId).getLikesCount());

        mockMvc.perform(post("/posts/%d/like".formatted(postId))
                        .param("like", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/%d".formatted(postId)));
        assertEquals(likesCount, postService.getPostById(postId).getLikesCount());
    }

    @Test
    void addComment() throws Exception {
        Long postId = 1L;

        PostDto postDto_1 = new PostDto();
        postDto_1.setId(postId);
        postDto_1.setComments(List.of());

        CommentDto commentDto = new CommentDto(1L, "commentText");
        doAnswer(invocation -> {postDto_1.setComments(List.of(commentDto)); return null;}).when(postService).addComment(eq(1L), Mockito.any(CommentDto.class));
        when(postService.getPostById(postId)).thenReturn(postDto_1);

        mockMvc.perform(post("/posts/%d/comments".formatted(postId))
                        .param("text", "commentText"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/%d".formatted(postId)));

        assertEquals(1, postService.getPostById(postId).getComments().size());
    }

}

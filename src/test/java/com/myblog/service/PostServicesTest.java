package com.myblog.service;

import com.myblog.dto.CommentDto;
import com.myblog.dto.PostDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class PostServicesTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostService postService;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM posts");

        jdbcTemplate.execute("INSERT INTO posts (title, text, tags, image_path, likes_count) VALUES ('title_1', 'text_1', 'tag1 tag2 tags3', 'image_path_1', 1)");
        jdbcTemplate.execute("INSERT INTO posts (title, text, tags, image_path, likes_count) VALUES ('title_2', 'text_2', 'tag2 tag3 tags4', 'image_path_2', 2)");
    }

    @Test
    void findPosts() {
        assertEquals(0, postService.findPosts("tag1_no").size());
        assertEquals(2, postService.findPosts("").size());
        assertEquals(1, postService.findPosts("tag1").size());
        assertEquals(2, postService.findPosts("tag2").size());
    }

    @Test
    void getPostById() {
        Long postId = postService.findPosts("").getFirst().getId();
        PostDto postDto = postService.getPostById(postId);
        assertNotNull(postDto);
    }

    @Test
    void addPost() {
        assertEquals(2, postService.findPosts("").size());
        PostDto postDto = new PostDto();
        postDto.setTitle("title_3");
        postDto.setText("text_3");
        postDto.setTagsAsText("tag3 tag4 tags5");
        postDto.setImagePath("image_path_3");
        postDto.setLikesCount(3);

        postService.addPost(postDto);
        assertEquals(3, postService.findPosts("").size());
    }

    @Test
    void likePost() {
        Long postId = postService.findPosts("").getFirst().getId();
        PostDto postDto = postService.getPostById(postId);
        Integer likesCount = postDto.getLikesCount();

        postService.likePost(postId, true);
        postDto = postService.getPostById(postId);
        assertEquals(likesCount + 1, postDto.getLikesCount());

        postService.likePost(postId, false);
        postDto = postService.getPostById(postId);
        assertEquals(likesCount, postDto.getLikesCount());
    }

    @Test
    void editPost() {
        Long postId = postService.findPosts("").getFirst().getId();
        PostDto postDto = postService.getPostById(postId);
        postDto.setTitle("changedTitle");
        postDto.setText("changedText");
        postDto.setTagsAsText("changedTag");
        postDto.setImagePath("changedImagePath");
        postService.editPost(postDto);

        PostDto changedPostDto = postService.getPostById(postId);
        assertEquals("changedTitle", changedPostDto.getTitle());
        assertEquals("changedText", changedPostDto.getText());
        assertEquals(List.of("changedTag"), changedPostDto.getTags());
        assertEquals("changedTag", changedPostDto.getTagsAsText());
        assertEquals("changedImagePath", changedPostDto.getImagePath());
    }

    @Test
    void addComment() {
        PostDto postDto = postService.findPosts("").getFirst();
        assertEquals(0, postDto.getComments().size());

        CommentDto commentDto = new CommentDto(null, "postComment");
        postService.addComment(postDto.getId(), commentDto);
        PostDto postWithCommentDto = postService.getPostById(postDto.getId());
        assertEquals(1, postWithCommentDto.getComments().size());
        assertEquals("postComment", postWithCommentDto.getComments().getFirst().getText());
    }

    @Test
    void editComment() {
        PostDto postDto = postService.findPosts("").getFirst();
        assertEquals(0, postDto.getComments().size());

        CommentDto commentDto = new CommentDto(null, "postComment");
        postService.addComment(postDto.getId(), commentDto);
        PostDto postWithCommentDto = postService.getPostById(postDto.getId());
        assertEquals(1, postWithCommentDto.getComments().size());
        assertEquals("postComment", postWithCommentDto.getComments().getFirst().getText());

        CommentDto changedCommentDto = new CommentDto(postWithCommentDto.getComments().getFirst().getId(), "changedPostComment");
        postService.editComment(postDto.getId(), changedCommentDto);
        PostDto postWithChangedCommentDto = postService.getPostById(postDto.getId());
        assertEquals(1, postWithChangedCommentDto.getComments().size());
        assertEquals("changedPostComment", postWithChangedCommentDto.getComments().getFirst().getText());
    }

    @Test
    void deleteComment() {
        PostDto postDto = postService.findPosts("").getFirst();
        assertEquals(0, postDto.getComments().size());

        CommentDto commentDto = new CommentDto(null, "postComment");
        postService.addComment(postDto.getId(), commentDto);
        PostDto postWithCommentDto = postService.getPostById(postDto.getId());
        assertEquals(1, postWithCommentDto.getComments().size());
        assertEquals("postComment", postWithCommentDto.getComments().getFirst().getText());

        postService.deleteComment(postWithCommentDto.getComments().getFirst().getId());
        PostDto postWithNoCommentDto = postService.getPostById(postDto.getId());
        assertEquals(0, postWithNoCommentDto.getComments().size());
    }

    @Test
    void deletePost() {
        assertEquals(2, postService.findPosts("").size());

        postService.deletePost(postService.findPosts("").getFirst().getId());
        assertEquals(1, postService.findPosts("").size());
    }

}
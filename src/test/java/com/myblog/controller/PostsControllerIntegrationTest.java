package com.myblog.controller;

import com.myblog.configuration.WebConfiguration;
import com.myblog.dto.CommentDto;
import com.myblog.dto.PostDto;
import com.myblog.service.ImageService;
import com.myblog.service.PostService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitConfig(classes = {WebConfiguration.class})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
class PostsControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostService postService;

    @Autowired
    private ImageService imageService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build(); // Настройка с контекстом

        // Очистка базы данных
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM posts");

        // Добавление тестовых данных
        jdbcTemplate.execute("INSERT INTO posts (title, text, tags, image_path, likes_count) VALUES ('title_1', 'text_1', 'tag1 tag2 tags3', 'image_path_1', 1)");
        jdbcTemplate.execute("INSERT INTO posts (title, text, tags, image_path, likes_count) VALUES ('title_2', 'text_2', 'tag2 tag3 tags4', 'image_path_2', 2)");
    }

    @Test
    void homePage() throws Exception {
        mockMvc.perform(get(""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));
    }

    @Test
    void getPosts() throws Exception {
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
    void getPost() throws Exception {
        PostDto postDto = new PostDto();
        postDto.setTitle("title_3");
        postDto.setText("text_3");
        postDto.setTagsAsText("tag3 tag4 tags5");
        postDto.setImagePath("image_path_3");
        postDto.setLikesCount(3);
        Long id = postService.addPost(postDto);

        mockMvc.perform(get("/posts/" + id))
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(status().isOk());
    }

    @Test
    void addPostPage() throws Exception {
        mockMvc.perform(get("/posts/add"))
                .andExpect(view().name("add-post"))
                .andExpect(status().isOk());
    }

    @Test
    void createPost() throws Exception {
        assertEquals(2, postService.findPosts("").size());
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "testImage.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "imageContent".getBytes()
        );

        String redirectUrl = mockMvc.perform(multipart("/posts")
                        .file(imageFile)
                        .param("title", "title_3")
                        .param("tags", "tag3 tag4 tags5")
                        .param("text", "text_3")
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().is3xxRedirection()).andReturn().getResponse().getRedirectedUrl();
        assertEquals(3, postService.findPosts("").size());

        Long addedPostId = Long.valueOf(redirectUrl.substring(redirectUrl.lastIndexOf("/")+1));
        imageService.deleteImage(postService.getPostById(addedPostId).getImagePath());
    }

    @Test
    void getImage() throws Exception {
        Long postId = 1L;
        String imagePath = "testImage.jpg";
        byte[] imageBytes = "imagesContent".getBytes();
        Resource resource = new ByteArrayResource(imageBytes);

        PostDto postDto = new PostDto();
        postDto.setId(postId);
        postDto.setImagePath(imagePath);

        PostService mockPostService = mock(PostService.class);
        when(mockPostService.getPostById(postId)).thenReturn(postDto);

        ImageService mockImageService = mock(ImageService.class);
        when(mockImageService.getImage(imagePath)).thenReturn(resource);

        PostsController postsController = new PostsController(mockImageService, mockPostService);
        MockMvc mockMvc2 = MockMvcBuilders.standaloneSetup(postsController).build();

        mockMvc2.perform(get("/images/%d".formatted(postId)))
                .andExpect(status().isOk())
                .andExpect(content().bytes(imageBytes));
    }

    @Test
    void likePost() throws Exception {
        Long postId = postService.findPosts("").getFirst().getId();
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
    void editPostPage() throws Exception {
        Long postId = postService.findPosts("").getFirst().getId();

        mockMvc.perform(get("/posts/%d/edit".formatted(postId)))
                .andExpect(view().name("add-post"))
                .andExpect(model().attributeExists("post"))
                .andExpect(status().isOk());
    }

    @Test
    void editPost() throws Exception {
        PostDto postDto = postService.findPosts("").getFirst();

        assertNotEquals("title_3_changed", postDto.getTitle());
        assertNotEquals("tag3_changed tag4_changed tags5_changed", postDto.getTagsAsText());
        assertNotEquals("text_3_changed", postDto.getText());

        Long postId = postDto.getId();
        assertEquals(2, postService.findPosts("").size());

        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "testImage.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "imageContent".getBytes()
        );

        mockMvc.perform(multipart("/posts/%d".formatted(postId))
                        .file(imageFile)
                        .param("title", "title_3_changed")
                        .param("tags", "tag3_changed tag4_changed tags5_changed")
                        .param("text", "text_3_changed")
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/%d".formatted(postId)));

        assertEquals(2, postService.findPosts("").size());

        PostDto changedPostDto = postService.getPostById(postId);
        assertEquals("title_3_changed", changedPostDto.getTitle());
        assertEquals("tag3_changed tag4_changed tags5_changed", changedPostDto.getTagsAsText());
        assertEquals("text_3_changed", changedPostDto.getText());

        imageService.deleteImage(postService.getPostById(postId).getImagePath());
    }

    @Test
    void addComment() throws Exception {
        PostDto postDto = postService.findPosts("").getFirst();
        Long postId = postDto.getId();
        int commentCount = postDto.getComments().size();

        mockMvc.perform(post("/posts/%d/comments".formatted(postId))
                        .param("text", "commentText"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/%d".formatted(postId)));

        PostDto postWithCommentDto = postService.getPostById(postId);
        assertEquals(commentCount + 1, postWithCommentDto.getComments().size());
    }

    @Test
    void editComment() throws Exception {
        PostDto postDto = postService.findPosts("").getFirst();
        assertEquals(0, postDto.getComments().size());
        Long postId = postDto.getId();
        CommentDto commentDto = new CommentDto(null, "commentText");
        postService.addComment(postId, commentDto);

        PostDto postWithCommentDto = postService.getPostById(postId);
        assertEquals(1, postWithCommentDto.getComments().size());
        CommentDto addedComment = postWithCommentDto.getComments().getFirst();
        assertEquals("commentText", addedComment.getText());

        mockMvc.perform(post("/posts/%d/comments/%d".formatted(postId, addedComment.getId()))
                        .param("text", "changedCommentText"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/%d".formatted(postId)));

        PostDto postWithChangedCommentDto = postService.getPostById(postId);
        assertEquals(1, postWithChangedCommentDto.getComments().size());
        CommentDto changedComment = postWithChangedCommentDto.getComments().getFirst();
        assertEquals("changedCommentText", changedComment.getText());
    }

    @Test
    void deleteComment() throws Exception {
        PostDto postDto = postService.findPosts("").getFirst();
        assertEquals(0, postDto.getComments().size());
        Long postId = postDto.getId();
        CommentDto commentDto = new CommentDto(null, "commentText");
        postService.addComment(postId, commentDto);

        PostDto postWithCommentDto = postService.getPostById(postId);
        assertEquals(1, postWithCommentDto.getComments().size());

        Long addedCommentId = postWithCommentDto.getComments().getFirst().getId();
        mockMvc.perform(post("/posts/%d/comments/%d/delete".formatted(postId, addedCommentId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/%d".formatted(postId)));

        PostDto postWithNoCommentDto = postService.getPostById(postId);
        assertEquals(0, postWithNoCommentDto.getComments().size());

    }

    @Test
    void deletePost() throws Exception {

/*
        @PostMapping(value = "/posts/{id}/delete")
        public String deletePost(@PathVariable(name = "id") Long postId) {
            postService.deletePost(postId);
            return "redirect:/posts";
        }
*/
        assertEquals(2,  postService.findPosts("").size());

        Long postId = postService.findPosts("").getFirst().getId();
        mockMvc.perform(post("/posts/%d/delete".formatted(postId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts"));

        assertEquals(1,  postService.findPosts("").size());
    }

    @AfterAll
    static void clearImages() {
        System.out.println();

//        postService.findPosts("");

//        Path directory = Paths.get(imagePath);

/*
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    Files.delete(file);
                }
            }
        }
*/

    }
}
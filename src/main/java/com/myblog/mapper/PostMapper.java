package com.myblog.mapper;

import com.myblog.domain.Post;
import com.myblog.dto.PostDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostMapper {

    public PostDto toPostDto(Post post) {
        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setText(post.getText());
        postDto.setTagsAsText(post.getTags());
        postDto.setImagePath(post.getImagePath());
        postDto.setLikesCount(post.getLikesCount());

        postDto.setTags(List.of(postDto.getTagsAsText().split(" ")));
        postDto.setTextPreview(postDto.getText().length() < 100 ? postDto.getText() : postDto.getText().substring(0, 100) + " ...");
        postDto.setTextParts(List.of(postDto.getText().split("\r\n")));

        return postDto;
    }

    public Post toPost(PostDto postDto) {
        return new Post(
                postDto.getId(),
                postDto.getTitle(),
                postDto.getText(),
                postDto.getTagsAsText(),
                postDto.getImagePath(),
                postDto.getLikesCount()
        );
    }
}

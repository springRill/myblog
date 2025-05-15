package com.myblog.mapper;

import com.myblog.domain.Comment;
import com.myblog.dto.CommentDto;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentDto toCommentDto(Comment comment){
        return new CommentDto(
                comment.getId(),
                comment.getText()
        );
    }

    public Comment toComment(Long postId, CommentDto commentDto){
        return new Comment(
                commentDto.getId(),
                postId,
                commentDto.getText()
        );
    }

}

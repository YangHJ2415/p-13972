package com.back.domain.post.postComment.controller;

import com.back.domain.member.member.entity.Member;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import com.back.domain.post.postComment.dto.PostCommentDto;
import com.back.domain.post.postComment.entity.PostComment;
import com.back.global.exception.ServiceException;
import com.back.global.rq.Rq;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
@Tag(name = "ApiV1PostCommentController", description = "API 댓글 컨트롤러")
public class ApiV1PostCommentController {
    private final PostService postService;
    private final Rq rq;

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "다건 조회")
    public List<PostCommentDto> getItems(
            @PathVariable int postId
    ) {
        Post post = postService.findById(postId).get();

        return post
                .getComments()
                .stream()
                .map(PostCommentDto::new)
                .toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "단건 조회")
    public PostCommentDto getItem(
            @PathVariable int postId,
            @PathVariable int id
    ) {
        Post post = postService.findById(postId).get();

        PostComment postComment = post.findCommentById(id).get();

        return new PostCommentDto(postComment);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "삭제")
    public RsData<Void> delete(
            @PathVariable int postId,
            @PathVariable int id
    ) {
        Member actor = rq.getActor();

        Post post = postService.findById(postId).get();

        PostComment postComment = post.findCommentById(id).get();

        if (!actor.equals(postComment.getAuthor())) // 현재 사용자가 댓글 작성자와 일치하는지 확인
            throw new ServiceException("403-1", "댓글 삭제 권한이 없습니다.");

        postService.deleteComment(post, postComment);

        return new RsData<>(
                "200-1",
                "%d번 댓글이 삭제되었습니다.".formatted(id)
        );
    }


    record PostCommentModifyReqBody(
            @NotBlank
            @Size(min = 2, max = 100)
            String content
    ) {
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "수정")
    public RsData<Void> modify(
            @PathVariable int postId,
            @PathVariable int id,
            @Valid @RequestBody PostCommentModifyReqBody reqBody
    ) {
        Member actor = rq.getActor();

        Post post = postService.findById(postId).get();

        PostComment postComment = post.findCommentById(id).get();

        if(!actor.equals(postComment.getAuthor())) throw new ServiceException("403-1", "댓글 수정 권한이 없습니다.");

        postService.modifyComment(postComment, reqBody.content);

        return new RsData<>(
                "200-1",
                "%d번 댓글이 수정되었습니다.".formatted(id)
        );
    }


    record PostCommentWriteReqBody(
            @NotBlank
            @Size(min = 2, max = 100)
            String content
    ) {
    }

    @PostMapping
    @Transactional
    @Operation(summary = "작성")
    public RsData<PostCommentDto> write(
            @PathVariable int postId,
            @Valid @RequestBody PostCommentWriteReqBody reqBody
    ) {
        Member actor = rq.getActor();

        Post post = postService.findById(postId).get();

        PostComment postComment = postService.writeComment(actor, post, reqBody.content);

        // 트랜잭션 끝난 후 수행되어야 하는 더티체킹 및 여러가지 작업들을 지금 당장 수행해라.
        postService.flush();

        return new RsData<>(
                "201-1",
                "%d번 댓글이 작성되었습니다.".formatted(postComment.getId()),
                new PostCommentDto(postComment)
        );
    }
}

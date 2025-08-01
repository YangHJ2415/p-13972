package com.back.domain.post.post.controller;

import com.back.domain.member.member.entity.Member;
import com.back.domain.post.post.dto.PostDto;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
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
@Validated // 유효성 검사 활성화
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "ApiV1PostController", description = "API 글 컨트롤러")
public class ApiV1PostController {
    private final PostService postService;
    private final Rq rq;

    @GetMapping
    @Transactional(readOnly = true)
    @Operation(summary = "다건 조회")
    public List<PostDto> getItems() {
        List<Post> items = postService.findAll();

        return items
                .stream()
                .map(PostDto::new) // PostDto로 변환
                .toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @Operation(summary = "단건 조회")
    public PostDto getItem(@PathVariable int id) {
        Post post = postService.findById(id).get();

        return new PostDto(post);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "삭제")
    public RsData<Void> delete(
            @PathVariable int id
    ){
        Member actor = rq.getActor(); // 현재 로그인한 사용자 정보 가져오기

        Post post = postService.findById(id).get();
        if (!actor.equals(post.getAuthor()))
            throw new ServiceException("403-1", "글 삭제 권한이 없습니다."); // 작성자와 현재 사용자가 일치하는지 확인

        postService.delete(post);

        return new RsData<>(
                "200-1",
                "%d번 글이 삭제되었습니다.".formatted(id)
        );
    }


    record PostWriteReqBody(
            @NotBlank
            @Size(min = 2, max = 100)
            String title,
            @NotBlank
            @Size(min = 2, max = 5000)
            String content
    ) {
    }

    @PostMapping
    @Transactional
    @Operation(summary = "작성")
    public RsData<PostDto> write( // 글 작성 API
            @Valid @RequestBody PostWriteReqBody reqBody // 요청 바디 유효성 검사
    ) {
        Member actor = rq.getActor(); // 현재 로그인한 사용자 정보 가져오기

        Post post = postService.write(actor, reqBody.title, reqBody.content); // 글 작성 서비스 호출

        return new RsData<>( // 응답 데이터 생성
                "201-1",
                "%d번 글이 작성되었습니다.".formatted(post.getId()),
                new PostDto(post) // 응답 바디에 작성자 이름, 생성일 등 포함
        );
    }

    record PostModifyReqBody(
            @NotBlank
            @Size(min = 2, max = 100)
            String title,
            @NotBlank
            @Size(min = 2, max = 5000)
            String content
    ) {
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "수정")
    public RsData<Void> modify(
            @PathVariable int id,
            @Valid @RequestBody PostModifyReqBody reqBody
    ){
        Member actor = rq.getActor(); // 현재 로그인한 사용자 정보 가져오기

        Post post = postService.findById(id).get();
        if (!actor.equals(post.getAuthor())) { // 작성자와 현재 사용자가 일치하는지 확인
            throw new ServiceException("403-1", "권한이 없습니다.");
        }

        postService.modify(post, reqBody.title, reqBody.content);

        return new RsData<>(
                "200-1",
                "%d번 글이 수정되었습니다.".formatted(post.getId())
        );
    }
}
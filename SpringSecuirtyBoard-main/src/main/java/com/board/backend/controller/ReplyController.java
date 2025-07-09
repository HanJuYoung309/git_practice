package com.board.backend.controller;

import com.board.backend.domain.Board;
import com.board.backend.dto.BoardResponse;
import com.board.backend.dto.ReplyRequest;
import com.board.backend.dto.ReplyResponse;
import com.board.backend.service.BoardService;
import com.board.backend.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;
    private final BoardService boardService;

    //댓글 작성
    @PostMapping("/{boardId}/replies")
    public ResponseEntity<ReplyResponse> createReply(@PathVariable long boardId, @RequestBody ReplyRequest replyRequest,
                                                     @AuthenticationPrincipal UserDetails userDetails){
        //1.사용자 정보 유효성 검사
        if(userDetails== null || userDetails.getUsername() == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        //2. 게시글 존재 여부 확인
        try {
            BoardResponse board= boardService.getBoardById(boardId);
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ReplyResponse(null,"게시글을 찾을수 없습니다."));
        }

        //3. 댓글 내용 유효성 검사
        if(replyRequest.getReplyContent()==null || replyRequest.getReplyContent().trim().isEmpty()){
            return ResponseEntity.badRequest().body(new ReplyResponse(null, "댓글 내용은 비어 있을 수 없습니다"));
        }

        try{
            ReplyResponse createReply= replyService.createReply(boardId,replyRequest.getReplyContent(), userDetails.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED).body(createReply);
        }catch (Exception e){

            //댓글 생성중 발생할수있는 예외처리
            System.err.println("댓글 생성 중 오류 발생"+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ReplyResponse(null,"댓글 생성에 실패했습니다."));
        }

    }

    //댓글 조회

    /**
     * 특정 게시글의 댓글 목록을 페이지네이션하여 조회하는 API 엔드포인트입니다.
     *
     * @param boardId   댓글을 조회할 게시글의 ID (URL 경로 변수)
     * @param pageable  페이지네이션 정보 (페이지 번호, 크기, 정렬 등)
     * @return          조회된 댓글 목록 (Page<ReplyResponse>)과 HTTP 상태 코드 (200 OK 또는 404 Not Found)
     */
    @GetMapping("/{boardId}/replies") // RESTful한 경로: 특정 게시글의 댓글들
    public ResponseEntity<Page<ReplyResponse>> getReplies(
            @PathVariable Long boardId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        // 1. 게시글 존재 여부 확인
        if (!boardService.existsById(boardId)) { // BoardService에 existsById 메서드 추가 필요
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            // 2. ReplyService를 호출하여 댓글 목록 조회
            // replyService.getRepliesByBoardId(boardId, pageable)와 같은 메서드를 호출
            Page<ReplyResponse> replies = replyService.getRepliesByBoardId(boardId, pageable);

            // 3. 조회된 댓글 목록과 200 OK 상태 코드 반환
            return ResponseEntity.ok(replies);

        } catch (Exception e) {
            // 댓글 조회 중 발생할 수 있는 기타 예외 처리
            System.err.println("댓글 조회 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //댓글 수정
    @PutMapping("/{boardId}/replies")
    public ResponseEntity<ReplyResponse> updateReply(@PathVariable long boardId, @RequestBody ReplyRequest replyRequest,
                                                     @AuthenticationPrincipal UserDetails userDetails){
        //1.사용자 정보 유효성 검사
        if(userDetails== null || userDetails.getUsername() == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        //2. 게시글 존재 여부 확인
        try {
            BoardResponse board= boardService.getBoardById(boardId);
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ReplyResponse(null,"게시글을 찾을수 없습니다."));
        }

        //3. 댓글 내용 유효성 검사
        if(replyRequest.getReplyContent()==null || replyRequest.getReplyContent().trim().isEmpty()){
            return ResponseEntity.badRequest().body(new ReplyResponse(null, "댓글 내용은 비어 있을 수 없습니다"));
        }

        try{
            ReplyResponse createReply= replyService.updateReply(boardId,replyRequest.getReplyContent(), userDetails.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED).body(createReply);
        }catch (Exception e){

            //댓글 생성중 발생할수있는 예외처리
            System.err.println("댓글 생성 중 오류 발생"+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ReplyResponse(null,"댓글 생성에 실패했습니다."));
        }

    }
}

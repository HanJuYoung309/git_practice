package com.board.backend.dto;

import com.board.backend.domain.Reply;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyResponse {

    private Long replyId;
    private Long boardId; // 어떤 게시글의 댓글인지
    private String replyContent;
    private String replyAuthor;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private String message; // 에러 메시지 등을 담을 필드 (선택 사항)

    public static ReplyResponse from(Reply reply){
        if(reply == null){
            return null;
        }
        return new ReplyResponse(
                reply.getReplyId(),
                reply.getBoard() != null ? reply.getBoard().getId() : null, // 게시글이 null이 아닐 때만 ID 가져오기
                reply.getReplyContent(),
                reply.getReplyAuthor(),
                reply.getCreatedAt(),
                reply.getLastModifiedAt(),
                "댓글이 성공적으로 작성되었습니다." // 성공 메시지
        );
    }

    // 에러 응답을 위한 생성자 (선택 사항)
    public ReplyResponse(Long replyId, String message) {
        this.replyId = replyId;
        this.message = message;
    }


}

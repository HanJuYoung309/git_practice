package com.board.backend.service;

import com.board.backend.domain.Board;
import com.board.backend.domain.Reply;
import com.board.backend.dto.ReplyResponse;
import com.board.backend.repository.BoardRepository;
import com.board.backend.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReplyService {

     private final ReplyRepository replyRepository;
     private final BoardRepository boardRepository;

     /*
       새로운 댓글을 생성
      */
    @Transactional
    public ReplyResponse createReply(Long boardId, String replyContent,String replyAuthor) {

        //게시글 존재 여부 확인
        Board board=boardRepository.findById(boardId).
                orElseThrow(()-> new IllegalArgumentException("게시글을 찾을수 없습니다.ID:"+boardId));

        // Reply 엔티티 생성
        Reply reply=Reply.builder()
                .board(board)
                .replyContent(replyContent)
                .replyAuthor(replyAuthor)
                .build();

        //댓글 저장
        Reply savedReply=replyRepository.save(reply);

        return ReplyResponse.from(savedReply);


    }

    @Transactional(readOnly = true)
    public Page<ReplyResponse> getRepliesByBoardId(Long boardId, Pageable pageable) {

        Page<Reply> replyPageable= replyRepository.findByBoardId(boardId,pageable);

        return  replyPageable.map(ReplyResponse::from);


    }

    @Transactional
    public ReplyResponse updateReply(long boardId, String replyContent, String username) {

        //게시글 존재 여부 확인
        Board board=boardRepository.findById(boardId).
                orElseThrow(()-> new IllegalArgumentException("게시글을 찾을수 없습니다.ID:"+boardId));

        // Reply 엔티티 생성
        Reply reply=Reply.builder()
                .board(board)
                .replyContent(replyContent)
                .build();

        //댓글 저장
        Reply savedReply=replyRepository.save(reply);

        return ReplyResponse.from(savedReply);

    }
}

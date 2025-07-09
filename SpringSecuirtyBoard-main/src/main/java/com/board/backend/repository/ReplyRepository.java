package com.board.backend.repository;

import com.board.backend.domain.Board;
import com.board.backend.domain.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository  extends JpaRepository<Reply, Long> {

    Page<Reply> findByBoardId(Long boardId, Pageable pageable);
}

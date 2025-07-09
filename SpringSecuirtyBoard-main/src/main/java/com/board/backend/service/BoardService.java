package com.board.backend.service;

import com.board.backend.domain.Board;
import com.board.backend.domain.User;
import com.board.backend.dto.BoardRequest;
import com.board.backend.dto.BoardResponse;
import com.board.backend.repository.BoardRepository;
import com.board.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository; // 게시글 작성자 정보 가져오기 위함

    @Transactional
    public BoardResponse createBoard(BoardRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("작성자 정보를 찾을 수 없습니다."));

        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .build();
        return BoardResponse.fromEntity(boardRepository.save(board));
    }

    public Page<BoardResponse> getAllBoards(Pageable pageable, String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            // 예시: 제목과 내용에서 keyword를 검색
            return boardRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable)
                    .map(BoardResponse::fromEntity);
        }
        // 검색어가 없는 경우 기존처럼 모든 게시글 반환
        return boardRepository.findAll(pageable).map(BoardResponse::fromEntity);
    }

    public BoardResponse getBoardById(Long id) {
        return boardRepository.findById(id)
                .map(BoardResponse::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    @Transactional
    public BoardResponse updateBoard(Long id, BoardRequest request, String username) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!board.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("게시글 수정 권한이 없습니다.");
        }

        board.setTitle(request.getTitle());
        board.setContent(request.getContent());
        return BoardResponse.fromEntity(boardRepository.save(board));
    }

    @Transactional
    public void deleteBoard(Long id, String username) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!board.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("게시글 삭제 권한이 없습니다.");
        }
        boardRepository.delete(board);


    }

    public boolean existsById(Long boardId) {
        return boardRepository.existsById(boardId);
    }
}
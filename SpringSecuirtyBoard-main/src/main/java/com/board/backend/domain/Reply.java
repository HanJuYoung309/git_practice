package com.board.backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference; // 부모 엔티티 참조 시 사용
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List; // 불필요할 수 있어 사용 여부에 따라 삭제 가능

@Entity // JPA 엔티티임을 명시
@Table(name = "replies") // 테이블명 지정 (선택 사항이지만 명확하게 해줌)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 사용 시 protected 기본 생성자 권장
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자 자동 생성
@Builder // 빌더 패턴 사용 가능
@EntityListeners(AuditingEntityListener.class) // 엔티티 생성/수정 시간 자동화를 위한 리스너
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB가 ID 자동 생성
    private Long replyId; // 댓글 ID (PK)

    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계: 여러 댓글이 하나의 게시글에 속함
    @JoinColumn(name = "board_id", nullable = false) // 외래키 컬럼명 지정, null 불가능
    @JsonBackReference // 순환 참조 방지 (부모 방향에서 자식을 참조할 때 사용)
    private Board board; // 이 댓글이 속한 게시글

    @Column(nullable = false, length = 1000) // 댓글 내용은 필수, 길이 제한
    private String replyContent; // 댓글 내용

    @Column(nullable = false)
    private String replyAuthor; // 댓글 작성자

    @CreatedDate // 엔티티가 생성될 때 현재 시간을 자동 저장
    @Column(updatable = false) // 생성일은 업데이트되지 않음
    private LocalDateTime createdAt; // 생성일시

    @LastModifiedDate // 엔티티가 업데이트될 때 현재 시간을 자동 저장
    private LocalDateTime lastModifiedAt; // 최종 수정일시

    // 대댓글 기능을 위한 부모-자식 관계 (선택 사항)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_reply_id") // 부모 댓글의 ID를 외래키로 가짐
    @JsonBackReference("parent-child-replies") // 다른 JsonBackReference 이름으로 구분
    private Reply parentReply; // 부모 댓글

    @OneToMany(mappedBy = "parentReply", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("parent-child-replies") // 다른 JsonManagedReference 이름으로 구분
    private List<Reply> childReplies = new ArrayList<>(); // 자식 댓글들

    // 비즈니스 로직에 따라 필요한 편의 메서드 추가 가능
    public void updateContent(String newContent) {
        this.replyContent = newContent;
    }
}

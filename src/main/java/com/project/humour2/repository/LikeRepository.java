package com.project.humour2.repository;

import com.project.humour2.entity.Board;
import com.project.humour2.entity.Like;
import com.project.humour2.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByBoardIdAndMemberId(Long boardId, Long memberId);

    Like findByBoardIdAndMemberId(Long boardId, Long memberId);

    // 특정 게시물에 대한 좋아요 수 조회
    int countByBoard(Board board);

    // 게시물과 사용자에 대한 좋아요 정보 조회
    Like findByBoardAndMember(Long boardId, Long memberId);

}

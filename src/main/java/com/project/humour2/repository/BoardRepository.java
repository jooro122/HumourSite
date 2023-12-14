package com.project.humour2.repository;

import com.project.humour2.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board,Long> {

    //제목 검색
    Page<Board> findByTitleContaining(String Keyword, Pageable pageable);

    //제목 + 내용 검색
    Page<Board> findByTitleContainingOrContentContaining(String keyword, String keyword1, Pageable pageable);

    //글쓴이 검색
    @Query("SELECT b FROM Board b JOIN Member m ON b.member.id = m.id WHERE m.userName LIKE %:userName%")
    Page<Board> findByUserName(@Param("userName") String userName, Pageable pageable);

    //내가 쓴 글
    @Query("SELECT b FROM Board b JOIN Member m ON b.member.id = m.id WHERE m.email LIKE %:email%")
    Page<Board> findByEmail(@Param("email") String email, Pageable pageable);

    //해당 게시판 전체 목록
    Page<Board> findByBoardName(String keyword, Pageable pageable);

    //해당 게시판의 카테고리 목록
    Page<Board> findByBoardNameAndCategory(String boardName, Long category, Pageable pageable);

    //게시판 좋아요 n개 이상일때 best 게시판으로 정렬 쿼리
    @Query("SELECT b FROM Board b LEFT JOIN b.likes l GROUP BY b HAVING COUNT(l) >= 1 ORDER BY COUNT(l) DESC")
    List<Board> findBoardsOrderByLikes();
    
    
}

package com.project.humour2.service;

import com.project.humour2.entity.Board;
import com.project.humour2.entity.Like;
import com.project.humour2.entity.Member;
import com.project.humour2.repository.BoardRepository;
import com.project.humour2.repository.LikeRepository;
import com.project.humour2.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;



    public void addLike(Long boardId, Long memberId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Like like = new Like();
        like.setBoard(board);
        like.setMember(member);

        likeRepository.save(like);
    }

    public void removeLike(Long boardId, Long memberId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Like like = likeRepository.findByBoardIdAndMemberId(boardId, memberId);
        if (like != null) {
            likeRepository.delete(like);
        }
    }

    public int getLikesCount(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        board.setId(boardId);
        return likeRepository.countByBoard(board);
    }

    public boolean isLiked(Long boardId, Long memberId) {
        return likeRepository.existsByBoardIdAndMemberId(boardId, memberId);
    }

}
package com.project.humour2.service;

import com.project.humour2.dto.CommentRequestDTO;
import com.project.humour2.dto.CommentResponseDTO;
import com.project.humour2.entity.Board;
import com.project.humour2.entity.Comment;
import com.project.humour2.entity.Member;
import com.project.humour2.repository.BoardRepository;
import com.project.humour2.repository.CommentRepository;
import com.project.humour2.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;


    public Long writeComment(CommentRequestDTO commentRequestDTO, Long boardId, String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new UsernameNotFoundException("해당 이메일이 존재하지 않습니다.");
        }
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));
        Comment comment = Comment.builder()
                .content(commentRequestDTO.getContent())
                .board(board)
                .member(member)
                .build();
        commentRepository.save(comment);

        return comment.getId();
    }


    public List<CommentResponseDTO> commentList(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));
        List<Comment> comments = commentRepository.findByBoard(board);

        return comments.stream()
                .map(comment -> CommentResponseDTO.builder()
                        .comment(comment)
                        .build())
                .collect(Collectors.toList());
    }


    public void updateComment(CommentRequestDTO commentRequestDTO, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        comment.update(commentRequestDTO.getContent());
        commentRepository.save(comment);
    }


    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public CommentResponseDTO getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        return CommentResponseDTO.builder()
                .comment(comment)
                .build();
    }
}


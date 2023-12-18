package com.project.humour2.service;

import com.project.humour2.dto.BoardRequestDTO;
import com.project.humour2.dto.BoardResponseDTO;
import com.project.humour2.entity.Board;
import com.project.humour2.entity.Like;
import com.project.humour2.entity.Member;
import com.project.humour2.repository.BoardRepository;
import com.project.humour2.repository.LikeRepository;
import com.project.humour2.repository.MemberRepository;
import com.project.humour2.util.BoardUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final LikeRepository likeRepository;

    public Long saveBoard(BoardRequestDTO boardRequestDto, String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new IllegalStateException("해당 이메일을 찾을 수 없습니다.");
        }


        Board board = Board.builder()
                .title(boardRequestDto.getTitle())
                .content(boardRequestDto.getContent())
                .member(member)
                .views(0L)
                .boardName(boardRequestDto.getBoardName())
                .category(boardRequestDto.getCategory())
                .build();
        boardRepository.save(board);
        return board.getId();
    }

    public BoardResponseDTO boardDetail(Long id) {
        Optional<Board> boardOptional = boardRepository.findById(id);
        Board board = boardOptional.orElseThrow(() -> new UsernameNotFoundException("해당 게시물을 찾을 수 없습니다."));

        BoardResponseDTO boardResponseDTO = BoardResponseDTO.builder()
                .board(board)
                .build();

        return boardResponseDTO;
    }

    public Page<BoardResponseDTO> boardList(Pageable pageable, String searchType, String keyword,
                                            String boardName,Long category,String email) {
        Page<Board> boardList;

        if ("title".equals(searchType)) {
            boardList = boardRepository.findByTitleContaining(keyword, pageable);
        } else if ("titleAndContent".equals(searchType)) {
            boardList = boardRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
        } else if ("userName".equals(searchType)) {
            boardList = boardRepository.findByUserName(keyword,pageable);
        }else if ("email".equals(searchType)) {
            boardList = boardRepository.findByEmail(email, pageable);
        }
        else if (boardName != null && !boardName.isEmpty() && (category == null)) {
            // boardName만 있는 경우
            boardList = boardRepository.findByBoardName(boardName, pageable);
        } else if (boardName != null && !boardName.isEmpty() && category != null) {
            // boardName + category 있는 경우
            boardList = boardRepository.findByBoardNameAndCategory(boardName, category, pageable);
        } else {
            // 모든 조건이 해당하지 않는 경우
            boardList = boardRepository.findAll(pageable);
        }

        // 각 게시글의 카테고리 텍스트를 설정
        List<BoardResponseDTO> boardDTOList = boardList.stream()
                .map(board -> {
                    BoardResponseDTO boardResponseDTO = new BoardResponseDTO(board);
                    // 추가적으로 썸네일 이미지 및 카테고리 정보를 가져와 설정
                    String content = board.getContent();
                    List<String> imageUrls = BoardUtil.summernoteImageUrls(content);
                    if (!imageUrls.isEmpty()) {
                        boardResponseDTO.setContent(imageUrls.get(0));
                    }
                    String categoryText = BoardUtil.getCategoryText(board.getCategory());
                    boardResponseDTO.setCategoryText(categoryText);
                    return boardResponseDTO;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(boardDTOList, pageable, boardList.getTotalElements());
    }


    public Long boardUpdate(Long id, BoardRequestDTO boardRequestDTO) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        board.update(boardRequestDTO.getTitle(), boardRequestDTO.getContent(),boardRequestDTO.getBoardName(),boardRequestDTO.getCategory());
        boardRepository.save(board);

        Long boardUpdate = board.getId();

        return boardUpdate;
    }

    public void boardRemove(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        boardRepository.delete(board);
    }


    public void increaseViews(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));
        board.increaseViews(); // Board 엔티티 클래스에 정의된 조회수 증가 메소드를 호출합니다.
        boardRepository.save(board); // 변경된 조회수를 데이터베이스에 저장합니다.
    }

    //추천수(좋아요) 기준으로 게시판 정렬
    public List<BoardResponseDTO> getBoardsOrderByLikes() {
        List<Board> boardsOrderByLikes = boardRepository.findBoardsOrderByLikes();
        return boardsOrderByLikes.stream()
                .map(board -> {
                    // Board 엔티티에서 필요한 정보를 가져와 BoardResponseDTO를 생성
                    BoardResponseDTO boardResponseDTO = new BoardResponseDTO(board);
                    // 추가적으로 썸네일 이미지 및 카테고리 정보를 가져와 설정
                    String content = board.getContent();
                    List<String> imageUrls = BoardUtil.summernoteImageUrls(content);
                    if (!imageUrls.isEmpty()) {
                        boardResponseDTO.setContent(imageUrls.get(0));
                    }
                    // 카테고리 텍스트를 얻어와 설정
                    String categoryText = BoardUtil.getCategoryText(board.getCategory());
                    boardResponseDTO.setCategoryText(categoryText);
                    return boardResponseDTO;
                })
                .collect(Collectors.toList());
    }

    //내가 좋아요 한 글 조회
    public List<BoardResponseDTO> getLikedBoardsByUserEmail(String email) {
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new UsernameNotFoundException("해당 이메일이 존재하지 않습니다.");
        }

        List<Like> likedBoards = likeRepository.findLikedBoardIdsByMember(member);

        return likedBoards.stream()
                .map(Like::getBoard) // Like 엔티티에서 Board로 변환
                .map(board -> BoardResponseDTO.builder().board(board).build())
                .collect(Collectors.toList());
    }

}

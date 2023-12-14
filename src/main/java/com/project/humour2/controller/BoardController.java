package com.project.humour2.controller;

import com.google.gson.JsonObject;
import com.project.humour2.dto.BoardRequestDTO;
import com.project.humour2.dto.BoardResponseDTO;
import com.project.humour2.dto.CommentResponseDTO;
import com.project.humour2.entity.Member;
import com.project.humour2.service.BoardService;
import com.project.humour2.service.CommentService;
import com.project.humour2.service.LikeService;
import com.project.humour2.service.MemberService;
import com.project.humour2.util.BoardUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;
    private final MemberService memberService;
    private final LikeService likeService;

    @GetMapping("/list")
    public String list(Model model, @PageableDefault(page = 0, size = 10, sort = "id",
            direction = Sort.Direction.DESC) Pageable pageable,
                       String searchType, String keyword,
                       @RequestParam(name = "boardName", required = false) String boardName,
                       @RequestParam(name = "category", required = false) Long category, BoardResponseDTO boardResponseDTO, String email) {

        Page<BoardResponseDTO> boardList = boardService.boardList(pageable, searchType, keyword, boardName, category, email);


        if (!boardList.isEmpty()) {
            // 컨트롤러에서 모델에 전체 카테고리 텍스트를 추가
            List<String> boardCategory = boardList.stream()
                    .map(BoardResponseDTO::getCategoryText)
                    .distinct()
                    .collect(Collectors.toList());
            model.addAttribute("boardCategory", boardCategory);
        }
        // 영어 boardName을 한글로 변환해서 모델에 추가
        String koreanBoardName = BoardUtil.getKoreanBoardName(boardName);
        model.addAttribute("boardName", koreanBoardName);
        model.addAttribute("category", category);
        // -- 여기까지 --

        model.addAttribute("boardList", boardList);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);

        return "board/list";
    }


    @GetMapping("/best")
    public String best(Model model) {
        // 좋아요 수를 기준으로 정렬된 게시물을 가져와 모델에 추가
        List<BoardResponseDTO> boardsOrderByLikes = boardService.getBoardsOrderByLikes();
        model.addAttribute("boardsOrderByLikes", boardsOrderByLikes);

        return "board/best";
    }


    @GetMapping("/write")
    public String writeForm(Model model, Authentication authentication,
                            @RequestParam(name = "boardName", required = false) String boardName) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/member/login"; // 권한이 없을 경우 홈페이지로 리다이렉트
        }
        model.addAttribute("boardName", boardName);
        return "board/write";
    }


    @PostMapping("/write")
    public String write(@Valid BoardRequestDTO boardRequestDTO, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/member/login"; // 권한이 없을 경우 홈페이지로 리다이렉트
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        boardService.saveBoard(boardRequestDTO, userDetails.getUsername());


        return "redirect:/board/list?boardName=" + boardRequestDTO.getBoardName() +
                "&category=" + boardRequestDTO.getCategory();
    }


    @GetMapping("/{id}")
    public String boardDetail(@PathVariable Long id, Model model) {
        BoardResponseDTO boardResponseDTO = boardService.boardDetail(id);
        List<CommentResponseDTO> commentResponseDTO = commentService.commentList(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        int likesCount = likeService.getLikesCount(id); // 해당 메서드를 이용하여 좋아요 수 조회


        //좋아요 기능 사용시 인증절차
        if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal())) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String userEmail = userDetails.getUsername();
            Member member = memberService.findByEmail(userEmail);
            Long memberId = member.getId();
            model.addAttribute("memberId", memberId);
        }


        model.addAttribute("comments", commentResponseDTO);
        model.addAttribute("board", boardResponseDTO);
        model.addAttribute("id", id);
        model.addAttribute("likesCount", likesCount);

        return "board/detail";
    }

    @GetMapping("/{id}/update")
    @PreAuthorize("@boardSecurity.checkAuthorization(#id, authentication)")//권한 설정(본인, 관리자)
    public String boardUpdateForm(@PathVariable Long id, Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/member/login"; // 권한이 없을 경우 홈페이지로 리다이렉트
        }

        BoardResponseDTO boardResponseDTO = boardService.boardDetail(id);

        model.addAttribute("board", boardResponseDTO);
        model.addAttribute("id", id);


        return "board/update";
    }

    @PostMapping("/{id}/update")
    public String boardUpdate(@PathVariable Long id, BoardRequestDTO boardRequestDTO) {
        boardService.boardUpdate(id, boardRequestDTO);

        return "redirect:/board/" + id;
    }

    @GetMapping("/{id}/remove")
    @PreAuthorize("@boardSecurity.checkAuthorization(#id, authentication)")//권한 설정(본인, 관리자)
    public String boardRemove(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/member/login"; // 권한이 없을 경우 홈페이지로 리다이렉트
        }

        boardService.boardRemove(id);

        return "redirect:/";
    }

    @GetMapping("/{id}/views")
    public ResponseEntity<String> increaseViews(@PathVariable Long id) {
        boardService.increaseViews(id);
        return ResponseEntity.ok("조회수 증가");
    }

    //썸머노트 이미지 업로드
    @PostMapping(value = "/uploadSummernoteImageFile", produces = "application/json")
    @ResponseBody
    public JsonObject uploadSummernoteImageFile(@RequestParam("file") MultipartFile multipartFile) {

        JsonObject jsonObject = new JsonObject();

        String fileRoot = "C:\\summernote_image\\";    //저장될 외부 파일 경로
        String originalFileName = multipartFile.getOriginalFilename();    //오리지날 파일명
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));    //파일 확장자

        String savedFileName = UUID.randomUUID() + extension;    //저장될 파일 명

        File targetFile = new File(fileRoot + savedFileName);

        try {
            InputStream fileStream = multipartFile.getInputStream();
            FileUtils.copyInputStreamToFile(fileStream, targetFile);    //파일 저장
            jsonObject.addProperty("url", "/board/summernoteImage/" + savedFileName);
            jsonObject.addProperty("responseCode", "success");

        } catch (IOException e) {
            FileUtils.deleteQuietly(targetFile);    //저장된 파일 삭제
            jsonObject.addProperty("responseCode", "error");
            e.printStackTrace();
        }

        return jsonObject;
    }

}

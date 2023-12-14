package com.project.humour2.entity;


import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "board")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Board extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "board_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    private Long views; // 조회수

    @Column(nullable = false)
    private String boardName;

    @Column(nullable = false)
    private Long category;


    // 게시글의 조회수 증가 메서드
    public void increaseViews() {
        this.views++;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private Set<Like> likes = new HashSet<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();


    public void update(String title, String content, String boardName, Long category) {
        this.title = title;
        this.content = content;
        this.boardName = boardName;
        this.category = category;
    }
}

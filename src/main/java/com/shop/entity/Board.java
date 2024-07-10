package com.shop.entity;

import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "imageSet")
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    @Column(nullable = false , length = 500)
    private String title;

    @Column(nullable = false , length = 2000)
    private String content;

    @Column(nullable = false , length = 50)
    private String writer;

    @OneToMany(mappedBy = "board",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY,
    orphanRemoval = true) // BoardImage의 board변수
    @Builder.Default
    @BatchSize(size = 20)
    private Set<BoardImage> imageSet = new HashSet<>();

    public void change(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void addImage(String uuid, String fileName){
        BoardImage boardImage = BoardImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .ord(imageSet.size())
                .board(this)
                .build();
        imageSet.add(boardImage);
    }

    public void clearImages(){
        imageSet.forEach(boardImage -> boardImage.changeBoard(null));
        this.imageSet.clear();
    }
}

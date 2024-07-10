package com.shop.entity;


import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "Reply", indexes = {@Index(name = "idx_reply_bno",columnList = "bno")})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "board") // 한쪽의 객체를 사용하지않기위해 tostring에서는 무조껀 exclude를 기입한다.
public class Reply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bno")
    private Board board;

    private String replyText;

    private String replyer;

    public void changeText(String text){
        this.replyText = text;
    }
}

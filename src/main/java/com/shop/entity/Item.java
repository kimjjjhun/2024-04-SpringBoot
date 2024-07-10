package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.exception.OutOfStockException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity //클래스를 엔티티로 선언
@Table(name = "item")  //엔티티와 매핑할 테이블을 지정
@Setter
@Getter
@ToString
public class Item extends BaseEntity {
    
    @Id //테이블의 기본키에 사용할 속성을 지정
    @Column(name = "item_id") //컬러명을 item_id로 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) //키 값을 생성하는 전략 명시 ( MYSQL같은경우 IDENTITY,AUTO사용)

    private Long id;    //상품 코드

    @Column(nullable = false,length = 50) // null값을 허용하지않고 길이를 50자로 제한
    private String itemNm;  //상품 명

    @Column(name = "price", nullable = false)   //컬럼명 price로 설정 , null값을 허용하지 않는다.
    private int price;  //가격

    @Column(nullable = false)
    private int stockNumber;    //재고수량
    
    @Lob    //BLOB,CLOB타입 매핑 CLOB:문자형대용량파일 , BLOB:이미지,사운드,비디오같은 멀티파일을 저장하기위한 데이터타입
    @Column(nullable = false)
    private String itemDetail;  //상품 상세 설명

    @Enumerated(EnumType.STRING)    //enum타입 매핑
    private ItemSellStatus itemSellStatus;  //  상품 판매 상태

    @ManyToMany
    @JoinTable(
            name = "member_item",
            joinColumns = @JoinColumn(name = "member_id"),inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Member> member;

    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    public void removeStock(int StockNumber){
        int restStock = this.stockNumber - StockNumber;
        if(restStock < 0){
            throw new OutOfStockException("상품의 재고가 부족 합니다. (현재 재고 수량 : " + this.stockNumber + ")");
        }
        this.stockNumber = restStock;
    }

    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }
}

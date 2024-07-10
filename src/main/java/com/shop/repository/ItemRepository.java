package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> , QuerydslPredicateExecutor<Item>,ItemRepositoryCustom {
    //상품 조회하기
    List<Item> findByItemNmOrItemDetail(String itemNm,String itemDetail);
    //상품 조회하기(변수값보다 작은 데이터를 조회하는 메소드 LessThan)
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

    @Query(value = "select * from item i where i.item_detail like %:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);



}

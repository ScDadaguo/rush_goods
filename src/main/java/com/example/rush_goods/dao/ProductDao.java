package com.example.rush_goods.dao;

import com.example.rush_goods.pojo.ProductPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDao {
//    获取产品
    public ProductPo getProduct(Long id);

//    减少库存,
    public  int decreaseProduct(@Param("id") Long id,
                                @Param("quantity") int quantity);



}

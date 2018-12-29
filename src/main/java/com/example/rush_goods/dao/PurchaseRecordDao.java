package com.example.rush_goods.dao;

import com.example.rush_goods.pojo.PurchaseRecordPo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRecordDao {

    public int insertPurchaseRecord(PurchaseRecordPo pr);

}

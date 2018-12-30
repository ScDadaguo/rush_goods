package com.example.rush_goods.service;

import com.example.rush_goods.pojo.PurchaseRecordPo;

import java.util.List;

public interface PurchaseService {
    /**
    * @Description: 处理购买业务
    * @Param: [userId 用户编号, productId 产品编号, quantity 购买数量]
    * @return: boolean 成功或失败
    * @Author: 文兆杰
    * @Date: 2018/12/28
    */

    public boolean purchaseRedis(Long userId,Long productId,int quantity);
    public boolean dealRedisPurchase (List<PurchaseRecordPo> prpList);


}

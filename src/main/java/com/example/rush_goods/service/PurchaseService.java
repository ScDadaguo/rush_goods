package com.example.rush_goods.service;

public interface PurchaseService {
    /**
    * @Description: 处理购买业务
    * @Param: [userId 用户编号, productId 产品编号, quantity 购买数量]
    * @return: boolean 成功或失败
    * @Author: 文兆杰
    * @Date: 2018/12/28
    */
    public boolean purchase(Long userId,Long productId,int quantity);


}

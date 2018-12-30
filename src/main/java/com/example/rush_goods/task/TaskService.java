package com.example.rush_goods.task;

import com.example.rush_goods.pojo.PurchaseRecordPo;

/**
* @Description: 定时任务接口
* @Param:
* @return:
* @Author: 文兆杰
* @Date: 2018/12/29
*/
public interface TaskService {
        //购买定时任务
    public void purchaseTask();

   public PurchaseRecordPo createPurchaseRecord(Long productId, String prStr);
}

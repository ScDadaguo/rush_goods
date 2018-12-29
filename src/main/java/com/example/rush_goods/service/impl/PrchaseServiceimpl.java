package com.example.rush_goods.service.impl;

import com.example.rush_goods.dao.ProductDao;
import com.example.rush_goods.dao.PurchaseRecordDao;
import com.example.rush_goods.pojo.ProductPo;
import com.example.rush_goods.pojo.PurchaseRecordPo;
import com.example.rush_goods.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrchaseServiceimpl implements PurchaseService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private PurchaseRecordDao purchaseRecordDao;



    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public boolean purchase(Long userId, Long productId, int quantity) {
        //      当前时间
        long start = System.currentTimeMillis();
//        循环尝试直至成功
        while (true) {
            long end = System.currentTimeMillis();
            //如果循环时间大于100ms返回终止循环
            if (end - start > 100) {
                return false;
            }


            //        获取产品
            ProductPo product = productDao.getProduct(productId);
//        比较库存和购买数量
            if (product.getStock() < quantity) {
//            库存不足
                return false;
            }
//      获取当前版本号
            int version = product.getVersion();
//        扣减库存，同时将当前版本号发送给前台进行比较
            int result = productDao.decreaseProduct(productId, quantity, version);
//        如果更新数据失败，说明数据在多线程中被其他线程，导致失败返回
//        当使用时间戳的时候，就然他继续循环，直至时间结束
            if (result == 0) {
                continue;
            }

//        初始化购买记录
            PurchaseRecordPo pr = this.initPurchaseRecord(userId, product, quantity);
//        插入购买信息
            purchaseRecordDao.insertPurchaseRecord(pr);
            return true;
        }
    }

    /**
    * @Description: 初始化购买信息
    * @Param: [userid, product, quantity]
    * @return: com.example.rush_goods.pojo.PurchaseRecordPo
    * @Author: 文兆杰
    * @Date: 2018/12/28
    */
    private PurchaseRecordPo initPurchaseRecord(
            Long userid,ProductPo product,int quantity)
    {
        PurchaseRecordPo pr=new PurchaseRecordPo();
        pr.setNote("购买日志，时间："+System.currentTimeMillis());
        pr.setPrice(product.getPrice());
        pr.setProductId(product.getId());
        pr.setQuantity(quantity);
        double sum=product.getPrice()*quantity;
        pr.setSum(sum);
        pr.setUserId(userid);
        return pr;
    }
}

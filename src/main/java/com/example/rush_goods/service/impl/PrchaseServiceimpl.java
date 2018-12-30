package com.example.rush_goods.service.impl;

import com.example.rush_goods.dao.ProductDao;
import com.example.rush_goods.dao.PurchaseRecordDao;
import com.example.rush_goods.pojo.PurchaseRecordPo;
import com.example.rush_goods.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class PrchaseServiceimpl implements PurchaseService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private PurchaseRecordDao purchaseRecordDao;
    //使用Redis Lua 晌应请求
    @Autowired
    StringRedisTemplate stringRedisTemplate = null;

    String purchaseScript =
            //先把产品保存到集合中
            "redis.call('sadd',KEYS[1],ARGV[2])\n"
                    //购买列表
                    + "local productPurchaseList=KEYS[2]..ARGV[2] \n"
                    //用户编号
                    + "local userId=ARGV[1] \n"
                    //产品键
                    + "local product='product_'..ARGV[2] \n"
                    //购买数量
                    + "local quantity = tonumber(ARGV[3]) \n"
                    //当前库存
                    + "local stock=tonumber(redis.call('hget',product,'stock'))\n"
//                    + "local stock=tonumber(10)\n"
                    //价格
                    + "local price=tonumber(redis.call('hget',product,'price'))\n"
//                    + "local price=tonumber(10)\n"
                    //购买时间
                    + "local purchase_date=ARGV[4] \n"
                    //库存不足，返回0
                    + "if stock<quantity then return 0 end \n"
                    //减库存
                    + "stock=stock-quantity \n"
                    + "redis.call('hset',product,'stock',tostring(stock)) \n"
                    //计算价格
                    + "local sum=price*quantity \n"
                    //合并购买记录数据
                    + "local purchaseRecord=userId..','..quantity..','"
                    + "..sum..','..price..','..purchase_date \n"

                    //将购买记录数据保存到list中
                    + "redis.call('rpush',productPurchaseList,purchaseRecord) \n"
                    //返回成功
                    + "return 1 \n";

    //    redis 购买记录集合前缀
    private static final String PURCHASE_PRODUCT_LIST = "purchase_list_";
    //抢购商品集合
    private static final String PRODUCT_SCHEDULE_LIST_SET = "product_schedule_set";
    //32位SHA1编码，第一次执行的时候让redis进行缓存脚本返回
    private String shal=null;




    /**
    * @Description: 每次执行，把数据存入
    * @Param: [userId, productId, quantity]
    * @return: boolean
    * @Author: 文兆杰
    * @Date: 2018/12/30
    */
    @Override
    public boolean purchaseRedis(Long userId, Long productId, int quantity) {
        Long purchaseDate=System.currentTimeMillis();
        Jedis jedis=null;
        try {
            jedis= (Jedis) stringRedisTemplate.getConnectionFactory().getConnection().getNativeConnection();
            if (shal == null) {
                shal=jedis.scriptLoad(purchaseScript);
            }
            //执行脚本，返回结果
            Object res=jedis.evalsha(shal,2,PRODUCT_SCHEDULE_LIST_SET,
                    PURCHASE_PRODUCT_LIST,userId+"",productId+"",
                    quantity+"",purchaseDate+"");
            Long result= (Long) res;
            return result==1;
        } finally {
            if (jedis!=null && jedis.isConnected())
                jedis.close();
        }
    }




    /**
     * @Description: 保存购买记录
     * @Param: [prpList]
     * @return: boolean
     * @Author: 文兆杰
     * @Date: 2018/12/29
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean dealRedisPurchase(List<PurchaseRecordPo> prpList) {
        for (PurchaseRecordPo prp : prpList) {
            purchaseRecordDao.insertPurchaseRecord(prp);
            productDao.decreaseProduct(prp.getProductId(),prp.getQuantity());
        }
        return true;
    }



}

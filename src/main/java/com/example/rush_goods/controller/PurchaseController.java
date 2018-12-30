package com.example.rush_goods.controller;

import com.example.rush_goods.pojo.PurchaseRecordPo;
import com.example.rush_goods.service.PurchaseService;
import org.apache.ibatis.annotations.Insert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class PurchaseController {

    @Autowired
    PurchaseService purchaseService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate=null;

    public static final  String PRODUCT_SCHEDULE_SET="product_schedule_set";
    public static final String PURCHASE_PRODUCT_LIST="purchase_list_";
    public static final int ONE_TIME_SIZE=100;



//    定义jsp视图
    @GetMapping("/test")
    public  ModelAndView testPage(){
        ModelAndView mv=new ModelAndView("test");
        return mv;
    }


    @PostMapping(value = "/purchaseRedis" )
    @ResponseBody
    public Result purchaseRedis(@RequestBody PurchaseRecordPo pr) {
         Long userId = pr.getUserId();
        Long productId=pr.getProductId();
        int quantity=pr.getQuantity();
        boolean success=purchaseService.purchaseRedis(userId,productId,quantity);

        String messge=success?"抢购成功，抢了："+quantity+"件":"抢购失败";
        Result result = new Result(success, messge);
        return result;
    }

    @RequestMapping("/task")
    @ResponseBody
    public Result purchaseTask(){
        System.out.println("定时任务开始。。。");
        Set<String> productIdList=
                stringRedisTemplate.opsForSet().members(PRODUCT_SCHEDULE_SET);
        List<PurchaseRecordPo> prpList=new ArrayList<>();
        for (String productIdStr :productIdList){
            Long productId =Long.parseLong(productIdStr);
            String purchaseKey=PURCHASE_PRODUCT_LIST+productId;
            BoundListOperations<String,String> ops
                    =stringRedisTemplate.boundListOps(purchaseKey);
            //计算记录数
            long size=stringRedisTemplate.opsForList().size(purchaseKey);
            long times=size%ONE_TIME_SIZE==0?
                    size/ONE_TIME_SIZE:size/ONE_TIME_SIZE+1;
            for (int i = 0; i <times ; i++) {
                //获取至多TIME_SIZE个抢红包信息
                List<String> prList =null;
                if (i == 0) {
                    prList = ops.range(i * ONE_TIME_SIZE, (i + 1) * ONE_TIME_SIZE);
                } else {
                    prList=ops.range(i*ONE_TIME_SIZE+1,(i+1)*ONE_TIME_SIZE);
                }
                for (String prStr : prList) {
                    PurchaseRecordPo prp=createPurchaseRecord(productId,prStr);
                    prpList.add(prp);
                }

                try {
//                    //该方法采用新建事务的方式，不会导致全局事务回滚
                    purchaseService.dealRedisPurchase(prpList);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                //清楚列表为空，等待重新写入数据
                prpList.clear();
            }
            //删除购买列表
            stringRedisTemplate.delete(purchaseKey);
            //从商品集合中删除商品
            stringRedisTemplate.opsForSet().remove(PRODUCT_SCHEDULE_SET,productIdStr);

        }
        boolean success=true;
        System.out.println("定时任务结束。。。。");

        String messge=success?"缓存存入数据库成功":"缓存失败";
        Result result = new Result(success, messge);
        return result;
    }






    class Result {
        private boolean success=false;
        private  String message=null;
        public Result() {
        }

        public  Result(boolean success,String message){
            this.success=success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public  PurchaseRecordPo createPurchaseRecord(Long productId, String prStr) {
        String[] arr=prStr.split(",");
        Long userId=Long.parseLong(arr[0]);
        int quantity=Integer.parseInt(arr[1]);
        double sum=Double.valueOf(arr[2]);
        double price=Double.valueOf(arr[3]);
        Long time=Long.parseLong(arr[4]);
        Timestamp purchaserTime=new Timestamp(time);
        PurchaseRecordPo pr=new PurchaseRecordPo();
        pr.setProductId(productId);
        pr.setPurchaseTime(purchaserTime);
        pr.setPrice(price);
        pr.setQuantity(quantity);
        pr.setSum(sum);
        pr.setUserId(userId);
        pr.setNote("购买时间：时间"+purchaserTime.getTime());
        return pr;
    }

}

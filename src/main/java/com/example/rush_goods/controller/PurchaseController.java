package com.example.rush_goods.controller;

import com.example.rush_goods.service.PurchaseService;
import org.apache.ibatis.annotations.Insert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class PurchaseController {
    @Autowired
    PurchaseService purchaseService;



    @GetMapping("/purchase/{userId}/{productId}/{quantity}")
    public Result purchase(@PathVariable Long userId, @PathVariable  Long productId, @PathVariable Integer quantity) {
        boolean success=purchaseService.purchase(userId,productId,quantity);
        String messge=success?"抢购成功":"抢购失败";
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

}

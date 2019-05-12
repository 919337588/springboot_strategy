package com.cx.mapper.entity;

import lombok.Data;

@Data
public class PaymentChannelEntity {
   /** ID */
   private Integer id;
   /** 渠道名称 */
   private String channelName;
   /** 渠道ID */
   private String channelId;
   /**
    * 策略执行beanId
    */
   private String strategyBeanId;

}

package com.cx.strategy.context;

import com.cx.mapper.PaymentChannelMapper;
import com.cx.mapper.entity.PaymentChannelEntity;
import com.cx.strategy.PayStrategy;
import com.cx.utils.SpringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class PayContextStrategy {
    @Autowired
    private PaymentChannelMapper paymentChannelMapper;
    @Autowired
    private SpringUtils springUtils;
    public String toPayHtml(String payCode){
         //1.使用payCode参数查询数据库获取beanid
        PaymentChannelEntity paymentChannel = paymentChannelMapper.getPaymentChannel(payCode);
        if(paymentChannel==null){
            return  "没有该渠道信息";
        }
        //2.获取到bean的id之后，使用spring容器获取实例对象
        String strategyBeanId = paymentChannel.getStrategyBeanId();
        if(StringUtils.isEmpty(strategyBeanId)){
            return  "该渠道没有配置beanid";
        }
        // 3.执行该实现的方法即可.... aliPayStrategy
        PayStrategy payStrategy=SpringUtils.getBean(strategyBeanId, PayStrategy.class);
        return  payStrategy.toPayHtml();
     }
     // 优化 支付渠道存放内存中...
}

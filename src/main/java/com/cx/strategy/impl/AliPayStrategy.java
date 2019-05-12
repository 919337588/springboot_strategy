package com.cx.strategy.impl;

import com.cx.strategy.PayStrategy;
import org.springframework.stereotype.Component;


@Component
public class AliPayStrategy   implements PayStrategy {
    public String toPayHtml() {
        return "调用支付宝支付接口...";
    }
    //AliPayStrategy 注入spring容器 aliPayStrategy
}

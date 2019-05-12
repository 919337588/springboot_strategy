# springboot_strategy
支付聚合平台_策略模式
聚合支付平台
比如搭建聚合支付平台的时候，这时候需要对接很多第三方支付接口，比如支付宝、微信支付、小米支付等。

通过传统if代码判断的，后期的维护性非常差！

public  String toPayHtml2(String payCode){
    if(payCode.equals("ali_pay")){
        return  "调用支付宝接口...";
    }
    if(payCode.equals("xiaomi_pay")){
        return  "调用小米支付接口";
    }
    if(payCode.equals("yinlian_pay")){
        return  "调用银联支付接口...";
    }
    return  "未找到该接口...";
}



这时候可以通过策略模式解决多重if判断问题。

为什么叫做策略模式
每个if判断都可以理解为就是一个策略。

什么是策略模式
策略模式是对算法的包装，是把使用算法的责任和算法本身分割开来，委派给不同的对象管理，最终可以实现解决多重if判断问题。

1.环境(Context)角色：持有一个Strategy的引用。

2.抽象策略(Strategy)角色：这是一个抽象角色，通常由一个接口或抽象类实现。此角色给出所有的具体策略类所需的接口。

3.具体策略(ConcreteStrategy)角色：包装了相关的算法或行为。

定义策略接口->实现不同的策略类->利用多态或其他方式调用策略



聚合支付平台转为策略模式架构思想如下

PayContextStrategy为调用算法角色，
PayStrategy 为支付算法接口 
AliPayStrategy，YinLianPayStrategy等为调用算法实际实现





具体逻辑为

1web端请求支付  带上支付方式的pay_code

2control接受到请求 使用PayContextStrategy调用具体支付算法

3PayContextStrategy根据pay_code重数据库拿到具体的支付算法实现类的bean名称，最后获取bean调用算法实现。

扩展： PayContextStrategy本身也继承一个接口 ，接口的实现类可以通过数据库获取bean,也能通过缓存，也能通过配置文件，这样扩展可以理解为桥接模式。



PayStrategy(抽象角色)
public interface PayStrategy {

  

    /**

     * 共同算法实现骨架

     * @return

     */

     public String toPayHtml();

}




ConcreteStrategy (具体实现角色)
@Component
public class AliPayStrategy  implements PayStrategy {
    public String toPayHtml() {
        return "调用支付宝支付接口";
    }
}





@Component
public class XiaoMiPayStrategy implements PayStrategy {
    public String toPayHtml() {
        return "调用小米支付接口";
    }
}



PayContextService (上下文)
@RestController
public class PayContextService {

    @Autowired
    private PaymentChannelMapper paymentChannelMapper;
    @Autowired
    private  SpringUtils springUtils;
    @RequestMapping("/toPayHtml")
    public  String toPayHtml(String payCode){
        // 1.验证参数
        if(StringUtils.isEmpty(payCode)){
            return  "payCode不能为空!";
        }
        // 2.使用PayCode查询
        PaymentChannelEntity paymentChannel = paymentChannelMapper.getPaymentChannel(payCode);
        if(paymentChannel==null){
            return  "该渠道为空...";
        }
        // 3.获取策略执行的beanid
        String strategyBeanId = paymentChannel.getStrategyBeanId();
        // 4.使用strategyBeanId获取对应spring容器bean信息
        PayStrategy payStrategy = springUtils.getBean(strategyBeanId, PayStrategy.class);
        // 5.执行具体策略算法
        return  payStrategy.toPayHtml();
    }

}


相关SQL语句
DROP TABLE IF EXISTS `payment_channel`;
CREATE TABLE `payment_channel` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `CHANNEL_NAME` varchar(32) NOT NULL COMMENT '渠道名称',
  `CHANNEL_ID` varchar(32) NOT NULL COMMENT '渠道ID',
  `strategy_bean_id` varchar(255) DEFAULT NULL COMMENT '策略执行beanid',
  PRIMARY KEY (`ID`,`CHANNEL_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='支付渠道 ';

-- ----------------------------
-- Records of payment_channel
-- ----------------------------
INSERT INTO `payment_channel` VALUES ('4', '支付宝渠道', 'ali_pay', 'aliPayStrategy');
INSERT INTO `payment_channel` VALUES ('5', '小米支付渠道', 'xiaomi_pay', 'xiaoMiPayStrategy');


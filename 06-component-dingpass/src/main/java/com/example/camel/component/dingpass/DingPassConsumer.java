package com.example.camel.component.dingpass;

import com.alibaba.fastjson.JSONObject;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.DefaultConsumer;
import sun.misc.BASE64Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;

public class DingPassConsumer extends DefaultConsumer {
    private String httpUrl;
    private String securityKey;
    private ConsumerTemplate consumerTemplate;

    public DingPassConsumer(Endpoint endpoint, Processor processor) {
        super(endpoint, processor);
        httpUrl=endpoint.getEndpointUri();
    }


    @Override
    protected void doStart() throws Exception {

        super.doStart();
        getEndpoint().getCamelContext().addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {


                /**
                 * 试验得出，onException和errorHandler两种方式适合在出现异常添加处理逻辑的场景，比如JDBC事务异常回滚数据
                 * doTry方式适合做异常响应，拦截掉异常信息并给予用户友好响应信息
                 */

//                onException(Exception.class).process(new Processor() {
//                    @Override
//                    public void process(Exchange exchange) throws Exception {
//                        exchange.getMessage().setBody("出错了");
//                    }
//                });
                from("jetty:"+httpUrl)
//                        .errorHandler(defaultErrorHandler().onExceptionOccurred(new Processor() {
//                            @Override
//                            public void process(Exchange exchange) throws Exception {
//                                Exception exception=exchange.getException();
//                                exchange.setException(null);
//                                String exceptionMsg="系统异常";
//                                if(exception instanceof DingPassException){
//                                    exceptionMsg=exception.getMessage();
//                                }
//                                exchange.getMessage().setBody(exceptionMsg);
//                            }
//                        }))
                        .doTry()
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                Message message=exchange.getIn();
                                String signature=message.getHeader("x-ddpaas-signature", String.class);
                                if(signature==null||signature.trim().length()==0){
                                    throw new DingPassException("签名串为空");
                                }
                                Long signatureTimestamp=message.getHeader("x-ddpaas-signature-timestamp", Long.class);
                                if(signatureTimestamp==null){
                                    throw new DingPassException("签名时间戳为空");
                                }
                                //密钥
                                String apiSecret="3r8hpt2xkfKgDQMqrWiuH7uMs8Pzr7GbwBEAisuwH5H";
                                //鉴定签名
                                if(signature.equals(calcSignature(apiSecret,signatureTimestamp))==false){
                                    throw new DingPassException("签名错误");
                                }
//                                String paramBody=exchange.getIn().getBody(String.class);
//                                if(StringUtils.isBlank(paramBody)){
//                                    throw new DingPassException("请求参数为空");
//                                }
//                                JSONObject jsonObject= JSON.parseObject(paramBody);
                                //这种方式可以自动转换content-type为x-www-form-urlencoded这个格式的数据，JSON格式的不行
                                Map<String, Object> params=exchange.getIn().getBody(Map.class);
                                System.out.println("===========");
                                System.out.println("接收到的http的请求参数"+new JSONObject(params).toJSONString());
                                System.out.println("===========");
                                Map<String, Object> bodyParams=new HashMap<>();
                                //设置固定的一个loginName
                                String loginName="admin";
                                bodyParams.put("loginName",loginName);
                                exchange.getIn().setBody(bodyParams);
                                getProcessor().process(exchange);
                            }
                        })
                        .doCatch(Exception.class)
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                Throwable throwable=exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
                                throwable.printStackTrace();
                                String exceptionMsg="系统错误";
                                if(throwable instanceof DingPassException){
                                    exceptionMsg="出错了，错误信息："+throwable.getMessage();
                                }
                                exchange.getMessage().setBody(exceptionMsg);
                            }
                        })
                        .end();
            }
        });
    }

    /**
     * 获取签名信息
     * @param apiSecret：密钥
     * @param ts:签名时时间戳
     * @return true
     */
    private String calcSignature(String apiSecret, long ts) throws Exception {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(apiSecret.getBytes(), "HmacSHA256");
            mac.init(key);
            return new String(
                    new BASE64Encoder().encode(mac.doFinal(Long.toString(ts).getBytes())));
        } catch (Exception e) {
            throw new DingPassException("签名失败 sign api secret failed", e);
        }
    }

    @Override
    protected void doStop() throws Exception {
        consumerTemplate.stop();
        super.doStop();
    }
}

package com.example.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.eclipse.jetty.security.AbstractLoginService;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;

import java.util.Collections;

public class Main {
    public static void main(String[] args) throws Exception {
        DefaultCamelContext defaultCamelContext=new DefaultCamelContext();
        defaultCamelContext.start();
        ConstraintSecurityHandler constraintSecurityHandler=new ConstraintSecurityHandler();
        constraintSecurityHandler.setAuthenticator(new BasicAuthenticator());
        constraintSecurityHandler.setLoginService(new AbstractLoginService() {
            @Override
            protected String[] loadRoleInfo(AbstractLoginService.UserPrincipal userPrincipal) {
                //返回用户拥有的权限
                return new String[]{"admin"};
            }

            @Override
            protected UserPrincipal loadUserInfo(String s) {
                return new UserPrincipal(s, new Credential() {
                    @Override
                    public boolean check(Object o) {
                        //在这里判断用户名和账号是否匹配
                        //账号admin 密码admin123
                        if("admin".equals(s)&&"admin123".equals(String.valueOf(o))){
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
        ConstraintMapping constraintMapping=new ConstraintMapping();
        Constraint constraint=new Constraint();
        //是否需要登录
        constraint.setAuthenticate(true);
        //需要的权限
        constraint.setRoles(new String[]{"admin"});
        //名称
        constraint.setName("BASIC");
        constraintMapping.setConstraint(constraint);
        //映射的路径
        constraintMapping.setPathSpec("/*");
        //认证处理器设置限制的映射信息
        constraintSecurityHandler.setConstraintMappings(Collections.singletonList(constraintMapping));
        //将处理器绑定到camel上下文的注册器中
        defaultCamelContext.getRegistry().bind("securityHandler",constraintSecurityHandler);
        defaultCamelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jetty:http://0.0.0.0:8888/basicAuthTest?handlers=securityHandler")
                        .setBody(exchange -> "test result");
            }
        });
    }
}

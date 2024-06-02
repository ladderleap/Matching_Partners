package com.yupi.yupao;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@MapperScan("com.yupi.yupao.mapper")
@EnableScheduling
public class MyApplication {
    @Bean
    public Connector connector(){

        Connector connector=new Connector("org.apache.coyote.http11.Http11NioProtocol");

        connector.setScheme("http");

        connector.setPort(8080);

        connector.setSecure(false);

        connector.setRedirectPort(8443);

        return connector;

    }
    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory(){

        TomcatServletWebServerFactory tomcat =new TomcatServletWebServerFactory(){

            @Override

            protected void postProcessContext(Context context) {

                SecurityConstraint securityConstraint=new SecurityConstraint();

                securityConstraint.setUserConstraint("CONFIDENTIAL");

                SecurityCollection collection=new SecurityCollection();

                collection.addPattern("/");

                securityConstraint.addCollection(collection);

                context.addConstraint(securityConstraint);

            }

        };

        tomcat.addAdditionalTomcatConnectors(connector());

        return tomcat;

    }
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }

}

// 作_者 [程序员_鱼皮](https://yupi.icu/)
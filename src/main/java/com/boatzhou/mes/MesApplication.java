package com.boatzhou.mes;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目启动入口。
 *
 * <p>你可以把它理解成“应用总开关”：</p>
 * <p>1) 启动 Spring Boot 自动装配。</p>
 * <p>2) 扫描并注册 MyBatis 的 Mapper 接口。</p>
 */
@SpringBootApplication
@MapperScan("com.boatzhou.mes.mapper")
public class MesApplication {

    /**
     * 程序主方法。
     *
     * @param args JVM 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(MesApplication.class, args);
    }
}

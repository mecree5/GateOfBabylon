package org.move.fast.common.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * @author sgw
 * @Description
 * @time 2022/11/22 8:05 下午
 */
public class MybatisPlusGenerator {
    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/GateOfBabylon?useUnicode=true&useSSL=false&characterEncoding=utf8", "root", "123456")
                .globalConfig(builder -> {
                    builder.author("YinShiJie") // 设置作者
                            //.enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir(System.getProperty("user.dir") + "/src/main/java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("org.move.fast") // 设置父包名
                            .moduleName("module") // 设置父包模块名
                            .service("service.auto")  // 设置自定义service路径,不设置就是默认路径
                            .serviceImpl("service.auto.impl")
                            .mapper("mapper.auto")
                            .entity("entity.auto")
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, System.getProperty("user.dir") + "/src/main/resources/mapper/auto/")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("sys_conf", "vpn_user", "vpn_vmess") // 设置需要生成的表名
                            .addTablePrefix("tb_"); // 设置过滤表前缀

                })
                .templateConfig(builder -> builder.controller("")) //不生成controller
                .templateConfig(builder -> builder.service(""))
                .templateConfig(builder -> builder.serviceImpl(""))
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
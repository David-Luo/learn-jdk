package com.lang.apt.mapstruct;

import org.junit.jupiter.api.Test;
import org.mapstruct.ap.MappingProcessor;

import javax.tools.ToolProvider;

public class StructMapTest {

    @Test
    public void testCompile(){
        // 获取java编译器
        javax.tools.JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

        String[] ops = {
                "-processor", MappingProcessor.class.getName(),
                "-d", "target/compile",
                "src/main/java/com/lang/apt/mapstruct/PersonMapper.java"};
        int i = javaCompiler.run(null, null, null, ops);
        System.out.println("编译完成："+i);
    }

    public static void main(String[] args) {
        StructMapTest test = new StructMapTest();
        test.testCompile();
    }
}

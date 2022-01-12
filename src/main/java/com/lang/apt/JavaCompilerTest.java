package com.lang.apt;

import javax.tools.*;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JavaCompilerTest {
    public static void main(String[] args) {
        test();
    }

    private static void test() {
        // 获取java编译器
        javax.tools.JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        //
        InputStream first = null; // 程序的输入  null 用 system.in
        OutputStream second = System.out; // 程序的输出 null 用 system.out
        OutputStream third = System.out;  // 程序的错误输出 .,null 用 system.err
        // 程序编译参数 注意 我们编译目录是我们的项目目录
        String[] ops = {
                //"-cp", "/CLASSPATH/TO/CheckGetterProcessor",
                "-processor", "com.lang.apt.CheckGetterProcessor",
                "-d", ".",
                "src/main/test/com/lang/apt/Foo.java"};
        // 0 表示成功， 其他表示出现了错误
        int i = javaCompiler.run(first, second, third, ops);
        if (i == 0) {
            System.out.println("成功");
        } else {
            System.out.println("错误");
        }
    }

    public void compileJavaFiles(List<File> file) {
        DocumentationTool tool = ToolProvider.getSystemDocumentationTool();
        javax.tools.JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        String testClassDir = "src/main/test/";
        try (StandardJavaFileManager fm = tool.getStandardFileManager(null, null, null)) {
            Iterable<? extends JavaFileObject> files =
                    fm.getJavaFileObjectsFromFiles(Arrays.asList(new File(testClassDir, "com/lang/apt/Foo.java")));

            Iterable<String> options = Arrays.asList(
//                    "--add-exports", "jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
                    "--processor-path", testClassDir,
                    "-processor", "com.lang.tool.CheckGetterProcessor",
                    "-s", ".",
                    "-d", ".");
            Iterable<String> classes =Collections.singleton("com.lang.tool.Foo") ;
            JavaCompiler.CompilationTask task = javaCompiler.getTask(null, fm, null, options, classes, files);
//            task.setProcessors(Collections.singleton(new CheckGetterProcessor()));
            if (!task.call())
                throw new AssertionError("compilation failed");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

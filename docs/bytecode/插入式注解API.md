 &#8195; &#8195;在JDK 1.5之后，Java语言提供了对注解（Annotation）的支持，这些注解与普通的Java代码一样，是在运行期间发挥作用的。在JDK 1.6中实现了JSR-269规范JSR-269：Pluggable Annotations Processing API（插入式注解处理API）。提供了一组插入式注解处理器的标准API在编译期间对注解进行处理。Annotation Processor在编译期间而不是运行期间处理Annotation, Annotation Processor相当于编译器的一个插件,所以称为插入式注解处理.在这些插件里面，可以读取、修改、添加抽象语法树中的任意元素。如果这些插件在处理注解期间对语法树进行了修改，编译器将回到解析及填充符号表的过程重新处理，直到所有插入式注解处理器都没有再对语法树进行修改为止，每一次循环称为一个Round，也就是第一张图中的回环过程。 有了编译器注解处理的标准API后，我们的代码才有可能干涉编译器的行为，由于语法树中的任意元素，甚至包括代码注释都可以在插件之中访问到，所以通过插入式注解处理器实现的插件在功能上有很大的发挥空间。只要有足够的创意，程序员可以使用插入式注解处理器来实现许多原本只能在编码中完成的事情。

从Sun Javac的代码来看，编译过程大致可以分为3个过程:

- 解析与填充符号表过程
- 插入式注解处理器的注解处理过程
- 分析与字节码生成过程
![在这里插入图片描述](https://img-blog.csdnimg.cn/730030368d844d1abb83524bfbaedc9f.png#pic_center)

Javac编译动作的入口是com.sun.tools.javac.main.JavaCompiler类，上述3个过程的代码逻辑集中在这个类的compile()和compile2()方法中。在JavaCompiler源码中，插入式注解处理器的初始化过程是在initPorcessAnnotations()方法中完成的，而它的执行过程则是在processAnnotations()方法中完成的，这个方法判断是否还有新的注解处理器需要执行，如果有的话，通过com.sun.tools.javac.processing.JavacProcessingEnvironment类的doProcessing()方法生成一个新的JavaCompiler对象对编译的后续步骤进行处理。
![在这里插入图片描述](https://img-blog.csdnimg.cn/c99d250aa37140abbc9e4fcf459a5636.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6ICB6J665Lid,size_20,color_FFFFFF,t_70,g_se,x_16#pic_center)

当编译器以默认的编译参数编译时，它会执行以下步骤：

- 1.1. Parse： 读入一堆*.java源代码，并且把读进来的符号（Token）映射到AST节点上去。
- 1.2. Enter: 把类的定义放到符号表（Symbol Table）中去。
- 2. Process annotations: 可选的。处理编译单元（compilation units）里面所找到的标记（annotation）。
- 3.1. Attribute: 为AST添加属性。这一步包含名字解析(name resolution)，类型检测(type checking)和常数折叠(constant fold)。
- 3.2. Flow: 为前面得到的AST执行流分析（Flow analysis）操作。这个步骤包含赋值(assignment)的检查和可执行性(reachability)的检查。
- 3.3. Desugar: 重写AST， 并且把一些复杂的语法转化成一般的语法。
- 3.4. Generate: 生成源文件或者类文件。

注解处理器主要有三个用途。

- 一是定义编译规则，并检查被编译的源文件。
- 二是修改已有源代码。
- 三是生成新的源代码。

其中，第二种涉及了 Java 编译器的内部 API，因此并不推荐。第三种较为常见，是 OpenJDK 工具 jcstress，以及 JMH 生成测试代码的方式。

# 编写插入式处理器

编写插入式注解处理器，要点如下：

- 重写init方法，获取一些必要的构建对象
- 重写process方法，实现Builder逻辑
- 用@SupportedAnnotationTypes注解指明感兴趣的注解类型
- 用@SupportedSourceVersion注解指明源码版本

```java
package foo;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.SOURCE)
public @interface CheckGetter {
}
```

```java

package bar;

import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;

import foo.CheckGetter;

@SupportedAnnotationTypes("foo.CheckGetter")
@SupportedSourceVersion(SourceVersion.RELEASE_10)
public class CheckGetterProcessor extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    // TODO: annotated ElementKind.FIELD
    for (TypeElement annotatedClass : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(CheckGetter.class))) {
      for (VariableElement field : ElementFilter.fieldsIn(annotatedClass.getEnclosedElements())) {
        if (!containsGetter(annotatedClass, field.getSimpleName().toString())) {
          processingEnv.getMessager().printMessage(Kind.ERROR,
              String.format("getter not found for '%s.%s'.", annotatedClass.getSimpleName(), field.getSimpleName()));
        }
      }
    }
    return true;
  }

  private static boolean containsGetter(TypeElement typeElement, String name) {
    String getter = "get" + name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    for (ExecutableElement executableElement : ElementFilter.methodsIn(typeElement.getEnclosedElements())) {
      if (!executableElement.getModifiers().contains(Modifier.STATIC)
          && executableElement.getSimpleName().toString().equals(getter)
          && executableElement.getParameters().isEmpty()) {
        return true;
      }
    }
    return false;
  }
}
```

```java

package foo;     // PackageElement

class Foo {      // TypeElement
  int a;           // VariableElement
  static int b;    // VariableElement
  Foo () {}        // ExecutableElement
  void setA (      // ExecutableElement
    int newA         // VariableElement
  ) {}
}
```

为了Debug插入式注解处理器，最方便的办法就是在java代码中执行编译。

```java
JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        String[] ops = {"-processor", "com.lang.tool.CheckGetterProcessor",
               "src/main/test/com/lang/tool/Foo.java"};
int compilationResult = compiler.run(null, null, null, ops);
// javac -cp /CLASSPATH/TO/CheckGetterProcessor -processor bar.CheckGetterProcessor Foo.java
```

# 参考文献
1. [Java-JSR-269-插入式注解处理器](https://liuyehcf.github.io/2018/02/02/Java-JSR-269-%E6%8F%92%E5%85%A5%E5%BC%8F%E6%B3%A8%E8%A7%A3%E5%A4%84%E7%90%86%E5%99%A8/)
2. [Lombok原理分析与功能实现](https://blog.mythsman.com/post/5d2c11c767f841464434a3bf/)
3. [深入拆解 Java 虚拟机-注解处理器](https://time.geekbang.org/column/article/40189)
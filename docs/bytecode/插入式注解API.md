 &#8195; &#8195;在JDK 1.5之后，Java语言提供了对注解（Annotation）的支持，这些注解与普通的Java代码一样，是在运行期间发挥作用的。在JDK 1.6中实现了JSR-269规范JSR-269：Pluggable Annotations Processing API（插入式注解处理API）。提供了一组插入式注解处理器的标准API在编译期间对注解进行处理。Annotation Processor在编译期间而不是运行期间处理Annotation, Annotation Processor相当于编译器的一个插件,所以称为插入式注解处理.在这些插件里面，可以读取、修改、添加抽象语法树中的任意元素。如果这些插件在处理注解期间对语法树进行了修改，编译器将回到解析及填充符号表的过程重新处理，直到所有插入式注解处理器都没有再对语法树进行修改为止，每一次循环称为一个Round，也就是第一张图中的回环过程。 有了编译器注解处理的标准API后，我们的代码才有可能干涉编译器的行为，由于语法树中的任意元素，甚至包括代码注释都可以在插件之中访问到，所以通过插入式注解处理器实现的插件在功能上有很大的发挥空间。只要有足够的创意，程序员可以使用插入式注解处理器来实现许多原本只能在编码中完成的事情。

从Sun Javac的代码来看，编译过程大致可以分为3个过程:

- 解析与填充符号表过程
- 插入式注解处理器的注解处理过程
- 分析与字节码生成过程
![在这里插入图片描述](https://img-blog.csdnimg.cn/730030368d844d1abb83524bfbaedc9f.png#pic_center)

Javac编译动作的入口是com.sun.tools.javac.main.JavaCompiler类，上述3个过程的代码逻辑集中在这个类的compile()和compile2()方法中。在JavaCompiler源码中，插入式注解处理器的初始化过程是在initPorcessAnnotations()方法中完成的，而它的执行过程则是在processAnnotations()方法中完成的，这个方法判断是否还有新的注解处理器需要执行，如果有的话，通过com.sun.tools.javac.processing.JavacProcessingEnvironment类的doProcessing()方法生成一个新的JavaCompiler对象对编译的后续步骤进行处理。
![在这里插入图片描述](https://img-blog.csdnimg.cn/c99d250aa37140abbc9e4fcf459a5636.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA6ICB6J665Lid,size_20,color_FFFFFF,t_70,g_se,x_16#pic_center)


标记处理可能会发生很多轮。每一轮处理器只处理特定的一些标记，并且生成的源文件或者类文件，交给下一轮来处理。如果处理器被要求只处理特定的某一轮，那么他也会处理后续的那些次，包括最后一轮，就算最后一轮没有可以处理的标记。处理器可能也会去处理被这个工具生成的文件。

后一个方法处理前一轮生成的标记类型，并且返回是否这些标记会声明。如果返回是True，那么后续的处理器就不会去处理它们。如果返回是false，那么后续处理器会继续处理它们。一个处理器可能总是返回同样的逻辑值，或者是根据选项改变结果。

```java
 public synchronized void init(ProcessingEnvironment processingEnv)

 public boolean process(Set<? extends TypeElement> annotations,RoundEnvironment roundEnv)
```
这两个方法都是在标记处理过程中被java编译器调用的。第一个方法用来初始化插件，只被调用一次。而第二个方法每一轮标记处理都会被调用，并且在所有处理都结束后还会调用一次。

如何把标记处理器注册成服务
Java提供了一个注册服务的机制。如果一个标记处理器被注册成了一个服务，编译器就会自动的去找到这个标记处理器。注册的方法是，在classpath中找到一个叫META-INF/services的文件夹，然后放入一个javax.annotation.processing.Processor的文件。文件格式是很明显的，就是要包含要注册的标记处理器的完整名称。每个名字都要占单独的一行。

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

Pluggable Annotation Processing API建立了Java 语言本身的一个模型,它把method、package、constructor、type、variable、enum、annotation等Java语言元素映射为Types和Elements，从而将Java语言的语义映射成为对象，我们可以在javax.lang.model包下面可以看到这些类。
在APT里面，Java源代码会被解释称类似XML的结构，比如类，字段，方法，方法的参数等都会被解释称一个元素，每个元素都是Element的实例

插件化注解处理API的使用步骤大概如下：


1、自定义一个Annotation Processor，需要继承​​javax.annotation.processing.AbstractProcessor​​，并覆写process方法。
2、自定义一个注解，注解的元注解需要指定​​@Retention(RetentionPolicy.SOURCE)​​。
3、需要在声明的自定义Annotation Processor中使用​​javax.annotation.processing.SupportedAnnotationTypes​​指定在第2步创建的注解类型的名称(注意需要全类名，"包名.注解类型名称"，否则会不生效)。
4、需要在声明的自定义Annotation Processor中使用​​javax.annotation.processing.SupportedSourceVersion​​指定编译版本。
5、可选操作，可以通在声明的自定义Annotation Processor中使用​​javax.annotation.processing.SupportedOptions​​指定编译参数。

接着需要指定Processor，如果使用IDEA的话，Compiler->Annotation Processors中的Enable annotation processing必须勾选。然后可以通过下面几种方式指定指定Processor。


1、直接使用编译参数指定，例如：javac -processor club.throwable.processor.AnnotationProcessor Main.java。
2、通过服务注册指定，就是META-INF/services/javax.annotation.processing.Processor文件中添加club.throwable.processor.AnnotationProcessor。
3、通过Maven的编译插件的配置指定

# 参考文献
1. [Java-JSR-269-插入式注解处理器](https://liuyehcf.github.io/2018/02/02/Java-JSR-269-%E6%8F%92%E5%85%A5%E5%BC%8F%E6%B3%A8%E8%A7%A3%E5%A4%84%E7%90%86%E5%99%A8/)
2. [Lombok原理分析与功能实现](https://blog.mythsman.com/post/5d2c11c767f841464434a3bf/)
3. [深入拆解 Java 虚拟机-注解处理器](https://time.geekbang.org/column/article/40189)
4. [自定义APT之：调试](https://blog.csdn.net/wengliuhu/article/details/113920085)
Java编译器的内核
当OpenJDK编译器以默认的编译参数编译时，它会执行以下步骤：

a) Parse： 读入一堆*.java源代码，并且把读进来的符号（Token）映射到AST节点上去。
b) Enter: 把类的定义放到符号表（Symbol Table）中去。
c) Process annotations: 可选的。处理编译单元（compilation units）里面所找到的标记（annotation）。
d) Attribute: 为AST添加属性。这一步包含名字解析(name resolution)，类型检测(type checking)和常数折叠(constant fold)。
e) Flow: 为前面得到的AST执行流分析（Flow analysis）操作。这个步骤包含赋值(assignment)的检查和可执行性(reachability)的检查。
f) Desugar: 重写AST， 并且把一些复杂的语法转化成一般的语法。
g) Generate: 生成源文件或者类文件。

具体而言:
1 Parse
作为第一步，词法分析器（lexical analyzer）把输入的字符流（character sequence）映射成一个符号流(token sequence)。然后Parser再把生成的符号流映射成一个抽象语法树（AST）
由接口com.sun.tools.javac.parser.Parser定义.
2 Enter
在这个步骤中，编译器会找到当前范围（enclosing scope）中发现的所有的定义(definitions)，并且把这些定义注册成符号（symbols）。Enter这个步骤又分为以下两个阶段：

在第一个阶段，编译器会注册所有类的符号，并且把这写符号和相应的范围（scope）联系在一起。实现方法是使用一个Visitor（访问者）类，由上而下的遍历AST，访问所有的类，包括类里面的内部类。Enter给每一个类的符号都添加了一个MemberEnter对象，这个对象是由第二个阶段来调用的

在第二个阶段中，这些类被MemberEnter对象所完成（completed，即完成类的成员变量的Enter）。首先，MemberEnter决定一个类的参数，父类和接口。然后这些符号被添加进了类的范围中。不像前一个步骤，这个步骤是懒惰执行的。类的成员只有在被访问时，才加入类的定义中的。这里的实现，是通过安装一个完成对象（member object）到类的符号中。这些对象可以在需要时调用member-enter

最后，enter把所有的顶层类（top-level classes）放到一个todo-queue中，

由类com.sun.tools.javac.comp.Enter实现,实际是由JCTree.Visitor定义.

3 Process Annotations
如果存在标记处理器，并且编译参数里面指定要处理标记，那么这个过程就会处理在某个编译单元里面的标记。JSR269定义了一个接口，可以用来写这种Annotation处理插件。然而，这个接口的功能非常有限，并且不能用Collective Behavior扩展这种语言。主要的限制是JSR269不提供子方法的反射调用。
由接口javax.annotation.processing.Processor定义.
4 Attribute
为Enter阶段生成的所有AST添加属性。应当注意，Attribte可能会需要额外的文件被解析（Parse），通过SourceCompleter加入到符号表中。

大多数的环境相关的分析都是发生在这个阶段的。这些分析包括名称解析，类型检查，常数折叠。这些都是子任务。有些子任务调用下列的一些类，但也可能调用其他的。

l Check：这是用于类型检查的类。当有完成错误（completion error）或者类型错误时，它就会报错。

l Resovle: 这是名字解析的类。如果解析失败，就会报错。

l ConstFold: 这是参数折叠类。常数折叠用于简化在编译时的常数表达式。

l Infer：类参数引用的类。

由类com.sun.tools.javac.comp.Attr实现,实际是由JCTree.Visitor定义.

5 Flow
这个阶段会对添加属性后的类，执行数据流的检查。存活性分析（liveness analysis） 检查是否每个语句都可以被执行到。异常分析（Excepetion analysis） 检查是豆每个被抛出的异常都是声明过的，并且这些异常是否都会被捕获。确定行赋值（definite assignment）分析保证每个变量在使用时已经被赋值。而确定性不赋值（definite unassignment）分析保证final变量不会被多次赋值。
由类com.sun.tools.javac.comp.Flow实现.
6 Desugar
除去多余的语法，像内部类，类的常数，assertion断言语句，foreach循环等。
由各种com.sun.tools.javac.comp.TreeTranslator的实现类实现通过的功能.

7  Generate
这是最终的阶段。这个阶段生成许多源文件或者类文件。到底是生成源文件还是类文件取决于编译选项。

由com.sun.tools.javac.jvm.ClassWriter实现.




# 抽象语法树
一个标准的.java文件解析后JCTree的结构如下:

- JCCompilationUnit: 编译单位,即一个源代码文件
  - JCPackageDecl: 包声明
  - JCImport: 包导入声明
  - JCClassDecl: 类声明
    - JCVariableDecl: 类成员变量
    - JCMethodDecl: 方法声明
      - JCModifiers: 方法描述
        - JCAnnotation: 方法注解
      - JCVariableDecl: 方法参数


com.sun.source.tree
com.sun.tools.javac.tree


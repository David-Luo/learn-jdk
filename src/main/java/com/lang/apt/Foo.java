package com.lang.apt;     // PackageElement
@CheckGetter
class Foo {      // TypeElement
    static int b;    // VariableElement
    int a;           // VariableElement

    Foo() {
    }        // ExecutableElement

    void setA(      // ExecutableElement
                    int newA         // VariableElement
    ) {
    }
}

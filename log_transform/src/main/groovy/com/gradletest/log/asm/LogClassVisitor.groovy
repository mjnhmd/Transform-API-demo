package com.gradletest.log.asm

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * @author lyl* @Date 2019/7/21
 * This class is used for:
 */
class LogClassVisitor extends ClassVisitor {
    private String superName
    private Map<String, List<Parameter>> methodParametersMap;
    private LogMethodVisitor debugMethodAdapter;
    private String className;

    private List<String> includeMethods = new ArrayList<String>();
    private List<String> implMethods = new ArrayList<>();
    LogClassVisitor(final ClassVisitor cv, final Map<String, List<Parameter>> methodParametersMap) {
        super(Opcodes.ASM5, cv)
        this.methodParametersMap = methodParametersMap;
    }

    public void attachIncludeMethodsAndImplMethods(List<String> includeMethods,List<String> implMethods){
        this.includeMethods.addAll(includeMethods);
        this.implMethods.addAll(implMethods);
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.superName = superName
        // 记录该类实现了哪些接口
        this.className = name;
        super.visit(version, access, name, signature, superName, interfaces)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
//        if (!checkSuperClass(this.superName)) {
//            return super.visitMethod(access, name, desc, signature, exceptions)
//        }
//        if ('onCreate(Landroid/os/Bundle;)V' == (name + desc)) {
//            println "log >>> method name = ${name + desc}"
//            MethodVisitor methodVisitor = this.cv.visitMethod(access, name, desc, signature, exceptions)
//            return new LogMethodVisitor(methodVisitor, name)
//        }

//        return super.visitMethod(access, name, desc, signature, exceptions)




        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if(includeMethods.contains(name)){
            String methodUniqueKey = name + desc;
            debugMethodAdapter = new LogMethodVisitor(className, methodParametersMap.get(methodUniqueKey), name, access, desc, mv);
            if(implMethods.contains(name)){
                debugMethodAdapter.switchToDebugImpl();
            }
            return debugMethodAdapter;
        }
        return mv;
    }

    /**
     * 一般情况下，这里应该使用注解
     * @param superName
     * @return
     */
//    static boolean checkSuperClass(String superName) {
//        println "log inject >>> superName = ${superName}"
//        return superName == 'androidx/appcompat/app/AppCompatActivity'
//    }

}

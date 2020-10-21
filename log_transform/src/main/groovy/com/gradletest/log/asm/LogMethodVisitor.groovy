package com.gradletest.log.asm


import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
/**
 * @author lyl* @Date 2019/7/21
 * This class is used for:
 */
class LogMethodVisitor extends LocalVariablesSorter implements Opcodes {

//    String name

//    LogMethodVisitor(MethodVisitor mv, String name) {
//        super(Opcodes.ASM6, mv)
//        this.name = name
//    }


    private List<Parameter> parameters;
    private String className;
    private String methodName;
    private boolean debugMethod = true;
    private boolean debugMethodWithCustomLogger = false;
    private int timingStartVarIndex;
    private String methodDesc;

    public LogMethodVisitor(String className, List<Parameter> parameters, String name, int access, String desc, MethodVisitor mv) {
        super(Opcodes.ASM5, access, desc, mv);
        if(!className.endsWith("/")) {
            this.className = className.substring(className.lastIndexOf("/") + 1);
        } else {
            this.className = className;
        }
        this.parameters = parameters;
        this.methodName = name;
        this.methodDesc = desc;
    }

    public void switchToDebugImpl(){
        debugMethod = true;
        debugMethodWithCustomLogger = false;
    }
    /**
     *    L2
     *     LINENUMBER 13 L2
     *     LDC "log_inject"
     *     LDC "onCreate"
     *     INVOKESTATIC android/util/Log.e (Ljava/lang/String;Ljava/lang/String;)I
     *     POP
     */
    @Override
    void visitCode() {
//        super.visitCode()
        // 在方法之前插入 Log.e("", "")
        // 这两个是参数
//        visitLdcInsn('log_inject')
//        visitLdcInsn(this.name)
//        visitMethodInsn(Opcodes.INVOKESTATIC, 'android/util/Log', 'e', '(Ljava/lang/String;Ljava/lang/String;)I', false)
        // 这里的用法有点奇怪，还需要研究一下
        // visitXXX 实际上会触发 MethodWriter 的方法，这些方法会将我们想要写入的字节码存放起来
        // 最后统一的写入到输出的 class 文件中



        super.visitCode();
        if(!debugMethod && !debugMethodWithCustomLogger) return;
        int printUtilsVarIndex = newLocal(Type.getObjectType("com/gradletest/ParameterPrinter"));
        mv.visitTypeInsn(NEW, "com/gradletest/ParameterPrinter");
        mv.visitInsn(DUP);
        mv.visitLdcInsn(className);
        mv.visitLdcInsn(methodName);
        mv.visitMethodInsn(INVOKESPECIAL, "com/gradletest/ParameterPrinter", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", false);
        mv.visitVarInsn(ASTORE, printUtilsVarIndex);
        for(int i = 0; i < parameters.size(); i++) {
            Parameter parameter = parameters.get(i);
            String name = parameter.name;
            String desc = parameter.desc;
            int index = parameter.index;
            int opcode = Utils.getLoadOpcodeFromDesc(desc);
            String fullyDesc = String.format("(Ljava/lang/String;%s)Lcom/gradletest/ParameterPrinter;", desc);
            visitPrint(printUtilsVarIndex, index, opcode, name, fullyDesc);
        }
        mv.visitVarInsn(ALOAD, printUtilsVarIndex);
        if(debugMethod) {
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/gradletest/ParameterPrinter", "print", "()V", false);
        } else if(debugMethodWithCustomLogger) {
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/gradletest/ParameterPrinter", "printWithCustomLogger", "()V", false);
        }
        //Timing
        timingStartVarIndex = newLocal(Type.LONG_TYPE);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        mv.visitVarInsn(Opcodes.LSTORE, timingStartVarIndex);

    }

    private void visitPrint(int varIndex, int localIndex, int opcode, String name, String desc){
        mv.visitVarInsn(ALOAD, varIndex);
        mv.visitLdcInsn(name);
        mv.visitVarInsn(opcode, localIndex);
        mv.visitMethodInsn(INVOKEVIRTUAL,
                "com/gradletest/ParameterPrinter",
                "append",
                desc, false);
        mv.visitInsn(POP);
    }

    /**
     *     MAXSTACK = 2
     *     MAXLOCALS = 2
     * @param maxStack
     * @param maxLocals
     */
    @Override
    void visitMaxs(int maxStack, int maxLocals) {
        // 修改后方法需要的栈帧 可以从 byteCode 里面看到
        super.visitMaxs(2, 2)
    }

    @Override
    void visitInsn(int opcode) {
        if ((debugMethod || debugMethodWithCustomLogger) && ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW)) {
            Type returnType = Type.getReturnType(methodDesc);
            String returnDesc = methodDesc.substring(methodDesc.indexOf(")") + 1);
            if(returnDesc.startsWith("[") || returnDesc.startsWith("L")) {
                returnDesc = "Ljava/lang/Object;"; //regard object extended from Object or array as object
            }
            //store origin return value
            int resultTempValIndex = -1;
            if(returnType != Type.VOID_TYPE || opcode == ATHROW) {
                if(opcode == ATHROW){
                    returnType = Type.getType("Ljava/lang/Object;");
                }
                resultTempValIndex = newLocal(returnType);
                int storeOpcocde = Utils.getStoreOpcodeFromType(returnType);
                if(opcode == ATHROW) storeOpcocde = ASTORE;
                mv.visitVarInsn(storeOpcocde, resultTempValIndex);
            }
            //parameter1 parameter2
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            mv.visitVarInsn(LLOAD, timingStartVarIndex);
            mv.visitInsn(LSUB);
            int index = newLocal(Type.LONG_TYPE);
            mv.visitVarInsn(LSTORE, index);
            mv.visitLdcInsn(className);    //parameter 1 string
            mv.visitLdcInsn(methodName);   //parameter 2 string
            mv.visitVarInsn(LLOAD, index); //parameter 3 long
            //parameter 4
            if(returnType != Type.VOID_TYPE || opcode == ATHROW) {
                int loadOpcode = Utils.getLoadOpcodeFromType(returnType);
                if(opcode == ATHROW) {
                    loadOpcode = ALOAD;
                    returnDesc = "Ljava/lang/Object;";
                }
                mv.visitVarInsn(loadOpcode, resultTempValIndex);
                String formatDesc = String.format("(Ljava/lang/String;Ljava/lang/String;J%s)V", returnDesc);
                if(debugMethod) {
                    mv.visitMethodInsn(INVOKESTATIC, "com/gradletest/ResultPrinter", "print", formatDesc, false);
                } else {
                    mv.visitMethodInsn(INVOKESTATIC, "com/gradletest/ResultPrinter", "printWithCustomLogger", formatDesc, false);
                }
                mv.visitVarInsn(loadOpcode, resultTempValIndex);
            } else {
                mv.visitLdcInsn("void");
                if(debugMethod) {
                    mv.visitMethodInsn(INVOKESTATIC, "com/gradletest/ResultPrinter", "print", "(Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V", false);
                } else {
                    mv.visitMethodInsn(INVOKESTATIC, "com/gradletest/ResultPrinter", "printWithCustomLogger", "(Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V", false);
                }
            }
        }
        super.visitInsn(opcode);
    }
}

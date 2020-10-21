package com.gradletest.log.asm.prego;

import com.gradletest.log.asm.Parameter;
import com.gradletest.log.asm.prego.DebugPreGoClassAdapter;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Quinn on 19/09/2018.
 */
public class DebugPreGoMethodAdapter extends MethodVisitor implements Opcodes {

    private Map<String, List<Parameter>> methodParametersMap;
    private List<Parameter> parameters = new ArrayList<>();
    private String methodKey;
    private boolean needParameter = false;;
    private List<Label> labelList = new ArrayList<>();
    private DebugPreGoClassAdapter.MethodCollector methodCollector;
    private String methodName;
    private boolean useImpl = false;


    public DebugPreGoMethodAdapter(String methodName,String methodKey, Map<String, List<Parameter>> methodParametersMap, MethodVisitor mv, boolean needParameter, DebugPreGoClassAdapter.MethodCollector methodCollector) {
        super(Opcodes.ASM5, mv);
        this.methodName = methodName;
        this.methodKey = methodKey;
        this.methodParametersMap = methodParametersMap;
        this.needParameter = needParameter;
        this.methodCollector = methodCollector;
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        if(!"this".equals(name) && start == labelList.get(0) && needParameter) {
            Type type = Type.getType(desc);
            if(type.getSort() == Type.OBJECT || type.getSort() == Type.ARRAY) {
                parameters.add(new Parameter(name, "Ljava/lang/Object;", index));
            } else {
                parameters.add(new Parameter(name, desc, index));
            }
        }
        super.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public void visitEnd() {
        methodCollector.onIncludeMethod(methodName,false);
        methodParametersMap.put(methodKey, parameters);
        super.visitEnd();
    }

    @Override
    public void visitLabel(Label label) {
        labelList.add(label);
        super.visitLabel(label);
    }


}

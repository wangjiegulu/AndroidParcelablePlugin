package com.wangjie.androidparcelableplugin.types.primitivewrapper;

import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.wangjie.androidparcelableplugin.types.SupportTypeGenerator;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 1/27/16.
 */
public class PrimitiveWrapperTypeGenerator implements SupportTypeGenerator {
    private String typeNameHolder;

    public PrimitiveWrapperTypeGenerator(String typeNameHolder) {
        this.typeNameHolder = typeNameHolder;
    }

    @Override
    public void writeMethod(PsiCodeBlock methodBody, PsiElementFactory factory, PsiField field) {
        if (null == methodBody) {
            return;
        }
        methodBody.add(factory.createStatementFromText("out.writeValue(" + field.getName() + ");", null));
    }

    @Override
    public void readMethod(PsiCodeBlock methodBody, PsiElementFactory factory, PsiField field) {
        if (null == methodBody) {
            return;
        }
        methodBody.add(factory.createStatementFromText(field.getName() + " = (" + typeNameHolder + ")in.readValue(" + typeNameHolder + ".class.getClassLoader());", null));
    }
}

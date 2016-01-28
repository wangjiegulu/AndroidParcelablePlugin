package com.wangjie.androidparcelableplugin.types.primitive;

import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.wangjie.androidparcelableplugin.types.SupportTypeGenerator;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 1/27/16.
 */
public class PrimitiveTypeGenerator implements SupportTypeGenerator {

    @Override
    public void writeMethod(PsiCodeBlock methodBody, PsiElementFactory factory, PsiField field) {
        if (null == methodBody) {
            return;
        }
        String typeNameHolder = field.getType().getPresentableText();
        String temp = typeNameHolder.substring(0, 1).toUpperCase() + typeNameHolder.substring(1);
        methodBody.add(factory.createStatementFromText("out.write" + temp + "(" + field.getName() + ");", null));
    }

    @Override
    public void readMethod(PsiCodeBlock methodBody, PsiElementFactory factory, PsiField field) {
        if (null == methodBody) {
            return;
        }
        String typeNameHolder = field.getType().getPresentableText();
        String temp = typeNameHolder.substring(0, 1).toUpperCase() + typeNameHolder.substring(1);
        methodBody.add(factory.createStatementFromText(field.getName() + " = in.read" + temp + "();", null));
    }
}

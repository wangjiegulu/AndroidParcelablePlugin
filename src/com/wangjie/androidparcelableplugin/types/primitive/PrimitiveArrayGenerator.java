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
public class PrimitiveArrayGenerator implements SupportTypeGenerator {

    @Override
    public void writeMethod(PsiCodeBlock methodBody, PsiElementFactory factory, PsiField field) {
        if (null == methodBody) {
            return;
        }
        String typeNameHolder = field.getType().getDeepComponentType().getPresentableText();
        String temp = typeNameHolder.substring(0, 1).toUpperCase() + typeNameHolder.substring(1);
        methodBody.add(factory.createStatementFromText("out.write" + temp + "Array(" + field.getName() + ");", null));
    }

    @Override
    public void readMethod(PsiCodeBlock methodBody, PsiElementFactory factory, PsiField field) {
        if (null == methodBody) {
            return;
        }
        String fieldName = field.getName();
        String typeNameHolder = field.getType().getDeepComponentType().getPresentableText();
        String temp = typeNameHolder.substring(0, 1).toUpperCase() + typeNameHolder.substring(1);
//        methodBody.add(factory.createStatementFromText(fieldName + " = new " + typeNameHolder + "[in.readInt()];", null));
//        methodBody.add(factory.createStatementFromText("in.read" + temp + "Array(" + fieldName + ");", null));

        // nickNames = in.createStringArray();
        methodBody.add(factory.createStatementFromText(fieldName + " = in.create" + temp + "Array();", null));

    }
}

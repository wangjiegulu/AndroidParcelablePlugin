package com.wangjie.androidparcelableplugin.types.common;

import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.wangjie.androidparcelableplugin.types.SupportTypeGenerator;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 1/27/16.
 */
public class CommonTypeGenerator implements SupportTypeGenerator {

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
        PsiType fieldType = field.getType();
        String presentableType = field.getType().getPresentableText();
        PsiType[] parameterTypes = ((PsiClassReferenceType) fieldType).getParameters();

        String classLoaderTypeName;
        if(0 == parameterTypes.length){
            classLoaderTypeName = presentableType;
        }else{
            classLoaderTypeName = parameterTypes[0].getPresentableText();
        }
        methodBody.add(factory.createStatementFromText(field.getName() + " = (" + presentableType + ")in.readValue(" + classLoaderTypeName + ".class.getClassLoader());", null));
    }
}

package com.wangjie.androidparcelableplugin.types.serializable;

import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.wangjie.androidparcelableplugin.types.SupportTypeGenerator;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 1/27/16.
 */
public class SerializableTypeGenerator implements SupportTypeGenerator {

    @Override
    public void writeMethod(PsiCodeBlock methodBody, PsiElementFactory factory, PsiField field) {
        if (null == methodBody) {
            return;
        }
        // out.writeSerializable();
        methodBody.add(factory.createStatementFromText("out.writeSerializable(" + field.getName() + ");", null));
    }

    @Override
    public void readMethod(PsiCodeBlock methodBody, PsiElementFactory factory, PsiField field) {
        if (null == methodBody) {
            return;
        }

        // p = in.readSerializable()
        methodBody.add(factory.createStatementFromText(field.getName() + " = (" + field.getType().getPresentableText() + ") in.readSerializable();", null));
    }
}

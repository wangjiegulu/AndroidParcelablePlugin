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
public class PrimitiveBooleanGenerator implements SupportTypeGenerator {
    @Override
    public void writeMethod(PsiCodeBlock methodBody, PsiElementFactory factory, PsiField field) {
        if (null == methodBody) {
            return;
        }
        methodBody.add(factory.createStatementFromText("out.writeByte((byte) (" + field.getName() + " ? 1 : 0));", null));
    }

    @Override
    public void readMethod(PsiCodeBlock methodBody, PsiElementFactory factory, PsiField field) {
        if (null == methodBody) {
            return;
        }
        methodBody.add(factory.createStatementFromText(field.getName() + " = 1 == in.readByte();", null));
    }
}

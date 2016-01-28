package com.wangjie.androidparcelableplugin.types.parcelable;

import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.wangjie.androidparcelableplugin.types.SupportTypeGenerator;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 1/27/16.
 */
public class ParcelableTypeGenerator implements SupportTypeGenerator {

    @Override
    public void writeMethod(PsiCodeBlock methodBody, PsiElementFactory factory, PsiField field) {
        if (null == methodBody) {
            return;
        }
        // out.writeParcelable(p, 0);
        methodBody.add(factory.createStatementFromText("out.writeParcelable(" + field.getName() + ", 0);", null));
    }

    @Override
    public void readMethod(PsiCodeBlock methodBody, PsiElementFactory factory, PsiField field) {
        if (null == methodBody) {
            return;
        }

        // p = in.readParcelable(getClass().getClassLoader())
        methodBody.add(factory.createStatementFromText(field.getName() + " = in.readParcelable(" + field.getType().getPresentableText() + ".class.getClassLoader());", null));
    }
}

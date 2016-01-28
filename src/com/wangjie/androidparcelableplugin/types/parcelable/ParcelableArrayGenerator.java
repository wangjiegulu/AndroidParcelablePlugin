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
public class ParcelableArrayGenerator implements SupportTypeGenerator {

    @Override
    public void writeMethod(PsiCodeBlock methodBody, PsiElementFactory factory, PsiField field) {
        if (null == methodBody) {
            return;
        }
        // out.writeParcelableArray(phoneArray, 0);
        methodBody.add(factory.createStatementFromText("out.writeTypedArray(" + field.getName() + ", 0);", null));
    }

    @Override
    public void readMethod(PsiCodeBlock methodBody, PsiElementFactory factory, PsiField field) {
        if (null == methodBody) {
            return;
        }

        String fieldName = field.getName();
        String deepComponentType = field.getType().getDeepComponentType().getPresentableText();

        // phoneArray = in.createTypedArray(Phone.CREATOR);
        methodBody.add(factory.createStatementFromText(fieldName + " = in.createTypedArray(" + deepComponentType + ".CREATOR);", null));


    }
}

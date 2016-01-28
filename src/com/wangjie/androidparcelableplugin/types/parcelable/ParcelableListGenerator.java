package com.wangjie.androidparcelableplugin.types.parcelable;

import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.wangjie.androidparcelableplugin.types.SupportTypeGenerator;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 1/27/16.
 */
public class ParcelableListGenerator implements SupportTypeGenerator {

    @Override
    public void writeMethod(PsiCodeBlock methodBody, PsiElementFactory factory, PsiField field) {
        if (null == methodBody) {
            return;
        }
        // out.writeTypedList(p);
        methodBody.add(factory.createStatementFromText("out.writeTypedList(" + field.getName() + ");", null));
    }

    @Override
    public void readMethod(PsiCodeBlock methodBody, PsiElementFactory factory, PsiField field) {
        if (null == methodBody) {
            return;
        }

        String fieldName = field.getName();
        String parameterType = ((PsiClassReferenceType) field.getType()).getParameters()[0].getPresentableText();

//        // phones = new ArrayList<>();
//        // in.readTypedList(phones, Phone.CREATOR);
//        methodBody.add(factory.createStatementFromText(fieldName + " = new ArrayList<" + parameterType + ">();", null));
//        methodBody.add(factory.createStatementFromText("in.readTypedList(" + fieldName + ", " + parameterType + ".CREATOR);", null));

        // phones = in.createTypedArrayList(Phone.CREATOR);
        methodBody.add(factory.createStatementFromText(fieldName + " = in.createTypedArrayList(" + parameterType + ".CREATOR);", null));


    }
}

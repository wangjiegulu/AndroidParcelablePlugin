package com.wangjie.androidparcelableplugin.types;

import com.intellij.psi.PsiCodeBlock;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 1/27/16.
 */
public interface SupportTypeGenerator {
    void writeMethod(PsiCodeBlock methodBody, PsiElementFactory factory, PsiField field);
    void readMethod(PsiCodeBlock methodBody, PsiElementFactory factory, PsiField field);
}

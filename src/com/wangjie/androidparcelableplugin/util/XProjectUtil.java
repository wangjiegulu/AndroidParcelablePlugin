package com.wangjie.androidparcelableplugin.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 1/27/16.
 */
public class XProjectUtil {
    /**
     * Class -> PsiClass
     *
     * @param clazz
     * @return
     */
    public static PsiClass getClassByName(Class clazz, Project project) {
        return JavaPsiFacade.getInstance(project).findClass(clazz.getName(), GlobalSearchScope.allScope(project));
    }

    public static PsiClassType getTypeByName(String classFullName, Project project){
        return PsiType.getTypeByName(classFullName, project, GlobalSearchScope.allScope(project));
    }
    public static PsiClassType getTypeByClass(Class clazz, Project project){
        return getTypeByName(clazz.getName(), project);
    }
}

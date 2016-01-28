package com.wangjie.androidparcelableplugin.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
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

    public static PsiClassType getTypeByName(String classFullName, Project project) {
        return PsiType.getTypeByName(classFullName, project, GlobalSearchScope.allScope(project));
    }

    public static PsiClassType getTypeByClass(Class clazz, Project project) {
        return getTypeByName(clazz.getName(), project);
    }

    public static boolean isSameMethodParameterTypes(PsiParameter[] parameters, String... parameterTypeNames) {
        int len = parameters.length;
        if (len != parameterTypeNames.length) {
            return false;
        }

        for (int i = 0; i < len; i++) {
            if (!parameters[i].getType().getCanonicalText().equals(parameterTypeNames[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSameMethod(PsiMethod method, String methodName, String... parameterTypeNames) {
        return method.getName().equals(methodName)
                && isSameMethodParameterTypes(method.getParameterList().getParameters(), parameterTypeNames);
    }
}

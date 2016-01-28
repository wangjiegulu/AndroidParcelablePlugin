package com.wangjie.androidparcelableplugin.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiType;

import java.util.List;
import java.util.Map;

import static com.wangjie.androidparcelableplugin.util.XProjectUtil.getTypeByClass;
import static com.wangjie.androidparcelableplugin.util.XProjectUtil.getTypeByName;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 1/28/16.
 */
public class ParcelUtil {
    public static boolean isParcelable(PsiType psiType, Project project) {
        return getTypeByClass(Boolean.class, project).isAssignableFrom(psiType)
                ||
                getTypeByClass(Integer.class, project).isAssignableFrom(psiType)
                ||
                getTypeByClass(Float.class, project).isAssignableFrom(psiType)
                ||
                getTypeByClass(Long.class, project).isAssignableFrom(psiType)
                ||
                getTypeByClass(Double.class, project).isAssignableFrom(psiType)
                ||
                getTypeByClass(Byte.class, project).isAssignableFrom(psiType)
                ||
                getTypeByClass(Short.class, project).isAssignableFrom(psiType)
                ||
                getTypeByClass(List.class, project).isAssignableFrom(psiType)
                ||
                getTypeByClass(Map.class, project).isAssignableFrom(psiType)
                ||
                getTypeByName("android.util.SparseArray", project).isAssignableFrom(psiType)
                ||
                getTypeByName("android.os.IBinder", project).isAssignableFrom(psiType)
                ||
                getTypeByName("android.os.PersistableBundle", project).isAssignableFrom(psiType)
                ||
                getTypeByClass(CharSequence.class, project).isAssignableFrom(psiType)
                ||
                getTypeByName("android.util.Size", project).isAssignableFrom(psiType)
                ||
                getTypeByName("android.util.SizeF", project).isAssignableFrom(psiType);

    }

}

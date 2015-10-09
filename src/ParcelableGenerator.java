import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 10/9/15.
 */
public class ParcelableGenerator extends AnAction {
    private Project project;
    private PsiClass psiClass;
    private PsiElementFactory factory;

    public static final String PARCELABLE_PACKAGE = "android.os";
    public static final String PARCELABLE_CLASS_NAME = "Parcelable";
    //    public static final String PARCELABLE_CLASS_FULL_NAME = "android.os." + PARCELABLE_CLASS_NAME;
    public static final String PARCEL_CLASS_NAME = "Parcel";
    //    public static final String PARCEL_CLASS_FULL_NAME = "android.os." + PARCEL_CLASS_NAME;
    public static final String METHOD_DESCRIBE_CONTENT = "@Override\n" +
            "    public int describeContents(){\n" +
            "        return 0;\n" +
            "    }";
    public static final String FIELD_CREATOR = "public static final Parcelable.Creator<${PsiClassName}> CREATOR = new Parcelable.Creator<${PsiClassName}>(){\n" +
            "        @Override\n" +
            "        public ${PsiClassName}[] newArray(int size){\n" +
            "            return new ${PsiClassName}[size];\n" +
            "        }\n" +
            "        \n" +
            "        @Override\n" +
            "        public ${PsiClassName} createFromParcel(" + PARCEL_CLASS_NAME + " in){\n" +
            "            return new ${PsiClassName}(in);\n" +
            "        }\n" +
            "    };";

    public static final String METHOD_WRITE_TO_PARCEL = "@Override\n" +
            "    public void writeToParcel(Parcel out, int flags){\n" +
            "    }";

    public static final String METHOD_CONSTRUCTOR_FROM_PARCEL = "public ${PsiClassName}(Parcel in){" +
            "    }";

    public void actionPerformed(AnActionEvent e) {
        project = e.getProject();
        if (null == project) {
            return;
        }
        psiClass = getPsiClassFromContext(e);
        if (null == psiClass) {
            return;
        }

        factory = JavaPsiFacade.getElementFactory(project);

        new WriteCommandAction.Simple(project, psiClass.getContainingFile()) {

            @Override
            protected void run() throws Throwable {
                generateImplementsParcelableInterface();
            }
        }.execute();


    }


    /**
     * Class -> PsiClass
     *
     * @param clazz
     * @return
     */
    private PsiClass convertPsiClass(Class clazz) {
        return JavaPsiFacade.getInstance(project).findClass(clazz.getName(), GlobalSearchScope.allScope(project));
    }

    /**
     * 生成import
     */
    private void generateImports() {
        psiClass.addBefore(factory.createImportStatementOnDemand(PARCELABLE_PACKAGE), psiClass.getOriginalElement());
    }

    /**
     * 实现某个接口
     */
    private void generateImplementsParcelableInterface() {
        PsiClassType[] implementsListTypes = psiClass.getImplementsListTypes();
        for (PsiClassType psi : implementsListTypes) {
            PsiClass resolved = psi.resolve();
            // 如果该接口已经被实现了，则不再重新实现
            if (null != resolved && (PARCELABLE_PACKAGE + PARCELABLE_CLASS_NAME).equals(resolved.getQualifiedName())) {
                return;
            }
        }

        // 实现接口
        PsiJavaCodeReferenceElement referenceElement = factory.createReferenceFromText(PARCELABLE_CLASS_NAME, null);
        PsiReferenceList implementsList = psiClass.getImplementsList();
        if (null != implementsList) {
            implementsList.add(referenceElement);
        }

        generateImports();

        generateExtraMethods();
    }

    /**
     * 生成其它方法
     */
    private void generateExtraMethods() {
        String psiClassName = psiClass.getName();

        // 生成writeToParcel方法
        PsiMethod writeMethod = factory.createMethodFromText(METHOD_WRITE_TO_PARCEL, null);

        // 创建含有Parcel的构造方法
        PsiMethod parcelConstructor = factory.createMethodFromText(METHOD_CONSTRUCTOR_FROM_PARCEL.replace("${PsiClassName}", psiClassName), null);

        PsiField[] fields = psiClass.getAllFields();
        for (PsiField field : fields) {
            PsiModifierList modifierList = field.getModifierList();
            if (null != modifierList &&
                    modifierList.hasModifierProperty("static")
                    ) {
                continue;
            }
            String t = field.getType().getPresentableText();
            String fieldType = convertType(t);
            if (null == fieldType) {
                continue;
            }
            boolean isBoolean = isBoolean(t);
            String fieldName = field.getName();
            PsiCodeBlock writeMethodBody = writeMethod.getBody();
            if (null != writeMethodBody) {
                if (isBoolean) {
                    writeMethodBody.add(factory.createStatementFromText("out.write" + fieldType + "((byte) (" + fieldName + " ? 1 : 0));", null));
                } else {
                    writeMethodBody.add(factory.createStatementFromText("out.write" + fieldType + "(" + fieldName + ");", null));
                }
            }

            PsiCodeBlock constructorMethodBody = parcelConstructor.getBody();
            if (null != constructorMethodBody) {
                if (isBoolean) {
                    constructorMethodBody.add(factory.createStatementFromText(fieldName + " = 1 == in.read" + fieldType + "();", null));
                } else {
                    constructorMethodBody.add(factory.createStatementFromText(fieldName + " = in.read" + fieldType + "();", null));
                }
            }
        }

        psiClass.add(writeMethod);
        psiClass.add(parcelConstructor);

        // 生成describeContents方法
        psiClass.add(factory.createMethodFromText(METHOD_DESCRIBE_CONTENT, null));

        // 生成CREATOR
        psiClass.add(factory.createFieldFromText(FIELD_CREATOR.replace("${PsiClassName}", psiClassName), null));

    }


    /**
     * 获取当前光标位置的parent
     *
     * @param e
     * @return
     */
    private PsiClass getPsiClassFromContext(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset(); // 当前光标位置
        PsiElement elementAt = psiFile.findElementAt(offset);
        return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
    }

    private String convertType(String type) {
        switch (type) {
            case "int":
            case "Integer":
                return "Int";

            case "float":
            case "Float":
                return "Float";

            case "String":
                return "String";

            case "short":
            case "Short":
                return "Short";

            case "boolean":
            case "Boolean":
            case "byte":
            case "Byte":
                return "Byte";
            case "long":
            case "Long":
                return "Long";

            case "double":
            case "Double":
                return "Double";
        }
        return null;
    }

    private boolean isBoolean(String type) {
        return "Boolean".equals(type) || "boolean".equals(type);
    }
}

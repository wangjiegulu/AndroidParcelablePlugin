package com.wangjie.androidparcelableplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.wangjie.androidparcelableplugin.types.SupportTypeGenerator;
import com.wangjie.androidparcelableplugin.types.common.CommonTypeGenerator;
import com.wangjie.androidparcelableplugin.types.parcelable.ParcelableArrayGenerator;
import com.wangjie.androidparcelableplugin.types.parcelable.ParcelableListGenerator;
import com.wangjie.androidparcelableplugin.types.parcelable.ParcelableTypeGenerator;
import com.wangjie.androidparcelableplugin.types.primitive.PrimitiveArrayGenerator;
import com.wangjie.androidparcelableplugin.types.primitive.PrimitiveBooleanGenerator;
import com.wangjie.androidparcelableplugin.types.primitive.PrimitiveTypeGenerator;
import com.wangjie.androidparcelableplugin.types.serializable.SerializableTypeGenerator;
import com.wangjie.androidparcelableplugin.util.ParcelUtil;

import java.util.HashMap;
import java.util.List;

import static com.wangjie.androidparcelableplugin.util.XProjectUtil.getTypeByClass;
import static com.wangjie.androidparcelableplugin.util.XProjectUtil.getTypeByName;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 10/9/15.
 */
public class ParcelableGenerator extends AnAction {
    private HashMap<String, SupportTypeGenerator> generatorMapper = new HashMap<>();

    {

        CommonTypeGenerator commonTypeGenerator = new CommonTypeGenerator();

        // primitive type
        PrimitiveTypeGenerator primitiveTypeGenerator = new PrimitiveTypeGenerator();
        generatorMapper.put("boolean", new PrimitiveBooleanGenerator());
        generatorMapper.put("int", primitiveTypeGenerator);
        generatorMapper.put("float", primitiveTypeGenerator);
        generatorMapper.put("long", primitiveTypeGenerator);
        generatorMapper.put("double", primitiveTypeGenerator);
        generatorMapper.put("byte", primitiveTypeGenerator);
        generatorMapper.put("String", primitiveTypeGenerator);
        generatorMapper.put("short", primitiveTypeGenerator);

        // primitive type wrapper, common type instead!
        generatorMapper.put("Boolean", commonTypeGenerator);
        generatorMapper.put("Integer", commonTypeGenerator);
        generatorMapper.put("Float", commonTypeGenerator);
        generatorMapper.put("Long", commonTypeGenerator);
        generatorMapper.put("Double", commonTypeGenerator);
        generatorMapper.put("Byte", commonTypeGenerator);
        generatorMapper.put("Short", commonTypeGenerator);

        // primitive array
        PrimitiveArrayGenerator primitiveArrayGenerator = new PrimitiveArrayGenerator();
        generatorMapper.put("boolean[]", primitiveArrayGenerator);
        generatorMapper.put("int[]", primitiveArrayGenerator);
        generatorMapper.put("float[]", primitiveArrayGenerator);
        generatorMapper.put("long[]", primitiveArrayGenerator);
        generatorMapper.put("double[]", primitiveArrayGenerator);
        generatorMapper.put("byte[]", primitiveArrayGenerator);
        generatorMapper.put("String[]", primitiveArrayGenerator);
        generatorMapper.put("short[]", primitiveArrayGenerator);

        // type implementing Parcelable
        generatorMapper.put("Parcelable", new ParcelableTypeGenerator());

        // type implementing Parcelable
        generatorMapper.put("Serializable", new SerializableTypeGenerator());

        // list of type implementing Parcelable
        generatorMapper.put("ParcelableList", new ParcelableListGenerator());

        // Parcelable array
        generatorMapper.put("ParcelableArray", new ParcelableArrayGenerator());

        // common type
        generatorMapper.put("common", commonTypeGenerator);

    }

    private Project project;
    private PsiJavaFileImpl psiFile;
    private Editor editor;

    private PsiElementFactory factory;

    public static final String PARCELABLE_PACKAGE = "android.os";
    public static final String PARCELABLE_CLASS_SIMPLE_NAME = "Parcelable";
    public static final String PARCELABLE_CLASS_FULL_NAME = PARCELABLE_PACKAGE + "." + PARCELABLE_CLASS_SIMPLE_NAME;

    public static final String SERIALIZABLE_CLASS_FULL_NAME = "java.io.Serializable";

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

        PsiFile pf = e.getData(LangDataKeys.PSI_FILE);
        editor = e.getData(PlatformDataKeys.EDITOR);
        if (pf == null || editor == null) {
            return;
        }

        if (!(pf instanceof PsiJavaFileImpl)) {
            return;
        }

        psiFile = (PsiJavaFileImpl) pf;

        final PsiClass targetPsiClass = getPsiClassFromContext();
        if (null == targetPsiClass) {
            return;
        }

        factory = JavaPsiFacade.getElementFactory(project);

        new WriteCommandAction.Simple(project, targetPsiClass.getContainingFile()) {

            @Override
            protected void run() throws Throwable {
                generateImplementsParcelableInterface(targetPsiClass);
            }
        }.execute();


    }


    /**
     * 生成import
     */
    private void generateImports() {
        PsiImportList psiImportList = psiFile.getImportList();
        if (null == psiImportList.findOnDemandImportStatement(PARCELABLE_PACKAGE)) {
            psiImportList.add(factory.createImportStatementOnDemand(PARCELABLE_PACKAGE));
        }
    }

    /**
     * 实现某个接口
     */
    private void generateImplementsParcelableInterface(PsiClass targetPsiClass) {
        PsiClassType[] implementsListTypes = targetPsiClass.getImplementsListTypes();
        for (PsiClassType psi : implementsListTypes) {
            PsiClass resolved = psi.resolve();
            // 如果该接口已经被实现了，则不再重新实现
            if (null != resolved && (PARCELABLE_PACKAGE + PARCELABLE_CLASS_SIMPLE_NAME).equals(resolved.getQualifiedName())) {
                return;
            }
        }

        // 实现接口
        PsiJavaCodeReferenceElement referenceElement = factory.createReferenceFromText(PARCELABLE_CLASS_SIMPLE_NAME, null);
        PsiReferenceList implementsList = targetPsiClass.getImplementsList();
        if (null != implementsList) {
            implementsList.add(referenceElement);
        }

        generateImports();

        generateExtraMethods(targetPsiClass);
    }

    /**
     * 生成其它方法
     */
    private void generateExtraMethods(PsiClass psiClass) {
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
            PsiType fieldType = field.getType();

            // 取指定的generator
            SupportTypeGenerator supportTypeGenerator = generatorMapper.get(fieldType.getPresentableText());

            if (null == supportTypeGenerator) {
                if (getTypeByName(PARCELABLE_CLASS_FULL_NAME, project).isAssignableFrom(fieldType)) { // 是Parcelable类型
                    supportTypeGenerator = generatorMapper.get("Parcelable");
                } else if (1 == fieldType.getArrayDimensions() && getTypeByName(PARCELABLE_CLASS_FULL_NAME, project).isAssignableFrom(fieldType.getDeepComponentType())) { // Parcelable类型数组
                    supportTypeGenerator = generatorMapper.get("ParcelableArray");
                } else if (getTypeByClass(List.class, project).isAssignableFrom(fieldType)) { // List类型
                    if (fieldType instanceof PsiClassReferenceType) {
                        PsiType[] parametersTypes = ((PsiClassReferenceType) fieldType).getParameters();
                        if (1 == parametersTypes.length) {
                            PsiType parametersType = parametersTypes[0];
                            if (getTypeByName(PARCELABLE_CLASS_FULL_NAME, project).isAssignableFrom(parametersType)) { // 是Parcelable类型的List
                                supportTypeGenerator = generatorMapper.get("ParcelableList");
                            }
                        }
                    }
                }
            }

            // 尝试使用默认的generator
            if (null == supportTypeGenerator && ParcelUtil.isParcelable(fieldType, project)) {
                supportTypeGenerator = generatorMapper.get("common");
            }

            if(null == supportTypeGenerator){
                if (getTypeByName(SERIALIZABLE_CLASS_FULL_NAME, project).isAssignableFrom(fieldType)) { // 是Serializable类型
                    supportTypeGenerator = generatorMapper.get("Serializable");
                }
            }

            if (null == supportTypeGenerator) {
                continue;
            }

            PsiCodeBlock writeMethodBody = writeMethod.getBody();
            supportTypeGenerator.writeMethod(writeMethodBody, factory, field);

            PsiCodeBlock constructorMethodBody = parcelConstructor.getBody();
            supportTypeGenerator.readMethod(constructorMethodBody, factory, field);
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
     * @return
     */
    private PsiClass getPsiClassFromContext() {
        // 获取当前文件中的所有类
        PsiClass[] classes = ((PsiJavaFileImpl) psiFile).getClasses();
        int len = classes.length;
        // 如果当前文件中没有类,则返回null
        if (len <= 0) {
            return null;
        }
        // 如果当前文件中只有一个类,则直接返回该类
        if (1 == len) {
            return classes[0];
        }

        // 如果当前文件中有多个类,则由光标决定作用在哪个类上面
        int offset = editor.getCaretModel().getOffset(); // 当前光标位置
        PsiElement elementAt = psiFile.findElementAt(offset);
        return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
    }

}

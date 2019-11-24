package com.dream.spring.excel.processor;

import com.dream.spring.excel.*;
import com.dream.spring.excel.annotation.Column;
import com.dream.spring.excel.annotation.*;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import io.swagger.annotations.Api;
import org.apache.poi.ss.usermodel.CellType;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * @author DreamJM
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.dream.spring.excel.annotation.ExcelSupport")
@SupportedOptions("debug")
@AutoService(Processor.class)
public class ExcelExportProcessor extends AbstractProcessor {

    private static final String DEFAULT_NAME = "ExcelController";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        TypeElement springCheck = processingEnv.getElementUtils().getTypeElement("org.springframework.web.bind.annotation.RestController");
        if (springCheck == null) {
            error("Spring Web Not Found!!!");
            return false;
        }
        Map<String, ControllerModel> controllers = new HashMap<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(ExcelSupport.class)) {
            TypeElement typeElement = (TypeElement) element;
            ExcelSupport supportAnn = typeElement.getAnnotation(ExcelSupport.class);
            String controllerName = supportAnn.value();
            if ("".equals(controllerName)) {
                String fullName = typeElement.getQualifiedName().toString();
                controllerName = fullName.substring(0, fullName.lastIndexOf(".") + 1) + DEFAULT_NAME;
            }
            ControllerModel controller = controllers.computeIfAbsent(controllerName, ControllerModel::new);
            Api apiAnn = typeElement.getAnnotation(Api.class);
            if (apiAnn != null) {
                controller.addTags(apiAnn.tags());
            }
            for (Element childElem : typeElement.getEnclosedElements()) {
                ExcelExport exportAnn = childElem.getAnnotation(ExcelExport.class);
                if (exportAnn != null) {
                    controller.addRef(typeElement);
                    controller.addMethod(new ExcelMethodModel(exportAnn, (ExecutableElement) childElem, typeElement));
                }
            }
        }
        for (ControllerModel controller : controllers.values()) { //FIXME: just support one round compile only
            generateExcelController(controller, processingEnv.getFiler());
        }
        return false;
    }

    private void generateExcelController(ControllerModel controller, Filer filer) {
        try (Writer sw = filer.createSourceFile(controller.getFullName()).openWriter()) {
            log("Generating " + controller.getFullName() + " source code");
            TypeSpec.Builder typeBuilder =
                    TypeSpec.classBuilder(controller.getName()).addModifiers(Modifier.PUBLIC).addAnnotation(AnnotationSpec.builder(ClassName
                            .get(processingEnv.getElementUtils().getTypeElement("org.springframework.web.bind.annotation.RestController")))
                            .build());
            if (processingEnv.getElementUtils().getTypeElement("io.swagger.annotations.Api") != null) {
                typeBuilder.addAnnotation(AnnotationSpec.builder(Api.class).addMember("tags", "\"Excel Export API\"")
                        .addMember("description",
                                "\"" + controller.getTags().stream().reduce((s1, s2) -> s1 + "," + s2).orElse("") + "\"")
                        .build());
            }
            for (TypeElement ref : controller.getRefs()) {
                typeBuilder
                        .addField(FieldSpec.builder(TypeName.get(ref.asType()), controller.getRefName(ref)).addAnnotation(
                                AnnotationSpec.builder(ClassName.get(processingEnv.getElementUtils()
                                        .getTypeElement("org.springframework.beans.factory.annotation.Autowired"))).build()).build());
            }
            for (ExcelMethodModel method : controller.getMethods()) {
                typeBuilder.addMethod(generateMethod(method, controller));
            }
            JavaFile controllerFile = JavaFile.builder(controller.getPackageName(), typeBuilder.build()).build();
            controllerFile.writeTo(sw);
        } catch (IOException e) {
            error(e.getMessage());
        }
    }

    private MethodSpec generateMethod(ExcelMethodModel method, ControllerModel controller) {
        MethodSpec.Builder builder =
                MethodSpec.methodBuilder(method.getMethodElement().getSimpleName().toString()).addModifiers(Modifier.PUBLIC)
                        .returns(void.class);
        // Annotations
        String mappingMethod = "GetMapping";
        for (AnnotationMirror ann : method.getMethodElement().getAnnotationMirrors()) {
            if (!ann.toString().startsWith("@com.dream.spring.excel.annotation")) {
                if (!ann.toString().startsWith("@org.springframework.web.bind.annotation")) {
                    builder.addAnnotation(AnnotationSpec.get(ann));
                } else if (ann.toString().contains("PostMapping")) {
                    mappingMethod = "PostMapping";
                } else if (ann.toString().contains("PutMapping")) {
                    mappingMethod = "PutMapping";
                } else if (ann.toString().contains("DeleteMapping")) {
                    mappingMethod = "DeleteMapping";
                }
            }
        }
        builder.addAnnotation(
                AnnotationSpec.builder(
                        ClassName.get(processingEnv.getElementUtils()
                                .getTypeElement("org.springframework.web.bind.annotation." + mappingMethod)))
                        .addMember("value", "\"" + method.getAnnotation().value() + "\"").build());
        builder.addAnnotations(buildAnnotations(method.getAnnotation().annotations()));
        // Parameters
        Set<String> paramNames = new HashSet<>();
        String servletRespName = null;
        for (VariableElement variable : method.getMethodElement().getParameters()) {
            ParamIgnore paramIgnore = variable.getAnnotation(ParamIgnore.class);
            if (paramIgnore != null) {
                continue;
            }
            String paramName = variable.getSimpleName().toString();
            if (processingEnv.getTypeUtils().isSubtype(variable.asType(),
                    processingEnv.getElementUtils().getTypeElement("javax.servlet.http.HttpServletResponse").asType())) {
                servletRespName = paramName;
            }
            paramNames.add(paramName);
            ParameterSpec.Builder varBuilder = ParameterSpec.builder(TypeName.get(variable.asType()), paramName);
            for (AnnotationMirror varAnn : variable.getAnnotationMirrors()) {
                varBuilder.addAnnotation(AnnotationSpec.get(varAnn));
            }
            builder.addParameter(varBuilder.build());
        }
        if (servletRespName == null) {
            servletRespName = "response";
            int i = 1;
            while (paramNames.contains(servletRespName)) {
                servletRespName = "response" + i;
                i++;
            }
            builder.addParameter(ParameterSpec.builder(
                    TypeName.get(processingEnv.getElementUtils().getTypeElement("javax.servlet.http.HttpServletResponse").asType()),
                    servletRespName).build());
        }
        // Exceptions
        boolean ioExceptionIncluded = false;
        for (TypeMirror throwType : method.getMethodElement().getThrownTypes()) {
            if (processingEnv.getTypeUtils()
                    .isSubtype(processingEnv.getElementUtils().getTypeElement("java.io.IOException").asType(), throwType)) {
                ioExceptionIncluded = true;
            }
            builder.addException(TypeName.get(throwType));
        }
        if (!ioExceptionIncluded) {
            builder.addException(TypeName.get(processingEnv.getElementUtils().getTypeElement("java.io.IOException").asType()));
        }
        // Response Header
        if (!"".equals(method.getAnnotation().fileName())) {
            String fileName = method.getAnnotation().fileName();
            if (fileName.contains("{timestamp}")) {
                fileName = "String.format(\"" + fileName.replace("{timestamp}", "%1$d") + "\",System.currentTimeMillis())";
                builder.addStatement("$1L.setHeader(\"Content-Disposition\",\"attachment;filename=\"+ $2L +\".xlsx\")", servletRespName,
                        fileName);
            } else {
                builder.addStatement("$1L.setHeader(\"Content-Disposition\",\"attachment;filename=$2L.xlsx\")", servletRespName, fileName);
            }
        } else {
            builder.addStatement("$1L.setHeader(\"Content-Disposition\",\"attachment;filename=\"+System.currentTimeMillis()+\".xlsx\")",
                    servletRespName);
        }
        builder.addStatement("$1L.setContentType(\"application/msexcel\")", servletRespName);
        // Get Controller's result
        String params = method.getMethodElement().getParameters().stream().map(param -> {
            ParamIgnore ignore = param.getAnnotation(ParamIgnore.class);
            return ignore == null ? param.getSimpleName().toString() : ignore.value();
        }).reduce((p1, p2) -> p1 + "," + p2).orElse("");
        builder.addStatement(CodeBlock
                .of("$1L result = $2L.$3L($4L)", method.getMethodElement().getReturnType().toString(),
                        controller.getRefName(method.getRef()), method.getMethodElement().getSimpleName().toString(), params));
        // Parse Excel Sheet Element
        StringBuilder sheetAccessBuilder = new StringBuilder("result");
        DeclaredType returnType = ((DeclaredType) method.getMethodElement().getReturnType());
        DeclaredType sheetListType = findSheet(returnType, sheetAccessBuilder);
        builder.addStatement(sheetListType.toString() + " sheet = " + sheetAccessBuilder.toString());
        // Parse Excel Result Detail
        DeclaredType sheetType = (DeclaredType) sheetListType.getTypeArguments().get(0);
        parseSheet(builder, sheetType, servletRespName);
        return builder.build();
    }

    private List<AnnotationSpec> buildAnnotations(AnnotationDef[] annDefs) {
        List<AnnotationSpec> annSpecs = new ArrayList<>();
        for (AnnotationDef annDef : annDefs) {
            String annName = null;
            try {
                annDef.clazz();
            } catch (MirroredTypeException mte) {
                annName = mte.getTypeMirror().toString();
            }
            AnnotationSpec.Builder annBuilder =
                    AnnotationSpec.builder(ClassName.get(processingEnv.getElementUtils().getTypeElement(annName)));
            for (AnnotationMember member : annDef.members()) {
                String memberAnnName = null;
                try {
                    member.annotation();
                } catch (MirroredTypeException mte) {
                    memberAnnName = mte.getTypeMirror().toString();
                }
                String[] values = member.value();
                StringBuilder strValue = new StringBuilder();
                if (values.length > 1) {
                    strValue.append("{");
                }
                for (int i = 0; i < values.length; i++) {
                    String value = values[i];
                    if (!"com.dream.spring.excel.annotation.AnnotationIgnore".equals(memberAnnName)) {
                        strValue.append("@").append(memberAnnName);
                        if (!"".equals(value)) {
                            strValue.append("(").append(value).append(")");
                        }
                    } else {
                        strValue.append(value);
                    }
                    if (i < values.length - 1) {
                        strValue.append(",");
                    }
                }
                if (values.length > 1) {
                    strValue.append("}");
                }
                annBuilder.addMember(member.name(), strValue.toString());
            }
            annSpecs.add(annBuilder.build());
        }
        return annSpecs;
    }

    private DeclaredType findSheet(DeclaredType type, StringBuilder sheetAccessBuilder) {
        Element element = type.asElement();
        for (Element childElem : element.getEnclosedElements()) {
            if (childElem.getAnnotation(SheetWrapper.class) != null) {
                sheetAccessBuilder.append(".").append("get").append(captureName(childElem.getSimpleName().toString())).append("()");
                DeclaredType wrapperType = (DeclaredType) processingEnv.getTypeUtils().asMemberOf(type, childElem);
                if (isCollection(wrapperType) || TypeKind.ARRAY.equals(wrapperType.getKind())) { // Find Collection
                    return wrapperType;
                } else { // Continue look up
                    return findSheet(wrapperType, sheetAccessBuilder);
                }
            }
        }
        error("Sheet Annotation NOT FOUND!!!!");
        throw new RuntimeException("Sheet Annotation NOT FOUND!!!!");
    }

    private void parseSheet(MethodSpec.Builder builder, DeclaredType sheetType, String respParamName) {
        Element sheetElem = sheetType.asElement();
        Sheet sheetAnn = sheetElem.getAnnotation(Sheet.class);
        if (sheetAnn == null) {
            error("@Sheet Annotation Missing on " + sheetType.toString());
            throw new RuntimeException("@Sheet Annotation Missing");
        }
        String i18nMethod = parseI18nMethod(sheetAnn.i18n());
        // SheetStyle
        builder.addStatement(
                CodeBlock.of("$1L.Builder styleBuilder = $1L.builder($2L)", SheetStyle.class.getName(),
                        parseI18nParam(i18nMethod, sheetAnn.value(), sheetAnn.i18nSupport())));
        builder.addStatement(CodeBlock.of("styleBuilder.setDefaultWidth($1L)", sheetAnn.defaultWidth()));
        builder.addStatement(CodeBlock.of("styleBuilder.setHeaderHeight($1L)", sheetAnn.headerHeight()));
        builder.addStatement(CodeBlock.of("styleBuilder.setCategoryHeight($1L)", sheetAnn.categoryHeight()));
        builder.addStatement(CodeBlock.of("styleBuilder.setContentRowHeight($1L)", sheetAnn.contentRowHeight()));
        builder.addStatement(CodeBlock.of("styleBuilder.setDefaultHeaderStyle($1L)", parseStyle(sheetAnn.defaultHeaderStyle())));
        builder.addStatement(CodeBlock.of("styleBuilder.setDefaultCategoryStyle($1L)", parseStyle(sheetAnn.defaultCategoryStyle())));
        builder.addStatement(CodeBlock.of("styleBuilder.setDefaultStyle($1L)", parseStyle(sheetAnn.defaultStyle())));
        builder.addStatement(CodeBlock.of("styleBuilder.setOffset($1L, $2L)", sheetAnn.xOffset(), sheetAnn.yOffset()));
        builder.addStatement(CodeBlock.of("styleBuilder.setFreezeHeader($1L)", sheetAnn.freezeHeader()));
        // Headers
        int offset = sheetAnn.indexIncluded() ? 1 : 0;
        builder.addStatement(
                CodeBlock.of("$1L[] columns = new $1L[$2L]", com.dream.spring.excel.Column.class.getName(),
                        sheetAnn.headers().length + offset));
        if (sheetAnn.indexIncluded()) {
            builder.addStatement(
                    CodeBlock.of("columns[0]=$1L.builder($2L).setWidth(6).build()", com.dream.spring.excel.Column.class.getName(),
                            parseI18nParam(i18nMethod, "No.", true)));
        }
        Column[] columns = new Column[sheetAnn.headers().length];
        for (int i = 0; i < sheetAnn.headers().length; i++) {
            Header header = sheetAnn.headers()[i];
            Column column = findFieldAnnotation(sheetType, header.field());
            columns[i] = column;
            String headTitle = parseI18nParam(i18nMethod, header.value(), header.i18nSupport());
            if (header.note().necessary()) {
                headTitle = "\"*\"+" + headTitle;
            }
            if (!"".equals(header.note().content())) {
                if (header.note().wrapLine()) {
                    headTitle += "+\"\\n\"";
                }
                String note = parseI18nParam(i18nMethod, header.note().content(), header.note().i18nSupported());
                if (header.note().brace()) {
                    note = "\"(\"+" + note + "+\")\"";
                }
                headTitle += "+" + note;
            }
            builder.addStatement(CodeBlock
                    .of("columns[$1L] = $2L.builder($3L).setWidth($4L).setHeaderStyle($5L).setStyle($6L).build()", i + offset,
                            com.dream.spring.excel.Column.class.getName(), headTitle, header.width(),
                            parseStyle(header.style()), column == null ? "null" : parseStyle(column.style())));
        }
        // Categories
        builder.addStatement(CodeBlock.of("$1L[] categories = new $1L[$2L]", HeaderCategory.class.getName(), sheetAnn.categories().length));
        for (int i = 0; i < sheetAnn.categories().length; i++) {
            Category category = sheetAnn.categories()[i];
            builder.addStatement(CodeBlock
                    .of("categories[$1L] = $2L.builder($3L,$4L,$5L).setStyle($6L).build()", i, HeaderCategory.class.getName(),
                            parseI18nParam(i18nMethod, category.value(), category.i18nSupport()), category.start() + offset,
                            category.end() + offset, parseStyle(category.style())));
        }
        // Data
        builder.addStatement(CodeBlock
                .of("java.util.List<java.util.Map<Integer, $1L>> dataset = new java.util.ArrayList<>()", CellData.class.getName()));
        builder.addStatement(
                CodeBlock.of("java.util.Map<String,$1L> cellStyleCache=new java.util.HashMap<>()", CustomStyle.class.getName()));
        builder.addCode(CodeBlock.of("int i = 1;\nfor($1L line : sheet) {\n", sheetType));
        builder.addStatement(CodeBlock.of("java.util.Map<Integer,$1L> item = new java.util.HashMap<>()", CellData.class.getName()));
        if (sheetAnn.indexIncluded()) {
            builder.addStatement(CodeBlock.of("item.put(0,$1L.builder(String.valueOf(i)).build())", CellData.class.getName()));
        }
        for (int i = 0; i < sheetAnn.headers().length; i++) {
            Header header = sheetAnn.headers()[i];
            Column column = columns[i];
            String commonCode;
            if (column != null && column.i18nSupport()) {
                commonCode = "item.put($1L,$2L.builder(" + String.format(i18nMethod, "$3L.$4L(line$5L)") + ")";
            } else {
                commonCode = "item.put($1L,$2L.builder($3L.$4L(line$5L))";
            }
            if (column == null) {
                builder.addStatement(
                        CodeBlock.of(commonCode + ".build())", i + offset, CellData.class.getName(), StringUtils.class.getName(), "valueOf",
                                parseFieldGet(header.field())));
            } else {
                String className = StringUtils.class.getName();
                String method = "valueOf";
                try {
                    column.converter().clazz();
                } catch (MirroredTypeException mte) {
                    className = mte.getTypeMirror().toString();
                    method = column.converter().method();
                }
                builder.addStatement(CodeBlock.of("$1L cellStyle$2L=null", CustomStyle.class.getName(), i));
                int index = 0;
                for (CellItemStyle itemStyle : column.cellStyles()) {
                    builder.addCode(CodeBlock.of("$1Lif($2L) {\n", index > 0 ? "else " : "",
                            itemStyle.condition().replace("{value}", "line" + parseFieldGet(header.field()))));
                    builder.addStatement("cellStyle$1L=cellStyleCache.computeIfAbsent(\"$1L#$2L\", key -> $3L)", i, itemStyle.condition(),
                            parseStyle(itemStyle.style()));
                    builder.addCode(CodeBlock.of("}\n"));
                    index++;
                }
                builder.addStatement(CodeBlock.of(commonCode + ".setType($6L).setStyle(cellStyle$7L).build())", i + offset,
                        CellData.class.getName(), className, method, parseFieldGet(header.field()),
                        CellType.class.getName() + "." + column.type().name(), i));
            }
        }
        builder.addStatement("dataset.add(item)");
        builder.addCode(CodeBlock.of("i++;\n}\n"));
        // Compose Excel Exporter
        builder.addStatement(CodeBlock.of("new $1L(styleBuilder.build(),columns,categories,dataset,$2L.getOutputStream()).exportExcel()",
                ExportExcel.class.getName(), respParamName));
    }

    private static String parseFieldGet(String fieldStr) {
        String[] fields = fieldStr.split("[.]");
        StringBuilder sb = new StringBuilder();
        for (String field : fields) {
            sb.append(".get").append(captureName(field)).append("()");
        }
        return sb.toString();
    }

    private Column findFieldAnnotation(TypeMirror type, String fieldStr) {
        String[] fields = fieldStr.split("[.]");
        if (fields.length == 0) {
            return null;
        }
        Element element = null;
        for (String field : fields) {
            element = findChild(type, field);
            type = processingEnv.getTypeUtils().asMemberOf((DeclaredType) type, element);
        }
        return element.getAnnotation(Column.class);
    }

    private Element findChild(TypeMirror type, String field) {
        List<Element> elements = new ArrayList<>();
        composeAllElements(processingEnv.getTypeUtils().asElement(type), elements);
        for (Element element : elements) {
            for (Element childElem : element.getEnclosedElements()) {
                if (ElementKind.FIELD.equals(childElem.getKind()) && childElem.getSimpleName().toString().equals(field)) {
                    return childElem;
                }
            }
        }
        error("Field " + field + " not found in " + type.toString());
        throw new RuntimeException("Field " + field + " not found!");
    }

    private void composeAllElements(Element element, List<Element> elements) {
        elements.add(element);
        for (TypeMirror type : processingEnv.getTypeUtils().directSupertypes(element.asType())) {
            Element parentElem = ((DeclaredType) type).asElement();
            if (ElementKind.CLASS.equals(parentElem.getKind()) && !"java.lang.Object".equals(parentElem.getSimpleName().toString())) {
                composeAllElements(parentElem, elements);
            }
        }
    }

    private boolean isCollection(TypeMirror type) {
        TypeElement collectionType = processingEnv.getElementUtils().getTypeElement("java.util.Collection");
        WildcardType WILDCARD_TYPE_NULL = processingEnv.getTypeUtils().getWildcardType(null, null);
        DeclaredType parentType = processingEnv.getTypeUtils().getDeclaredType(collectionType, WILDCARD_TYPE_NULL);
        return processingEnv.getTypeUtils().isAssignable(type, parentType);
    }

    private static String captureName(String name) {
        char[] cs = name.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    private String parseI18nMethod(I18n i18nAnn) {
        String className = "String";
        try {
            i18nAnn.clazz();
        } catch (MirroredTypeException mte) {
            className = mte.getTypeMirror().toString();
        }
        return className + "." + i18nAnn.method() + "(%1$s)";
    }

    private String parseI18nParam(String i18nMethod, String value, boolean supported) {
        String quoteValue = "\"" + value + "\"";
        return supported ? String.format(i18nMethod, quoteValue) : quoteValue;
    }

    private String parseStyle(CellStyle style) {
        if (style.useDefault()) {
            return "null";
        }
        return String.format(
                "%1$s.builder().setBg((short)%2$d).setFontColor((short)%3$d).setFontName(\"%4$s\").setFontSize(%5$d).setHorizontalAlignment(%6$s).setVerticalAlignment(%7$s).build()",
                CustomStyle.class.getName(), style.backgroundColor().getIndex(), style.fontColor().getIndex(), style.fontName(),
                style.fontSize(), style.horizontalAlignment().getClass().getName() + "." + style.horizontalAlignment().name(),
                style.verticalAlignment().getClass().getName() + "." + style.verticalAlignment().name());
    }

    private void log(String msg) {
        if (processingEnv.getOptions().containsKey("debug")) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
        }
    }

    private void error(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg);
    }
}

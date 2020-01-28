/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dream.spring.excel.processor;

import com.dream.spring.excel.*;
import com.dream.spring.excel.annotation.Column;
import com.dream.spring.excel.annotation.*;
import com.dream.spring.excel.bean.ExcelExportConfig;
import com.dream.spring.excel.bean.ExcelI18n;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import org.apache.poi.ss.usermodel.CellType;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.tools.Diagnostic;
import java.io.*;
import java.util.*;

/**
 * @author DreamJM
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.dream.spring.excel.annotation.ExcelSupport")
@SupportedOptions(ExcelExportProcessor.OPTION_DEBUG)
@AutoService(Processor.class)
public class ExcelExportProcessor extends AbstractProcessor {

    private static final String DEFAULT_NAME = "ExcelController";

    static final String OPTION_DEBUG = "debug";

    private static final String REPLACE_CACHE_FILE_TIMESTAMP = "{timestamp}";

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
            // Collect @ExcelSupport component information
            ExcelSupport supportAnn = typeElement.getAnnotation(ExcelSupport.class);
            String controllerName = supportAnn.value();
            if ("".equals(controllerName)) {
                String fullName = typeElement.getQualifiedName().toString();
                controllerName = fullName.substring(0, fullName.lastIndexOf(".") + 1) + DEFAULT_NAME;
            }
            ControllerModel controller = controllers.computeIfAbsent(controllerName, ControllerModel::new);
            // Collect swagger @Api tags to generated swagger document for generated Excel Api
            for (AnnotationMirror ann : typeElement.getAnnotationMirrors()) {
                if ("io.swagger.annotations.Api".equals(ann.getAnnotationType().toString())) {
                    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : ann.getElementValues().entrySet()) {
                        if ("tags()".equals(entry.getKey().toString())) {
                            String values = entry.getValue().toString();
                            if (values.startsWith("{")) {
                                values = values.substring(1);
                            }
                            if (values.endsWith("}")) {
                                values = values.substring(0, values.length() - 1);
                            }
                            values = values.replaceAll("\"", "");
                            controller.addTags(values.split(","));
                        }
                    }
                }
            }
            // Collect excel data api methods
            for (Element childElem : typeElement.getEnclosedElements()) {
                ExcelExport exportAnn = childElem.getAnnotation(ExcelExport.class);
                if (exportAnn != null) {
                    controller.addRef(typeElement);
                    controller.addMethod(new ExcelMethodModel(exportAnn, (ExecutableElement) childElem, typeElement));
                }
            }
        }
        for (ControllerModel controller : controllers.values()) {
            //FIXME: just support one round compilation only
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
            // Adds swagger @Api annotation if supported
            TypeElement apiElement = processingEnv.getElementUtils().getTypeElement("io.swagger.annotations.Api");
            if (apiElement != null) {
                typeBuilder.addAnnotation(
                        AnnotationSpec.builder(ClassName.get(apiElement)).addMember("tags", "\"Excel Export API\"")
                                .addMember("description",
                                        "\"" + controller.getTags().stream().reduce((s1, s2) -> s1 + "," + s2).orElse("") + "\"")
                                .build());
            }
            // Makes data components refs autowired
            TypeElement autowiredElem =
                    processingEnv.getElementUtils().getTypeElement("org.springframework.beans.factory.annotation.Autowired");
            for (TypeElement ref : controller.getRefs()) {
                typeBuilder
                        .addField(FieldSpec.builder(TypeName.get(ref.asType()), controller.getRefName(ref)).addAnnotation(
                                AnnotationSpec.builder(ClassName.get(autowiredElem)).build()).build());
            }
            // Adding ObjectProvider of ExcelExportConfig
            TypeElement opElem = processingEnv.getElementUtils().getTypeElement("org.springframework.beans.factory.ObjectProvider");
            TypeElement configElem = processingEnv.getElementUtils().getTypeElement("com.dream.spring.excel.bean.ExcelExportConfig");
            typeBuilder.addField(FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(opElem), TypeName.get(configElem.asType())),
                    "configProvider").addAnnotation(AnnotationSpec.builder(ClassName.get(autowiredElem)).build()).build());
            // Adding ObjectProvider of ExcelI18n
            TypeElement i18nElem = processingEnv.getElementUtils().getTypeElement("com.dream.spring.excel.bean.ExcelI18n");
            typeBuilder.addField(FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(opElem), TypeName.get(i18nElem.asType())),
                    "i18nProvider").addAnnotation(AnnotationSpec.builder(ClassName.get(autowiredElem)).build()).build());
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
        // Method Annotations
        String mappingMethod = "GetMapping";
        for (AnnotationMirror ann : method.getMethodElement().getAnnotationMirrors()) {
            if (!ann.toString().startsWith("@com.dream.spring.excel.annotation")) {
                if (!ann.toString().startsWith("@org.springframework.web.bind.annotation")) {
                    // Add annotations except for spring web annotation
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
            //If HttpServletResponse isn't in input arguments, then add it.
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
            if (fileName.contains(REPLACE_CACHE_FILE_TIMESTAMP)) {
                fileName = "String.format(\"" + fileName.replace(REPLACE_CACHE_FILE_TIMESTAMP, "%1$d") + "\",System.currentTimeMillis())";
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
        // Cache
        if (method.getAnnotation().caches().length > 0) {
            int index = 0;
            for (Cacheable conf : method.getAnnotation().caches()) {
                builder.addCode("$1Lif($2L) {\n", index == 0 ? "" : "else ", conf.condition());
                builder.addStatement("$1T cacheFile = $2T.newestFile(\"$3L\")", File.class, FileUtils.class, conf.cacheDir());
                builder.addCode("if(cacheFile != null) {\n");
                builder.addStatement("long timestamp = Long.parseLong(cacheFile.getName())");
                builder.addCode("if(!$1L.$2L) {\n", controller.getRefName(method.getRef()), conf.checkUpdateMethod());
                builder.addCode("try($1T fis=new $1T(cacheFile)) {\n" +
                        "$2T output = $3L.getOutputStream();\nbyte[] b=new byte[1024];\nint length;\n" +
                        "while((length=fis.read(b))>0){\noutput.write(b,0,length);\n}\n" +
                        "output.flush();\n}\n", FileInputStream.class, OutputStream.class, servletRespName);
                builder.addCode("return;}\n");
                builder.addCode("}\n");
                builder.addCode("}\n");
                index++;
            }
        }
        // Get Controller's result
        String params = method.getMethodElement().getParameters().stream().map(param -> {
            ParamIgnore ignore = param.getAnnotation(ParamIgnore.class);
            return ignore == null ? param.getSimpleName().toString() : ignore.value();
        }).reduce((p1, p2) -> p1 + "," + p2).orElse("");
        builder.addStatement("$1L result = $2L.$3L($4L)", method.getMethodElement().getReturnType().toString(),
                controller.getRefName(method.getRef()), method.getMethodElement().getSimpleName().toString(), params);
        // Parse Excel Sheet Element
        StringBuilder sheetAccessBuilder = new StringBuilder("result");
        DeclaredType returnType = ((DeclaredType) method.getMethodElement().getReturnType());
        DeclaredType sheetListType = findSheet(returnType, sheetAccessBuilder);
        builder.addStatement(sheetListType.toString() + " sheet = " + sheetAccessBuilder.toString());
        // Parse Excel Result Detail
        DeclaredType sheetType = (DeclaredType) sheetListType.getTypeArguments().get(0);
        parseSheet(builder, sheetType);
        // Compose Excel Exporter
        if (method.getAnnotation().caches().length > 0) { //
            int index = 0;
            for (Cacheable conf : method.getAnnotation().caches()) {
                builder.addCode("$1Lif($2L) {\n", index == 0 ? "" : "else ", conf.condition());
                builder.addStatement("long maxTimestamp = $1L.$2L", controller.getRefName(method.getRef()), conf.timestampMethod());
                builder.addStatement("$1T cacheFile = $2T.file(\"$3L\",maxTimestamp)", File.class, FileUtils.class, conf.cacheDir());
                builder.addCode("try($1T fos=new $1T(cacheFile)) {\n" +
                                "new $2T(styleBuilder.build(),columns,categories,dataset,fos).exportExcel();\n}\n",
                        FileOutputStream.class, ExportExcel.class);
                builder.addCode("try($1T fis=new $1T(cacheFile)) {\n" +
                        "$2T output = $3L.getOutputStream();\nbyte[] b=new byte[1024];\nint length;\n" +
                        "while((length=fis.read(b))>0){\noutput.write(b,0,length);\n}\n" +
                        "output.flush();\n}\n", FileInputStream.class, OutputStream.class, servletRespName);
                builder.addCode("\n} else {\n");
                builder.addStatement("new $1T(styleBuilder.build(),columns,categories,dataset,$2L.getOutputStream()).exportExcel()",
                        ExportExcel.class, servletRespName);
                builder.addCode("\n}");
                index++;
            }
        } else {
            builder.addStatement("new $1T(styleBuilder.build(),columns,categories,dataset,$2L.getOutputStream()).exportExcel()",
                    ExportExcel.class, servletRespName);
        }
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
        if (isCollection(type) || TypeKind.ARRAY.equals(type.getKind())) {
            // Find Collection
            return type;
        } else {
            // Continue look up
            Element element = type.asElement();
            for (Element childElem : element.getEnclosedElements()) {
                if (childElem.getAnnotation(SheetWrapper.class) != null) {
                    sheetAccessBuilder.append(".").append("get").append(capitalizeName(childElem.getSimpleName().toString())).append("()");
                    return findSheet((DeclaredType) processingEnv.getTypeUtils().asMemberOf(type, childElem), sheetAccessBuilder);
                }
            }
        }
        error("Sheet Annotation NOT FOUND!!!!");
        throw new RuntimeException("Sheet Annotation NOT FOUND!!!!");
    }

    private void parseSheet(MethodSpec.Builder builder, DeclaredType sheetType) {
        Element sheetElem = sheetType.asElement();
        Sheet sheetAnn = sheetElem.getAnnotation(Sheet.class);
        if (sheetAnn == null) {
            error("@Sheet Annotation Missing on " + sheetType.toString());
            throw new RuntimeException("@Sheet Annotation Missing");
        }
        if ("".equals(sheetAnn.i18n().method())) {
            builder.addStatement("$T i18nMethod = i18nProvider.getIfAvailable()", ExcelI18n.class);
        } else {
            builder.addStatement("$T i18nMethod = " + generateI18nMethod(sheetAnn.i18n()), ExcelI18n.class);
        }

        String i18nMethod = "i18nMethod.i18n(%1$s)";
        // Global Config
        builder.addStatement("$T config = configProvider.getIfAvailable()", ExcelExportConfig.class);

        // SheetStyle
        builder.addStatement("$1T.Builder styleBuilder = $1T.builder($2L)", SheetStyle.class,
                parseI18nParam(i18nMethod, sheetAnn.value(), sheetAnn.i18nSupport()));
        builder.addStatement("styleBuilder.setDefaultWidth($1L)", getDefaultConfig(sheetAnn.defaultWidth(), "getDefaultWidth()"));
        builder.addStatement("styleBuilder.setHeaderHeight($1L)", getDefaultConfig(sheetAnn.headerHeight(), "getHeaderHeight()"));
        builder.addStatement("styleBuilder.setCategoryHeight($1L)", getDefaultConfig(sheetAnn.categoryHeight(), "getCategoryHeight()"));
        builder.addStatement("styleBuilder.setContentRowHeight($1L)",
                getDefaultConfig(sheetAnn.contentRowHeight(), "getContentRowHeight()"));
        builder.addStatement("styleBuilder.setOffset($1L, $2L)", getDefaultConfig(sheetAnn.xOffset(), "getColumnOffset()"),
                getDefaultConfig(sheetAnn.yOffset(), "getRowOffset()"));
        if (sheetAnn.defaultHeaderStyle().useDefault()) {
            builder.addCode("if (config != null && config.getDefaultHeaderStyle() != null) {\n" +
                    "styleBuilder.setDefaultHeaderStyle($1L);\n}\n", parseConfigStyle("getDefaultHeaderStyle()"));
        } else {
            builder.addStatement("styleBuilder.setDefaultHeaderStyle($1L)", parseStyle(sheetAnn.defaultHeaderStyle()));
        }
        if (sheetAnn.defaultCategoryStyle().useDefault()) {
            builder.addCode("if (config != null && config.getDefaultCategoryStyle() != null) {\n" +
                    "styleBuilder.setDefaultCategoryStyle($1L);\n}\n", parseConfigStyle("getDefaultCategoryStyle()"));
        } else {
            builder.addStatement("styleBuilder.setDefaultCategoryStyle($1L)", parseStyle(sheetAnn.defaultCategoryStyle()));
        }
        if (sheetAnn.defaultStyle().useDefault()) {
            builder.addCode("if (config != null && config.getDefaultStyle() != null) {\n" +
                    "styleBuilder.setDefaultStyle($1L);\n}\n", parseConfigStyle("getDefaultStyle()"));
        } else {
            builder.addStatement("styleBuilder.setDefaultStyle($1L)", parseStyle(sheetAnn.defaultStyle()));
        }
        if (sheetAnn.freezeHeader().length > 0) {
            builder.addStatement("styleBuilder.setFreezeHeader($1L)", sheetAnn.freezeHeader()[0]);
        } else {
            builder.addCode("if (config != null) {\n styleBuilder.setFreezeHeader(config.isFreezeHeader());\n}\n");
        }
        // Headers
        int offset = sheetAnn.indexIncluded() ? 1 : 0;
        builder.addStatement("$1T[] columns = new $1T[$2L]", com.dream.spring.excel.Column.class, sheetAnn.headers().length + offset);
        if (sheetAnn.indexIncluded()) {
            builder.addStatement("columns[0]=$1T.builder($2L).setWidth(6).build()", com.dream.spring.excel.Column.class,
                    parseI18nParam(i18nMethod, "No.", true));
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
                String note = parseI18nParam(i18nMethod, header.note().content(), header.note().i18nSupport());
                if (header.note().brace()) {
                    note = "\"(\"+" + note + "+\")\"";
                }
                headTitle += "+" + note;
            }
            builder.addStatement("columns[$1L] = $2T.builder($3L).setWidth($4L).setHeaderStyle($5L).setStyle($6L).build()", i + offset,
                    com.dream.spring.excel.Column.class, headTitle, header.width(),
                    parseStyle(header.style()), column == null ? "null" : parseStyle(column.style()));
        }
        // Categories
        builder.addStatement("$1T[] categories = new $1T[$2L]", HeaderCategory.class, sheetAnn.categories().length);
        for (int i = 0; i < sheetAnn.categories().length; i++) {
            Category category = sheetAnn.categories()[i];
            builder.addStatement("categories[$1L] = $2T.builder($3L,$4L,$5L).setStyle($6L).build()", i, HeaderCategory.class,
                    parseI18nParam(i18nMethod, category.value(), category.i18nSupport()), category.start() + offset,
                    category.end() + offset, parseStyle(category.style()));
        }
        // Data
        builder.addStatement("$1T<$2T<Integer, $3T>> dataset = new $4T<>()", List.class, Map.class, CellData.class, ArrayList.class);
        builder.addStatement("$1T<String,$2T> cellStyleCache=new $3T<>()", Map.class, CustomStyle.class, HashMap.class);
        builder.addCode("int i = 1;\nfor($1L line : sheet) {\n", sheetType);
        builder.addStatement("$1T<Integer,$2T> item = new $3T<>()", Map.class, CellData.class, HashMap.class);
        if (sheetAnn.indexIncluded()) {
            builder.addStatement("item.put(0,$1T.builder(String.valueOf(i)).build())", CellData.class);
        }
        for (int i = 0; i < sheetAnn.headers().length; i++) {
            Header header = sheetAnn.headers()[i];
            Column column = columns[i];
            String commonCode;
            if (column != null && column.i18nSupport()) {
                commonCode = "item.put($1L,$2T.builder(" + String.format(i18nMethod, "$3T.$4L(line$5L)") + ")";
            } else {
                commonCode = "item.put($1L,$2T.builder($3L.$4L(line$5L))";
            }
            if (column == null) {
                builder.addStatement(commonCode + ".build())", i + offset, CellData.class, StringUtils.class.getName(), "valueOf",
                        parseFieldGet(header.field()));
            } else {
                String className = StringUtils.class.getName();
                String method = "valueOf";
                try {
                    column.converter().clazz();
                } catch (MirroredTypeException mte) {
                    className = mte.getTypeMirror().toString();
                    method = column.converter().method();
                }
                builder.addStatement("$1T cellStyle$2L=null", CustomStyle.class, i);
                int index = 0;
                for (CellItemStyle itemStyle : column.cellStyles()) {
                    builder.addCode("$1Lif($2L) {\n", index > 0 ? "else " : "",
                            itemStyle.condition().replace("{value}", "line" + parseFieldGet(header.field())));
                    builder.addStatement("cellStyle$1L=cellStyleCache.computeIfAbsent(\"$1L#$2L\", key -> $3L)", i, itemStyle.condition(),
                            parseStyle(itemStyle.style()));
                    builder.addCode("}\n");
                    index++;
                }
                builder.addStatement(commonCode + ".setType($6L).setStyle(cellStyle$7L).build())", i + offset,
                        CellData.class, className, method, parseFieldGet(header.field()),
                        CellType.class.getName() + "." + column.type().name(), i);
            }
        }
        builder.addStatement("dataset.add(item)");
        builder.addCode("i++;\n}\n");
    }

    private static String parseFieldGet(String fieldStr) {
        String[] fields = fieldStr.split("[.]");
        StringBuilder sb = new StringBuilder();
        for (String field : fields) {
            if (field.startsWith("is") || field.startsWith("get")) {
                sb.append(".").append(field);
            } else {
                sb.append(".get").append(capitalizeName(field));
            }
            sb.append("()");
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
        WildcardType wildcardTypeNull = processingEnv.getTypeUtils().getWildcardType(null, null);
        DeclaredType parentType = processingEnv.getTypeUtils().getDeclaredType(collectionType, wildcardTypeNull);
        return processingEnv.getTypeUtils().isAssignable(type, parentType);
    }

    private static String capitalizeName(String name) {
        char[] cs = name.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    private String generateI18nMethod(I18n i18nAnn) {
        String className = "String";
        try {
            i18nAnn.clazz();
        } catch (MirroredTypeException mte) {
            className = mte.getTypeMirror().toString();
        }
        return "code -> " + className + "." + i18nAnn.method() + "(code)";
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
                CustomStyle.class.getSimpleName(), style.backgroundColor().getIndex(), style.fontColor().getIndex(), style.fontName(),
                style.fontSize(), style.horizontalAlignment().getClass().getName() + "." + style.horizontalAlignment().name(),
                style.verticalAlignment().getClass().getName() + "." + style.verticalAlignment().name());
    }

    private String parseConfigStyle(String styleGetter) {
        return String.format(
                "config == null ? null : %1$s.builder().setBg((short)(config.%2$s.getBgColor().getIndex()))" +
                        ".setFontColor((short)(config.%2$s.getFontColor().getIndex()))" +
                        ".setFontName(config.%2$s.getFontName()).setFontSize(config.%2$s.getFontSize())" +
                        ".setHorizontalAlignment(config.%2$s.getHorizontalAlignment())" +
                        ".setVerticalAlignment(config.%2$s.getVerticalAlignment()).build()",
                CustomStyle.class.getSimpleName(), styleGetter);
    }

    private String getDefaultConfig(int value, String defaultGetter) {
        return value > 0 ? String.valueOf(value) : "config == null ? 0 : config." + defaultGetter;
    }

    private void log(String msg) {
        if (processingEnv.getOptions().containsKey(OPTION_DEBUG)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
        }
    }

    private void error(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg);
    }
}

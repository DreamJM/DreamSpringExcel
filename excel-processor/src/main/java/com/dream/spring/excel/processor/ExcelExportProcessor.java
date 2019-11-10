package com.dream.spring.excel.processor;

import com.dream.spring.excel.*;
import com.dream.spring.excel.annotation.*;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import io.swagger.annotations.Api;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
                    TypeSpec.classBuilder(controller.getName()).addModifiers(Modifier.PUBLIC).addAnnotation(RestController.class)
                            .addAnnotation(AnnotationSpec.builder(Api.class).addMember("tags", "\"Excel Export API\"")
                                    .addMember("description",
                                            "\"" + controller.getTags().stream().reduce((s1, s2) -> s1 + "," + s2).orElse("") + "\"")
                                    .build());
            for (TypeElement ref : controller.getRefs()) {
                typeBuilder
                        .addField(FieldSpec.builder(TypeName.get(ref.asType()), controller.getRefName(ref)).addAnnotation(
                                Autowired.class).build());
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
        for (AnnotationMirror ann : method.getMethodElement().getAnnotationMirrors()) {
            if (!ann.toString().startsWith("@com.dream.spring.excel.annotation") && !ann.toString()
                    .startsWith("@org.springframework.web.bind.annotation")) {
                builder.addAnnotation(AnnotationSpec.get(ann));
            }
        }
        builder.addAnnotation(
                AnnotationSpec.builder(GetMapping.class).addMember("value", "\"" + method.getAnnotation().value() + "\"").build());
        // Parameters
        Set<String> paramNames = new HashSet<>();
        String servletRespName = null;
        for (VariableElement variable : method.getMethodElement().getParameters()) {
            ParamIgnore paramIgnore = variable.getAnnotation(ParamIgnore.class);
            if (paramIgnore != null) {
                continue;
            }
            String paramName = variable.getSimpleName().toString();
            if (processingEnv.getTypeUtils()
                    .isSubtype(variable.asType(),
                            processingEnv.getElementUtils().getTypeElement("javax.servlet.ServletResponse").asType())) {
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
        // SheetStyle
        builder.addStatement(
                CodeBlock.of("$1L.Builder styleBuilder = $1L.Builder.builder(\"$2L\")", SheetStyle.class.getName(), sheetAnn.value()));
        builder.addStatement(CodeBlock.of("styleBuilder.setDefaultWidth($1L)", sheetAnn.defaultWidth()));
        builder.addStatement(CodeBlock.of("styleBuilder.setDefaultHeaderHeight($1L)", sheetAnn.defaultHeaderHeight()));
        builder.addStatement(CodeBlock.of("styleBuilder.setContentRowHeight($1L)", sheetAnn.contentRowHeight()));
        // Headers
        builder.addStatement(CodeBlock.of("$1L[] headers = new $1L[$2L]", ColumnHeader.class.getName(), sheetAnn.headers().length));
        for (int i = 0; i < sheetAnn.headers().length; i++) {
            Header header = sheetAnn.headers()[i];
            builder.addStatement(CodeBlock
                    .of("headers[$1L] = $2L.Builder.builder(\"$3L\").setWidth($4L).setStyle($5L).build()", i, ColumnHeader.class.getName(),
                            header.value(), header.width(), parseStyle(header.style())));
        }
        // Categories
        builder.addStatement(CodeBlock.of("$1L[] categories = new $1L[$2L]", HeaderCategory.class.getName(), sheetAnn.categories().length));
        for (int i = 0; i < sheetAnn.categories().length; i++) {
            Category category = sheetAnn.categories()[i];
            builder.addStatement(CodeBlock
                    .of("categories[$1L] = $2L.Builder.builder(\"$3L\",$4L,$5L).setStyle($5L).build()", i, category.value(),
                            category.start(), category.end(), parseStyle(category.style())));
        }
        // Data
        builder.addStatement(CodeBlock
                .of("java.util.List<java.util.Map<Integer, $1L>> dataset = new java.util.ArrayList<>()", CellData.class.getName()));
        builder.addCode(CodeBlock.of("for($1L line : sheet) {\n", sheetType));
        builder.addStatement(CodeBlock.of("java.util.Map<Integer,$1L> item = new java.util.HashMap<>()", CellData.class.getName()));
        for (int i = 0; i < sheetAnn.headers().length; i++) {
            Header header = sheetAnn.headers()[i];
            Cell cellAnn = findFieldAnnotation(sheetType, header.field());
            String commonCode = "item.put($1L,$2L.Builder.builder($3L.$4L(line$5L))";
            if (cellAnn == null) {
                builder.addStatement(
                        CodeBlock.of(commonCode + ".build())", i, CellData.class.getName(), String.class.getName(), "valueOf",
                                parseFieldGet(header.field())));
            } else {
                String className = "String";
                try {
                    cellAnn.converter().clazz();
                } catch (MirroredTypeException mte) {
                    className = mte.getTypeMirror().toString();
                }
                builder.addStatement(
                        CodeBlock.of(commonCode + ".setType($6L).setStyle($7L).build())", i, CellData.class.getName(), className,
                                cellAnn.converter().method(), parseFieldGet(header.field()),
                                CellType.class.getName() + "." + cellAnn.type().name(),
                                parseStyle(cellAnn.style())));
            }
        }
        builder.addStatement("dataset.add(item)");
        builder.addCode(CodeBlock.of("}\n"));
        // Compose Excel Exporter
        builder.addStatement(CodeBlock.of("new $1L(styleBuilder.build(),headers,categories,dataset,$2L.getOutputStream()).exportExcel()",
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

    private Cell findFieldAnnotation(DeclaredType type, String fieldStr) {
        String[] fields = fieldStr.split("[.]");
        if (fields.length == 0) {
            return null;
        }
        Element element = null;
        for (String field : fields) {
            element = findChild(type, field);
            type = (DeclaredType) processingEnv.getTypeUtils().asMemberOf(type, element);
        }
        return element.getAnnotation(Cell.class);
    }

    private Element findChild(DeclaredType type, String field) {
        Element element = type.asElement();
        for (Element childElem : element.getEnclosedElements()) {
            if (childElem.getSimpleName().toString().equals(field)) {
                return childElem;
            }
        }
        error("Field " + field + " not found in " + type.toString());
        throw new RuntimeException("Field " + field + " not found!");
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

    private String parseStyle(CellStyle style) {
        return String.format("%1$s.Builder.builder().setBg((short)%2$d).setHorizontalAlignment(%3$s).setVerticalAlignment(%4$s).build()",
                CustomStyle.class.getName(), style.backgroundColor().getIndex(),
                style.horizontalAlignment().getClass().getName() + "." + style.horizontalAlignment().name(),
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

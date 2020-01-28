# Spring-Dream-Excel
An automatic excel export api generator and excel file import utilities for Spring.
***
## Modules
* **excel-base**: Annotations used for excel export and basic excel import/export utilities are included. 
Excel processing is based on [poi](https://poi.apache.org/).
* **excel-processor**: Java annotation processor is used for generating excel export api (Spring Controller)
* **excel-starter**: Spring boot starter for dream excel. Configuration properties for excel generation are provided
(column width, row height, font etc.)
* **excel-test**: demo
***
## Usage
### Installation(Temporary)
Haven't published it to any maven repository yet, so install it to local maven repository temporarily
```
gradle publishToMavenLocal
```  
### Add Dependencies
* Gradle
```
repositories {
    mavenLocal()
    ... ...
}
dependencies {
    ... ...
    implementation project('com.dream.spring:excel-starter:1.0.0')
    annotationProcessor project('com.dream.spring:excel-processor:1.0.0')
}    
```
* Maven
```
<dependencies>
    ... ...
    <dependency>
        <groupId>com.dream.spring</groupId>
        <artifactId>excel-starter</artifactId>
        <version>1.0.0</version>
    </dependency>
    ... ...
</dependencies>
<plugins>
    <plugin>
        <artifactId>maven-compiler-plugin</artifactId>
        <configuration>
            <annotationProcessorPaths>
                <path>
                    <groupId>com.dream.spring</groupId>
                    <artifactId>excel-processor</artifactId>
                    <version>1.0.0</version>
                </path>
            </annotationProcessorPaths>
    </configuration>
</plugin>
```
### Integration with Spring Boot
'_**excel-starter**_' has provided configuration properties. The properties is started with '_dream.excel_' in 
'application.yml/properties'.  
You can find detail definitions in [DreamExcelProperties](excel-starter/src/main/java/com/dream/spring/excel/DreamExcelProperties.java)
and [ExcelExportConfig](excel-base/src/main/java/com/dream/spring/excel/bean/ExcelExportConfig.java)('_dream.excel.export_').<br/>
**Example**:
```
dream:
    excel:
        export:
            default-width: 12
            column-offset: 1
            row-offset: 1
            ... ...
        i18n:
            clazz: com.dream.spring.excel.test.util.MessageUtils
            method: get
```
For Internationalization, you can use above '_i18n_' properties. But the method specified should accept one **_String_** 
input argument, have public static modifier and have _**String**_ return value type. 
(see [MessageUtils](excel-test/src/main/java/com/dream/spring/excel/test/util/MessageUtils.java) for example).  
Strongly recommending to implement [ExcelI18n Interface](excel-base/src/main/java/com/dream/spring/excel/bean/ExcelI18n.java) 
and inject it into spring context instead of configure through Configuration Properties.  
Example:
```java
@Component
public class MyExcel18n implements ExcelI18n {
    String i18n(String code) {
    
    }
} 
```
### Integration with SpringMVC
* Uses '**_excel-base_**' instead of '_**excel-starter**_' dependency.
* Creates and injects [ExcelExportConfig](excel-base/src/main/java/com/dream/spring/excel/bean/ExcelExportConfig.java) object
```
@Bean
public ExcelExportConfig excelExportConfig() {
    ... ...
}
```
* Optionally implements and injects [ExcelI18n Interface](excel-base/src/main/java/com/dream/spring/excel/bean/ExcelI18n.java) object

### Export Excel
Please refer to [excel-test](excel-test) for detailed example
#### Locates the original data service
Controllers, Services or Repositories can be used for export api's original data source.  
* On the target component, annotated with **_@ExcelSupport_**
* On the original data source method, annotated with **_@ExcelExport_**  
Example:
```
@ExcelSupport("com.dream.spring.excel.test.controller.excel.ExcelController")
@RestController
public class TestController {

    private long timestamp;

    @ExcelExport(value = "/api/excel/test",
            annotations = {@AnnotationDef(clazz = TestAnnotation.class, members = {@AnnotationMember(name = "value", value = "\"hello\""),
                    @AnnotationMember(name = "children", value = "value=\"child\"", annotation = ChildValue.class)})})
    @GetMapping("/api/test")
    public Result<PageResult<Test>> test(@RequestParam(required = false) String param1, @ParamIgnore("-1") @RequestParam int type,
                                         @ParamIgnore @RequestParam(required = false) Integer pageNum,
                                         @ParamIgnore @RequestParam(required = false) Integer pageSize) {
        ... ...
    }
```

#### Defines the excel columns and appearances
Above method's return value will be used for the data to generated Excel. So we will use '_**Test**_' for definition of 
Excel columns and appearance.  
Example:
```
@Sheet(value = "Test", i18nSupport = false, indexIncluded = true,
        categories = {@Category(value = "test.child", start = 4, end = 5)},
        headers = {
                @Header(value = "test.name", field = "name", width = 15, note = @HeaderNote(necessary = true, content = "test_note", i18nSupport = false)),
                @Header(value = "test.value", field = "value"), @Header(value = "test.type", field = "type", width = 8),
                @Header(value = "test.date", field = "date", width = 20), @Header(value = "test.childName", field = "component.childName"),
                @Header(value = "test.childValue", field = "component.childValue")})
public class Test extends BaseTest {

    private String name;

    @Column(converter = @Converter(clazz = ConverterUtils.class, method = "formatType"),
            cellStyles = @CellItemStyle(condition = "{value} == 1", style = @CellStyle(backgroundColor = IndexedColors.BLUE, fontColor = IndexedColors.WHITE)))
    private int type;

    @Column(converter = @Converter(clazz = ConverterUtils.class, method = "formatDate"))
    private Date date;
    
    private Component component;
    ... ...
}
```
* **_@Sheet_** is annotated to define the sheet data, like headers and what field is used to fill the corresponding 
column's cell. For example, `@Header(... field = "component.childValue" ...)` defines that the corresponding column 
will use _component_ field's _childValue_ field as value to fill in each row data.
* **_@Column_** is used to convert the original value to the String to fill the cell or specify some special appearance 
for some cells 

#### Locate @Sheet
Sometimes the **_@Sheet_** annotated class is not in Collection or Array as the return value of **_@ExcelExport_** 
method, it may be wrapped in other class as the above example shows.  
In order to locate it, we will use @SheetWrapper to annotated the target field in the return class.
```
public class Result<T> {

    @SheetWrapper
    private T data;
    
    ... ...
}
public class PageResult<T> {

    @SheetWrapper
    private List<T> values;
    
    ....
 }
```
The annotation processor will find until the @SheetWrapper annotated field is Collection or Array type.

#### Other Key Points
* **_ExcelI18n_** will be used for internationalize the header name, sheet name, category name and so on 
if corresponding **_i18nSupport_** is true

#### Generated Code Example
```
@RestController
public class ExcelController {
  @Autowired
  TestController ref0;

  @Autowired
  ObjectProvider<ExcelExportConfig> configProvider;

  @Autowired
  ObjectProvider<ExcelI18n> i18nProvider;

  @GetMapping("/api/excel/test")
  @TestAnnotation(
      value = "hello",
      children = @com.dream.spring.excel.test.annotation.ChildValue(value="child")
  )
  public void test(@RequestParam(required = false) String param1, HttpServletResponse response)
      throws IOException {
      ... ...
  }
  ... ...
}
```

### Import Excel
Refer to [ImportController](excel-test/src/main/java/com/dream/spring/excel/test/controller/ImportController.java) for example
* Dependencies can be defined between sheets
* Parsing, checking and writing are in bulk, bundle size can be specified
* Check rule of columns can be defined in advance. If rule was broken, then the error information and row number will be collected 

## License
TLVCodec is released under the [Apache 2.0 license](LICENSE)

    Copyright 2020 Meng Jiang.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

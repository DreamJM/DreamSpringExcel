package com.dream.spring.excel.model;

import com.dream.spring.excel.annotation.*;
import com.dream.spring.excel.utils.ConverterUtils;
import com.dream.spring.excel.utils.MessageUtils;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.util.Date;

/**
 * @author DreamJM
 */
@Sheet(value = "Test", i18nSupport = false, i18n = @I18n(clazz = MessageUtils.class, method = "get"), indexIncluded = true,
        defaultStyle = @CellStyle(fontName = "宋体"),
        categories = {@Category(value = "test.child", start = 4, end = 5)},
        headers = {@Header(value = "test.name", field = "name", width = 15),
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

    public Test(String name, String value, int type, Date date, Component component) {
        super(value);
        this.name = name;
        this.type = type;
        this.date = date;
        this.component = component;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public Component getComponent() {
        return component;
    }
}

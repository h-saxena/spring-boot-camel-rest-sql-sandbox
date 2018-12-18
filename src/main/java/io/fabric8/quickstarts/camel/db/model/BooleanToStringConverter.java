package io.fabric8.quickstarts.camel.db.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * The Class BooleanToStringConverter.
 */
@Converter
public class BooleanToStringConverter implements AttributeConverter<Boolean, String> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.
     * Object)
     */
    @Override
    public String convertToDatabaseColumn(Boolean value) {
        return (value != null && value) ? "Y" : "N";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang.
     * Object)
     */
    @Override
    public Boolean convertToEntityAttribute(String value) {
        return "Y".equals(value);
    }
}

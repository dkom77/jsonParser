package ru.komrakov.jsonParser.MyJSONClasses;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import ru.komrakov.jsonParser.JSONPrimitiveClass;

public class MyJSONPrimitive extends JSONPrimitiveClass {

    public MyJSONPrimitive(Object o) {
        super(o);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}

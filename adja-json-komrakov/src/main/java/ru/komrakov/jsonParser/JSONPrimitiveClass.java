package ru.komrakov.jsonParser;

import ru.nojs.json.JSONArray;
import ru.nojs.json.JSONNull;
import ru.nojs.json.JSONObject;
import ru.nojs.json.JSONPrimitive;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONPrimitiveClass implements JSONPrimitive{
    protected Object value;

    public JSONPrimitiveClass(Object o){
        value = o;

        if (((String)o).equalsIgnoreCase("null")){
            value = null;
        }

        if (looksLikeBoolean((String)o)){
            value = o;
            this.getAsBoolean(); //value checked during process
        }
    }

    @Override
    public BigDecimal getAsBigDecimal() {
        return null;
    }

    @Override
    public BigInteger getAsBigInteger() {
        return null;
    }

    @Override
    public boolean getAsBoolean() {

        String probe = (String) value;

        if (StringHelper.stringValueInQuotes(probe)){
            throw new IllegalStateException("Boolean value shouldn't be in quotes");
        }

        if (looksLikeBoolean(probe)) {
            if (probe.equals("true")) {
                return true;
            }
            if (probe.equals("false")) {
                return false;
            }
            throw new IllegalArgumentException("Boolean value spelled incorrectly");
        }
        throw new IllegalStateException("Value is not boolean");
    }

    @Override
    public byte getAsByte() {
        return Byte.parseByte((String)value);
    }

    @Override
    public char getAsCharacter() {
        return (Character)value;
    }

    @Override
    public double getAsDouble() {
        return Double.parseDouble((String) value);
    }

    @Override
    public float getAsFloat() {
        return Float.parseFloat((String) value);
    }

    @Override
    public int getAsInt() {
        return Integer.parseInt((String) value);
    }

    @Override
    public JSONArray getAsJsonArray() {
        return null;
    }

    @Override
    public JSONNull getAsJsonNull() {
        return null;
    }

    @Override
    public JSONObject getAsJsonObject() {
        return null;
    }

    @Override
    public JSONPrimitive getAsJsonPrimitive() {
        return this;
    }

    @Override
    public long getAsLong() {
        return Long.parseLong((String)value);
    }

    @Override
    public Number getAsNumber() {
        return (Number)value;
    }

    @Override
    public short getAsShort() {
        return Short.parseShort((String)value);
    }

    @Override
    public String getAsString() {

        if (StringHelper.containNotClosedQuotes((String)value)){
            throw new IllegalArgumentException("String value shouldn't contain not closed quote");
        }

        return StringHelper.getRidOfQuotes((String) value);
    }

    @Override
    public boolean isJsonArray() {
        return false;
    }

    @Override
    public boolean isJsonNull() {
        return (value == null)||((String)value).equalsIgnoreCase("null");
    }

    @Override
    public boolean isJsonObject() {
        return false;
    }

    @Override
    public boolean isJsonPrimitive() {
        return true;
    }

    private Boolean looksLikeBoolean(String string) {
        String regex = ".*([fF][aA][lL][sS][eE]).*|.*([tT][rR][uU][eE]).*";
        Matcher m = Pattern.compile(regex).matcher(string);
        return m.matches();
    }

}

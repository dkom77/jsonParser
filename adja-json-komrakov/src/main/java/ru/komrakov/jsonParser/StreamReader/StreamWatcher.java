package ru.komrakov.jsonParser.StreamReader;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Set;

public class StreamWatcher {

    public final static char END_OF_STREAM = (char)-1;
    private final static String NO_MORE_SYMBOLS_TO_READ = "";

     private enum JSON_SYMBOLS_SET {
        JSON_OBJECT_START ("{"),
        JSON_OBJECT_END ("}"),
        JSON_OBJECT_SEPARATOR (","),
        JSON_ARRAY_START ("["),
        JSON_ARRAY_END ("]"),
        JSON_PROPERTY_VALUE_DELIMITER (":");

        private String value;

        JSON_SYMBOLS_SET(String value){
            this.value = value;
        }

        public String get(){
            return value;
        }

        public static boolean contains(String probe){
            return values.contains(probe);
        }

        final static Set<String> values = ImmutableSet.of(
                JSON_OBJECT_START.get(),
                JSON_OBJECT_END.get(),
                JSON_OBJECT_SEPARATOR.get(),
                JSON_ARRAY_START.get(),
                JSON_ARRAY_END.get(),
                JSON_PROPERTY_VALUE_DELIMITER.get()
        );
    }

    private String value;

    public StreamWatcher(String value){
        this.value = value;
    }

    public StreamWatcher(char value){
        this.value = String.valueOf(value);
    }

    public StreamWatcher(Character[] value){
        this.value = new String(ArrayUtils.toPrimitive(value));
    }

    public boolean jsonObjectStarted(){
        return value.equals(JSON_SYMBOLS_SET.JSON_OBJECT_START.get());
    }

    public boolean jsonObjectEnded(){
        return value.equals(JSON_SYMBOLS_SET.JSON_OBJECT_END.get());
    }

    public boolean jsonObjectSeparatorFound(){
        return value.equals(JSON_SYMBOLS_SET.JSON_OBJECT_SEPARATOR.get());
    }

    public boolean jsonArrayStarted(){
        return value.equals(JSON_SYMBOLS_SET.JSON_ARRAY_START.get());
    }

    public boolean jsonArrayEnded(){
        return value.equals(JSON_SYMBOLS_SET.JSON_ARRAY_END.get());
    }

    public boolean jsonPropertyValueDelimiterFound(){
        return value.equals(JSON_SYMBOLS_SET.JSON_PROPERTY_VALUE_DELIMITER.get());
    }

    public boolean isJSONControlSymbol(){
        return JSON_SYMBOLS_SET.contains(value);
    }

    public boolean noMoreSymbolsToRead(){
        return value.equals(NO_MORE_SYMBOLS_TO_READ);
    }

    public boolean endOfStreamReached(){
        return value.charAt(0) == END_OF_STREAM;
    }
}

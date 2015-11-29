package ru.komrakov.jsonParser.StreamReader;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Set;

public class StreamReaderWatcher {

    public final static char END_OF_STREAM = (char)-1;

    private final static String NO_MORE_SYMBOLS_TO_READ = "";

    //FIXME: DRY: сколько раз в этом проекте пришлось объявить { } и тп? :) все надо собрать в одну кучу.
    //FIXME: причем судя по всему токены лучше хранить в enum. (Android отдельная песня)
    //вопрос: может лучше не enum a ImmutableSet?
    /* можно так сделать:
    */
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



    /*
       но тогда вместо value.equals(StreamReaderWatcher.JSON_ARRAY_END) где value это String
       надо будет использовать value.equals(StreamReaderWatcher.JSON_ARRAY_END.get())

       а для того чтобы понять относится ли строка (содержащая спец. символ JSON) к enum, надо обходить его и проверять все значения
    */

    /*
    public final static String JSON_OBJECT_START = "{";
    public final static String JSON_OBJECT_END = "}";
    public final static String JSON_OBJECT_SEPARATOR = ",";
    public final static String JSON_ARRAY_START = "[";
    public final static String JSON_ARRAY_END = "]";
    public final static String JSON_PROPERTY_VALUE_DELIMITER = ":";

    private final static Set<Character> JSON_SYMBOLS_SET = ImmutableSet.of(JSON_OBJECT_START.charAt(0),
            JSON_OBJECT_END.charAt(0), JSON_OBJECT_SEPARATOR.charAt(0), JSON_ARRAY_START.charAt(0),
            JSON_ARRAY_END.charAt(0), JSON_PROPERTY_VALUE_DELIMITER.charAt(0));
    */

    private String value;

    public StreamReaderWatcher(String value){
        this.value = value;
    }

    public StreamReaderWatcher(char value){
        this.value = String.valueOf(value);
    }

    public StreamReaderWatcher(Character[] value){
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

    //public boolean noMoreSymbolsToRead(){
    //    return value.equals(STREAM_SYMBOLS_SET.NO_MORE_SYMBOLS_TO_READ.get());
    //}

    /*
    public String getEmptyStreamRepresentation(){
        return STREAM_SYMBOLS_SET.NO_MORE_SYMBOLS_TO_READ.get();
    }
    */


    /*
    static Character[] convertCodeSequenceToArray(List<Character> codes) {
        return codes.toArray(new Character[codes.size()]);
    }


    static String convertCodeSequenceToString(Character[] codes){
        char[] codesSeq = ArrayUtils.toPrimitive(codes);
        byte[] bytes = new String(codesSeq).getBytes();
        return new String(bytes, Charsets.UTF_8);
    }


    static boolean isJSONControlSymbol(char c){
        //return JSON_SYMBOLS_SET.contains(c);
        return JSON_SYMBOLS_SET.contains(c);
    }
    */
}

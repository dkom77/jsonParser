package ru.komrakov.jsonParser.StreamReader;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

public class StreamReaderStatic {

    public final static int END_OF_STREAM = -1;
    public final static String NO_MORE_SYMBOLS_TO_READ = "";

    //FIXME: DRY: сколько раз в этом проекте пришлось объявить { } и тп? :) все надо собрать в одну кучу.
    //FIXME: причем судя по всему токены лучше хранить в enum. (Android отдельная песня)
    //вопрос: может лучще не enum a ImmutableSet?
    /* можно так сделать:

     public enum Tokens {
        JSON_OBJECT_START ("{"),
        JSON_OBJECT_END ("}"),
        JSON_OBJECT_SEPARATOR (","),
        JSON_ARRAY_START ("["),
        JSON_ARRAY_END ("]"),
        JSON_PROPERTY_VALUE_DELIMITER (":");

        private String value;
        Tokens (String value){
            this.value = value;
        }

        public String get(){
            return value;
        }
    }

       но тогда вместо value.equals(StreamReaderStatic.JSON_ARRAY_END) где value это String
       надо будет использовать value.equals(StreamReaderStatic.JSON_ARRAY_END.get())

       а для того чтобы понять относится ли строка (содержащая спец. символ JSON) к enum надо обходить его и проверять все значения
    */

    public final static String JSON_OBJECT_START = "{";
    public final static String JSON_OBJECT_END = "}";
    public final static String JSON_OBJECT_SEPARATOR = ",";
    public final static String JSON_ARRAY_START = "[";
    public final static String JSON_ARRAY_END = "]";
    public final static String JSON_PROPERTY_VALUE_DELIMITER = ":";

    private final static Set<Character> JSON_SYMBOLS_SET = ImmutableSet.of(JSON_OBJECT_START.charAt(0),
            JSON_OBJECT_END.charAt(0), JSON_OBJECT_SEPARATOR.charAt(0), JSON_ARRAY_START.charAt(0),
            JSON_ARRAY_END.charAt(0), JSON_PROPERTY_VALUE_DELIMITER.charAt(0));

    static Integer[] convertCodeSequenceToArray(List<Integer> codes) {
        return codes.toArray(new Integer[codes.size()]);
    }

    static String convertCodeSequenceToString(Integer[] codes){
        byte[] codesSeq = new byte[codes.length];
        int index = 0;
        for(Integer i: codes){
            codesSeq[index] = i.byteValue();
            index++;
        }
        return new String(codesSeq, Charsets.UTF_8);
    }

    static boolean isJSONControlSymbol(char c){
        return JSON_SYMBOLS_SET.contains(c);
    }

    static boolean isJSONControlSymbol(int i){
        return JSON_SYMBOLS_SET.contains((char)i);
    }
}

package ru.komrakov.jsonParser.StreamReader;

import com.google.common.base.Charsets;
import java.util.List;

public class StreamReaderStatic {

    public final static int END_OF_STREAM = -1;
    public final static String NO_MORE_SYMBOLS_TO_READ = "";

    //FIXME: DRY: сколько раз в этом проекте пришлось объявить { } и тп? :) все надо собрать в одну кучу.
    //FIXME: причем судя по всему токены лучше хранить в enum. (Android отдельная песня)
    public final static String JSON_OBJECT_START = "{";
    public final static String JSON_OBJECT_END = "}";
    public final static String JSON_OBJECT_SEPARATOR = ",";
    public final static String JSON_ARRAY_START = "[";
    public final static String JSON_ARRAY_END = "]";
    public final static String JSON_PROPERTY_VALUE_DELIMITER = ":";

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
}

package ru.komrakov.jsonParser.StreamReader;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamReader {
    private static int READ_AHEAD_BUFFER_SIZE = 10;
    private Reader reader;

    private static Map<Character,String> сharEventMap = new HashMap<>();
    static {
        сharEventMap.put('{',StreamReaderStatic.JSON_OBJECT_START);
        сharEventMap.put('}',StreamReaderStatic.JSON_OBJECT_END);
        сharEventMap.put(',',StreamReaderStatic.JSON_OBJECT_SEPARATOR);
        сharEventMap.put('[',StreamReaderStatic.JSON_ARRAY_START);
        сharEventMap.put(']',StreamReaderStatic.JSON_ARRAY_END);
        сharEventMap.put(':', StreamReaderStatic.JSON_PROPERTY_VALUE_DELIMITER);
    }

    public StreamReader(Reader r){
        this.reader = r;
    }

    public Integer[] readNext(){
        return buildSequence();
    }

    private Integer[] buildSequence(){
        List<Integer> codes = new ArrayList<>();
        int code = readCharCodeFromStream();
        markStreamPosition();

        if(isTerminal(code)){
            return new Integer[]{StreamReaderStatic.END_OF_STREAM};
        }

        if (сharEventMap.get((char)code) != null){
            codes = new ArrayList<>();
            codes.add(code);
            return StreamReaderStatic.convertCodeSequenceToArray(codes);
        }

        while (!isTerminal(code)){
            codes.add(code);
            code = readCharCodeFromStream();
            if (сharEventMap.get((char)code) != null){
                restoreStreamPosition();
                return StreamReaderStatic.convertCodeSequenceToArray(codes);
            }
            markStreamPosition();
        }

        return StreamReaderStatic.convertCodeSequenceToArray(codes);
    }

    private Boolean isTerminal(Integer code){
        return code == StreamReaderStatic.END_OF_STREAM;
    }

    private void markStreamPosition(){
        try {
            if (reader != null){
                reader.mark(READ_AHEAD_BUFFER_SIZE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void restoreStreamPosition(){
        try {
            if (reader != null){
                reader.reset();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //FIXME: private char readCharFromStream() наверное избавил бы от кучи лишних кастов и прочих возможных ошибок
    private int readCharCodeFromStream() {
        int code = -1;
        try {
            if (reader != null){
                code = reader.read();
            }else{
                code = -1;
                return code;
            }
            if (code == -1){
                reader.close();
                reader = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
    }
}

package ru.komrakov.jsonParser.StreamReader;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SmartStreamReader {

    private StreamReader streamReader;
    private Set<Character> INDENTATION_SYMBOLS = ImmutableSet.of(' ', '\t', '\n', '\r');

    public SmartStreamReader(Reader reader){
        this.streamReader = new StreamReader(reader);
    }

    public String readNext() throws IllegalArgumentException{
        Integer[] chunk = streamReader.readNext();

        if (chunk[0] == StreamReaderStatic.END_OF_STREAM){
            return StreamReaderStatic.NO_MORE_SYMBOLS_TO_READ;
        }
        chunk = removeInsignificantSymbols(chunk);
        chunk = removeEscapeChar(chunk);

        String value = StreamReaderStatic.convertCodeSequenceToString(chunk);

        if (value.equals(StreamReaderStatic.JSON_PROPERTY_VALUE_DELIMITER)){
            value = readNext();
        }

        if (value.isEmpty()){
            value = readNext();
        }

        return value;
    }

    //FIXME: на такие вещи надо писать юнит-тесты :(
    private Integer[] removeInsignificantSymbols(Integer[] chunk){
        List<Integer> result = new ArrayList<>();
        boolean remove = true;
        for (int probe: chunk){

            if (probe == StreamReaderStatic.QUOTES_SYMBOL_CODE){
                if ((result.size() == 0)||(result.get(result.size()-1) != StreamReaderStatic.ESCAPE_SYMBOL_CODE)){
                    remove = !remove;
                }
            }

            if (remove){
                if (!INDENTATION_SYMBOLS.contains((char)probe))
                    result.add(probe);
            }else{
                result.add(probe);
            }

        }
        return StreamReaderStatic.convertCodeSequenceToArray(result);
    }

    private Integer[] removeEscapeChar(Integer[] chunk){
        //get rid of \" combination
        List<Integer> result = new ArrayList<>();
        for (int i:chunk){
            if (i == StreamReaderStatic.QUOTES_SYMBOL_CODE){
                if (result.size() == 0){
                    result.add(i);
                    continue;
                }
                if (result.get(result.size()-1) == StreamReaderStatic.ESCAPE_SYMBOL_CODE) {
                    result.remove(result.size()-1);
                }
            }
            result.add(i);
        }
        return StreamReaderStatic.convertCodeSequenceToArray(result);
    }
}

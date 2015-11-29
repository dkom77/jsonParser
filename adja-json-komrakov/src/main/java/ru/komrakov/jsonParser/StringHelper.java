package ru.komrakov.jsonParser;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StringHelper {
    private final static int ESCAPE_SYMBOL_CODE = 92;
    private final static int QUOTES_SYMBOL_CODE = 34;
    private final static String QUOTE = String.valueOf((char) QUOTES_SYMBOL_CODE);
    private final static Set<Character> INDENTATION_SYMBOLS = ImmutableSet.of(' ', '\t', '\n', '\r');

    private String value;

    public StringHelper(String value){
        this.value = value;
    }

    public StringHelper(Character[] chunk){
        this.value = new String(ArrayUtils.toPrimitive(chunk));
    }

    public String get(){
        return value;
    }

    public StringHelper removeQuotes(){
        if (isValueInQuotes()){
            value = value.substring(1, value.length()-1);
        }
        return this;
    }

    public boolean isValueInQuotes(){
        return (value.startsWith(QUOTE))&&(value.endsWith(QUOTE));
    }

    public boolean isValueContainNotClosedQuotes(){
        return value.startsWith(QUOTE)&&(!value.endsWith(QUOTE))
                    ||(!value.startsWith(QUOTE))&&value.endsWith(QUOTE);
    }

    public StringHelper removeEscapeChar(){
        //get rid of \" combination
        char[] chunk = value.toCharArray();

        List<Character> result = new ArrayList<>();
        for (char i:chunk){
            if (i == (char)QUOTES_SYMBOL_CODE){
                if (result.size() == 0){
                    result.add(i);
                    continue;
                }
                if (result.get(result.size()-1).equals((char)ESCAPE_SYMBOL_CODE)) {
                    result.remove(result.size()-1);
                }
            }
            result.add(i);
        }
        value = new String(ArrayUtils.toPrimitive(result.toArray(new Character[result.size()])));
        return this;
    }

    public StringHelper removeInsignificantSymbols(){
        char[] chunk = value.toCharArray();
        List<Character> result = new ArrayList<>();
        boolean remove = true;
        for (char probe: chunk){

            if (probe == QUOTES_SYMBOL_CODE){
                if ((result.size() == 0)||(result.get(result.size()-1) != ESCAPE_SYMBOL_CODE)){
                    remove = !remove;
                }
            }

            if (remove){
                if (!INDENTATION_SYMBOLS.contains(probe))
                    result.add(probe);
            }else{
                result.add(probe);
            }

        }
        value = new String(ArrayUtils.toPrimitive(result.toArray(new Character[result.size()])));
        return this;
    }
}

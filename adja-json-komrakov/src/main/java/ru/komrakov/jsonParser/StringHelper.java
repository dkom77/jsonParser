package ru.komrakov.jsonParser;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StringHelper {
    private final static int ESCAPE_SYMBOL_CODE = 92;
    private final static int QUOTES_SYMBOL_CODE = 34;
    private final static String QUOTE = new String(new char[]{(char)QUOTES_SYMBOL_CODE});
    private final static Set<Character> INDENTATION_SYMBOLS = ImmutableSet.of(' ', '\t', '\n', '\r');

    public static String getRidOfQuotes(String value){
        if (stringValueInQuotes(value)){
            value = value.substring(1, value.length()-1);
        }
        return value;
    }

    public static boolean stringValueInQuotes(String value){
        return (value.startsWith(QUOTE))&&(value.endsWith(QUOTE));
    }

    public static boolean containNotClosedQuotes(String value){
        return value.startsWith(QUOTE)&&(!value.endsWith(QUOTE))
                    ||(!value.startsWith(QUOTE))&&value.endsWith(QUOTE);
    }

    public static List<Character> removeEscapeChar(Character[] chunk){
        //get rid of \" combination
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
        return result;
    }

    //FIXME: на такие вещи надо писать юнит-тесты :(
    public static List<Character> removeInsignificantSymbols(Character[] chunk){
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
        return result;
    }
}

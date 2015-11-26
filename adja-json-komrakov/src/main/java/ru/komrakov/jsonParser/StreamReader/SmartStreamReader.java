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

    public final static int ESCAPE_SYMBOL_CODE = 92;
    public final static int QUOTES_SYMBOL_CODE = 34;
    //public final String JSON_PROPERTY_VALUE_DELIMITER = ":";

    public static String getRidOfQuotes(String value){
        String quote = new String(new char[]{(char)QUOTES_SYMBOL_CODE});
        if ((value.startsWith(quote))&&(value.endsWith(quote))){
            value = value.substring(1, value.length()-1);
        }
        return value;
    }

    /*
        private String getRidOfQuotes(String value){
        Character firstChar = value.charAt(0);
        Character lastChar = value.charAt(value.length()-1);
        //FIXME: контест по обфускации? :)
        //String QUOTE = "\"";
        //FIXME value.startsWith(QUOTE) && value.endsWith(QUOTE)
        if ((firstChar.equals((char)34))&&(lastChar.equals((char)34))){
            value = value.substring(1, value.length()-1);
        }
        return value;
    }
     */

    private StreamReader streamReader;
    // FIXME: это на самом деле Set<Character> (done)
     Set<Character> INDENTATION_SYMBOLS = ImmutableSet.of(' ', '\t', '\n', '\r');
    //private final static Character[] JSON_MEANINGLESS_SYMBOLS = {' ','\r','\n','\t'};
    //FIXME: здесь и далее везде - без нужды нет необходимости использовать boxed версии примитивов
    //FIXME: Screening? :) ESCAPE_SYMBOL_CODE
    //FIXME: не понимаю зачем вообще надо было использовать коды символов, а не сами символы.
    //используя коды символов было проще визуально сравнивать выводимые строки с непечатными символами




    public SmartStreamReader(Reader reader){
        this.streamReader = new StreamReader(reader);
    }

    public String readNext() throws IllegalArgumentException{
        Integer[] chunk = streamReader.readNext();
        //FIXME: была же зачем-то константа END_OF_STREAM, раз уж завел :) (done)
        if (chunk[0] == StreamReader.END_OF_STREAM){
            return StreamReader.NO_MORE_SYMBOLS_TO_READ;
        }
        chunk = removeInsignificantSymbols(chunk);
        chunk = removeEscapeChar(chunk);

        String value = StreamReaderStatic.convertCodeSequenceToString(chunk);

        /*
        if (value.equals(JSON_PROPERTY_VALUE_DELIMITER)){
            value = readNext();
        }
        */

        if (value.equals(StreamReader.JSON_PROPERTY_VALUE_DELIMITER)){
            value = readNext();
        }

        //FIXME: value.isEmpty() (done)
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

            /*
            if (probe == QUOTES_SYMBOL_CODE){
                if (result.size() == 0){
                    remove = !remove;
                }
                //FIXME: скорее всего это баг. Чаще всего new Integer(1) != new Integer(1)
                //FIXME: сравнение двух объектов делается методом Object::equals()
                //константа теперь примитивного типа
                if ((result.size() != 0)&&(result.get(result.size()-1) != ESCAPE_SYMBOL_CODE)){
                    remove = !remove;
                }
            }*/


            if (probe == QUOTES_SYMBOL_CODE){

                /*
                if (result.size() == 0){
                    remove = !remove;
                    break;
                }

                if (result.get(result.size()-1) != ESCAPE_SYMBOL_CODE){
                    remove = !remove;
                    break;
                }*/

                if ((result.size() == 0)||(result.get(result.size()-1) != ESCAPE_SYMBOL_CODE)){
                    remove = !remove;
                }
            }

            if (remove){
                //if (!isMeaningless((char)probe)){
                if (!INDENTATION_SYMBOLS.contains((char)probe))
                    result.add(probe);
            }else{
                result.add(probe);
            }

        }
        return StreamReaderStatic.convertCodeSequenceToArray(result);
    }

    private Integer[] removeEscapeChar(Integer[] chunk){
        //FIXME: \" ?
        //get rid of \" combination
        List<Integer> result = new ArrayList<>();
        for (int i:chunk){
            if (i == QUOTES_SYMBOL_CODE){
                if (result.size() == 0){
                    result.add(i);
                    continue;
                }
                //FIXME: тоже самое, IDEA даже подсвечивает, мол, смотри, баг!
                //исправил тип константы - теперь примитивного типа
                if (result.get(result.size()-1) == ESCAPE_SYMBOL_CODE) {
                    result.remove(result.size()-1);
                }
            }
            result.add(i);
        }
        return StreamReaderStatic.convertCodeSequenceToArray(result);

    }

    /*
    private Integer[] convertCodeSequenceToArray(List<Integer> codes) {
        //FIXME:  return codes.toArray(new Integer[codes.size()]);
        //Integer[] codesSeq = new Integer[codes.size()];
        //for (int i = 0; i < codes.size(); i++) {
        //   codesSeq[i] = codes.get(i);
        //}
        //return codesSeq;

        return codes.toArray(new Integer[codes.size()]);

    }*/

    /*
    private String convertCodeSequenceToString(Integer[] codes){
        byte[] codesSeq = new byte[codes.length];
        for (int i = 0; i < codes.length; i++){
            codesSeq[i] = (byte)(int)(codes[i]);
        }
        //FIXME: Локаль дефолтная не всегда UTF-8, строка же у нас всегда UTF-8, JSON другим не бывает.
        return new String(codesSeq, Charsets.UTF_8); /*FIXME: UTF-8 */

        //char 8 bit 0 +65535
        //byte 8 bit -128 +127

        /*
        Convert from String to byte[]:

        String s = "some text here";
        byte[] b = s.getBytes("UTF-8");
        Convert from byte[] to String:

        byte[] b = {(byte) 99, (byte)97, (byte)116};
        String s = new String(b, "US-ASCII");
        */
   // }

    //FIXME: с Set<Character> эта функция не нужна, JSON_MEANINGLESS_SYMBOLS.contains()
    /*
    private Boolean isMeaningless(Character symbol){
        Boolean checkResult = false;
        for (Character c:JSON_MEANINGLESS_SYMBOLS){
            if(c.equals(symbol)){
                checkResult = true;
                break;
            }
        }
        return checkResult;
    }*/


}

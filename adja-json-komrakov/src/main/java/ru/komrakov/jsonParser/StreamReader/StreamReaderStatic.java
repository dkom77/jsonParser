package ru.komrakov.jsonParser.StreamReader;

import com.google.common.base.Charsets;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;

public class StreamReaderStatic {


    static Integer[] convertCodeSequenceToArray(List<Integer> codes) {
        return codes.toArray(new Integer[codes.size()]);
    }

    static String convertCodeSequenceToString(Integer[] codes){

        //Byte[] codesSeq = Arrays.asList(codes).toArray(new Byte[codes.length]);

        //casting Array of Integer[] into Array of Byte[]
        //Byte[] codesSeq = Arrays.copyOf(codes, codes.length, Byte[].class); // <- doesn't do type convertion!
        //unboxing Byte[] -> byte[]
        //byte[] ab = ArrayUtils.toPrimitive(codesSeq);

        byte[] codesSeq = new byte[codes.length];
        int index = 0;
        for(Integer i: codes){
            codesSeq[index] = i.byteValue();
            index++;
        }




        /*
        byte[] codesSeq = new byte[codes.length];
        for (int i = 0; i < codes.length; i++){
            codesSeq[i] = (byte)(int)(codes[i]);
        }
        */
        //FIXME: Локаль дефолтная не всегда UTF-8, строка же у нас всегда UTF-8, JSON другим не бывает.
        //return new String(ArrayUtils.toPrimitive(codesSeq), Charsets.UTF_8); /*FIXME: UTF-8 */
        return new String(codesSeq, Charsets.UTF_8); /*FIXME: UTF-8 */
    }
}

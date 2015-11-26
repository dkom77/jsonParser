package ru.komrakov.jsonParser.StreamReader;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.ArrayUtils;
import ru.komrakov.jsonParser.StringHelper;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SmartStreamReader {

    private StreamReader streamReader;

    public SmartStreamReader(Reader reader){
        this.streamReader = new StreamReader(reader);
    }

    public String readNext() throws IllegalArgumentException{
        //Integer[] chunk = streamReader.readNext();
        Character[] chunk = streamReader.readNext();

        if (chunk[0].equals(StreamReaderStatic.END_OF_STREAM)){
            return StreamReaderStatic.NO_MORE_SYMBOLS_TO_READ;
        }
        chunk = StreamReaderStatic.convertCodeSequenceToArray(StringHelper.removeInsignificantSymbols(chunk));
        chunk = StreamReaderStatic.convertCodeSequenceToArray(StringHelper.removeEscapeChar(chunk));

        String value = StreamReaderStatic.convertCodeSequenceToString(chunk);

        if (value.equals(StreamReaderStatic.JSON_PROPERTY_VALUE_DELIMITER)){
            value = readNext();
        }

        if (value.isEmpty()){
            value = readNext();
        }

        return value;
    }
}

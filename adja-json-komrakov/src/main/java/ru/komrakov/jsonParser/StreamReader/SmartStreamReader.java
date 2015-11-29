package ru.komrakov.jsonParser.StreamReader;

import ru.komrakov.jsonParser.StringHelper;

import java.io.Reader;

public class SmartStreamReader {

    private StreamReader streamReader;

    public SmartStreamReader(Reader reader){
        this.streamReader = new StreamReader(reader);
    }

    public String readNext() throws IllegalArgumentException{
        Character[] chunk = streamReader.readNext();

        if (chunk[0].equals(StreamReaderStatic.END_OF_STREAM)){
            return StreamReaderStatic.NO_MORE_SYMBOLS_TO_READ;
        }

        String value = new StringHelper(chunk).removeInsignificantSymbols().removeEscapeChar().get();

        if (value.equals(StreamReaderStatic.JSON_PROPERTY_VALUE_DELIMITER)){
            value = readNext();
        }

        if (value.isEmpty()){
            value = readNext();
        }

        return value;
    }
}

package ru.komrakov.jsonParser.StreamReader;

import ru.komrakov.jsonParser.StringHelper;

import java.io.IOException;
import java.io.Reader;

public class SmartStreamReader {

    private StreamReader streamReader;

    public SmartStreamReader(Reader reader){
        this.streamReader = new StreamReader(reader);
    }

    public String readNext() throws IllegalArgumentException, IOException{
        Character[]chunk = streamReader.readNext();

        if (chunk[0].equals(StreamReaderWatcher.END_OF_STREAM)){
            return new String();
        }

        String value = new StringHelper(chunk)
                .removeInsignificantSymbols()
                .removeEscapeChar()
                .get();

        //if (value.equals(StreamReaderWatcher.JSON_SYMBOLS_SET.JSON_PROPERTY_VALUE_DELIMITER.get())){
        if (new StreamReaderWatcher(value).jsonPropertyValueDelimiterFound()){
            value = readNext();
        }

        if (value.isEmpty()){
            value = readNext();
        }

        return value;
    }
}

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
        StreamWatcher watcher = new StreamWatcher(chunk);

        if (watcher.endOfStreamReached()){
            return new String();
        }

        String value = new StringHelper(chunk)
                .removeInsignificantSymbols()
                .removeEscapeChar()
                .get();

        if (watcher.jsonPropertyValueDelimiterFound()){
            value = readNext();
        }

        if (value.isEmpty()){
            value = readNext();
        }

        return value;
    }
}

package ru.komrakov.jsonParser;

import ru.komrakov.jsonParser.StreamReader.SmartStreamReader;
import ru.komrakov.jsonParser.StreamReader.StreamWatcher;
import ru.nojs.json.JSONElement;
import ru.nojs.json.StreamingJsonParser;

import java.io.IOException;
import java.io.Reader;

public class ImplementedJsonParser implements StreamingJsonParser {

    @Override
    public JSONElement parse(Reader r) throws IllegalArgumentException{
        SmartStreamReader reader = new SmartStreamReader(r);
        try{
            return buildJSON(null, reader);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    private JSONElement buildJSON(JSONElement element, SmartStreamReader reader) throws IllegalArgumentException, IOException{

        String value = reader.readNext();
        StreamWatcher readerWatcher = new StreamWatcher(value);

        if (readerWatcher.noMoreSymbolsToRead()) {
            return element;
        }

        if (readerWatcher.jsonArrayStarted()) {
            element = new JSONArrayClass();
            while (!readerWatcher.jsonArrayEnded()) {
                JSONElement arrayMember = buildJSON(null, reader);
                ((JSONArrayClass) element).add(arrayMember);
                value = reader.readNext();
                readerWatcher = new StreamWatcher(value);
            }
            return element;
        }

        if (readerWatcher.jsonObjectStarted()) {
            element = new JSONObjectClass();
            while (!readerWatcher.jsonObjectEnded()) {
                value = reader.readNext();//skipping "{"
                String propertyName = new StringHelper(value).removeQuotes().get();
                JSONElement propertyValue = buildJSON(null, reader);
                if (propertyValue == null) {
                    throw new IllegalArgumentException("JSON property can't be null");
                }
                ((JSONObjectClass) element).add(propertyName, propertyValue);
                value = reader.readNext();
                readerWatcher = new StreamWatcher(value);
            }
            return element;
        }
        element = new JSONPrimitiveClass(value);

        return element;
    }
}

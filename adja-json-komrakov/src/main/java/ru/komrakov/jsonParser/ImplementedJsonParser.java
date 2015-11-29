package ru.komrakov.jsonParser;

import ru.komrakov.jsonParser.StreamReader.SmartStreamReader;
import ru.komrakov.jsonParser.StreamReader.StreamReaderWatcher;
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
        StreamReaderWatcher readerWatcher = new StreamReaderWatcher(value);
        //if (value.equals(StreamReaderWatcher.NO_MORE_SYMBOLS_TO_READ)) {
        if (readerWatcher.noMoreSymbolsToRead()) {
            return element;
        }



        //if (value.equals(StreamReaderWatcher.JSON_ARRAY_START)) {
        if (readerWatcher.jsonArrayStarted()) {
            element = new JSONArrayClass();
            //while (!value.equals(StreamReaderWatcher.JSON_ARRAY_END)) {
            while (!readerWatcher.jsonArrayEnded()) {
                JSONElement arrayMember = buildJSON(null, reader);
                ((JSONArrayClass) element).add(arrayMember);
                value = reader.readNext();
                readerWatcher = new StreamReaderWatcher(value);
            }
            return element;
        }
        //if (value.equals(StreamReaderWatcher.JSON_OBJECT_START)) {
        if (readerWatcher.jsonObjectStarted()) {
            element = new JSONObjectClass();
            //while (!value.equals(StreamReaderWatcher.JSON_OBJECT_END)) {
            while (!readerWatcher.jsonObjectEnded()) {
                value = reader.readNext();//skipping "{"
                //readerHelper = new StreamReaderWatcher(value);
                //String propertyName = StringHelper.removeQuotes(value);
                String propertyName = new StringHelper(value).removeQuotes().get();
                JSONElement propertyValue = buildJSON(null, reader);
                if (propertyValue == null) {
                    throw new IllegalArgumentException("JSON property can't be null");
                }
                ((JSONObjectClass) element).add(propertyName, propertyValue);
                value = reader.readNext();
                readerWatcher = new StreamReaderWatcher(value);
            }
            return element;
        }
        element = new JSONPrimitiveClass(value);

        return element;
    }
}

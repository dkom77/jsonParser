package ru.komrakov.jsonParser;

import ru.komrakov.jsonParser.StreamReader.SmartStreamReader;
import ru.komrakov.jsonParser.StreamReader.StreamReaderStatic;
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
        if (value.equals(StreamReaderStatic.NO_MORE_SYMBOLS_TO_READ)) {
            return element;
        }

        if (value.equals(StreamReaderStatic.JSON_ARRAY_START)) {
            element = new JSONArrayClass();
            while (!value.equals(StreamReaderStatic.JSON_ARRAY_END)) {
                JSONElement arrayMember = buildJSON(null, reader);
                ((JSONArrayClass) element).add(arrayMember);
                value = reader.readNext();
            }
            return element;
        }
        if (value.equals(StreamReaderStatic.JSON_OBJECT_START)) {
            element = new JSONObjectClass();
            while (!value.equals(StreamReaderStatic.JSON_OBJECT_END)) {
                value = reader.readNext();//get rid of "{"
                //String propertyName = StringHelper.removeQuotes(value);
                String propertyName = new StringHelper(value).removeQuotes().get();
                JSONElement propertyValue = buildJSON(null, reader);
                if (propertyValue == null) {
                    throw new IllegalArgumentException("JSON property can't be null");
                }
                ((JSONObjectClass) element).add(propertyName, propertyValue);
                value = reader.readNext();
            }
            return element;
        }
        element = new JSONPrimitiveClass(value);

        return element;
    }
}

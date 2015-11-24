package ru.komrakov.jsonParser;

import ru.nojs.json.JSONElement;
import ru.nojs.json.StreamingJsonParser;

import java.io.Reader;

public class ImplementedJsonParser implements StreamingJsonParser {

    @Override
    public JSONElement parse(Reader r) throws IllegalArgumentException{
        SmartStreamReader reader = new SmartStreamReader(r);

        //FIXME: зачем так делать? только бесить варнингами IDEA-инспектора. Лучше убрать, здесь и везде
        //Комментарии пока оставил чтобы виднее было где и как было исправлено
        return buildJSON(null, reader);
    }

    private JSONElement buildJSON(JSONElement element, SmartStreamReader reader) throws IllegalArgumentException{
        String value = reader.readNext();

        if (value.equals(StreamReader.NO_MORE_SYMBOLS_TO_READ)) {
            return element;
        }

        if (value.equals(StreamReader.JSON_ARRAY_START)) {
            element = new JSONArrayClass();
            while (!value.equals(StreamReader.JSON_ARRAY_END)) {
                JSONElement arrayMember = buildJSON(null, reader);
                ((JSONArrayClass) element).add(arrayMember);
                value = reader.readNext();
            }
            return element;
        }
        if (value.equals(StreamReader.JSON_OBJECT_START)) {
            element = new JSONObjectClass();
            while (!value.equals(StreamReader.JSON_OBJECT_END)) {
                value = reader.readNext();//get rid of "{"
                String propertyName = getRidOfQuotes(value);
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

    //FIXME: в vcs обычно не хранят закоменченый код. На то она и vcs. Если нужно что-то старое - чекаут.
    //ф-я main в этом классе не нужна. использовалась для тестов

}

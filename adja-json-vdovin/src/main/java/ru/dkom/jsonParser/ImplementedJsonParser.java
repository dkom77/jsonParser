package ru.dkom.jsonParser;

import ru.nojs.json.JSONArray;
import ru.nojs.json.JSONElement;
import ru.nojs.json.StreamingJsonParser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImplementedJsonParser implements StreamingJsonParser {
    private final static String WAITING_FOR_INPUT = "WAITING_FOR_INPUT";
    private final static String COMPILING_VALUE = "COMPILING_VALUE";
    private final static String VALUE_HAS_BEEN_READ = "VALUE_HAS_BEEN_READ";
    private final static String READING_ARRAY = "READING_ARRAY";
    private final static String ARRAY_HAS_BEEN_READ = "ARRAY_HAS_BEEN_READ";
    private final static String END_OF_STREAM = "END_OF_STREAM";

    private String jsonState;
    private List<String> jsonStateStack;


    String JSONString;

    public String readStream(Reader r){

        String currentValue = "";
        StringBuilder stringBuilder = new StringBuilder();
        Character symbol = null;

        JSONEvent event = new JSONEvent();
        String evDescriptor = new String();

        while (true){
            symbol = readSymbol(r);
            if (jsonState.equals(END_OF_STREAM)){
                break;
            }
            evDescriptor = event.checkEvent(symbol);
            //just reading value shouldn't change parser state


            /*
            if (jsonState.equals(VALUE_HAS_BEEN_READ)){
            //if (!jsonState.equals(COMPILING_VALUE)){
                currentValue = stringBuilder.toString();
                stringBuilder = new StringBuilder();

                //jsonState = jsonStateStack.get(jsonStateStack.size()-2);
                jsonStateStack.remove(jsonStateStack.size()-1);
                jsonState = jsonStateStack.get(jsonStateStack.size()-1);
                break;
            }*/

            //if (jsonState.equals(COMPILING_VALUE)){
            /*
            if (evDescriptor.equals(JSONEvent.READING_VALUE)){
                stringBuilder.append(symbol);
            }
            if (evDescriptor.equals(JSONEvent.JSON_OBJECT_SEPARATOR)){
                updateState(evDescriptor);
                currentValue = stringBuilder.toString();
                stringBuilder = new StringBuilder();
                break;
            }*/
            if (event.getValue().equals("")){

                updateState(evDescriptor);

                if (stringBuilder.length() == 0){
                    continue;
                }

                currentValue = stringBuilder.toString();
                stringBuilder = new StringBuilder();
                break;
            }else{
                stringBuilder.append(symbol);
            }
        }

        return currentValue;
    }

    @Override
    public JSONElement parse(Reader r) {
        JSONArray jsonArray = new JSONArrayClass();
        jsonStateStack.add(jsonState);

        String currentValue = "";
        StringBuilder stringBuilder = new StringBuilder();


        while(!jsonState.equals(END_OF_STREAM)){
            currentValue = readStream(r);
            if (currentValue.equals("")){
                continue;
            }
            if (jsonState.equals(READING_ARRAY)){
                JSONPrimitiveClass p = new JSONPrimitiveClass(currentValue);
                JSONElementClass el = new JSONElementClass(p);
                jsonArray.add(el);
            }
            //System.out.println(currentValue + " : " + jsonStateStack.get(jsonStateStack.size() - 1));
            System.out.println(currentValue + " : " + jsonState);
        }

        jsonStateStack.add(jsonState);

        for (String s:jsonStateStack){
            System.out.println(s);
        }

        //JSONString = read(r);
        //JSONArray array = readAsArray(r);
        JSONElementClass element = new JSONElementClass(jsonArray);

        return element;
        //return null;
    }


    private void updateState(String event){
        jsonState = COMPILING_VALUE;

/*
        if (event.equals(JSONEvent.JSON_OBJECT_SEPARATOR)
                ||event.equals(JSONEvent.JSON_ARRAY_END)
                ||event.equals(JSONEvent.JSON_OBJECT_END)){
            jsonState = VALUE_HAS_BEEN_READ;
            jsonStateStack.add(jsonState);
            //jsonState = jsonStateStack.get(jsonStateStack.size()-1);
        }
*/
        //if (event.equals(JSONEvent.JSON_OBJECT_SEPARATOR)){
        //    jsonState = VALUE_HAS_BEEN_READ;
        //    jsonStateStack.add(jsonState);
        //}

        if (event.equals(JSONEvent.JSON_ARRAY_START)){
            jsonState = READING_ARRAY;
            jsonStateStack.add(jsonState);
        }

        if (event.equals(JSONEvent.JSON_ARRAY_END)){
            //jsonState = VALUE_HAS_BEEN_READ;
            //jsonStateStack.add(jsonState);
            jsonState = ARRAY_HAS_BEEN_READ;
            jsonStateStack.add(ARRAY_HAS_BEEN_READ);
            //jsonStateStack.add(WAITING_FOR_INPUT);
        }

        if (event.equals(JSONEvent.JSON_OBJECT_SEPARATOR)){
            //jsonState = VALUE_HAS_BEEN_READ;
            //jsonStateStack.add(jsonState);
            jsonState = jsonStateStack.get(jsonStateStack.size()-1);
        }


        //if (!jsonState.equals(COMPILING_VALUE)){
        //    jsonStateStack.add(jsonState);
        //}



    }

    public ImplementedJsonParser() {
        jsonStateStack = new ArrayList<>();
        jsonState = WAITING_FOR_INPUT;
    }

    private Character readSymbol(Reader r) {
        Character c = null;
        try {
            int code = r.read();
            c = (char)code;
            if (code == -1){
                jsonState = END_OF_STREAM;
                r.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return c;
    }


    private String eliminateBrackets(String string) {
        String regex = "\\[([^]]+)\\]";
        Matcher m = Pattern.compile(regex).matcher(string);
        while (m.find()) {
            return m.group(1);
        }
        return "not array";
    }



    public static void main(String[] args) {
        ImplementedJsonParser sjp = new ImplementedJsonParser();
        //String jsonArray = "[1,2,3,4,856]";
        //JSONElement je = sjp.parse(new StringReader(jsonArray));

        String str = "\"test\"";
        JSONElement je = sjp.parse(new StringReader(str));
    }


}

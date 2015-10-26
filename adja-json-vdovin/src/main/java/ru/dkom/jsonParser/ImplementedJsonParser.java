package ru.dkom.jsonParser;

import ru.nojs.json.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ImplementedJsonParser implements StreamingJsonParser {
    private final static String WAITING_FOR_INPUT = "WAITING_FOR_INPUT";
    private final static String COMPILING_VALUE = "COMPILING_VALUE";
    private final static String VALUE_HAS_BEEN_READ = "VALUE_HAS_BEEN_READ";
    private final static String READING_ARRAY = "READING_ARRAY";
    private final static String ARRAY_HAS_BEEN_READ = "ARRAY_HAS_BEEN_READ";
    private final static String END_OF_STREAM = "END_OF_STREAM";

    private final static String READING_OBJECT = "READING_OBJECT";
    private final static String OBJECT_HAS_BEEN_READ = "OBJECT_HAS_BEEN_READ";

    private final static String READING_PRIMITIVE = "READING_PRIMITIVE";

    private final static String READING_JSON_PROPERTY = "READING_JSON_PROPERTY";

    private String jsonState;
    private JSONStateStack jsonStateStack;

    private String streamState;

    public String readStream(Reader r){

        String currentValue = "";
        StringBuilder stringBuilder = new StringBuilder();
        Character symbol;
        int code;

        JSONEvent event = new JSONEvent();
        String evDescriptor = new String();
        Boolean readInQuotes = false;

        List<Integer>codes = new ArrayList<>();

        while (true) {
            code = readCode(r);

            System.out.println(code);

            if (code == -1){
                updateState(JSONEvent.READING_VALUE);
                streamState = END_OF_STREAM;
                //currentValue = stringBuilder.toString();
                break;
            }

            evDescriptor = event.checkEvent((char)code,readInQuotes);

            if (evDescriptor.equals(JSONEvent.INSIGNIFICANT_SYMBOL)){
                continue;
            }

            int lastCode = -1;
            /*
            if (codes.size()>0){
                lastCode = codes.get(codes.size() - 1);
                if ((lastCode == 92)&&(code == 34)){
                    readInQuotes = !readInQuotes;
                }
            }*/

            if (code == 34){
                readInQuotes = !readInQuotes;
                if (codes.size()>0){
                    lastCode = codes.get(codes.size() - 1);
                    if ((lastCode == 92)&&(code == 34)){
                        readInQuotes = !readInQuotes;
                    }
                }
            }

            codes.add(code);

            if (evDescriptor.equals(JSONEvent.JSON_OBJECT_START)){
                updateState(evDescriptor);
                codes.remove(codes.size()-1);
                break;
            }
            if(evDescriptor.equals(JSONEvent.JSON_OBJECT_END)){
                updateState(evDescriptor);
                codes.remove(codes.size()-1);
                break;
            }
            if (evDescriptor.equals(JSONEvent.JSON_ARRAY_START)){
                updateState(evDescriptor);
                codes.remove(codes.size()-1);
                break;
            }
            if(evDescriptor.equals(JSONEvent.JSON_ARRAY_END)){
                updateState(evDescriptor);
                codes.remove(codes.size()-1);
                break;
            }
            if(evDescriptor.equals(JSONEvent.JSON_OBJECT_SEPARATOR)){
                updateState(evDescriptor);
                codes.remove(codes.size()-1);
                break;
            }
        }

        //symbols with code 92 34 replacing with 34
        int current = 65535;
        int previous = 65535;
        int[] codesArray = new int[codes.size()];
        for(int i = 0; i < codes.size(); i++){
            codesArray[i] = codes.get(i);
        }

        int clearSize = codesArray.length;
        boolean quotesMode = false;
        List<Integer> quotesPlaces = new ArrayList<>();
        for(int i = 0; i < codesArray.length; i++) {

            current = codesArray[i];
            if (current == 34) {
                quotesPlaces.add(i);
            }
        };
            /*
        //eliminate pairing quotes
        //replace first and last
        for (int i = 0; i < quotesPlaces.size()/2; i ++){
            codesArray[quotesPlaces.get(i)] = 65535;
            codesArray[quotesPlaces.get(quotesPlaces.size() -1 - i)] = 65535;
            clearSize = clearSize - 2;
        }
*/
        //removing /" combination
        for(int i = 0; i < codesArray.length; i++){
            if(i < 1){
                continue;
            }
            current = codesArray[i];
            previous = codesArray[i - 1];

            if ((previous == 92)&&(current == 34)){
                codesArray[i - 1] = 65535;
                clearSize--;
            }
        }

        char[]converter = new char[clearSize];
        int j = 0;
        for(int i = 0; i < codesArray.length; i++){
            if (codesArray[i] == 65535){
                continue;
            }
            converter[j] = (char)(int)codesArray[i];
            j++;
        }

        if (converter.length > 0){
            if ((!readInQuotes)&&(converter[0]==34)){
                //remove first and last symbol
                char[] swap = new char[converter.length - 2];
                for (int i = 1; i < converter.length - 1; i ++){
                    swap[i-1] = converter[i];
                }
                converter = swap;
            }
        }


        currentValue = new String(converter);
        return currentValue;
    }

    @Override
    public JSONElement parse(Reader r) {
        JSONArray jsonArray = new JSONArrayClass();
        JSONObject jsonObject = new JSONObjectClass();

        jsonState = WAITING_FOR_INPUT;
        streamState = WAITING_FOR_INPUT;
        jsonStateStack = new JSONStateStack();
        jsonStateStack.push(jsonState);

        JSONElement element = null;

        String currentValue = "";
        List<String> values = new ArrayList<>();

        while(!streamState.equals(END_OF_STREAM)){

            values.add(readStream(r));
            currentValue = values.get(values.size()-1);

            if (currentValue.equals("")){
                continue;
            }

            jsonState = jsonStateStack.readLast();

            if (jsonState.equals(READING_ARRAY)){
                JSONPrimitiveClass p = new JSONPrimitiveClass(currentValue);
                JSONElementClass el = new JSONElementClass(p);
                jsonArray.add(el);
            }

            if (jsonState.equals(ARRAY_HAS_BEEN_READ)){
                JSONPrimitiveClass p = new JSONPrimitiveClass(currentValue);
                JSONElementClass el = new JSONElementClass(p);
                jsonArray.add(el);
                jsonStateStack.push(WAITING_FOR_INPUT);
                element = new JSONElementClass(jsonArray);
                jsonArray = new JSONArrayClass();
            }

            if (jsonState.equals(READING_OBJECT)){
                String propName = values.get(values.size()-2);
                jsonObject.addProperty(propName, currentValue);

            }

            if (jsonState.equals(OBJECT_HAS_BEEN_READ)){
                String propName = values.get(values.size()-2);
                jsonObject.addProperty(propName, currentValue);
                jsonStateStack.push(WAITING_FOR_INPUT);
                element = new JSONElementClass(jsonObject);
                jsonObject = new JSONObjectClass();
            }

            if (jsonState.equals(READING_JSON_PROPERTY)){

                String propName = values.get(values.size()-2);
                jsonObject.addProperty(propName, currentValue);
            }

            if(jsonState.equals(READING_PRIMITIVE)){
                element = new JSONElementClass(new JSONPrimitiveClass(currentValue));
            }
            System.out.println(currentValue + " : " + jsonStateStack.readLast());
        }


        jsonStateStack.push(jsonState);

        //JSONElementClass element = new JSONElementClass(jsonArray);


        System.out.println(jsonStateStack.toString());

        return element;
    }


    private void updateState(String event){
        jsonState = WAITING_FOR_INPUT;

        if (jsonState.equals(END_OF_STREAM)){
            jsonState = READING_PRIMITIVE;
            jsonStateStack.push(jsonState);
        }

        //primitive detection
        if(event.equals(JSONEvent.READING_VALUE)){
            jsonState = READING_PRIMITIVE;
            jsonStateStack.push(jsonState);
        }

        if (event.equals(JSONEvent.JSON_OBJECT_START)){
            jsonState = READING_OBJECT;
            jsonStateStack.push(jsonState);
        }

        if (event.equals(JSONEvent.JSON_OBJECT_END)){
            jsonState = OBJECT_HAS_BEEN_READ;
            jsonStateStack.push(jsonState);
        }

        if (event.equals(JSONEvent.JSON_ARRAY_START)){
            jsonState = READING_ARRAY;
            jsonStateStack.push(jsonState);
        }

        if (event.equals(JSONEvent.JSON_ARRAY_END)){
            jsonState = ARRAY_HAS_BEEN_READ;
            jsonStateStack.push(jsonState);
        }

        if (event.equals(JSONEvent.JSON_OBJECT_SEPARATOR)){
            jsonState = jsonStateStack.readLast();
        }

        if (event.equals(JSONEvent.JSON_PRIMITIVE_VALUE_START)){
            jsonState = READING_JSON_PROPERTY;
            jsonStateStack.push(jsonState);
        }

    }

    public ImplementedJsonParser() {
        //jsonStateStack = new ArrayList<>();
        jsonState = WAITING_FOR_INPUT;
        jsonStateStack = new JSONStateStack();
    }


    private int readCode(Reader r) {
        int code = 65535;
        try {
            code = r.read();
            //c = (char)code;
            if (code == -1){
                //jsonState = END_OF_STREAM;
                streamState = END_OF_STREAM;
                r.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
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

    public static void main(String[] args) {
        ImplementedJsonParser sjp = new ImplementedJsonParser();
        JSONElement je = null;
        JSONArray a = null;
        String str = "";
        int size = 0;

        //String jsonArray = "[1,2,3,4,856]";
        //je = sjp.parse(new StringReader(jsonArray));

        //JSONArray a = je.getAsJsonArray();

        //int size = a.size();
        //System.out.println(size);

        //str = "\"test\"";
        //str = "test    ";
        str = "  true";
        je = sjp.parse(new StringReader(str));
        Boolean t = je.getAsBoolean();
        System.out.println(t);


        //str = "\t [\t\t\n\n\r    true , \r\t\n  false\r\t\n] \n";
        //je = sjp.parse(new StringReader(str));
        //a = je.getAsJsonArray();
        //size = a.size();
        //System.out.println(size);
        //str = "    test    ";
        //sjp.parse(new StringReader(str));

        //String jsnobj = "{\"a\":1}";
        //je = sjp.parse(new StringReader(jsnobj));

        je = sjp.parse(new StringReader("test\""));
        System.out.println(je.getAsString());

        je = sjp.parse(new StringReader("\"\t\r\n\\\" \""));
        String test = je.getAsString();
        String model = "\t\r\n\" ";
        System.out.println("Model as string: " + model);
        System.out.println("Test as string: " + test);
        System.out.println(test.equals("\t\r\n\"\"\" "));
        System.out.println(test.equals(model));

        char[] modelChars = new char[model.length()];
        char[] testChars = new char[test.length()];

        modelChars = model.toCharArray();
        testChars = test.toCharArray();

        System.out.print("model: ");
        for (char c: modelChars){
            System.out.print((int) c + " ");
        }
        System.out.println();
        System.out.print("test: ");
        for (char c: testChars){
            System.out.print((int) c + " ");
        }


    }


}

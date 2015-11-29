package ru.komrakov.jsonParser.StreamReader;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class StreamReader {
    private final static int READ_AHEAD_BUFFER_SIZE = 10;
    private Reader reader;

    public StreamReader(Reader r) {
        this.reader = r;
    }

    public Character[] readNext() throws IOException{
        return buildSequence();
    }

    private Character[] buildSequence() throws IOException{
        List<Character> codes = new ArrayList<>();

        char code = readCharFromStream();
        markStreamPosition();

        if (isTerminal(code)) {
            return new Character[]{StreamReaderWatcher.END_OF_STREAM};
        }

        //if (StreamReaderWatcher.isJSONControlSymbol(code)) {
        if (new StreamReaderWatcher(code).isJSONControlSymbol()) {
            codes = new ArrayList<>();
            codes.add(code);
            //return StreamReaderWatcher.convertCodeSequenceToArray(codes);
            return codes.toArray(new Character[codes.size()]);
        }

        while (!isTerminal(code)) {
            codes.add(code);
            code = readCharFromStream();

            //if (StreamReaderWatcher.isJSONControlSymbol(code)) {
            if (new StreamReaderWatcher(code).isJSONControlSymbol()) {
                restoreStreamPosition();
                //return StreamReaderWatcher.convertCodeSequenceToArray(codes);
                return codes.toArray(new Character[codes.size()]);
            }
            markStreamPosition();
        }

        // return StreamReaderWatcher.convertCodeSequenceToArray(codes);
        return codes.toArray(new Character[codes.size()]);
    }

    private Boolean isTerminal(char code) {
        return code == StreamReaderWatcher.END_OF_STREAM;
    }

    private void markStreamPosition() {
        try {
            if (reader != null) {
                reader.mark(READ_AHEAD_BUFFER_SIZE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void restoreStreamPosition() {
        try {
            if (reader != null) {
                reader.reset();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private char readCharFromStream() throws IOException{
        char code = StreamReaderWatcher.END_OF_STREAM;

        try {
            if (reader != null) {
                code = (char) reader.read();
            }
        } catch (IOException readFromStreamException) {
            closeReader();
            throw new IOException("Failed to read from stream");
        }

        if (code == StreamReaderWatcher.END_OF_STREAM) {
            closeReader();
        }

        return code;
    }

    private void closeReader() throws IOException {
        if (reader != null) {
            try{
                reader.close();
            }catch (IOException e){
                throw new IOException("Failed to close stream");
            }finally {
                reader = null;
            }
        }
    }
}



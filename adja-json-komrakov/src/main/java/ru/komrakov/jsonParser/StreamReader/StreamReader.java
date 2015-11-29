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
        StreamWatcher watcher = new StreamWatcher(code);

        markStreamPosition();
        if (watcher.endOfStreamReached()) {
            return new Character[]{StreamWatcher.END_OF_STREAM};
        }

        if (new StreamWatcher(code).isJSONControlSymbol()) {
            codes = new ArrayList<>();
            codes.add(code);
            return codes.toArray(new Character[codes.size()]);
        }

        while (!watcher.endOfStreamReached()) {
            codes.add(code);
            code = readCharFromStream();
            watcher = new StreamWatcher(code);

            if (new StreamWatcher(code).isJSONControlSymbol()) {
                restoreStreamPosition();
                return codes.toArray(new Character[codes.size()]);
            }
            markStreamPosition();
        }

        return codes.toArray(new Character[codes.size()]);
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
        char code = StreamWatcher.END_OF_STREAM;

        try {
            if (reader != null) {
                code = (char) reader.read();
            }
        } catch (IOException readFromStreamException) {
            closeReader();
            throw new IOException("Failed to read from stream");
        }

        if (code == StreamWatcher.END_OF_STREAM) {
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



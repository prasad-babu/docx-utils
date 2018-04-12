package com.dplabs.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileRepository {
    InputStream read(String _fileName) throws IOException;
    OutputStream getOutputStream(String _fileName) throws IOException;
}

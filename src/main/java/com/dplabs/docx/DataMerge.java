package com.dplabs.docx;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public interface DataMerge {

    static final String DOCX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    /**
     *
     * @param _placeHolders
     */
    void replacePlaceHoldersWithDocx(Map<String, String> _placeHolders) throws IOException;

    /**
     *
     * @param _placeHolders
     */
    void replacePlaceHolderWithHtml(Map<String, String> _placeHolders) throws IOException;

    /**
     *
     * @throws IOException
     */
    void save(OutputStream _outputStream) throws IOException;

}

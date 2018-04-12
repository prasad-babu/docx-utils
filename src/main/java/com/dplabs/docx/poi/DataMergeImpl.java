package com.dplabs.docx.poi;

import com.dplabs.docx.DataMerge;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class DataMergeImpl implements DataMerge {
    @Override
    public void replacePlaceHoldersWithDocx(Map<String, String> _placeHolders) throws IOException {
        throw new UnsupportedOperationException("Not yet Implemented");
    }

    @Override
    public void replacePlaceHolderWithHtml(Map<String, String> _placeHolders) throws IOException {
        throw new UnsupportedOperationException("Not yet Implemented");
    }

    @Override
    public void save(OutputStream _outputStream) throws IOException {
        throw new UnsupportedOperationException("Not yet Implemented");
    }
}

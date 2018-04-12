package com.dplabs.docx.docx4j;

import com.dplabs.docx.DataMerge;
import com.dplabs.file.FileRepository;
import org.docx4j.TraversalUtil;
import org.docx4j.XmlUtils;
import org.docx4j.finders.ClassFinder;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTAltChunk;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class DataMergeImpl implements DataMerge {

    private WordprocessingMLPackage mlPackage;
    private FileRepository repository;
    private int chunkSeq;

    public DataMergeImpl(FileRepository _repository, InputStream _inputStream) throws
            Docx4JException,
            IOException {
        repository = _repository;
        mlPackage = WordprocessingMLPackage.load(_inputStream);
    }

    @Override
    public void replacePlaceHoldersWithDocx(Map<String, String> _placeHolders) throws IOException{
        for (Map.Entry<String, String> entry : _placeHolders.entrySet()) {
            try {
                insertDocx(entry.getKey(), entry.getValue());
            } catch (IOException | InvalidFormatException e) {
                throw new IOException(e.getCause());
            }
        }
    }

    protected void insertDocx(String _placeHolder, String _fileName) throws IOException, InvalidFormatException {
        try (InputStream inputStream = repository.read(_fileName)) {
            AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(
                                                new PartName("/part" + (++chunkSeq) + ".docx"));
            afiPart.setContentType(new ContentType(DOCX_CONTENT_TYPE));
            afiPart.setBinaryData(inputStream);

            MainDocumentPart main = mlPackage.getMainDocumentPart();
            Relationship altChunkRel = main.addTargetPart(afiPart);
            CTAltChunk chunk = Context.getWmlObjectFactory().createCTAltChunk();
            chunk.setId(altChunkRel.getId());
            replaceTextWith(main, chunk, _placeHolder);
        }
    }

    @Override
    public void replacePlaceHolderWithHtml(Map<String, String> _placeHolders) throws IOException {
        for (Map.Entry<String, String> entry : _placeHolders.entrySet()) {
            try {
                insertHtml(entry.getKey(), entry.getValue());
            } catch (InvalidFormatException e) {
                throw new IOException(e.getCause());
            }
        }
    }

    protected void insertHtml(String _placeHolder, String _html) throws InvalidFormatException {
        AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(
                                    new PartName("/part" + (++chunkSeq) + ".html"));
        afiPart.setBinaryData(_html.getBytes());
        afiPart.setContentType(new ContentType("text/html"));

        MainDocumentPart main = mlPackage.getMainDocumentPart();
        Relationship altChunkRel = main.addTargetPart(afiPart);

        CTAltChunk chunk = Context.getWmlObjectFactory().createCTAltChunk();
        chunk.setId(altChunkRel.getId() );

        replaceTextWith(main, chunk, _placeHolder);
    }

    protected void replaceTextWith(MainDocumentPart _main, CTAltChunk _chunk, String _placeHolder) {
        ClassFinder finder = new ClassFinder(Text.class) {
            @Override
            public List<Object> apply(Object o) {
                if (o.getClass().equals(typeToFind) && _placeHolder.equals(((Text) o).getValue())) {
                    results.add(o);
                }
                return null;
            }
        };
        new TraversalUtil(_main.getContent(), finder);
        for (Object o : finder.results) {
            Object o2 = XmlUtils.unwrap(o);
            if (o2 instanceof org.docx4j.wml.Text) {
                R r = (R) ((org.docx4j.wml.Text) o2).getParent();
                r.getContent().clear();
                r.getContent().add(_chunk);
            }
        }
    }

    @Override
    public void save(OutputStream _outputStream) throws IOException {
        try {
            mlPackage.save(_outputStream);
        } catch (Docx4JException e) {
           throw new IOException(e.getCause());
        }
    }
}

package gitlet;

import java.io.File;
import java.io.Serializable;

/**
 * Blob class. Represents a snapshot of a file at a specific moment.
 * Includes the serialized contents of the file, the sha1 code of
 * the file's contents, and the name of the file, as well as
 * getter functions for each variable.
 * @author Nicole Ni and Ina Nierotka
 */

public class Blob implements Serializable {
    /** Serialized content of the file. */
    private byte[] content;
    /** Unique sha1 code of the serialized content. */
    private String contentSHA1;
    /** Name of the file blob is created from. */
    private String fileName;

    /**
     * Constructor method. Creates a blob from a given file. Returns void.
     * @param file is the base file.
     */
    public Blob(File file) {
        byte[] contents = Utils.readContents(file);
        this.content = contents;
        this.contentSHA1 = Utils.sha1(content);
        this.fileName = file.getName();
    }

    /** Getter method for file content, returns content. */
    public byte[] getContent() {
        return content;
    }

    /** Getter method for contentSHA1, returns contentSHA1. */
    public String getContentSHA1() {
        return contentSHA1;
    }

    /** Getter method for name of the file, returns fileName. */
    public String getFileName() {
        return fileName;
    }
}

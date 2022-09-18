package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;


/** Represents a gitlet Commit object.
 *  Includes instance variables of the message, parentSHA1, timestamp,
 *  & all the blobs stored. The two class variables are for date format.
 *  Methods are getters for all variables except the date pattern, and
 *  one helper function to Repository.add().
 *
 *  @author Nicole Ni & Ina Nierotka
 */
public class Commit implements Serializable {

    /** The message of this Commit. */
    private String message;
    /** The SHA1 code of the parent Commit. */
    private String parentHash;
    /** the SHA1 code of a secondary parent Commit, used in merge. */
    private String secondParentHash = null;
    /** The time stamp of when the Commit was created. */
    private Date timeStamp = new Date(0);
    /** The map of tracked files and respective blobs stored in the Commit. */
    private TreeMap<String, File> blobs = new TreeMap<>();
    /** The pattern of the date used for printing. */
    private static final String PATTERN = "E MMM d HH:mm:ss yyyy Z";
    /** Variable necessary to implement date format pattern. */
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT =
            new SimpleDateFormat(PATTERN);

    /**
     * Constructor method for the commit. Sets all instance variables
     * except blobs, which is appended in add.
     * @param commitMessage This is a string of the commit message passed in.
     * @param parent This is a string of the SHA1 code of the parent.
     */
    public Commit(String commitMessage, String parent) {
        this.parentHash = parent;
        this.message = commitMessage;
        if (parent != null) {
            this.timeStamp = new Date();
        }
    }
    /** Getter method for time stamp, returns timeStamp. */
    public Date getTimeStamp() {
        return timeStamp;
    }

    /** Getter method for the Commit message, returns message. */
    public String getMessage() {
        return message;
    }

    /** Getter method for the list of blobs, returns blobs. */
    public TreeMap<String, File> getBlobs() {
        return blobs;
    }

    /** Getter method for the parentSHA1, returns parentHash. */
    public String getParentHash() {
        return parentHash;
    }

    /** Getter method for date format, returns SIMPLE_DATE_FORMAT. */
    public static SimpleDateFormat getSimpleDateFormat() {
        return SIMPLE_DATE_FORMAT;
    }

    /** Getter method for secondary parent, returns secondParentHash. */
    public String getSecondParentHash() {
        return secondParentHash;
    }

    /**
     * Setter method for secondary parent. Used in merge, returns void.
     * @param secondParentHashCode This is the SHA1 of the other branch used
     *                          in the merge.
     */
    public void setSecondParentHash(String secondParentHashCode) {
        secondParentHash = secondParentHashCode;
    }

    /**
     * Helper method for Repository.commit(). Actual functionality
     * of adding the blob files to the Commit. Returns void.
     * @param blob is the file of the blob in the Blobs directory
     *                    within .gitlet directory.
     * @param name is the name of the file the blob is created from.
     */
    public void addBlob(String name, File blob) {
        this.blobs.put(name, blob);
    }

    /**override the equals method for Commit object.
     * @param other is the other commit that is passed in
     * @return boolean
     */
    public boolean equals(Commit other) {
        return timeStamp.equals(other.timeStamp);
    }
}

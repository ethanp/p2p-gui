package p2p.file.meta;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import p2p.exceptions.CreateP2PFileException;
import util.Common;

/**
 * Ethan Petuchowski 1/7/15
 */
public class MetaP2PFile {

    protected final StringProperty filename;
    protected final IntegerProperty filesizeBytes;
    protected final StringProperty digest; /* only req'd bc we use 'tracker' as 'index' */

    public String formattedFilesizeString() {
        return Common.formatByteCountToString(getFilesizeBytes());
    }

    public MetaP2PFile(String filename, int filesize, String sha2digest)
            throws CreateP2PFileException
    {
        if (filesize < 0)
            throw new CreateP2PFileException("filesize can't be negative");
        if (filename == null || filename.length() < 1)
            throw new CreateP2PFileException("file must have a name");
        if (sha2digest == null || sha2digest.length() < 1)
            throw new CreateP2PFileException("file must have a digest for verification");

        this.filename = new SimpleStringProperty(filename);
        filesizeBytes = new SimpleIntegerProperty(filesize);
        digest = new SimpleStringProperty(sha2digest);
    }

    public String getFilename() { return filename.get(); }
    public StringProperty filenameProperty() { return filename; }
    public long getFilesizeBytes() { return filesizeBytes.get(); }
    public IntegerProperty filesizeBytesProperty() { return filesizeBytes; }
    public String getDigest() { return digest.get(); }

    public static MetaP2PFile genFake() {
        final String name = "file-"+Common.randInt(1000);
        final int size = Common.randInt(Common.MAX_FILESIZE);
        final String digest = "DEADBEEF";
        try {
            return new MetaP2PFile(name, size, digest);
        }
        catch (CreateP2PFileException e) {
            /* this should never occur (bc the file is FAKE) */
            e.printStackTrace();
        }
        return null;
    }
}

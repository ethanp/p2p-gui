package p2p.file.meta;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import util.Common;

/**
 * Ethan Petuchowski 1/7/15
 */
public class MetaP2PFile {

    protected final StringProperty filename;
    protected final LongProperty   filesizeBytes;
    protected final StringProperty digest; /* only req'd bc we use 'tracker' as 'index' */

    public String formattedFilesizeString() {
        return Common.formatByteCountToString(getFilesizeBytes());
    }

    public MetaP2PFile(String filename, long filesize, String sha2digest) {
        this.filename = new SimpleStringProperty(filename);
        filesizeBytes = new SimpleLongProperty(filesize);
        digest = new SimpleStringProperty(sha2digest);
    }

    public String getFilename() { return filename.get(); }
    public StringProperty filenameProperty() { return filename; }
    public long getFilesizeBytes() { return filesizeBytes.get(); }
    public LongProperty filesizeBytesProperty() { return filesizeBytes; }
    public String getDigest() { return digest.get(); }
}

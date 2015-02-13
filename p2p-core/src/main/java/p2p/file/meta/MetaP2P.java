package p2p.file.meta;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import p2p.exceptions.CreateP2PFileException;
import util.Common;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Ethan Petuchowski 1/7/15
 */
public class MetaP2P {

    protected final StringProperty filename;
    protected final IntegerProperty filesizeBytes;
    protected final StringProperty fileDigest; /* only req'd bc we use 'tracker' as 'index' */
    protected final String[] chunkDigests;
    protected final int numChunks;

    public String formattedFilesizeStr() {
        return Common.formatByteCount(getFilesize());
    }

    public String serializeToString() {
        StringBuilder sb = new StringBuilder();
        for (String chunkDigest : chunkDigests) {
            sb.append(chunkDigest+"\n");
        }
        return getFilename()+"\n"
               +getFilesize()+"\n"
               +getFileDigest()+"\n"
               +sb.toString();
    }

    public MetaP2P(String filename, int filesize, String sha2digest, String[] chunkDigestsArray)
            throws CreateP2PFileException
    {
        if (filesize < 0)
            throw new CreateP2PFileException("filesize can't be negative");
        if (filename == null || filename.length() < 1)
            throw new CreateP2PFileException("file must have a name");
        if (sha2digest == null || sha2digest.length() < 1)
            throw new CreateP2PFileException("file must have a fileDigest for verification");

        this.filename = new SimpleStringProperty(filename);
        filesizeBytes = new SimpleIntegerProperty(filesize);
        fileDigest = new SimpleStringProperty(sha2digest);
        numChunks = numChunksFromFilesize(filesize);
        chunkDigests = chunkDigestsArray;
    }

    private static int numChunksFromFilesize(double filesize) {
        return (int) Math.ceil(filesize/Common.NUM_CHUNK_BYTES);
    }

    public String getFilename() { return filename.get(); }
    public long getFilesize() { return filesizeBytes.get(); }
    public String getFileDigest() { return fileDigest.get(); }
    public int getNumChunks() { return numChunks; }

    public static MetaP2P genFake() {
        final String name = "file-"+Common.randInt(1000);
        final int size = Common.randInt(Common.MAX_FILESIZE);
        final String digest = "DEADBEEF";
        final String[] digestArr = {"MOAR DEADBEEF"};
        try {
            return new MetaP2P(name, size, digest, digestArr);
        }
        catch (CreateP2PFileException e) {
            /* should never occur */
            e.printStackTrace();
        }
        throw new RuntimeException("Shouldn't be here");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetaP2P)) return false;
        MetaP2P file = (MetaP2P) o;
        if (!getFileDigest().equals(file.getFileDigest())) return false;
        if (!getFilename().equals(file.getFilename())) return false;
        if (getFilesize() != file.getFilesize()) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = filename.hashCode();
        result = 31*result+filesizeBytes.hashCode();
        result = 31*result+fileDigest.hashCode();
        return result;
    }

    public static MetaP2P deserializeFromReader(BufferedReader reader)
            throws IOException
    {
        String filename = reader.readLine();
        String filesize = reader.readLine();
        String digest = reader.readLine();
        int filebytes = Integer.parseInt(filesize);
        int numChunks = numChunksFromFilesize(filebytes);
        String[] chunkDigests = new String[numChunks];
        for (int i = 0; i < numChunks; i++) {
            chunkDigests[i] = reader.readLine();
        }
        reader.readLine(); // bc as it stands, we send it with 2 trailing newlines
        try {
            return new MetaP2P(filename, filebytes, digest, chunkDigests);
        }
        catch (CreateP2PFileException e) {
            System.err.println("Could not create MetaP2P with");
            System.err.println("filename: "+filename);
            System.err.println("filesize: "+filesize);
            System.err.println("fileDigest: "+digest);
            System.err.println(e.getMessage());
            return null;
        }
    }
}

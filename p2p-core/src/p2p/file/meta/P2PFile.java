package p2p.file.meta;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import p2p.exceptions.CreateP2PFileException;
import p2p.file.chunk.Chunk;
import p2p.tracker.AbstractRemoteTracker;
import p2p.tracker.swarm.ClientSwarm;
import util.Common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Ethan Petuchowski 1/17/15
 */
public class P2PFile {
    protected final ObjectProperty<File>                localFile;

    public File getLocalFile() { return localFile.get(); }
    public ObjectProperty<File> localFileProperty() { return localFile; }

    protected final ListProperty<AbstractRemoteTracker> knownTrackers;
    protected final ListProperty<ClientSwarm>           swarms;
    protected final IntegerProperty                     bytesPerChunk;
    protected final IntegerProperty                     numChunks;
    protected final ListProperty<Chunk>                 dataChunks;

    protected final MetaP2PFile metaP2PFile;

    public P2PFile(File containingDirectory, MetaP2PFile metaP2PFile) {
        this.metaP2PFile = metaP2PFile;
        localFile = new SimpleObjectProperty<>(
                new File(containingDirectory, metaP2PFile.getFilename()));
        knownTrackers = new SimpleListProperty<>(FXCollections.observableArrayList());
        swarms = new SimpleListProperty<>(FXCollections.observableArrayList());
        bytesPerChunk = new SimpleIntegerProperty(Common.DEFAULT_BYTES_PER_CHUNK);
        numChunks = new SimpleIntegerProperty(
                (int) (metaP2PFile.getFilesizeBytes()/Common.DEFAULT_BYTES_PER_CHUNK));
        dataChunks = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    public static P2PFile importLocalFile(File fsFile) throws CreateP2PFileException {

        if (fsFile.isDirectory())
            throw new CreateP2PFileException("can't import a directory");

        File containingDirectory = fsFile.getParentFile();
        String filename = fsFile.getName();

        long filesize = fsFile.length();

        if (filesize > (long) Common.MAX_FILESIZE)
            throw new CreateP2PFileException("file is too big, max size is "+
                                             Common.formatByteCountToString(Common.MAX_FILESIZE));

        List<Chunk> chunks = new ArrayList<>((int) fsFile.length());

        // read the file
        try (BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(fsFile))) {

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new CreateP2PFileException("file must exist");
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new CreateP2PFileException("problem reading file");
        }
    }

    public P2PFile addTracker(AbstractRemoteTracker tracker) {
        knownTrackers.add(tracker);
        return this;
    }
}

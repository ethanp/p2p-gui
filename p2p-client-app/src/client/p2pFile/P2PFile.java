package client.p2pFile;

import client.tracker.RemoteTracker;
import client.tracker.swarm.ClientSwarm;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import p2p.exceptions.CreateP2PFileException;
import p2p.file.chunk.Chunk;
import p2p.file.meta.MetaP2PFile;
import p2p.peer.ChunksForService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
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
    protected final ListProperty<RemoteTracker>         knownTrackers;
    protected final ListProperty<ClientSwarm>           swarms;
    protected final IntegerProperty                     bytesPerChunk;
    protected final IntegerProperty                     numChunks;
    protected final ListProperty<Chunk>                 dataChunks;
    protected final ObjectProperty<ChunksForService>    availableChunks;

    protected final MetaP2PFile metaP2PFile;

    public P2PFile(File localFile, MetaP2PFile metaP2PFile) {
        this.metaP2PFile = metaP2PFile;
        this.localFile   = new SimpleObjectProperty<>(localFile);
        knownTrackers    = new SimpleListProperty<>(FXCollections.observableArrayList());
        swarms           = new SimpleListProperty<>(FXCollections.observableArrayList());
        bytesPerChunk    = new SimpleIntegerProperty(Common.DEFAULT_CHUNK_SIZE);
        int iChunks      = (int) (metaP2PFile.getFilesizeBytes()/Common.DEFAULT_CHUNK_SIZE);
        numChunks        = new SimpleIntegerProperty(iChunks);
        dataChunks       = new SimpleListProperty<>(FXCollections.observableArrayList());
        availableChunks  = new SimpleObjectProperty<>(new ChunksForService(iChunks));
    }

    /**
     * Create a path on the filesystem for the file that will be downloaded
     * DOES NOT add the file to the User's known Files
     *      because we don't have access to User stuff here
     *      that has to be done in the TrackersCell
     * return a new P2PFile representation of the file
     */
    public static P2PFile newP2PFileInDir(File parentDir, MetaP2PFile metaP2PFile) {
        return new P2PFile(new File(parentDir, metaP2PFile.getFilename()), metaP2PFile);
    }

    // TODO not finished yet
    public static P2PFile importLocalFile(File fsFile) throws CreateP2PFileException {

        if (fsFile.isDirectory())
            throw new CreateP2PFileException("can't import a directory");

        File containingDirectory = fsFile.getParentFile();
        String filename = fsFile.getName();

        long lFilesize = fsFile.length();

        if (lFilesize > (long) Common.MAX_FILESIZE)
            throw new CreateP2PFileException("file is too big, max size is "+
                                             Common.formatByteCountToString(Common.MAX_FILESIZE));
        int filesize = (int) lFilesize;

        List<Chunk> chunks = new ArrayList<>(filesize);

        // TODO read the file
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

        String digest = null; // TODO get digest

        MetaP2PFile meta = new MetaP2PFile(filename, filesize, digest);
        P2PFile toRet = P2PFile.newP2PFileInDir(containingDirectory, meta);

        /* since the file is local, of course ALL chunks are available for service */
        toRet.getAvailableChunks().setAllAsAvailable();

        // TODO implement P2PFile importLocalFile
        throw new NotImplementedException();
        // TODO I suppose this will have to be added to the Main.localFiles AFTER this finishes
    }

    public P2PFile addTracker(RemoteTracker tracker) {
        knownTrackers.add(tracker);
        return this;
    }

    public String getFilename() { return metaP2PFile.getFilename(); }
    public String formattedFilesizeString() { return metaP2PFile.formattedFilesizeString(); }

    public String getCompletenessString() {
        return String.format("%.2f%%",getAvailableChunks().getProportionAvailable());
    }

    public int              getBytesPerChunk()      { return bytesPerChunk.get();   }
    public int              getNumChunks()          { return numChunks.get();       }
    public File             getLocalFile()          { return localFile.get();       }
    public MetaP2PFile      getMetaP2PFile()        { return metaP2PFile;           }
    public ChunksForService getAvailableChunks()    { return availableChunks.get(); }
}

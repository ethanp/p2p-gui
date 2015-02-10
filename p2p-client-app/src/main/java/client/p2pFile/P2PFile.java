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
import p2p.file.meta.MetaP2P;
import p2p.peer.ChunksForService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import util.Common;
import util.SHA2Digest;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Ethan Petuchowski 1/17/15
 */
public class P2PFile {

    protected final ObjectProperty<File>                localFile;
    protected final ListProperty<ClientSwarm>           swarms;
    protected final IntegerProperty                     bytesPerChunk;
    protected final IntegerProperty                     numChunks;
    protected final ObjectProperty<ChunksForService>    availableChunks;

    protected final MetaP2P metaPFile;

    public P2PFile(File localFile, MetaP2P metaP2P) {
        this.metaPFile = metaP2P;
        this.localFile   = new SimpleObjectProperty<>(localFile);
        swarms           = new SimpleListProperty<>(FXCollections.observableArrayList());
        bytesPerChunk    = new SimpleIntegerProperty(Common.NUM_CHUNK_BYTES);
        int iChunks      = (int) Math.ceil((double) metaP2P.getFilesize()/Common.NUM_CHUNK_BYTES);
        numChunks        = new SimpleIntegerProperty(iChunks);
        availableChunks  = new SimpleObjectProperty<>(new ChunksForService(iChunks));
    }

    public static P2PFile importLocalFile(File fsFile) throws CreateP2PFileException, IOException {
        if (!fsFile.exists())
            throw new CreateP2PFileException("you can't import a non-existent file");
        if (fsFile.isDirectory())
            throw new CreateP2PFileException("you can't import a directory");
        if (fsFile.length() > (long) Common.MAX_FILESIZE)
            throw new CreateP2PFileException("file is too big, max size is "+
                                             Common.formatByteCount(Common.MAX_FILESIZE));

        P2PFile toRet = new P2PFile(
                fsFile,
                new MetaP2P(
                        fsFile.getName(),
                        (int) fsFile.length(),
                        SHA2Digest.createDigest(fsFile)
                )
        );

        /* since the file is local, of course ALL chunks are available for service */
        toRet.getAvailableChunks().setAllAsAvailable();

        return toRet;
    }

    public P2PFile addTracker(RemoteTracker tracker) {
        swarms.add(new ClientSwarm(metaPFile, tracker));

        /* should probably listSwarms() on the tracker
         * while adding the tracker itself to the ClientState
         */
        throw new NotImplementedException();
    }

    public boolean hasChunk(int index) {
        return getAvailableChunks().hasIdx(index);
    }

    public Chunk getChunk(int index) throws IOException {
        if (!hasChunk(index))
            return null;
        if (index >= getNumChunks())
            throw new IllegalArgumentException("this file only has "+getNumChunks()+" chunks, " +
                                               "but you wanted number "+index);

        RandomAccessFile file = new RandomAccessFile(getLocalFile(), "r");
        file.seek(index * getBytesPerChunk());

        byte[] buff;
        if (index == getNumChunks()-1) {
            /* last chunk can have different size */
            int lastChunkSize = (int) (getFilesizeBytes() % (long) getBytesPerChunk());
            if (lastChunkSize == 0)
                buff = new byte[getBytesPerChunk()];
            else
                buff = new byte[lastChunkSize];
        }
        else {
            buff = new byte[getBytesPerChunk()];
        }
        int bytesRead = file.read(buff);
        if (bytesRead < buff.length) {
            System.err.println("There was a problem reading the file");
            throw new IOException();
        }
        return new Chunk(buff);
    }

    public File             getLocalFile()          { return localFile.get();                   }
    public MetaP2P getMetaPFile()          { return metaPFile;                         }
    public String           getFilename()           { return metaPFile.getFilename();           }
    public String           formattedFileSizeStr()  { return metaPFile.formattedFilesizeStr();  }
    public long             getFilesizeBytes()      { return metaPFile.getFilesize();      }
    public int              getBytesPerChunk()      { return bytesPerChunk.get();               }
    public int              getNumChunks()          { return numChunks.get();                   }
    public String           getCompletenessString() { return String.format("%.2f%%",getAvailableChunks().getProportionAvailable()); }
    public ChunksForService getAvailableChunks()    { return availableChunks.get();             }
    public void             markChunkAsAvbl(int idx) { getAvailableChunks().setChunkAvailable(idx, true); }
}

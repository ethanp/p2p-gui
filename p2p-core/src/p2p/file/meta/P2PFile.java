package p2p.file.meta;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import p2p.tracker.AbstractRemoteTracker;
import p2p.tracker.swarm.RemoteSwarm;
import util.Common;

import java.io.File;

/**
 * Ethan Petuchowski 1/17/15
 */
public class P2PFile {
    protected final ObjectProperty<File>                localFile;
    protected final ListProperty<AbstractRemoteTracker> knownTrackers;
    protected final ListProperty<RemoteSwarm>           swarms;
    protected final IntegerProperty                     bytesPerChunk;
    protected final IntegerProperty                     numChunks;

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
    }


    public P2PFile addTracker(AbstractRemoteTracker tracker) {
        knownTrackers.add(tracker);
        return this;
    }
}

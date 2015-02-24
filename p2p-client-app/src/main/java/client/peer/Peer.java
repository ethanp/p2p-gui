package client.peer;

import Exceptions.FailedToFindServerException;
import Exceptions.ServersIOException;
import client.p2pFile.FileDownload;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import p2p.exceptions.ConnectToPeerException;
import p2p.exceptions.InvalidDataException;
import p2p.file.MetaP2P;
import p2p.peer.ChunksForService;
import p2p.peer.PeerAddr;
import util.Connection;
import util.StringsOutBytesIn;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static util.Common.secToNano;

/**
 * Ethan Petuchowski 1/18/15
 *
 * This is the Client's understanding of peers who
 * trackers have claimed contain files.
 *
 * It includes what chunks we think the Peer has of which files.
 *
 * We don't check whether the Client is online until
 * we actually HAVE to interact with them.
 */
public class Peer extends PeerAddr implements Runnable {

    private static final long DOWNLOAD_TIMEOUT = secToNano(3);
    private static final int ERRORS_THRESHOLD = 3;

    /**
     * The point of this is to maintain a single open socket to the Peer,
     * which is devoted to requesting specified Chunks from the Peer.
     */
    StringsOutBytesIn chunkConn = new StringsOutBytesIn(getServingAddr());

    /**
     * This is our current understanding of which Chunks of which files
     * the Peer is making available for us to download
     */
    MapProperty<MetaP2P, ChunksForService> chunksOfFiles;

    /**
     * These are the Files the Client is downloading
     * for which this Peer is a potential source of Chunks.
     *
     * It is also known to the FileDownload object that
     * this Peer is a participant in the download.
     */
    Set<FileDownload> ongoingFileDownloads = new HashSet<>();

    /**
     * I don't know whether this belongs here,
     * maybe it belongs in the `DownloadChunkJob`.
     */
    FileDownload fileCurrentlyDownloading;

    /**
     * This class run()s itself as a Thread to which it holds a pointer.
     *
     * The stream of ChunkDownloadJobs is managed by this (Peer) class.
     *
     * It is initiated via new Thread(this).start().
     *
     * We then use the pointer to this thread to interrupt()
     * the download process when ongoingFileDownloads isEmpty.
     *
     * Perhaps I should move this functionality to a separate class. I don't know.
     */
    Thread chunkDownloadQueuerThread;

    /**
     * This is for executing DownloadChunkJobs one at a time.
     */
    ExecutorService chunkDownloadThread = Executors.newSingleThreadExecutor();

    /**
     * Data for computing bandwidth and calculating a progress bar.
     *
     * The total period of time this Client has spent actively downloading Chunks from this Peer,
     * and how many bytes of Chunks were downloaded in that period.
     *
     * Ideally, we should consider that bandwidth consumed by
     * other Peer connections can affect this connections bandwidth.
     */
    long nanosecsDownloading;
    long bytesDownloaded;

    /**
     * Counters of how many times downloading from this Peer has gone haywire.
     * If it happens too much, the Client gives up on downloading from this Peer.
     */
    int countDownloadDataErrors;
    int countTimeouts;

    /**
     * Pipeline multiple simultaneous Availability Update Requests so that it only opens
     * one background connection at a time (to the Chunk Downloads connection).
     *
     * Ideally, this should open a connection that persists across Availability Update Requests.
     */
    ExecutorService availabilityUpdateThread = Executors.newSingleThreadExecutor();

    public Peer(InetSocketAddress peerListenAddr) {
        super(peerListenAddr);
    }

    @Override public void run() {
        while (true) {
            fileCurrentlyDownloading = decideFileToDownloadFrom();
            List<Integer> chunkIdxs = fileCurrentlyDownloading.decideChunksToDownload(this);
            if (chunkIdxs.get(0) == -1) {
                removeCurrentFileFromDownloads();
            }
            downloadChunks(chunkIdxs);
        }
    }

    /**
     * We put each index to download on the "Chunk Download Thread" one at a time
     * waiting for each to complete or timeout before starting the next.
     *
     * They all reuse the `chunkConn` field of this class (Peer) so that connections
     * don't have to be reestablished between completed Chunk Downloads.
     */
    private void downloadChunks(List<Integer> chunkIdxs) {
        for (int idx : chunkIdxs) {
            Callable<Boolean> task = new DownloadChunkJob(fileCurrentlyDownloading, idx, this);
            Future<Boolean> dlResult = chunkDownloadThread.submit(task);
            try {
                dlResult.wait(DOWNLOAD_TIMEOUT);
                boolean madeItToDisk = dlResult.get(3, TimeUnit.SECONDS);

                /* maybe we should update the UI here? */
                if (madeItToDisk) {
                    System.out.println(
                            "Chunk "+idx+" of "+
                            fileCurrentlyDownloading.getPFile().getFilename()+
                            " finished downloading.");
                }
            }
            catch (InterruptedException e) {
                System.err.println("Download "+task.toString()+" was interrupted");
            }
            catch (ExecutionException e) {
                if (e.getCause() instanceof InvalidDataException) {
                    downloadError();
                } else {
                    System.err.println("Unknown error "+e.getClass().getName()+" in "+task.toString());
                    e.printStackTrace();
                }
            }
            catch (TimeoutException e) {
                System.err.println("Download "+task.toString()+" timed out");
                downloadTimeout();
            }
        }
    }

    /**
     * always returns the first download in its local set "ongoingFileDownloads"
     */
    private FileDownload decideFileToDownloadFrom() {
        return ongoingFileDownloads.iterator().next();
    }

    public void connect() throws FailedToFindServerException, ConnectToPeerException, ServersIOException {
        if (!chunkConn.socket.isConnected()) {
            connect(chunkConn);
            if (chunksOfFiles == null)
                chunksOfFiles = new SimpleMapProperty<>();
        }
    }

    public void connect(Connection connection) throws ConnectToPeerException, FailedToFindServerException, ServersIOException {
        connection.connect();
    }

    public boolean hasChunk(int chunkIdx, MetaP2P mFile) {
        return chunksOfFiles.containsKey(mFile)
            && chunksOfFiles.get(mFile).hasIdx(chunkIdx);
    }

    double secondsSpentDownloading() { return nanosecsDownloading / 1E3; }
    double getAverageDownloadSpeed() { return bytesDownloaded / secondsSpentDownloading(); }


    public void addDownload(FileDownload fileDownload) {
        ongoingFileDownloads.add(fileDownload);
        if (ongoingFileDownloads.size() == 1) {
            startDownloading(fileDownload);
        }
    }

    private void startDownloading(FileDownload fileDownload) {
        fileCurrentlyDownloading = fileDownload;
        chunkDownloadQueuerThread = new Thread(this);
        chunkDownloadQueuerThread.start();
    }

    private void removeCurrentFileFromDownloads() {
        stopDownloading(fileCurrentlyDownloading);
        fileCurrentlyDownloading = ongoingFileDownloads.iterator().next();
    }

    public void stopDownloading(FileDownload fileDownload) {
        if (fileDownload != null) {
            ongoingFileDownloads.remove(fileDownload);
            if (ongoingFileDownloads.isEmpty()) {
               chunkDownloadQueuerThread.interrupt();
            }
        }
    }

    public void stopDownloading(MetaP2P metaP2P) {
        FileDownload fd = null;
        for (FileDownload fileDownload : ongoingFileDownloads) {
            if (fileDownload.getMFile().equals(metaP2P)) {
                fd = fileDownload;
                break;
            }
        }
        stopDownloading(fd);
    }

    private boolean didTimeout(long time) {
        return (System.nanoTime() - time) > DOWNLOAD_TIMEOUT;
    }

    /**
     * Maybe this should consider how many other Peer connections there are.
     */
    private void downloadTimeout() {
        if (++countTimeouts > ERRORS_THRESHOLD) {
            System.err.println("Passed timeout threshold");
            // disconnect from peer and don't reconnect for a little while?
        }
        fileCurrentlyDownloading = null;
    }

    void downloadError() {
        if (++countDownloadDataErrors > ERRORS_THRESHOLD) {
            System.err.println("Passed download errors threshold");
            // disconnect from peer and don't reconnect for a little while?
            System.err.println("this does nothing right now");
        }
        fileCurrentlyDownloading = null;
    }

    public boolean addFile(MetaP2P metaP2P) {
        if (!chunksOfFiles.containsKey(metaP2P)) {
            chunksOfFiles.put(metaP2P, new ChunksForService(metaP2P.getNumChunks()));
            return true;
        }
        return false;
    }

    public Set<MetaP2P> getFiles() {
        return chunksOfFiles.keySet();
    }

    public void addFiles(Set<MetaP2P> files) {
        for (MetaP2P file : files)
            addFile(file);
    }

    public void updateChunkAvbl(MetaP2P metaP2P) throws ServersIOException, FailedToFindServerException {
        availabilityUpdateThread.submit(new UpdateChunkAvblJob(metaP2P, this));
    }


    @Override public String toString() {
        return "Peer{"+
               " fileCurrentlyDownloading="+fileCurrentlyDownloading+
               ", nanosecsDownloading="+nanosecsDownloading+
               ", bytesDownloaded="+bytesDownloaded+
               " }";
    }

    public void markAbsent(MetaP2P metaP2P, int idx) {
        chunksOfFiles.get(metaP2P).updateIdx(idx, false);
    }
}


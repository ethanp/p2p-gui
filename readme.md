latex input:    mmd-article-header
Title:          p2p-gui
Author:         Ethan C. Petuchowski
Base Header Level:      1
latex mode:     memoir
Keywords:       Java, JavaFX, Java 8, P2P, BitTorrent, uTorrent
CSS:            http://fletcherpenney.net/css/document.css
xhtml header:   <script type="text/javascript" src="http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML"></script>
latex input:    mmd-natbib-plain
latex input:    mmd-article-begin-doc
latex footer:   mmd-memoir-footer

## TODOs

1. `SimpleServer`'s abstract server should definitely be *multithreaded*
   because---although `Tracker`s don't---each `PeerServer` needs to be able to
   respond to a *pool's* worth of requests at once.
2. The Peer-server's *serving* of `Chunk`s

## How a Client downloads a File

### Relevant Fields

    /* in package 'client' of module 'p2p-client' */
    public class Main {
        static ObservableList<AbstractRemoteTracker> knownTrackers;
    }

    /* in module 'p2p-core' */
    public class AbstractRemoteTracker {
        ListProperty<ClientSwarm> swarms;
    }

    public class ClientSwarm {
        ListProperty<RemotePeer> leechers;
        ListProperty<RemotePeer> seeders;
        ObjectProperty<MetaP2PFile> p2pFile;
        ObjectProperty<AbstractRemoteTracker> tracker;
    }

    public class RemotePeer {
        MapProperty<MetaP2PFile, ChunksForService> chunksOfFiles;
        ObjectProperty<InetSocketAddress> servingAddr;
    }

    public class MetaP2PFile {
        StringProperty filename;
        IntegerProperty filesizeBytes;
        StringProperty digest;
    }

### Mechanism

1. (done) The Client connects to (and instantiates) an `AbstractRemoteTracker`
   by Socket Address and on it calls `ClientSideTrackerProtocol`'s
   `listFiles()` RPC to obtain `ClientSwarm`s to fill in the `Tracker`'s
   `swarms` field.
2. Each of the `Swarm`s has a `MetaP2PFile`.
2. (done) Initially, the `chunksOfFiles` property of each `RemotePeer` in a
   `ClientSwarm` is empty, because the Tracker does not maintain that info (it
   only maintains the `servingAddr`s).
3. User right-clicks on a Tracker's file and selects `Download File`.
    * This triggers `TrackersCell.updateItem().isSwarm()->downloadFile()`.
4. We create a `P2PFile`, and hence a `File` in `Main`'s user-configured
   `localFiles` directory.
4. We also create a `FileDownload` object, which sends a each `RemotePeer`
   (both `seeders` and `leechers`) `PeerTalk` Protocol's `GET_AVAILABILITIES`
   which asks which `Chunk`s each has available for download, and store that
   info in the `RemotePeer`.
5. We determine the "availability" (replication count) of each `Chunk`.
6. For each chunk, choose a random `RemotePeer` to download from.
7. Instantiate `ChunkDownload` objects for each chunk.
8. Submit them all to `Main`'s `fileDownloadPool`.
9. When `ChunkDownload`'s `run()` calls `ClientSideChunkProcotol`'s
   `requestChunk()`, it kicks off `ServerSideChunk`'s `serveChunk()` which is
   asynchronous (starts a new thead to do the serving).
10. Each [some amount] downloaded during `requestChunk()` has its data stored
    in the appropriate offset of the file.
11. If a `ChunkDownload` ever `timeout()`s while receiving a *part* of a
    `Chunk`, it `cancel()`s itself.
12. When a `ChunkDownload` completes, it flips a bit in `FileDownload`'s
    `completeChunks` `BitSet`
13. When the last bit gets flipped, the hash (`MetaP2PFile digest`) is
    compared, and if all goes well the `FileDownload` is finished.

#### Chunk Download Protocol

##### What happens upon calling `ChunkDownload`'s `requestChunk()`

1. `Client` opens socket to `PeerServer`
2. `Client` sends `GET <filename> <chunkIdx>\n\n`
3. `Server` sends `<Chunk.chunkSize>\n\n`
4. `Client` figures out how many times to try to `read()` from the
   `InputStream`
5. `Server` sends `Chunk.data`
6. `Client` loops through, `read()`ing to a medium buffer, updating the
   `ProgressBar` (somewhere), and sometimes dumping the buffer into the local
   file at the appropriate offset.
7. The `Chunk.chunkSize` bytes have been read, the socket is `close()`d

### GUI Events

#### Chunk download progress bars

#### File download progress bars

#### Graph of download speed

1. There could be a global concurrent-safe counter that gets clicked every time
   [some amount] gets downloaded.
2. Then periodically (on the GUI thread) it figures out how much it has changed
   in the last [some timeframe] and updates the GUI's `downloadSpeed`.

### Improvements

1. Prioritize downloading from faster Peers
    * Keep track of `historicAvgSpeed` in `RemotePeer`
2. Cache recently-served chunks (not a lot) in the `PeerServer`

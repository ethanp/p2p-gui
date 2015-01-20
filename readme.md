<sub>
    This readme is for Github and *not* for MultiMarkdown conversion to Latex.
</sub>

## Plan for managing files and transfers

* I *don't* want to host local files available for upload within the program's
  memory
* This means that *instead* I must "serve" *specifiable* chunk-indexes
  directly from the filesystem.
* And I must be able to *write* to specifiable chunk-indices, even when
  previous indices have not been written.
* Let's say that ***within* a `Chunk` bytes are transferred in-order**
* This means I can write bytes *as they're received* into the file
* This will require a remarkably small amount of RAM
* This will likely easily accommodate a caching mechanism for frequently-
  served chunks


## How a Client downloads a File

### Relevant Fields

```java
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
```

### Mechanism

1. (done) The Client creates an `AbstractRemoteTracker` by Socket Address and
   uses the `listFiles()` RPC to obtain `ClientSwarm`s to fill its `swarms`
   field.
2. (done) Initially, the `chunksOfFiles` property of each `RemotePeer` in a
   `ClientSwarm` is empty, because the Tracker does not maintain that info, it
   only maintains the `servingAddr`s
3. User right-clicks on a Tracker's file and selects `Download File`
    * This triggers `TrackersCell.updateItem().isSwarm()->downloadFile()`
4. Create a `FileDownload` object sends a each `RemotePeer` asks both `seeders`
   and `leechers` which Chunks they have available for download and stores that
   info
5. Find the "availability" (replication count) of each file Chunk
6. 

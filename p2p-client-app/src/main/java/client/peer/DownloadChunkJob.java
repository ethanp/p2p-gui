package client.peer;

import p2p.file.meta.MetaP2P;

public class DownloadChunkJob extends PeerWork {
    MetaP2P metaP2P;
    int chunkIdx;

    public DownloadChunkJob(MetaP2P meta, int chunkIndex, Peer peer) {
        super(peer);
        metaP2P = meta;
        chunkIdx = chunkIndex;
    }

    @Override public void run() {}
}

package client.peer;

import Exceptions.FailedToFindServerException;
import Exceptions.ServersIOException;
import p2p.file.meta.MetaP2P;
import p2p.peer.ChunksForService;
import p2p.protocol.fileTransfer.PeerTalk;
import util.StringsOutBytesIn;

import java.io.IOException;

public class UpdateChunkAvblJob extends PeerWork {
        MetaP2P metaP2P;
        StringsOutBytesIn updateAvblConn = new StringsOutBytesIn(peer.getServingAddr());

        UpdateChunkAvblJob(MetaP2P meta, Peer peer) throws ServersIOException, FailedToFindServerException {
            super(peer);
            metaP2P = meta;
            updateAvblConn.connect();
        }

        @Override public void run() {
            try {
                getAvblFor(metaP2P);
            }
            catch (IOException | ServersIOException e) {
                e.printStackTrace();
            }
        }

        private void getAvblFor(MetaP2P metaP2P) throws IOException, ServersIOException {
            updateAvblConn.writer.println(PeerTalk.ToPeer.GET_AVBL);
            updateAvblConn.writer.println(metaP2P.serializeToString());
            ChunksForService chunksForService = ChunksForService.deserialize(updateAvblConn.in);
            synchronized (peer.chunksOfFiles) {
                peer.chunksOfFiles.put(metaP2P, chunksForService);
            }
        }
    }

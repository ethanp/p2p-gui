package p2p.tracker.swarm;

import p2p.file.meta.MetaP2P;
import p2p.peer.PeerAddr;
import p2p.tracker.Tracker;
import util.Common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ethan Petuchowski 1/10/15
 *
 * There are currently 2 subclassers:
 *
 *      1.) TrackerSwarm: the type of Swarm held by a Tracker to help a Client
 *
 *          a) find files to download
 *          b) find peers from whom to download a desired file
 *
 *
 *      2.) ClientSwarm: the type of Swarm held by a Client allowing it to
 *                       decide from whom to download which chunk
 */
public abstract class Swarm<T extends Tracker, P extends PeerAddr> {
    /* CODE */
    protected final List<P> seeders;
    protected final List<P> leechers;
    protected final MetaP2P metaP2P;
    protected T tracker;

    public Swarm(MetaP2P baseMetaP2P, T trkr) {
        metaP2P = baseMetaP2P;
        seeders = new ArrayList<>();
        leechers = new ArrayList<>();
        tracker = trkr;
    }

    public abstract Swarm<T, P> addFakePeers();

    public List<P> getAllPeers() {
        List<P> peers = new ArrayList<>(getLeechers());
        peers.addAll(getSeeders());
        return peers;
    }

    /* GARBAGE */
    public List<P> getSeeders() { return seeders; }
    public void addSeeders(P... seeders) { this.seeders.addAll(Arrays.asList(seeders)); }
    public void addSeeder(P seeder) { seeders.add(seeder); }
    public int numSeeders() { return getSeeders().size(); }

    public List<P> getLeechers() { return leechers; }
    public void addLeechers(P... leechers) { this.leechers.addAll(Arrays.asList(leechers)); }
    public void addLeecher(P leecher) { leechers.add(leecher); }
    public int numLeechers() { return getLeechers().size(); }

    public MetaP2P getMetaP2P() { return metaP2P; }

    public T getTracker() { return tracker; }
    public void setTracker(T tracker) { this.tracker = tracker; }

    public String getFilename() { return getMetaP2P().getFilename(); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Swarm)) return false;
        Swarm swarm = (Swarm) o;
        if (!leechers.equals(swarm.leechers)) return false;
        if (!metaP2P.equals(swarm.metaP2P)) return false;
        if (!seeders.equals(swarm.seeders)) return false;
        if (!tracker.equals(swarm.tracker)) return false;
        return true;
    }

    @Override public int hashCode() {
        int result = leechers.hashCode();
        result = 31*result+seeders.hashCode();
        result = 31*result+metaP2P.hashCode();
        result = 31*result+tracker.hashCode();
        return result;
    }

    public String toCLIString() {
        String formattedSize = Common.formatByteCount(metaP2P.getFilesize());
        return metaP2P.getFilename()+"  "
             + numSeeders()+" seeders  "
             + numLeechers()+" leechers  "
             + formattedSize;
    }

    /**
     * Format:
     * 1) meta,
     * 2) #seeders,
     * 3) seeder addrs,
     * 4) #leechers,
     * 5) leecher addrs
     */
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(getMetaP2P().serializeToString() +"\n");
        sb.append(numSeeders()+"\n");
        for (PeerAddr peer : getSeeders())
            sb.append(peer.addrStr()+"\n");
        sb.append(numLeechers()+"\n");
        for (PeerAddr peer : getLeechers())
            sb.append(peer.addrStr()+"\n");
        return sb.toString();
    }
}

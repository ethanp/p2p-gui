package client.managers;

import client.p2pFile.P2PFile;
import p2p.file.meta.MetaP2P;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;

/**
 * Ethan Petuchowski 2/8/15
 *
 * This object is responsible for knowing AND PERSISTING which
 * files in the OS's file system the user is actively serving.
 *
 * In the future: the default download directory should be saved to and loaded from the disk.
 */
public class LocalFilesManager implements Serializable {

    Collection<P2PFile> localFiles;
    File downloadDirectory;

    public LocalFilesManager() {
        downloadDirectory = new File("/Users/Ethan/Desktop/P2PDownloadDir");
    }

    public Collection<P2PFile> getLocalFiles() {
        return localFiles;
    }

    public File getDownloadsDir() {
        return downloadDirectory;
    }

    public void setDownloadDirectory(File downloadDirectory) {
        this.downloadDirectory = downloadDirectory;
    }

    public void addLocalFile(P2PFile pFile) {
        localFiles.add(pFile);
    }

    public void addLocalFiles(P2PFile... pFiles) {
        for (P2PFile pFile : pFiles)
            localFiles.add(pFile);
    }

    public boolean containsMeta(MetaP2P metaP2P) {
        return localFiles.contains(metaP2P);
    }
}

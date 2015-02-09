package client.managers;

import client.p2pFile.P2PFile;

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
public class FilesManager implements Serializable {

    Collection<P2PFile> localFiles;
    File downloadDirectory;

    public FilesManager() {
        downloadDirectory = new File("/Users/Ethan/Desktop/P2PDownloadDir");
    }

    public Collection<P2PFile> getLocalFiles() {
        return localFiles;
    }

    public File getDownloadDirectory() {
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
}

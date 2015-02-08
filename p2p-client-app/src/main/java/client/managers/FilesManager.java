package client.managers;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;

/**
 * Ethan Petuchowski 2/8/15
 *
 * This object is responsible for knowing AND PERSISTING which
 * files in the OS's file system the user is actively serving.
 */
public class FilesManager implements Serializable {
    Collection<File> localFiles;
    File downloadDirectory;

    public File getDownloadDirectory() {
        return downloadDirectory;
    }

    public void setDownloadDirectory(File downloadDirectory) {
        this.downloadDirectory = downloadDirectory;
    }
}

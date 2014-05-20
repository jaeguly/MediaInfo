package org.mediainfo.android.app;

import android.os.AsyncTask;
import android.widget.TextView;

import org.mediainfo.android.MediaInfo;
import org.mediainfo.android.app.util.FileTreeWalker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Asynchronous task that retrieves a media info from a given file.
 */
public class MediaInfoRetrieverTask extends AsyncTask<String, String, Void> {

    public MediaInfoRetrieverTask(TextView textView) {
        mTextView = textView;
    }

    public MediaInfoRetrieverTask(TextView textView, File outputDir) {
        mOutDir = outputDir;
        mTextView = textView;
    }

    @Override
    /** Override this method to perform a computation on a background thread. */
    protected Void doInBackground(String... paths) {

        // will contain a path of all files of a given path
        List<String> list = new ArrayList<String>();

        if (paths != null) {
            for (String path : paths) {
                new FileTreeWalker(list).walk(new File(path));
            }
        }

        MediaInfo mi = new MediaInfo();

        for (String path : list) {
            // checks cancelled
            if (isCancelled())
                break;

            mi.open(path);
            openOutput(path);

            // checks cancelled
            if (isCancelled())
                break;

            mi.option("Complete", "1");
            mi.option("Inform");

            printOutput("\n#\n# '" + path + "'\n#\n");
            printOutput(mi.inform());

            // checks cancelled
            if (isCancelled())
                break;

            mi.close();
            closeOutput();
        }

        if (isCancelled()) {
            mi.close();
            closeOutput();
        }

        // release all resources of mi
        mi.dispose();

        return null;
    }

    @Override
    /** Runs on the UI thread after publishProgress(Progress...) is invoked. */
    protected void onProgressUpdate(String... infos) {
        for (String info : infos)
            mTextView.append(info);
    }

    @Override
    /** Runs on the UI thread after doInBackground(Params...). */
    protected void onPostExecute(Void result) {
        // TODO: do something
    }

    @Override
    protected void onCancelled() {
        // TODO: do something
    }

    private void openOutput(String file) {
        if (mOutDir != null) {
            try {
                mOutStream = new FileOutputStream(
                        File.createTempFile(new File(file).getName(), ".info", mOutDir)
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void printOutput(String... params) {
        publishProgress(params);

        if (mOutStream != null) {
            for (String param : params) {
                try {
                    mOutStream.write(param.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void closeOutput() {
        if (mOutStream != null) {
            try {
                mOutStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected File mOutDir;
    protected FileOutputStream mOutStream;
    protected TextView mTextView;
}

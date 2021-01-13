package co.luism.iot.web.common;

import co.luism.iot.web.ui.vehicle.alarm.ImportExportComponent;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by luis on 02.02.15.
 */
public class FileUploader implements Upload.Receiver, Upload.SucceededListener {

    public File file;
    private static final Logger LOG = Logger.getLogger(FileUploader.class);
    private ImportExportComponent parent;

    public FileUploader() {

    }

    public FileUploader(ImportExportComponent parent) {
        this.parent = parent;
    }

    public OutputStream receiveUpload(String filename, String mimeType) {

        LOG.debug(String.format("receiveUpload %s", filename));
        parent.setStartProgress("START_UPLOAD");
        // Create upload stream
        FileOutputStream fos = null; // Stream to write to
        URL url = DataTagUploader.class.getClassLoader().getResource("uploads/");
        assert url != null;
        URI uriName;
        try {
            uriName = url.toURI().resolve(filename);
        } catch (URISyntaxException e) {
            LOG.error(e);
            return fos;
        }

        try{

            // Open the file for writing.
            file = new File(uriName);
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            new Notification("Could not open file<br/>",
                    e.getMessage(),
                    Notification.Type.ERROR_MESSAGE)
                    .show(Page.getCurrent());
            return null;
        }


        return fos; // Return the output stream to write to
    }

    public void uploadSucceeded(Upload.SucceededEvent event) {

        LOG.debug(String.format("uploadSucceeded %s", file.getName()));
        // Show the uploaded file in the image viewer
        //image.setVisible(true);
        //image.setSource(new FileResource(file));
        //new Notification("File Uploaded", file.getName(), Notification.Type.ASSISTIVE_NOTIFICATION)
        //        .show(Page.getCurrent());

        parent.setUploadFile(file, event.getSource());

    }

}

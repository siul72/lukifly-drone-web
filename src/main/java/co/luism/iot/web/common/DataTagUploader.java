package co.luism.iot.web.common;/*
  ____        _ _ _                   _____           _
 |  __ \     (_) | |                 / ____|         | |
 | |__) |__ _ _| | |_ ___  ___      | (___  _   _ ___| |_ ___ _ __ ___  ___
 |  _  // _` | | | __/ _ \/ __|      \___ \| | | / __| __/ _ \ '_ ` _ \/ __|
 | | \ \ (_| | | | ||  __/ (__       ____) | |_| \__ \ ||  __/ | | | | \__ \
 |_|  \_\__,_|_|_|\__\___|\___|     |_____/ \__, |___/\__\___|_| |_| |_|___/
                                            __/ /
 Railtec Systems GmbH                      |___/
 6052 Hergiswil

 SVN file informations:
 Subversion Revision $Rev: $
 Date $Date: $
 Commmited by $Author: $
*/

import co.luism.iot.web.ui.administration.BeanUpdateForm;
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
 * OnlineDIagnoseWeb
 * co.luism.diagnostics.web.common
 * Created by luis on 08.10.14.
 * Version History
 * 1.00.00 - luis - Initial Version
 */
public class DataTagUploader implements Upload.Receiver, Upload.SucceededListener {
    public File file;
    private static final Logger LOG = Logger.getLogger(DataTagUploader.class);
    private BeanUpdateForm parent;

    public OutputStream receiveUpload(String filename, String mimeType) {

        LOG.debug(String.format("receiveUpload %s", filename));
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


        parent.setIndeterminateProgress();
        return fos; // Return the output stream to write to
    }

    public void uploadSucceeded(Upload.SucceededEvent event) {

        LOG.debug(String.format("uploadSucceeded %s", file.getName()));
        // Show the uploaded file in the image viewer
        //image.setVisible(true);
        //image.setSource(new FileResource(file));
        new Notification("File Uploaded", file.getName(), Notification.Type.ASSISTIVE_NOTIFICATION)
                .show(Page.getCurrent());

        parent.setDataTagFile(file, event.getSource());

    }

    public BeanUpdateForm getParent() {
        return parent;
    }

    public void setParent(BeanUpdateForm parent) {
        this.parent = parent;
    }
}


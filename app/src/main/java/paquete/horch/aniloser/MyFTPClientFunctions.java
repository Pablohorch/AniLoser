package paquete.horch.aniloser;

import android.content.Context;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;

public class MyFTPClientFunctions {

    public FTPClient mFTPClient = null;

    public boolean ftpDisconnect() {
        try {
            mFTPClient.logout();
            mFTPClient.disconnect();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error occurred while disconnecting from ftp server.");
        }
        return false;
    }

    public boolean ftpConnect(String host, String username, String password, int port) {
        try {
            mFTPClient = new FTPClient();
            mFTPClient.connect(host, port);
            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                boolean status = mFTPClient.login(username, password);

                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();
                return status;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: could not connect to host " + host);
        }
        return false;
    }
    public boolean ftpUpload(String srcFilePath, String desFileName, String desDirectory, Context context) {
        boolean status = false;
        try {

            File file = new File(context.getFilesDir(),"fotoReguistrolistaFTP.jpg");


            if (file.exists())
                Log.e("FTPSTore","Archivco En el movil existente-"+file.getPath());



            FileInputStream srcFileStream = new FileInputStream(file);

            status = mFTPClient.storeFile("prueba.jpg", srcFileStream);

            srcFileStream.close();
            return status;
        } catch (FileNotFoundException i){
            Log.e("FTPSTore","AArchivo para subir no encontrado");
        }catch (IOException e) {
            e.printStackTrace();
            Log.e("Esto es una mierda ", "upload failed: " + e);
        }
        return status;
    }
    public boolean ftpDownload(String directorioFTP, Context context) {
        boolean status = false;
        try {

            File file = new File(context.getFilesDir(),"imagenDescargada.jpg");

            FileOutputStream srcFileStream = new FileOutputStream(file);
            // change working directory to the destination directory
            // if (ftpChangeDirectory(desDirectory)) {
            status = mFTPClient.retrieveFile(directorioFTP, srcFileStream);

            if (file.exists())
                Log.e("FTP ", "Creado");
            else
            Log.e("FTP", "no se cre el archivo");


                        // }
            srcFileStream.close();
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Esto es una mierda ", "upload failed: " + e);
        }
        return status;
    }



}

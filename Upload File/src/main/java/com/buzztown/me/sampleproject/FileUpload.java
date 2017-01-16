package com.buzztown.me.sampleproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.*;

/**
 * Created by Admin on 4/15/2015.
 */
public class FileUpload extends AsyncTask<Void, Void,Void> {
    @Override
    protected Void doInBackground(Void... voids) {
urlUpload();
        return null;
    }

    private void urlUpload(){
        HttpURLConnection connection=null;
        URL url=null;

        try {
            url = new URL("http","192.168.0.106", 8010, "" );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            // Connection
            String crlf = "\r\n", twoHyphens = "--", boundary = "*****";
            Log.d("Opening connection to ", url.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);

            // Request
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            String authHeaderVal = Base64.encodeToString("1:token".getBytes(), Base64.DEFAULT);
            connection.setRequestProperty("Authorization", "Basic " + authHeaderVal);

            DataOutputStream request = new DataOutputStream(connection.getOutputStream());
            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"fileUpload\";filename=\"sample.jpg\"" + crlf);
            request.writeBytes("Content-Type: image/jpg" + crlf);
            request.writeBytes(crlf);
            request.write(getFileBytes());
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
            request.flush();
            request.close();
            Log.d("request ", "closed");

            // Response
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();

            String line = "";
            while ((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }

            Log.d("response:", sb.toString());
            isr.close();
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            connection.disconnect();
        }


    }

    private void httpClientUpload(){
        CloseableHttpClient httpClient = HttpClients.custom().build();

        try {
            HttpPost httpRequest = new HttpPost("http://192.168.0.106:8010/p");
            String authHeader = Base64.encodeToString("1:token".getBytes(), Base64.DEFAULT);
            httpRequest.setHeader("Authorization", "Basic " + authHeader);
            String boundary = "-------------" + System.currentTimeMillis();
            httpRequest.setHeader("Content-type", "multipart/form-data; boundary="+boundary);

            // Attaching a file
            File file = new File("/storage/emulated/0/Download/aoks.jpg");
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            BufferedInputStream buf = new BufferedInputStream(fis);
            byte[] bytes = new byte[(int)file.length()];
            try {
                buf.read(bytes, 0, bytes.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    buf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            HttpEntity httpEntity = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .setBoundary(boundary)
                    .addPart("fileUpload", new ByteArrayBody(bytes, file.getName()))
                            //.addBinaryBody("fileUpload", file)
                    .build();
            httpRequest.setEntity(httpEntity);

            CloseableHttpResponse response = null;
            try {
                Log.d("Executing request", httpRequest.getRequestLine() + "");
                response = httpClient.execute(httpRequest);
                Log.d("Response status", response.getStatusLine()+"");
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(response!=null) {
                    try {
                        response.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] getFileBytes(){
        File file = new File("/storage/emulated/0/Download/aoks.jpg");

        byte[] bytes = new byte[(int) file.length()];
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
        /*BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;                     //Disable Dithering mode
        options.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();*/
    }
}

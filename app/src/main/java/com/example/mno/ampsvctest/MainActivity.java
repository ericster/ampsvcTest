package com.example.mno.ampsvctest;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView txtFileName = (TextView) findViewById(R.id.fileName);
        TextView apkFileName = (TextView) findViewById(R.id.apkName);
        TextView absPathName = (TextView) findViewById(R.id.absPath);
        TextView canPathName = (TextView) findViewById(R.id.canonPath);

        AssetManager assetManager = getAssets();
        try {
            String[] files = assetManager.list("Files");
            String[] apkfiles = assetManager.list("preinstalledapp");

            for(int i=0; i<files.length; i++) {
                txtFileName.append("\n=" + " file=" + " :" + i + "=" + " name=" + "> " + files[i]);
                apkFileName.append("\n=" + " file=" + " :" + i + "=" + " name=" + "> " + apkfiles[i]);
            }

            OutputStream out = null;
            InputStream in = getAssets().open("preinstalledapp/app-debug.apk");
            File outFile = new File(getExternalFilesDir("preinstalledapp"), "myapk.apk");
            out = new FileOutputStream(outFile);
            absPathName.setText(outFile.getAbsolutePath());


            byte[] buffer = new byte[1024];

            int read;
            while((read = in.read(buffer)) != -1) {

                out.write(buffer, 0, read);

            }

            in.close();
            in = null;

            out.flush();
            out.close();
            out = null;



            canPathName.setText(outFile.getCanonicalPath());


        } catch (IOException e) {
            e.printStackTrace();
        }

        deleteThread();
        installThread();

    }

    protected void deleteThread() {

        Thread t = new Thread() {
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                File outFile = new File(getExternalFilesDir("preinstalledapp"), "myapk.apk");
                outFile.delete();

                Handler mainHandler = new Handler(getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        TextView canPathName = (TextView) findViewById(R.id.canonPath);
                        canPathName.setText("deleted");

                    }
                };
                mainHandler.post(myRunnable);

                Log.d("ampsvcTest", "A thread for removing the 2nd apk");
            }
        };

        t.start();
    }

    protected void installThread() {

        Thread t = new Thread() {
            public void run() {
                File outFile = new File(getExternalFilesDir("preinstalledapp"), "myapk.apk");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(outFile), "application/vnd.android.package-archive");
                startActivity(intent);


                Handler mainHandler = new Handler(getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        TextView canPathName = (TextView) findViewById(R.id.canonPath);
                        canPathName.setText("installed");

                    }
                };
                mainHandler.post(myRunnable);

                Log.d("ampsvcTest", "A thread for removing the 2nd apk");
            }
        };

        t.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

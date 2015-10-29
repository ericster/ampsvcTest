package com.example.mno.ampsvctest;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.os.Environment.getExternalStorageDirectory;

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
            absPathName.setText(outFile.getAbsolutePath());
            canPathName.setText(outFile.getCanonicalPath());
            out = new FileOutputStream(outFile);

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

            Intent intent = new Intent(Intent.ACTION_VIEW);


            intent.setDataAndType(Uri.fromFile(outFile), "application/vnd.android.package-archive");
            startActivity(intent);

        } catch (IOException e) {
            e.printStackTrace();
        }

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

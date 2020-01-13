package com.skgleba.rfg2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.Random;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class FullscreenActivity extends AppCompatActivity {
    private static final int UI_ANIMATION_DELAY = 10;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private View mControlsView;
    public String dburl = "https://raw.githubusercontent.com/SKGleba/RFG/master/dbs/db.crypt15";
    public String nfurl = "https://raw.githubusercontent.com/SKGleba/RFG/master/dbs/sr.crypt15";
    public String apkurl = "https://raw.githubusercontent.com/SKGleba/RFG/master/CLIENT-android/cur-rfg.apk";
    private WebView mWebView;
    private PhotoView photoView;
    public byte[] arrout = new byte[19];
    public int wv = 0, random = 0, off = 0, inited = 0, isBigMenu = 0, isFav = 0, fetr = 0;
    FloatingActionButton fabnfo;
    FloatingActionButton more1;
    FloatingActionButton more2;
    FloatingActionButton more3;
    String EntryUrl;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.hide();
            }

            mControlsView.setVisibility(View.GONE);
        }
    };

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        photoView = findViewById(R.id.photo_view);
        mWebView = findViewById(R.id.activity_main_webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        fabnfo = findViewById(R.id.nfo);
        more1 = findViewById(R.id.more1);
        more2 = findViewById(R.id.more2);
        more3 = findViewById(R.id.more3);

        fabnfo.setAlpha(0.20f);
        more1.setAlpha(0f);
        more2.setAlpha(0f);
        more3.setAlpha(0f);

        fabnfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu(2);
            }
        });

        more3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                more3.animate().rotationBy(180);
                more2.animate().rotationBy(45);
                random = 0;
                if (isFav == 1) {
                    isFav = 0;
                } else
                    isFav = 1;
                showMenu(0);
                switchEntry(random);
            }
        });

        more2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                more2.animate().rotationBy(360);
                favorite(isFav);
            }
        });

        more1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentEntry();
                showMenu(0);
            }
        });

        final FloatingActionButton fabnext = findViewById(R.id.next);
        fabnext.setAlpha(0.20f);

        fabnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu(0);
                fabnext.animate().rotationBy(180);
                switchEntry(random);
            }
        });

        // StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        // StrictMode.setThreadPolicy(policy);
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
            Snackbar.make(mContentView, "Updating content dbs...", Snackbar.LENGTH_SHORT).show();
            new DownloadFile().execute(dburl);
            new DownloadFile().execute(nfurl);
            switchEntry(random);
        } else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(1);
    }

    public void showMenu(int show) {
        if (show == 2) {
            if (isBigMenu == 0) {
                isBigMenu = 1;
                fabnfo.setAlpha(1f);
                more1.setAlpha(1f);
                more2.setAlpha(1f);
                more3.setAlpha(1f);
                fabnfo.animate().rotationBy(90);
                more1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
                more2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
                more3.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
            } else {
                isBigMenu = 0;
                fabnfo.setAlpha(0.20f);
                more1.setAlpha(0f);
                more2.setAlpha(0f);
                more3.setAlpha(0f);
                fabnfo.animate().rotationBy(-90);
                more1.animate().translationY(0);
                more2.animate().translationY(0);
                more3.animate().translationY(0);
            }
        } else if (show == 1) {
            if (isBigMenu == 0) {
                isBigMenu = 1;
                fabnfo.setAlpha(1f);
                more1.setAlpha(1f);
                more2.setAlpha(1f);
                more3.setAlpha(1f);
                fabnfo.animate().rotationBy(90);
                more1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
                more2.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
                more3.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
            }
        } else {
            if (isBigMenu == 1) {
                isBigMenu = 0;
                fabnfo.setAlpha(0.20f);
                more1.setAlpha(0f);
                more2.setAlpha(0f);
                more3.setAlpha(0f);
                fabnfo.animate().rotationBy(-90);
                more1.animate().translationY(0);
                more2.animate().translationY(0);
                more3.animate().translationY(0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            new DownloadFile().execute(dburl);
            new DownloadFile().execute(nfurl);
            switchEntry(random);
        } else {
            Snackbar.make(mContentView, "Permissions req err", Snackbar.LENGTH_LONG).show();
        }

        return;
    }

    private class DownloadFile extends AsyncTask<String, String, String> {
        private String fileName;
        private String folder;

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1);

            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                folder = Environment.getExternalStorageDirectory() + File.separator + "rfg/";
                File directory = new File(folder);

                if (!directory.exists())
                    directory.mkdirs();

                OutputStream output = new FileOutputStream(folder + fileName);
                byte data[] = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
                return "Downloaded: " + fileName + " : " + total;
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "Something went wrong: " + fileName;
        }

        @Override
        protected void onPostExecute(String message) {
            Snackbar.make(mContentView, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    public void getCurrentEntry()
    {
        hide();
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "rfg/" + "sr.crypt15");

        int size = (int) file.length();

        if (size <= 0 || off > (size - 1))
            return;

        byte[] byteArr = new byte[20];

        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.skip(off);
            buf.read(byteArr, 0, 19);
            buf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String status = new String(byteArr);
        String Url = String.format("https://twitter.com/skgleba/status/%s", status);
        mWebView.loadUrl(Url);
    }

    public int switchEntry(int cur)
    {
        hide();
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "rfg/" + "db.crypt15");
        int size = (int) file.length();
        if (size <= 0)
            return 1;

        if (isFav == 0) {
            random = new Random().nextInt(size/20);
            while (random == cur) {
                random = new Random().nextInt(size / 20);
            }
        } else {
            File favs = new File(Environment.getExternalStorageDirectory() + File.separator + "rfg/" + "favdb.crypt15");
            int favsz = (int) favs.length();
            if (favsz <= 0)
                return 1;
            byte[] foff = new byte[4];
            foff[0] = 69;
            foff[1] = 69;
            while (foff[0] == 69 && foff[1] == 69) {
                fetr = fetr + 1;
                if (fetr * 4 + 4 > favsz)
                    fetr = 0;
                try {
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(favs));
                    buf.skip(fetr * 4);
                    buf.read(foff, 0, 4);
                    buf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ByteBuffer wrapped = ByteBuffer.wrap(foff);
            random = wrapped.getInt();
        }

        byte[] byteArr = new byte[20];

        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.skip(random * 20);
            buf.read(byteArr, 0, 20);
            buf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.arraycopy(byteArr, 0, arrout, 0, 19);
        String clin = new String(arrout);
        off = (random * 20);

        if (android.provider.Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 0) {
            if (byteArr[19] == 0x34) {
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        if (byteArr[18] == 0x67) {
            if (wv == 2) {
                mWebView.loadUrl("about:blank");
                mWebView.removeAllViews();
                mWebView.setVisibility(View.GONE);
            }
            wv = 1;
            EntryUrl = String.format("https://pbs.twimg.com/media/%s", clin);
            Glide.with(this).load(EntryUrl).into(photoView);
            photoView.setVisibility(View.VISIBLE);
        } else {
            if (wv == 1)
                photoView.setVisibility(View.GONE);
            wv = 2;
            EntryUrl = String.format("https://twitter.com/i/videos/%s", clin);
            mWebView.loadUrl(EntryUrl);
            mWebView.setVisibility(View.VISIBLE);
        }

        if (size == 20 && inited == 0 && isFav == 0) {
            Snackbar.make(mContentView, "Downloading latest .apk...", Snackbar.LENGTH_SHORT).show();
            new DownloadFile().execute(apkurl);
            inited = 1;
            return 1;
        }
        return 1;
    }

    public void favorite(int remove)
    {
        hide();

        if (remove == 0) {
            try {
                byte[] byteArr = ByteBuffer.allocate(4).putInt(random).array();
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + "rfg/" + "favdb.crypt15", true));
                bos.write(byteArr);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                Snackbar.make(mContentView, "Add fav failed...", Snackbar.LENGTH_SHORT).show();
            }
            Snackbar.make(mContentView, "Added to favorites", Snackbar.LENGTH_SHORT).show();
        } else {
            try {
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "rfg/" + "favdb.crypt15");
                int size = (int) file.length();
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                if (fetr == 0)
                    return;
                byte[] fmem = new byte[size];
                buf.read(fmem, 0, size);
                buf.close();
                fmem[fetr * 4] = 69;
                fmem[1 + fetr * 4] = 69;
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + "rfg/" + "favdb.crypt15"));
                bos.write(fmem, 0, size);
                bos.flush();
                bos.close();
                Snackbar.make(mContentView, "Removed from favorites", Snackbar.LENGTH_SHORT).show();
            } catch (IOException e) {
                Snackbar.make(mContentView, "Remove fav failed...", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void hide() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }

        mControlsView.setVisibility(View.GONE);
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}

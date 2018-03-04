package jp.techacademy.shuuhei.sutou.autoslideshowapp;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;


import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;



import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;
    private static final int PERMISSIONS_REQUEST_CODE = 100;

    Timer mTimer;
    TextView mTimerText;
    double mTimerSec = 0.0;
    int count = 0;

    Handler mHandler = new Handler();

    Button mStartStopButton;
    Button mPrevButton;
    Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTimerText = (TextView) findViewById(R.id.timer);
        mStartStopButton = (Button) findViewById(R.id.start_stop_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);
        mNextButton = (Button) findViewById(R.id.next_button);
        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);

        mStartStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper = new ViewFlipper(getApplicationContext());
                //自動でスライドショーを始める
                viewFlipper.setAutoStart(false);
                //スライドショーの感覚を指定する
                viewFlipper.setFlipInterval(1000);


                Log.d("count", "" + count + "");
                if ((count % 2) == 0) {
                    viewFlipper.startFlipping();
                } else {
                    viewFlipper.stopFlipping();
                }
                count += 1;
                //ViewFlipperの生成
                Log.d("aviewFlipper", "" + viewFlipper + "");


                //自動でスライドショーを始める
                viewFlipper.setAutoStart(false);
                //スライドショーの感覚を指定する
                viewFlipper.setFlipInterval(1000);
                Log.d("bviewFlipper", "" + viewFlipper + "");
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showPrevious();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
            }
        });



        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo() {

        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (cursor.moveToFirst()) {
            int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            Long id = cursor.getLong(fieldIndex);
            Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

            ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
            imageVIew.setImageURI(imageUri);

        }
        cursor.close();
    }
}


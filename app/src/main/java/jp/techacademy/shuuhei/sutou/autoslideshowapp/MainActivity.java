package jp.techacademy.shuuhei.sutou.autoslideshowapp;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.widget.Toast;

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
    int count = 0;
    Cursor cursor;

    Handler mHandler = new Handler();

    Button mStartStopButton;
    Button mPrevButton;
    Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartStopButton = (Button) findViewById(R.id.start_stop_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);
        mNextButton = (Button) findViewById(R.id.next_button);

        mStartStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((count % 2) == 0 ){
                    if (mTimer == null) {
                        mTimer = new Timer();
                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        StartStop();
                                    }
                                });
                            }
                        }, 2000, 2000);

                    }
                    mStartStopButton.setText("停止");
                    mPrevButton.setEnabled(false);
                    mNextButton.setEnabled(false);

                    count += 1;
                }
                else{
                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer = null;
                    }
                    mStartStopButton.setText("再生");
                    mPrevButton.setEnabled(true);
                    mNextButton.setEnabled(true);
                    count += 1;

                }

            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prev();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Next();
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
                Toast.makeText(this,"許可してください",Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
    }

    private void first(){
        first2();
        cursor.moveToFirst();
        field();

    }

    private void StartStop(){
            if(cursor.moveToNext()){
                field();
            }else{
                cursor.moveToFirst();
                field();
            }
    }

    private void Prev(){
        if(cursor.moveToPrevious()){
            field();
        }else{
            cursor.moveToLast();
            field();
        }
    }

    private void Next(){
        if(cursor.moveToNext()){
            field();
        }else{
            cursor.moveToFirst();
            field();
        }
    }

    private void getContentsInfo() {
        first();
    }

    private void  first2(){
        // 画像の情報を取得する
        ContentResolver resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );
    }

    private void field(){
        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        Log.d("id", "" + id + "");
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
        imageVIew.setImageURI(imageUri);
    }

}


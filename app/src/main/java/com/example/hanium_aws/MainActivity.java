package com.example.hanium_aws;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import static android.app.PendingIntent.getActivity;


//여기는 처음 부분 oncreate 부분 부터 작동하는 이미지 보여주고 지랄 났죠?

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;
    private ImageView imageView;
    private TextView textview;
    private Button button;
    private EditText edittext;
    Bitmap img;
    Uri selectedImageURI;
    AmazonS3 s3;
    TransferUtility transferUtility;

    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edittext=findViewById(R.id.edit_text);
        imageView = findViewById(R.id.imageView);
        button=findViewById(R.id.upload_btn);

        tedPermission();//permission보여주는 부분 False 일때만 계속 보여주고 True되면  가능
         CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                 getApplicationContext(),
                "us-east-1:17971bfd-0cd5-4f9b-8f3d-37f2bde670af", // Identity pool ID
                Regions.US_EAST_1 // Region
        );
         s3=new AmazonS3Client(credentialsProvider);
         s3.setRegion(Region.getRegion(Regions.US_EAST_1));
         s3.setEndpoint("s3.us-east-1.amazonaws.com");
         //System.out.println("확인해보기 1111111111");


        TransferNetworkLossHandler.getInstance(getApplicationContext());
         transferUtility=new TransferUtility(s3,getApplicationContext());

        //System.out.println("확인해보기 2222222222222");
        imageView.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                //System.out.println("확인해보기 444444444");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //System.out.println("이거 값을 찾아야함"+intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
                //file = new File(getRealPathFromURI(selectedImageURI));
            }

        });

        //System.out.println("확인해보기 333333333333");
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                TransferObserver observer=transferUtility.upload(
                        "textracttest7220",
                        String.valueOf(edittext.getText()+".jpg"),
                        file
                );

            }

        });

    }


//    /**
//     * Get a file path from a Uri. This will get the the path for Storage Access
//     * Framework Documents, as well as the _data field for the MediaStore and
//     * other file-based ContentProviders.
//     *
//     * @param context The context.
//     * @param uri The Uri to query.
//     * @author paulburke
//     */
//    //이 밑부분을 적용하면 text부분까지 액세스 할 수 있는데 문제가 이부분에서 저 Context 변수에다 뭘 넣어야 하는지 모르겠음 링크에 설명도 없고...ㅠ 그래서 그냥 image만 업로드 할 수 있게 만들었다.
//
//
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public static String getPath(final Context context, final Uri uri) {
//
//        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
//
//        // DocumentProvider
//        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
//            // ExternalStorageProvider
//            if (isExternalStorageDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                final String type = split[0];
//
//                if ("primary".equalsIgnoreCase(type)) {
//                    return Environment.getExternalStorageDirectory() + "/" + split[1];
//                }
//
//                // TODO handle non-primary volumes
//            }
//            // DownloadsProvider
//            else if (isDownloadsDocument(uri)) {
//
//                final String id = DocumentsContract.getDocumentId(uri);
//                final Uri contentUri = ContentUris.withAppendedId(
//                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
//
//                return getDataColumn(context, contentUri, null, null);
//            }
//            // MediaProvider
//            else if (isMediaDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                final String type = split[0];
//
//                Uri contentUri = null;
//                if ("image".equals(type)) {
//                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                } else if ("video".equals(type)) {
//                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                } else if ("audio".equals(type)) {
//                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                }
//
//                final String selection = "_id=?";
//                final String[] selectionArgs = new String[] {
//                        split[1]
//                };
//
//                return getDataColumn(context, contentUri, selection, selectionArgs);
//            }
//        }
//        // MediaStore (and general)
//        else if ("content".equalsIgnoreCase(uri.getScheme())) {
//            return getDataColumn(context, uri, null, null);
//        }
//        // File
//        else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            return uri.getPath();
//        }
//
//        return null;
//    }
//
//    /**
//     * Get the value of the data column for this Uri. This is useful for
//     * MediaStore Uris, and other file-based ContentProviders.
//     *
//     * @param context The context.
//     * @param uri The Uri to query.
//     * @param selection (Optional) Filter used in the query.
//     * @param selectionArgs (Optional) Selection arguments used in the query.
//     * @return The value of the _data column, which is typically a file path.
//     */
//    public static String getDataColumn(Context context, Uri uri, String selection,
//                                       String[] selectionArgs) {
//
//        Cursor cursor = null;
//        final String column = "_data";
//        final String[] projection = {
//                column
//        };
//
//        try {
//            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
//                    null);
//            if (cursor != null && cursor.moveToFirst()) {
//                final int column_index = cursor.getColumnIndexOrThrow(column);
//                return cursor.getString(column_index);
//            }
//        } finally {
//            if (cursor != null)
//                cursor.close();
//        }
//        return null;
//    }
//
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is ExternalStorageProvider.
//     */
//    public static boolean isExternalStorageDocument(Uri uri) {
//        return "com.android.externalstorage.documents".equals(uri.getAuthority());
//    }
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is DownloadsProvider.
//     */
//    public static boolean isDownloadsDocument(Uri uri) {
//        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
//    }
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is MediaProvider.
//     */
//    public static boolean isMediaDocument(Uri uri) {
//        return "com.android.providers.media.documents".equals(uri.getAuthority());
//    }
//
//


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String getRealPathFromURI(Uri contentUri) {
        if (contentUri.getPath().startsWith("/storage")) {
            return contentUri.getPath(); }
        String id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
        String[] columns = { MediaStore.Files.FileColumns.DATA };
        String selection = MediaStore.Files.FileColumns._ID + " = " + id;
        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, null);
        try { int columnIndex = cursor.getColumnIndex(columns[0]);
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex);
            }
        } finally {
            cursor.close();
        }
        return null;
    }


////여기는 절대 경로 구하는 곳.
//    private String getRealPathFromURI(Uri contentURI) {
//
//        String result;
//        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
//
//        if (cursor == null) { // Source is Dropbox or other similar local file path
//
//            result = contentURI.getPath();
//
//        } else {
//
//            cursor.moveToFirst();
//
//            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//
//            result = cursor.getString(idx);
//
//            cursor.close();
//
//        }
//
//
//        System.out.print("여기도 되는지도 확인 33333");
//
//        return result;
//
//    }

//권한 요청 부분임.
    private void tedPermission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                android.os.Process.killProcess(android.os.Process.myPid());
                // 권한 요청 실패
            }
        };


        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }


//    //AWS 업로드 부분
//    public void setAmazon(){
//
//        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
//                getApplicationContext(),
//                "us-east-1:17971bfd-0cd5-4f9b-8f3d-37f2bde670af", // Identity pool ID
//                Regions.US_EAST_1 // Region
//        );
//
//        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
//        final TransferUtility transferUtility = new TransferUtility(s3, getActivity());
//        final File file = new File();
//        final TransferObserver observer = transferUtility.upload(
//                "textracttest7220",
//                file.getName(),
//                file,
//                CannedAccessControlList.PublicRead);
//
//        observer.setTransferListener(new TransferListener() {
//            @Override
//            public void onStateChanged(int id, TransferState state) {
//                Log.e("onStateChanged", id + state.name());
//                if (state == TransferState.COMPLETED) {
//                    String url = "https://"+GeneralValues.AMAZON_BUCKET+".s3.amazonaws.com/" + observer.getKey();
//                    Log.e("URL :,", url);
////we just need to share this File url with Api service request.
//                }
//            }
//
//            @Override
//            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//            }
//
//            @Override
//            public void onError(int id, Exception ex) {
//                Toast.makeText(getActivity(), "Unable to Upload", Toast.LENGTH_SHORT).show();
//                ex.printStackTrace();
//            }
//        });
//
//
//    }
//


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());

                    selectedImageURI =data.getData();
                    file=new File(getRealPathFromURI(selectedImageURI));
                    System.out.println("첫번째 부분까지 되는지 확인");
                    System.out.println(selectedImageURI);
                    img = BitmapFactory.decodeStream(in);
                    System.out.println("여기부분 확인해야함");
                    //System.out.print(imageFile);

                    in.close();

                    //img가 사진임 이 이미지를 업로드 해야함.

                    textview = findViewById(R.id.confirm);

                    System.out.print(img);
                    imageView.setImageBitmap(img);
                } catch (Exception e) {

                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }
}

package com.example.rafael_cruz.prototipo.config;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

public class CustomFile {
    Bitmap bitmap;
    public CustomFile() {
        //obtem caminho do arquivo
        File file =  new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/Ecosocial/Profile/user_image.png");
        if (!file.exists()){
            writeImg(bitmap);
        }
    }
    public  void writeImg(Bitmap bmp){
        try {
            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/Ecosocial/Profile";
            File dir = new File(file_path);
            if(!dir.exists())
                dir.mkdirs();
            File file = new File(dir, "user_image.png");
            FileOutputStream fOut = new FileOutputStream(file);

            bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteImg(){
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/Ecosocial/Profile";
        File f0 = new File(dir, "user_image.png");
        boolean d0 = f0.delete();
    }
}

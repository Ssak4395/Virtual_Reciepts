package app_.smartreceipt.digitaldocs.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.IOException;

public class ImageResizer {

    private static int COMPRESSION_NOT_RECOMMENDED = 100;
    private static int MUST_COMPRESS_SIZE = 5;
    private static final int COMPRESSION_RECOMMENDED = 55;






    public static int resolveCompressionRawImage(Uri uri, Context context)
    {
        int SizeOfBitmap =0;
        try{
            Bitmap bmp =  MediaStore.Images.Media.getBitmap(context.getContentResolver(),uri);
            SizeOfBitmap = bmp.getAllocationByteCount();
        }catch (IOException e)
        {
            e.printStackTrace();
        }

        if(SizeOfBitmap / 1024 / 1024 <= 8)
        {
            //Toast.makeText(context,"COMPRESSION IS NOT RECOMMENDED THE IMAGE IS ALREADY VERY SMALL",Toast.LENGTH_LONG).show();
            return  COMPRESSION_NOT_RECOMMENDED;
        }
        if(SizeOfBitmap / 1024 / 1024 >= 20 && SizeOfBitmap / 60 / 60 <= 40)
        {
            //Toast.makeText(context,"COMPRESSION IS  RECOMMENDED THE IMAGE IS RELATIVELY LARGE",Toast.LENGTH_LONG).show();
            return  COMPRESSION_RECOMMENDED;
        }

        if(SizeOfBitmap / 1024 / 1024 >= 40)
        {
            //Toast.makeText(context,"COMPRESSION IS ABSOLUTELY RECOMMENDED",Toast.LENGTH_LONG).show();
            return  MUST_COMPRESS_SIZE;
        }


        return 0;
    }

    public static int resolveCompressionCroppedImage(Uri uri, Context context)
    {
        //TO-DO
    return 0;
    }


}

package net.coahr.three3.three.Util.ImageFactory;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import net.coahr.three3.three.Util.OtherUtils.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;


import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.graphics.Bitmap.Config.RGB_565;

/**
 * Created by 李浩 on 2018/3/19.
 */
    public class ZipImageFactory {
    private static ZipImageFactory zipImageFactory;

    public static ZipImageFactory getInstance() {
        if (zipImageFactory == null) {
            zipImageFactory = new ZipImageFactory();
        }
        return zipImageFactory;
    }

    /**
     * 通过图像路径获取位图
     *
     * @param imgPath
     * @return
     */
    public Bitmap getBitmap(String imgPath) {
        // 通过图像路径获取位图
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        // Do not compress
        newOpts.inSampleSize = 1;
        newOpts.inPreferredConfig = RGB_565;
        return BitmapFactory.decodeFile(imgPath, newOpts);
    }

    /**
     * 压缩图片,并保存
     *
     * @param bitmap
     * @param outPath
     * @throws FileNotFoundException
     */
    public void storeImage(Bitmap bitmap, String outPath)  {
        FileOutputStream   os=null;
        try {
            os = new FileOutputStream(outPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                os.flush();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 按尺寸压缩图片,并将图片生成指定的文件夹
     * Used to get thumbnail
     *
     * @param imgPath image path
     *                图片路径
     * @param pixelW  target pixel of width
     *                需要的宽
     * @param pixelH  target pixel of height
     *                需要的高
     * @return
     */
    public Bitmap ratio(String imgPath, float pixelW, float pixelH) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = ARGB_8888;
        /**
          ALPHA_8 代表8位Alpha位图
         ARGB_4444 代表16位ARGB位图
         ARGB_8888 代表32位ARGB位图
         RGB_565 代表16位RGB位图
         位图位数越高代表其可以存储的颜色信息越多，当然图像也就越逼真
         */
        // Get bitmap info, but notice that bitmap is null now
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 想要缩放的目标尺寸
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        // 压缩好比例大小后再进行质量压缩
//        return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    /**
     * 按尺寸压缩位图图片
     * Used to get thumbnail
     *
     * @param bitmap 位图
     * @param pixelW target pixel of width
     * @param pixelH target pixel of height
     * @return
     */
    public Bitmap ratio(Bitmap bitmap, float pixelW, float pixelH) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, os);
        if (os.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            os.reset();//重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, os);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = RGB_565;
        Bitmap bitmap2 = BitmapFactory.decodeStream(is, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        //压缩好比例大小后再进行质量压缩
//      return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

    /**
     * 按质量压缩位图，并将图像生成指定的路径
     *
     * @param image
     * @param outPath
     * @param maxSize target will be compressed to be smaller than this size.(kb)
     * @throws IOException
     */
    public void compressAndGenImage(Bitmap image, String outPath, int maxSize) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        int options = 100;
        // Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.JPEG, options, os);
        // Compress by loop
        while (os.toByteArray().length / 1024 > maxSize) {
            // Clean up os
            os.reset();
            // interval 10
            options -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, options, os);
        }

        // Generate compressed image file
        FileOutputStream fos = new FileOutputStream(outPath);
        fos.write(os.toByteArray());
        fos.flush();
        fos.close();
    }

    /**
     * 按质量压缩图片，并将图像生成指定的路径
     *
     * @param imgPath
     * @param outPath
     * @param maxSize     target will be compressed to be smaller than this size.(kb)
     * @param needsDelete Whether delete original file after compress
     * @throws IOException
     */
    public void compressAndGenImage(String imgPath, String outPath, int maxSize, boolean needsDelete) throws IOException {
        compressAndGenImage(getBitmap(imgPath), outPath, maxSize);

        // Delete original file
        if (needsDelete) {
            File file = new File(imgPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 按照尺寸比例生成图片,并保存到指定文件路径
     *
     * @param image
     * @param outPath
     * @param pixelW  target pixel of width
     * @param pixelH  target pixel of height
     * @throws FileNotFoundException
     */
    public String ratioAndGenThumb(Bitmap image, String outPath,String outName, float pixelW, float pixelH) throws FileNotFoundException {
        File outFile=new File(outPath,outName);
        String path = outFile.getPath();
        Bitmap bitmap = ratio(image, pixelW, pixelH);
        storeImage(bitmap, path);
        return path;
    }

    /**
     * 按照尺寸比例生成图片,并保存
     * 是否删除原图
     *
     * @param imgPath
     * @param outPath
     * @param pixelW      target pixel of width
     * @param pixelH      target pixel of height
     * @param needsDelete 压缩后是否删除原始文件
     * @throws FileNotFoundException
     */
    public String  ratioAndGenThumb(String imgPath, String outPath,String outName, float pixelW, float pixelH, boolean needsDelete) throws FileNotFoundException {
        File outFile=new File(outPath,outName);
        String path = outFile.getPath();
        Bitmap bitmap = ratio(imgPath, pixelW, pixelH);
        storeImage(bitmap, path);

        // Delete original file
        if (needsDelete) {
            File file = new File(imgPath);
            if (file.exists()) {
                file.delete();
            }
        }
        return path;
    }

    /**
     *  批量尺寸压缩
     * @param imgPath
     *                  压缩图片的地址
     * @param outPath
     *                   压缩后保存地址
     * @param pixelW
     * @param pixelH
     */
    public List<File> ratioAndFileName(HashSet<String> imgPath, String outPath, float pixelW, float pixelH){
        List<File> zipImageFile=new ArrayList<>();
        List<String> result = new ArrayList<>(imgPath);
        for (int i = 0; i <result.size() ; i++) {
            int index=i+1;
            File outFile=new File(outPath, FileUtils.getSystemTime_str()+"_"+index+".jpg");
            String path = outFile.getPath();
            Bitmap bitmap = ratio(result.get(i), pixelW, pixelH);
            storeImage(bitmap, path);
            zipImageFile.add(index-1,outFile);
            System.out.println("得到的压缩图片路径为："+zipImageFile.get(i));
        }
        return zipImageFile;
    }
    }
package net.coahr.three3.three.NetWork;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by yuwei on 2018/3/16.
 */

public class FileUtils {

    public static byte[] getBlock(long offset , File file , int blockSize)
    {
        byte[] result = new byte[blockSize];
        RandomAccessFile accessFile = null;
        try {
            accessFile = new RandomAccessFile(file , "r");
            accessFile.seek(offset);
            int readSize = accessFile.read(result);
            if (readSize == -1)
            {
                return null;
            }
            else if(readSize == blockSize)
            {
                return result;
            }
            else
            {
                byte[] tmpByte = new byte[readSize];
                System.arraycopy(result , 0 , tmpByte ,0 ,readSize );
                return tmpByte;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (accessFile != null)
            {
                try {
                    accessFile.close();

                }catch (IOException e)
                {

                }
            }
        }
        return null;

    }
}

package com.tiilii.fs2.ws;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by hunter on 2017/7/14.
 */
public class FileUtil {
    /**
     * 往文件中写入一行数据，换行
     * @param path
     * @param partNumber
     * @param tempFilePath
     * @throws IOException
     */
    public static void writeOneLineToFile(String path, int partNumber, String tempFilePath)
            throws IOException{
                File file = new File(path);
                FileWriter fw = new FileWriter(file, true);
                fw.write("\n" + partNumber + "=" + tempFilePath);
                fw.close();
    }

    /**
     * 删除目录及下面所有文件
     * @param file
     */
    public static void deleteFile(File file) {
        if (file.isFile()) {   //表示该文件不是文件夹
            file.delete();
        } else {
            //首先得到当前的路径
            String[] childFilePaths = file.list();
            for (String childFilePath : childFilePaths) {
                File childFile = new File(file.getAbsolutePath() + "\\" + childFilePath);
                deleteFile(childFile);
            }
            file.delete();
        }
    }
}

package com.tiilii.fs2.ws;

import com.alibaba.fastjson.JSON;
import com.tiilii.fs2.Util;
import com.tiilii.fs2.ws.exception.ParameterException;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hunter on 2017/7/12.
 */
@Component(value = "fsServiceB")
@WebService(endpointInterface = "com.tiilii.fs2.ws.IFSService",
        targetNamespace = "http://service.fs2.tiilii.com/")
public class FSServiceImpl implements IFSService {

    public String uploadPart(int partNumber, byte[] partData, int tempLength,  String name, String uuidName) {

        Map<String, Object> result = new HashMap<String, Object>();
        try {
            if(partNumber <= 0)
                throw new ParameterException("partNumber参数出错");
            if(partData == null || partData.length <= 0)
                throw new ParameterException("partData参数出错");
            if(name == null || "".equals(name))
                throw new ParameterException("name参数出错");

            if(uuidName == null || "".equals(uuidName)){
                // 随机生成临时文件夹名, 文件合并后的文件名
                uuidName = UUID.randomUUID().toString();
            }
            // 文件临时文件目录
            String tempFileName = "D:/fs-target" + "/"+ uuidName + "/";

            String configFilePath = tempFileName +"config.properties";
            File configFile = new File(configFilePath);
            // 如果路径不存在,则创建
            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }
            if(!configFile.exists()){
                configFile.createNewFile();
            }

            // 分割后的文件路径+文件名
            String targetFilePath =
                    tempFileName + "part_" + (partNumber);

            File targetFile = new File(targetFilePath);
            // 如果路径不存在,则创建
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            targetFile.createNewFile();
            OutputStream ops = new FileOutputStream(targetFile); //分割后文件
            ops.write(partData, 0, tempLength); //将信息写入碎片文件
            ops.close(); //关闭碎片文件

            //写入配置文件
            FileUtil.writeOneLineToFile(configFilePath, partNumber,targetFilePath);

            addSuccess(result);
            result.put("info", new WsResultSuccessInfo(partNumber,tempLength,name, uuidName));

        }catch (ParameterException e){
            addParameterException(result, e);
        }catch (Exception e){
            addException(result, e);
        }
        return JSON.toJSONString(result);
    }

    public String combine(String uuidName, String fileExtension){

        Map<String, Object> result = new HashMap<String, Object>();
        try {
            SimpleDateFormat yyMMdd = new SimpleDateFormat("yyMMdd");
            SimpleDateFormat HHmm = new SimpleDateFormat("HHmm");
            Date date = new Date();

            if (uuidName == null || "".equals(uuidName))
                throw new ParameterException("uuidName不能为空或者空串");
            if (fileExtension == null || "".equals(fileExtension))
                throw new ParameterException("fileExtension不能为空或者空串");

            String directoryPath = "D:/fs-target/" + uuidName;
            Properties config = new Properties();
            InputStream ips = null;
            ips = new FileInputStream(new File(directoryPath + "/" + "config.properties"));
            config.load(ips);
            ips.close();
            Set keySet = config.keySet();//需要将keySet转换为int型
            //将keySet迭代出来,转换成int类型的set,排序后存储进去
            Set<Integer> intSet = new TreeSet<Integer>();
            Iterator iterString = keySet.iterator();
            while (iterString.hasNext()) {
                String tempKey = (String) iterString.next();
                if ("name".equals(tempKey)) {
                } else {
                    int tempInt;
                    tempInt = Integer.parseInt(tempKey);
                    intSet.add(tempInt);
                }
            }

            Set<Integer> sortedKeySet = new TreeSet<Integer>();
            sortedKeySet.addAll(intSet);

            String classify = null;
            String imgArr = Util.getSystemProperty("imgArr");
            String videoArr = Util.getSystemProperty("videoArr");
            if(imgArr.indexOf(fileExtension) > 0){
                classify = "image";
            }else if(videoArr.indexOf(fileExtension) > 0){
                classify = "video";
            }else{
                classify = "others";
            }
            // 组合的文件路径
            String fileDisSubPath = "/upload/" + classify +
                    "/"+ yyMMdd.format(date) + "/" + HHmm.format(date) +
                    "/" + uuidName + "." + fileExtension;

            File disFile = new File(Util.getRootPath() + fileDisSubPath);
            // 如果路径不存在,则创建
            if (!disFile.getParentFile().exists()) {
                disFile.getParentFile().mkdirs();
            }
            disFile.createNewFile();
            OutputStream eachFileOutput = new FileOutputStream(disFile);

            Iterator iter = sortedKeySet.iterator();
            while (iter.hasNext()) {
                String key = new String("" + iter.next());
                if (key.equals("name")) {
                } else {
                    String fileNumber = null;
                    String filePath = null;
                    fileNumber = key;
                    filePath = config.getProperty(fileNumber);
                    //循环读取文件 --> 依次写入
                    InputStream eachFileInput = null;

                    eachFileInput = new FileInputStream(new File(filePath));

                    byte[] buffer = new byte[1024 * 1024 * 1];  //缓冲区文件大小为1M
                    int len = 0;
                    while ((len = eachFileInput.read(buffer, 0, 1024 * 1024 * 1)) != -1) {
                        eachFileOutput.write(buffer, 0, len);
                    }
                    eachFileInput.close();
                }
            }
            eachFileOutput.close();

            FileUtil.deleteFile(new File(directoryPath));
            result.put("visitPath", "http://59.175.213.89:9999/fs2" + fileDisSubPath);
            addSuccess(result);

        }catch (ParameterException e){
            addParameterException(result, e);
        }catch (Exception e){
            addException(result, e);
        }
        return JSON.toJSONString(result);
    }

    private void addSuccess(Map<String, Object> map){
        map.put("code", 0);
        map.put("desc", "success");
    }
    private void addParameterException(Map<String,Object> map, ParameterException e){
        map.put("exception", getExceptionMessage(e));
        map.put("code", "TiiFS-ERR0002");
        map.put("desc", "参数异常：  " + e.getMessage());
    }
    private void addException(Map<String,Object> map, Exception e){
        map.put("exception", getExceptionMessage(e));
        map.put("code", "TiiFS-ERR0001");
        map.put("desc", "系统异常！");
    }
    public String getExceptionMessage(Exception e) {
        return "FS==> message:" + e.getMessage() + ",StackTrack:" + e.getStackTrace();
    }
}

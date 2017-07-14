package com.tiilii.fs2.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Created by hunter on 2017/7/12.
 */
@WebService
public interface IFSService {

    @WebMethod
    String uploadPart(@WebParam int partNumber,   // 部分数据编号
                      @WebParam byte[] partData,  // 部分数据二进制
                      @WebParam int tempLength,   // 数据长度
                      @WebParam String name,      // 上传源文件名
                      @WebParam String uuidName); // 文件重命名

    @WebMethod
    String combine(@WebParam String uuidName,        //  文件重命名
                   @WebParam String fileExtension);  // 文件扩展名
}

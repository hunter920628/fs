﻿<!DOCTYPE html>
<html>
<meta charset="UTF-8">
<title>使用webuploader上传</title>
<!-- 1.引入文件 -->
<link rel="stylesheet" type="text/css" href="/css/webuploader.css">
<script type="text/javascript" src="/js/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="/js/webuploader/webuploader.js"></script>
<style type="text/css">
    #dndArea {  width: 200px;  height: 100px;  border-color: red;  border-style: dashed;  }
</style>
</head>
<body>
<!-- 2.创建页面元素 -->
<div id="upload">
    <div id="filePicker">文件上传</div>
</div>
<div id="fileList"></div>
<!-- 创建用于拖拽的区域 -->
<div id="dndArea">拖拽图片到此框中</div>
<!-- 3.添加js代码 -->
<script type="text/javascript">
    var ext;
    var uploader = WebUploader.create(
        {
            swf:"/js/webuploader/Uploader.swf",
            server:"/FileUploadServlet",
            pick:"#filePicker",
            auto:true,
            dnd:"#dndArea",
            disableGlobalDnd:true,
            paste:"#uploader",
            // 分块上传设置
            // 是否分块
            chunked:true,
            // 每块文件大小（默认5M）
            chunkSize:5*1024*1024,
            // 开启几个并非线程（默认3个）
            threads:3,
            // 在上传当前文件时，准备好下一个文件
            prepareNextFile:true,
            // 只允许选择图片文件。
            accept: {
                title: "Images",
                extensions: "gif,jpg,jpeg,bmp,png",
                mimeTypes: "image/*"
            }
        }
    );
    function returnExt(file) {
        filename = file.name;
        var index1 = filename.lastIndexOf(".");
        var index2 = filename.length;
        ext = filename.substring(index1, index2);//后缀名
        return ext;
    };

    // 生成缩略图和上传进度
    uploader.on("fileQueued", function(file) {
            ext = returnExt(file);

            // 把文件信息追加到fileList的div中
            $("#fileList").append("<div id='" + file.id + "'><img/><span>" + file.name + "</span><div><span class='percentage'><span></div></div>")

            // 制作缩略图
            // error：不是图片，则有error
            // src:代表生成缩略图的地址
            uploader.makeThumb(file, function(error, src) {
                if (error) {
                    $("#" + file.id).find("img").replaceWith("<span>无法预览&nbsp;</span>");
                } else {
                    $("#" + file.id).find("img").attr("src", src);
                }
            });
        }
    );

    // 监控上传进度
    // percentage:代表上传文件的百分比
    uploader.on("uploadProgress", function(file, percentage) {
        $("#" + file.id).find("span.percentage").text(Math.round(percentage * 100) + "%");
    });

    // 监听分块上传的时间点，断点续传
    var fileMd5;
    WebUploader.Uploader.register({
            "before-send-file":"beforeSendFile",
            "before-send":"beforeSend",
            "after-send-file":"afterSendFile"
        },{
            beforeSendFile:function(file) {
                // 创建一个deffered,用于通知是否完成操作
                var deferred = WebUploader.Deferred();

                // 计算文件的唯一标识，用于断点续传和妙传
                (new WebUploader.Uploader()).md5File(file, 0, 5*1024*1024)
                    .progress(function(percentage){
                        $("#"+file.id).find("span.state").text("正在获取文件信息...");
                    })
                    .then(function(val) {
                        fileMd5 = val;
                        $("#" + file.id).find("span.state").text("成功获取文件信息");
                        // 放行
                        deferred.resolve();
                    });
                // 通知完成操作
                return deferred.promise();
            },
            beforeSend:function(block) {
                var deferred = WebUploader.Deferred();

                // 支持断点续传，发送到后台判断是否已经上传过
                $.ajax(
                    {
                        type:"POST",
                        url:"/UploadActionServlet?action=checkChunk",
                        data:{
                            // 文件唯一表示
                            fileMd5:fileMd5,
                            // 当前分块下标
                            chunk:block.chunk,
                            // 当前分块大小
                            chunkSize:block.end-block.start
                        },
                        dataType:"json",
                        success:function(response) {
                            if(response.ifExist) {
                                // 分块存在，跳过该分块
                                deferred.reject();
                            } else {
                                // 分块不存在或不完整，重新发送
                                deferred.resolve();
                            }
                        }
                    }
                );
                // 发送文件md5字符串到后台
                this.owner.options.formData.fileMd5 = fileMd5;
                return deferred.promise();
           },
            afterSendFile:function() {
                // 通知合并分块
                $.ajax(
                    {
                        type:"POST",
                        url:"/UploadActionServlet?action=mergeChunks",
                        data:{
                            fileMd5:fileMd5,
                            ext:ext
                        },
                        dataType:"json",
                        success:function(response){
                            alert(response.visitPath);
                        }
                    }
                );
            }
        }
    );
</script>
</body>
</html>
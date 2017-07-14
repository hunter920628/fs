package com.tiilii.fs2.ws;

/**
 * Created by hunter on 2017/7/12.
 */
public class WsResultSuccessInfo {
    private int partNum;
    private int tempLength;
    private String name;
    private String uuidName;

    public WsResultSuccessInfo() {
    }

    public WsResultSuccessInfo(int partNum, int tempLength, String name, String uuidName) {
        this.partNum = partNum;
        this.tempLength = tempLength;
        this.name = name;
        this.uuidName = uuidName;
    }

    public int getPartNum() {
        return partNum;
    }

    public void setPartNum(int partNum) {
        this.partNum = partNum;
    }

    public int getTempLength() {
        return tempLength;
    }

    public void setTempLength(int tempLength) {
        this.tempLength = tempLength;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuidName() {
        return uuidName;
    }

    public void setUuidName(String uuidName) {
        this.uuidName = uuidName;
    }
}

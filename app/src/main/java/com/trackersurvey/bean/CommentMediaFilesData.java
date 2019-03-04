package com.trackersurvey.bean;

/**
 * Created by zh931 on 2018/5/11.
 * @author Eaa
 */

public class CommentMediaFilesData {
    private int fileID;
    private String fileName;   //文件路径名
    private String dateTime;  //对应评论的主键
    private int fileType;   //文件类型  图片1，视频2，音频3
    private String thumbnailName;//缩略图路径

    private int fileNo;     //文件编号

    public static final int TYPE_PIC = 1;
    public static final int TYPE_VIDEO = 2;
    public static final int TYPE_AUDIO =3;

    public CommentMediaFilesData() {
    }

    public CommentMediaFilesData(int fileNo, String fileName, String dateTime, int fileType, String thumbnailName, int fileID) {
        this.fileName = fileName;
        this.dateTime = dateTime;
        this.fileType = fileType;
        this.thumbnailName = thumbnailName;
        this.fileID = fileID;
        this.fileNo= fileNo;
    }

    public int getFileID() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getThumbnailName() {
        return thumbnailName;
    }

    public void setThumbnailName(String thumbnailName) {
        this.thumbnailName = thumbnailName;
    }

    public int getFileNo() {
        return fileNo;
    }

    public void setFileNo(int fileNo) {
        this.fileNo = fileNo;
    }

    @Override
    public String toString() {
        return "CommentMediaFilesData{" +
                "fileID=" + fileID +
                ", fileName='" + fileName + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", fileType=" + fileType +
                ", thumbnailName='" + thumbnailName + '\'' +
                ", fileNo=" + fileNo +
                '}';
    }
}

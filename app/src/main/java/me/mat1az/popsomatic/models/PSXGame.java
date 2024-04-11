package me.mat1az.popsomatic.models;

import java.io.Serializable;

import me.mat1az.popsomatic.dto.VCDHeader;

public class PSXGame implements Serializable {

    private String ID, name, DBTitle, size, dir;
    private byte[] data;
    private boolean isDB;
    private LaunchType launchType;
    private VCDHeader VCDHeader;

    public PSXGame(String ID, String DBTitle, String size, byte[] data, VCDHeader VCDHeader) {
        this.ID = ID;
        this.DBTitle = DBTitle;
        this.size = size;
        this.data = data;
        this.VCDHeader = VCDHeader;
    }

    public boolean isDB() {
        return isDB;
    }

    public void setDB(boolean DB) {
        this.isDB = DB;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDBTitle() {
        return DBTitle;
    }

    public void setDBTitle(String DBTitle) {
        this.DBTitle = DBTitle;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public LaunchType getLaunchType() {
        return launchType;
    }

    public void setLaunchType(LaunchType launchType) {
        this.launchType = launchType;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public VCDHeader getVCDHeader() {
        return VCDHeader;
    }

    public void setVCDHeader(VCDHeader VCDHeader) {
        this.VCDHeader = VCDHeader;
    }

    @Override
    public String toString() {
        return "PSXGame{" +
                "ID='" + ID + '\'' +
                ", title='" + name + '\'' +
                ", DBTitle='" + DBTitle + '\'' +
                ", size='" + size + '\'' +
                ", dir='" + dir + '\'' +
                ", isDB=" + isDB +
                ", launchType=" + launchType +
                ", VCDHeader=" + VCDHeader +
                '}';
    }

    public String info() {
        return  ID + '\n' +
                name + '\n' +
                DBTitle + '\n' +
                size + '\n' +
                dir;
    }
}

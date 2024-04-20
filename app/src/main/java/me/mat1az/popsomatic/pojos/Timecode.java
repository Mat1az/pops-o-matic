package me.mat1az.popsomatic.pojos;

public class Timecode {
    byte MM,SS,FF;

    public Timecode(byte MM, byte SS, byte FF) {
        this.MM = MM;
        this.SS = SS;
        this.FF = FF;
    }

    public byte getMM() {
        return MM;
    }

    public void setMM(byte MM) {
        this.MM = MM;
    }

    public byte getSS() {
        return SS;
    }

    public void setSS(byte SS) {
        this.SS = SS;
    }

    public byte getFF() {
        return FF;
    }

    public void setFF(byte FF) {
        this.FF = FF;
    }

    @Override
    public String toString() {
        return "TimeCode{" +
                "MM=" + MM +
                ", SS=" + SS +
                ", FF=" + FF +
                '}';
    }
}
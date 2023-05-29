package com.example.testapp;

public class Buah {
    private String nama;
    private String desc;
    public Buah(String setnama, String setdesc){
        this.nama = setnama;
        this.desc = setdesc;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getNama() {
        return nama;
    }

    public String getDesc() {
        return desc;
    }

}

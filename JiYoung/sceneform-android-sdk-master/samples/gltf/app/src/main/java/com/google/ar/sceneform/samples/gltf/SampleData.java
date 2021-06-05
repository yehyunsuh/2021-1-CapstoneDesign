package com.google.ar.sceneform.samples.gltf;

public class SampleData{
    private int image;
    private String key;
    private String fullName;
    private int size;

    public SampleData(int image, String key, String fullName, int size){
        this.image = image;
        this.key = key;
        this.fullName = fullName;
        this.size = size;
    }

    public int getImage(){
        return this.image;
    }

    public String getKey(){
        return this.key;
    }

    public String getFullName(){
        return this.fullName;
    }

    public int getSize() { return this.size; }
}
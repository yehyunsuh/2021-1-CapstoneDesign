package com.google.ar.sceneform.samples.gltf;

public class SampleData{
    private int image;
    private String key;
    private String fullName;

    public SampleData(int image, String key, String fullName){
        this.image = image;
        this.key = key;
        this.fullName = fullName;
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
}
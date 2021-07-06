package com.google.ar.sceneform.samples.gltf;

public class SampleData{
    private int image;
    private String key;
    private String fullName;
    private int size;

    public SampleData(int image, String key, String fullName, int size){ // 받은 매개변수 각각 변수에 지정
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

    public int getSize() { return this.size; } // get method -> 출력할 때 해당 메소드를 통하여 변수값을 받아옴.
}
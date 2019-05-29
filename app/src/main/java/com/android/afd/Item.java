package com.android.afd;

public class Item {

    private String name;
    private String image;
    private String lastModified;
    private Integer numberOfChildren;

    public Item (String name, String image, String lastModified, Integer nrChildren) {
        this.name = name;
        this.image = image;
        this.lastModified = lastModified;
        this.numberOfChildren = nrChildren;
    }

    public String getName() {
        return name;
    }

     public String getImage() {
        return image;
     }

     public Integer getNumberOfChildren() {
        return numberOfChildren;
     }

     public String getLastModified() {
        return lastModified;
     }
}

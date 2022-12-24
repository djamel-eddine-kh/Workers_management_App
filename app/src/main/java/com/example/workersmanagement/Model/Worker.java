package com.example.workersmanagement.Model;

import java.util.Arrays;

public class Worker {
    private Long  Id;
    private String Last_name;
    private String First_name;
    private String Email;
    private String Phone_number;
    private String Field;
    private String Date;
    private byte[] Image ;

    public Worker(Long Id, String Last_name, String First_name,String Phone,String Email,String Field,String Date,byte[] image) {
        this.Id=Id;
        this.Last_name=Last_name;
        this.First_name=First_name;
        this.Email=Email;
        this.Field=Field;
        this.Phone_number=Phone;
        this.Date=Date;
        this.Image=image;

    }

    public Long getId() {
        return Id;
    }

    public String getLast_name() {
        return Last_name;
    }

    public String getFirst_name() {
        return First_name;
    }

    public String getEmail() {
        return Email;
    }

    public String getPhone_number() {
        return Phone_number;
    }

    public String getField() {
        return Field;
    }

    public String getDate() {
        return Date;
    }

    @Override
    public String toString() {
        return "Worker{" +
                "Id=" + Id +
                ", Last_name='" + Last_name + '\'' +
                ", First_name='" + First_name + '\'' +
                ", Email='" + Email + '\'' +
                ", Phone_number='" + Phone_number + '\'' +
                ", Field='" + Field + '\'' +
                ", Date='" + Date + '\'' +
                ", Image=" + Arrays.toString(Image) +
                '}';
    }

    public byte[] getImage() {
        return Image;
    }
}


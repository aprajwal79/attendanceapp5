package com.nitap.attende.Model;

import com.ttv.face.FaceFeatureInfo;

public class Student {
    public String rollno;
    public String regno,year,sem;
    public String name,degree,branch,section,email;
    public String deviceHash;
    public String sectionId;
    public FaceFeatureInfo faceFeatureInfo;
    public Section sectionObject;
    public FaceFeatureInfo getFaceFeatureInfo() {
        return faceFeatureInfo;
    }

    public void setFaceFeatureInfo(FaceFeatureInfo faceFeatureInfo) {
        this.faceFeatureInfo = faceFeatureInfo;
    }

    public Student() {}
    public Student(int i,String s,String[] arr,FaceFeatureInfo fInfo) {
        // regno = year = sem  = i;
        name = rollno = degree = branch = section  = email = deviceHash  = sectionId =regno = year= sem= s;
        //courses = s;
        faceFeatureInfo = fInfo;
    }
    public String getRegno() {return regno;}

    public String getRollno() {return rollno;    }

    public String getYear() {return year;}
    public String getSem() {return sem;}

    public String getName() {return name;}
    public String getDegree() {return degree;}
    public String getSection() {return section;}
    public String getBranch() {return branch;}




    public String getDeviceHash() {
        return deviceHash;
    }

    public String getEmail() {
        return email;
    }

    public String getSectionId() {
        return sectionId;
    }


    /*
    public String[] getCourses() {
        return courses;
    }*/



    public void setBranch(String branch) {
        this.branch = branch;
    }



    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void setDeviceHash(String deviceHash) {
        this.deviceHash = deviceHash;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }




    public void setRegno(String regno) {
        this.regno = regno;
    }

    public void setRollno(String rollno) {
        this.rollno = rollno;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public void setSem(String sem) {
        this.sem = sem;
    }


    public void setYear(String year) {
        this.year = year;
    }
}

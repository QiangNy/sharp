package com.cydroid.tpicture.animal;

/**
 * Created by qiang on 4/11/18.
 */
public class Pig {
    private final int PATTERN_NUMBERS = 20;
    private int[] degree = new int[PATTERN_NUMBERS];
    private int[] xPos = new int[PATTERN_NUMBERS];
    private int success = 0;
    private StringBuffer testResult = new StringBuffer();
    private int camerIntId = 0;

    private int[] yPos = new int[PATTERN_NUMBERS];

    private String picPath;
    private String cameraID;


    public String getTestResult() {
        return testResult.toString();
    }

    public void setTestResult(String testresult) {
        this.testResult.append(testresult);
        this.testResult.append("\n");
    }



    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public int[] getDegree() {
        return degree;
    }

    public String getDegreetoString() {

        return String.format("" +
                "%d, %d, %d, %d, " +
                "%d, %d, %d, %d, " +
                "%d, %d, %d, %d, " +
                "%d, %d, %d, %d, " +
                "%d, %d, %d, %d",
                degree[0], degree[1], degree[2], degree[3],
                degree[4], degree[5], degree[6], degree[7],
                degree[8], degree[9], degree[10], degree[11],
                degree[12], degree[13], degree[14], degree[15],
                degree[16], degree[17], degree[18], degree[19]);
    }

    public int getSuccess() {
        return success;
    }

    public String getSuccesstoString() {
        String rel = null;
        if (success == 0) {
            rel = "Pass";
        }else {
            rel = "Fail";
        }
        return rel;
    }

    public void setDegree(int[] degree) {
        this.degree = degree;
    }

    public String getCameraID() {
        return cameraID;
    }

    public void setCameraID(String cameraID) {
        this.cameraID = cameraID;
    }



    public Pig() {

    }

    public int getCamerIntId() {
        return camerIntId;
    }

    public void setCamerIntId(int camerIntId) {
        this.camerIntId = camerIntId;
    }

    public Pig(String cameraID, int camerIntId, int success, String picPath, int[] degree) {
        this.cameraID = cameraID;
       // this.success = success;
        this.picPath = picPath;
        this.degree = degree;
        this.camerIntId = camerIntId;
    }

    public void setSuccess(int success) {
        this.success = this.success + success;
    }

}

package com.mobilegroup.core.beans;

public class MgDataResult {
    String requestId;
    boolean isSuccess;
    MgCarData receivedData;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public MgCarData getReceivedData() {
        return receivedData;
    }

    public void setReceivedData(MgCarData receivedData) {
        this.receivedData = receivedData;
    }
}

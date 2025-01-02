package com.mobilegroup.core.callbacks;


import com.mobilegroup.core.beans.MgDataResult;

public interface MgDataReceiver {
    void onReceiveData(MgDataResult result);

    void onError(String requestId, Exception error);

}

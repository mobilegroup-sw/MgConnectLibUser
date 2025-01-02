package com.mobilegroup.core.callbacks;

import com.mobilegroup.core.beans.MgDataResult;

public interface MgCallback {
    void onSuccess(String requestId, MgDataResult result);
    void onError(String requestId, Exception error);
}

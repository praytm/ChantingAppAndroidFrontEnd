package com.service.iscon.vcr.helper;

/**
 * Created by vinod on 10/4/2015.
 */
public interface AsyncProcessListener<T> {
    void ProcessFinished(T Result);
    void ProcessFailed(Exception E);

}

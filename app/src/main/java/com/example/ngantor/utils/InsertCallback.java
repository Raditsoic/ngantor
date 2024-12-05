package com.example.ngantor.utils;

public interface InsertCallback {
    void onInsertSuccess(long id);  // This will return the inserted ID
    void onInsertFailure(Throwable t);  // Handle any errors
}

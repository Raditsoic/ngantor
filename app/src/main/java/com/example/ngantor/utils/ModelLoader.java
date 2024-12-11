package com.example.ngantor.utils;

import android.content.Context;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ModelLoader {
    public static Interpreter loadModel(Context context, String modelFileName) {
        try {
            MappedByteBuffer modelBuffer = loadModelFile(context, modelFileName);

            return new Interpreter(modelBuffer);
        } catch (IOException e) {
            Log.e("ModelLoader", "Error loading TFLite model", e);
            return null;
        }
    }

    private static MappedByteBuffer loadModelFile(Context context, String modelPath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(context.getAssets().openFd(modelPath).getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();

        long startOffset = context.getAssets().openFd(modelPath).getStartOffset();
        long declaredLength = context.getAssets().openFd(modelPath).getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}

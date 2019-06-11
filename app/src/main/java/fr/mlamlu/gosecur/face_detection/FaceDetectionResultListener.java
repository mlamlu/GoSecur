package fr.mlamlu.gosecur.face_detection;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.ml.vision.face.FirebaseVisionFace;

import java.util.List;

import fr.mlamlu.gosecur.others.FrameMetadata;
import fr.mlamlu.gosecur.others.GraphicOverlay;

/**
 * Created by Jaison.
 */
public interface FaceDetectionResultListener {
    void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<FirebaseVisionFace> faces,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay);

    void onFailure(@NonNull Exception e);
}

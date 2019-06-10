package fr.mlamlu.gosecur;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

import fr.mlamlu.gosecur.camera.CameraSource;
import fr.mlamlu.gosecur.camera.CameraSourcePreview;
import fr.mlamlu.gosecur.others.GraphicOverlay;
import fr.mlamlu.gosecur.text_detection.TextRecognitionProcessor;

public class MainActivity extends AppCompatActivity {

	//region ----- Instance Variables -----

	private CameraSource cameraSource = null;
	private CameraSourcePreview preview;
	private GraphicOverlay graphicOverlay;

	private static String TAG = MainActivity.class.getSimpleName().toString().trim();

	public static final String NAME_MESSAGE =
			"NAME_M";
	public static final String PRENOM_MESSAGE =
			"PRENOM_M";
	public static final String ID_MESSAGE =
			"ID_M";

	//endregion

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//FirebaseApp.initializeApp(this);

		preview = (CameraSourcePreview) findViewById(R.id.camera_source_preview);
		if (preview == null) {
			Log.d(TAG, "Preview is null");
		}
		graphicOverlay = (GraphicOverlay) findViewById(R.id.graphics_overlay);
		if (graphicOverlay == null) {
			Log.d(TAG, "graphicOverlay is null");
		}

		createCameraSource();
		startCameraSource();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		startCameraSource();
	}

	/** Stops the camera. */
	@Override
	protected void onPause() {
		super.onPause();
		preview.stop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (cameraSource != null) {
			cameraSource.release();
		}
	}

	private void createCameraSource() {

		if (cameraSource == null) {
			cameraSource = new CameraSource(this, graphicOverlay);
			cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
		}

		cameraSource.setMachineLearningFrameProcessor(new TextRecognitionProcessor(this));
	}

	private void startCameraSource() {
		if (cameraSource != null) {
			try {
				if (preview == null) {
					Log.d(TAG, "resume: Preview is null");
				}
				if (graphicOverlay == null) {
					Log.d(TAG, "resume: graphOverlay is null");
				}
				preview.start(cameraSource, graphicOverlay);
			} catch (IOException e) {
				Log.e(TAG, "Unable to start camera source.", e);
				cameraSource.release();
				cameraSource = null;
			}
		}
	}


	public void succesGetInformation(String nom, String prenom,String idCard){
		onDestroy();
		Intent intent = new Intent(this, PortailActivity.class);
		intent.putExtra(NAME_MESSAGE,nom);
		intent.putExtra(PRENOM_MESSAGE,prenom);
		intent.putExtra(ID_MESSAGE,idCard);
		startActivity(intent);
	}
}

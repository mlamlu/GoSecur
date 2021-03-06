// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package fr.mlamlu.gosecur.text_detection;

import android.graphics.Color;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.mlamlu.gosecur.MainActivity;
import fr.mlamlu.gosecur.others.FrameMetadata;
import fr.mlamlu.gosecur.others.GraphicOverlay;


public class TextRecognitionProcessor {

	private static final String TAG = "TextRecProc";
	private MainActivity context;

	private final FirebaseVisionTextRecognizer detector;

	// Whether we should ignore process(). This is usually caused by feeding input data faster than
	// the model can handle.
	private final AtomicBoolean shouldThrottle = new AtomicBoolean(false);

	public TextRecognitionProcessor(MainActivity c) {
		detector = FirebaseVision.getInstance().getCloudTextRecognizer();
		context =c;
	}



	//region ----- Exposed Methods -----


	public void stop() {
		try {
			detector.close();
		} catch (IOException e) {
			Log.e(TAG, "Exception thrown while trying to close Text Detector: " + e);
		}
	}


	public void process(ByteBuffer data, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay) throws FirebaseMLException {

		if (shouldThrottle.get()) {
			return;
		}
		FirebaseVisionImageMetadata metadata =
				new FirebaseVisionImageMetadata.Builder()
						.setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
						.setWidth(frameMetadata.getWidth())
						.setHeight(frameMetadata.getHeight())
						.setRotation(frameMetadata.getRotation())
						.build();



		detectInVisionImage(FirebaseVisionImage.fromByteBuffer(data, metadata), frameMetadata, graphicOverlay);
	}

	//endregion

	//region ----- Helper Methods -----

	protected Task<FirebaseVisionText> detectInImage(FirebaseVisionImage image) {
		return detector.processImage(image);
	}


	protected void onSuccess(@NonNull FirebaseVisionText results, @NonNull FrameMetadata frameMetadata, @NonNull GraphicOverlay graphicOverlay) {

		graphicOverlay.clear();

		List<FirebaseVisionText.TextBlock> blocks = results.getTextBlocks();
		String prenom = "";
		String nom = "";
		String idCarte = "";
		for (int i = 0; i < blocks.size(); i++) {
			List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();

			for( FirebaseVisionText.Line line : lines) {

				Log.d("MLAMLU LINE", line.getText());


				if (line.getText().contains("Prenom") || line.getText().contains("Prénom")){
					if(line.getElements().size() > 1) {
						GraphicOverlay.Graphic textGraphic = new TextGraphic(graphicOverlay, line.getElements().get(1), Color.GREEN);
						graphicOverlay.add(textGraphic);
						prenom = line.getElements().get(1).getText();

						if (prenom.endsWith(",")) {
							prenom = prenom.substring(0, prenom.length() - 1);

						}


					}
				}else if (line.getText().contains("Nom")){

					int take;
					if(line.getElements().size() == 3 && line.getElements().get(1).getText().equalsIgnoreCase(":")){
						take = 2;
					}else take = 1;
					if(line.getElements().size() > 1) {
						nom = line.getElements().get(take).getText();
						if (nom.startsWith(":")) {
							nom = nom.substring(1,nom.length());
						}
						if(nom.equalsIgnoreCase("Nom")){
							nom = line.getElements().get(3).getText();
						}

						TextGraphic textGraphic = new TextGraphic(graphicOverlay, line.getElements().get(take), Color.GREEN);
						graphicOverlay.add(textGraphic);
					}
				}else if (line.getText().contains("CARTE NATIONALE")){
					Log.d("MLAMLU LINE ID ", line.getText() + " size : " + line.getElements().size());
					if(line.getElements().size() == 5 ){
						TextGraphic textGraphic = new TextGraphic(graphicOverlay, line.getElements().get(4), Color.GREEN);
						graphicOverlay.add(textGraphic);
						idCarte = line.getElements().get(4).getText();
					}

				}

			}
		}/*
		for (int i = 0; i < blocks.size(); i++) {
			List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
			for (int j = 0; j < lines.size(); j++) {
				List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
				for (int k = 0; k < elements.size(); k++) {
					GraphicOverlay.Graphic textGraphic = new TextGraphic(graphicOverlay, elements.get(k),Color.WHITE);
					graphicOverlay.add(textGraphic);

				}
			}
		}*/
		Log.d("MLAMLU Nom ", nom);
		Log.d("MLAMLU Prenom ", prenom);
		Log.d("MLAMLU idCarte  ", idCarte);


		if(nom != "" && prenom != "" && idCarte != ""){
		context.succesGetInformation(nom,prenom,idCarte);
		}
	}

	protected void onFailure(@NonNull Exception e) {
		Log.w(TAG, "Text detection failed." + e);
	}

	private void detectInVisionImage( FirebaseVisionImage image, final FrameMetadata metadata, final GraphicOverlay graphicOverlay) {

		detectInImage(image)
				.addOnSuccessListener(
						new OnSuccessListener<FirebaseVisionText>() {
							@Override
							public void onSuccess(FirebaseVisionText results) {
								shouldThrottle.set(false);
								TextRecognitionProcessor.this.onSuccess(results, metadata, graphicOverlay);
							}
						})
				.addOnFailureListener(
						new OnFailureListener() {
							@Override
							public void onFailure(@NonNull Exception e) {
								shouldThrottle.set(false);
								TextRecognitionProcessor.this.onFailure(e);
							}
						});
		// Begin throttling until this frame of input has been processed, either in onSuccess or
		// onFailure.
		shouldThrottle.set(true);
	}

	//endregion


}

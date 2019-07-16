package fr.mlamlu.gosecur;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;


public class PortailActivity extends Activity {


    private TextView nameText;
    private TextView prenomText;
    private TextView idText;
    private String name;
    private String prenom;
    private String idCard;
    private Button search;
    private EditText resultF;
    private ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portail);
        nameText = (TextView) findViewById(R.id.nameLabel);
        prenomText = (TextView) findViewById(R.id.prenomLabel);
        idText = (TextView) findViewById(R.id.cardLabel);
        Intent intent = getIntent();
        name = intent.getStringExtra(MainActivity.NAME_MESSAGE);
        prenom = intent.getStringExtra(MainActivity.PRENOM_MESSAGE);
        idCard = intent.getStringExtra(MainActivity.ID_MESSAGE);
        search = (Button) findViewById(R.id.search);
        resultF = (EditText) findViewById(R.id.resultF);
        image = (ImageView) findViewById(R.id.imageView);
        resultF.setEnabled(false);
        nameText.append(name);
        prenomText.append(prenom);
        idText.append(idCard);
    }


    public void search(View view){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("a", document.toString());
                                Log.d("c", document.getId() + " => " + document.getData());
                                if(document.getId().equalsIgnoreCase(name+"."+prenom)){
                                    Map<String, Object> map = document.getData();
                                    String base = (String) map.get("urlPhoto");
                                    Log.d("Image",base);
                                    byte[] decodedString = Base64.decode(base, Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,decodedString.length);
                                    image.setImageBitmap(decodedByte);
                                    map.remove("urlPhoto");
                                    for(Map.Entry<String, Object> entry : map.entrySet()) {

                                        // la clef peut être obtenue par entry.getKey()
                                        // la valeur correspondante par entry.getValue()

                                        resultF.append(entry.getKey() + " : " + entry.getValue() + "\n");
                                    }

                                }
                            }
                        } else {
                            Log.w("c", "Error getting documents.", task.getException());
                        }
                    }
                });

    }

}

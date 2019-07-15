package fr.mlamlu.gosecur;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

public class PortailActivity extends AppCompatActivity {


    private TextView nameText;
    private TextView prenomText;
    private TextView idText;
    private String name;
    private String prenom;
    private String idCard;
    private Button search;

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
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }

}

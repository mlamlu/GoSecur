package fr.mlamlu.gosecur;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class PortailActivity extends AppCompatActivity {


    private TextView nameText;
    private TextView prenomText;
    private TextView idText;
    private String name;
    private String prenom;
    private String idCard;

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

        nameText.append(name);
        prenomText.append(prenom);
        idText.append(idCard);
    }
}

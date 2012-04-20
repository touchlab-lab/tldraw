package co.touchlab.pdraw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import co.touchlab.pdraw.views.DrawView;

/**
 * Created by IntelliJ IDEA.
 * User: touchlab
 * Date: 4/20/12
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class Intro extends Activity {

    private EditText codeText;
    private Button goButton;
    private TextView intro;
    private TextView welcome;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.intro);
        
        welcome = (TextView) findViewById(R.id.welcome);
        welcome.setText("Welcome to Touch Lab");

        intro = (TextView) findViewById(R.id.intro);
        intro.setText("\nYou have 30 seconds to create" +
                "        your own masterpiece to be featured on the company website! \n\n" +
                "How to get started: \n\n" +
                "1. Go to www.touchlab.co \t(on a different device). \n\n" +
                "2. Enter the text code on the \tscreen in the box below. \n\n" +
                "3. Don't draw anything \noffensive.");
        
        codeText = (EditText) findViewById(R.id.code);
        
        goButton = (Button) findViewById(R.id.go);
        goButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intro.this, Draw.class);
                startActivity(intent);

            }
        });



    }
}
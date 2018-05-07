package games.winchester.unodeluxe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import games.winchester.unodeluxe.R;


public class Top extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
    }
    public void goToMain (View view){
        Intent intent = new Intent (this, MenuActivity.class);
        startActivity(intent);
        Topclassament();

    }
    public void Topclassament(){

        ArrayList<String> list =new ArrayList<String>();
        list.add("Primul");
        ListView mainListView = (ListView) findViewById( R.id.mainListView );
    }
}

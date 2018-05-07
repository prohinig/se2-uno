package unodeluxe.winchester.games.unomenu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.app.Activity;
import java.util.ArrayList;
import java.util.Arrays;


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

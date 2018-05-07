package unodeluxe.winchester.games.unomenu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.Activity;
import java.util.ArrayList;
import java.util.Arrays;

public class Rules extends AppCompatActivity {
   // String[] mobileArray = {
           // "1. Every player starts with seven cards, and they are dealt face down.",
          //  "2. The rest of the cards are placed in a Draw Pile face down.",
         //   "3. The top card should be placed in the Discard Pile, and the game begins!",
         //   "4. The first player is normally the player to the left of the dealer and gameplay usually follows a clockwise direction. Every player views his/her cards and tries to match the card in the Discard Pile.",
         //   "5. You have to match either by the number, color, or the symbol/Action.",
        //    "6. If the player has no matches or they choose not to play any of their cards even though they might have a match, they must draw a card from the Draw pile. If that card can be played, play it." };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

    //    ArrayAdapter adapter = new ArrayAdapter<String>(this,
             //  R.layout.activity_listview, mobileArray);
      // ListView mainListView = (ListView) findViewById( R.id.mainListView );
     //  ArrayList<String> list =new ArrayList<String>();
       //list.addAll(Arrays.asList(mobileArray));
      // ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,R.layout.simplerow,list);
     //  mainListView.setAdapter(listAdapter);

      // ListView listView = (ListView) findViewById(R.id.mobile_list);
      // listView.setAdapter(adapter);
    }
    public void goToMain (View view){
        Intent intent = new Intent (this,MenuActivity.class);
        startActivity(intent);
    }


}

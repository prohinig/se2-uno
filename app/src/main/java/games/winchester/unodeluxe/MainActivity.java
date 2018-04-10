package games.winchester.unodeluxe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView deck, stack;
    Deck cardDeck;
    Stack cardStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardDeck = new Deck();
        cardStack = new Stack();

        deck = (ImageView) findViewById(R.id.deckView);
        stack = (ImageView) findViewById(R.id.stackView);

        stack.setVisibility(View.INVISIBLE);

        deck.setClickable(true);
        deck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stack.setVisibility(View.VISIBLE);

                ArrayList<Card> cards = cardDeck.deal(1);
                Card card = cards.get(0);

                cardStack.playCard(card);

                Toast.makeText(MainActivity.this,
                        "Drawn card \n" +
                        "color: " + card.getColor() +
                        "\nsymbol: " + card.getSymbol(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }
}

package games.winchester.unodeluxe;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import games.winchester.unodeluxe.models.Card;
import games.winchester.unodeluxe.models.Game;

public class SwipeListener implements View.OnTouchListener {
    private float oldTouchValue = 0f;

    private final Game game;
    private LinearLayout handLayout;

    public SwipeListener(Game game, LinearLayout layout) {
        this.game = game;
        this.handLayout = layout;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getTag() == null) return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handleActionDown(event);
                return false;

            case MotionEvent.ACTION_UP:
                handleActionUp(v, event);
                return true;

            default:
                return false;
        }
    }

    private void handleActionDown(MotionEvent event){
        oldTouchValue = event.getY();
    }

    private void handleActionUp(View v, MotionEvent event){
        float newTouchValue = event.getY();
        float deltaY = Math.abs(newTouchValue - oldTouchValue);
        float minDistance = 50f;

        if (deltaY > minDistance) { // user swiped a card down
            if (newTouchValue > oldTouchValue && game.cheat((Card) v.getTag())) {
                handLayout.removeView(v);
            }
        } else if (game.cardClicked((Card) v.getTag())) { // user clicked a card
            handLayout.removeView(v);
        }
    }
}

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
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldTouchValue = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                float newTouchValue = event.getY();
                float deltaY = Math.abs(newTouchValue - oldTouchValue);
                float MIN_DISTANCE = 50f;

                if (deltaY > MIN_DISTANCE) {
                    // user swiped a card down
                    if (newTouchValue > oldTouchValue) {
                        if (v.getTag() == null) return false;
                        if (game.cheat((Card) v.getTag())) {
                            handLayout.removeView(v);
                        }
                        return true;
                    }
                } else if (deltaY <= MIN_DISTANCE) {
                    // user clicked a card
                    if (v.getTag() == null) return false;
                    if (game.cardClicked((Card) v.getTag())) {
                        handLayout.removeView(v);
                        return true;
                    }
                }
                break;
            default:
                break;
        }

        return false;
    }
}

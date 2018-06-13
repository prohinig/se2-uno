package games.winchester.unodeluxe.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import games.winchester.unodeluxe.R;
import games.winchester.unodeluxe.enums.CardColor;
import games.winchester.unodeluxe.models.Game;

public class ColorWishDialog extends DialogFragment {

    private Game game;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.ColorWishDialog_title)
                .setItems(R.array.colors_array, (dialog, which) -> {
                    // The 'which' argument contains the index position
                    // of the selected item
                    switch (which) {
                        case 0:
                            game.setActiveColor(CardColor.BLUE);
                            break;
                        case 1:
                            game.setActiveColor(CardColor.YELLOW);
                            break;
                        case 2:
                            game.setActiveColor(CardColor.GREEN);
                            break;
                        case 3:
                            game.setActiveColor(CardColor.RED);
                            break;
                        default:
                            Log.e("ColorWishDialog", "Invalid select option: " + which);
                            break;
                    }
                });
        return builder.create();
    }

    public void show(FragmentManager manager, String tag, Game game) {
        this.game = game;
        super.show(manager, tag);
    }

}

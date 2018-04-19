package sample;

import javafx.scene.control.Label;

import java.util.Observable;
import java.util.Observer;

public class ObservingLabel extends Label implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        setText(arg.toString());
    }

}

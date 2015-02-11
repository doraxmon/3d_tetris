package tetris;

import java.awt.*;
import javax.swing.*;

public abstract class Canvas3D extends JPanel{
	abstract Obj3D getObj();
	abstract void setObj(Obj3D obj);
}

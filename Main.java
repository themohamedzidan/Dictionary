package ex2;
import javax.swing.*;
import java.awt.*;

public class Main {

 public static void main(String[] args) {
     JFrame frame = new JFrame("Dictionary");
     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     DictionaryPannel d = new DictionaryPannel();
     frame.add(d);
     frame.setSize(500, 535);
     Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
     frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
     frame.setVisible(true);
 }
}
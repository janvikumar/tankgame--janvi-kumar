package tankrotationexample.menus;

import tankrotationexample.Launcher;
import tankrotationexample.util.AssetManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class EndGamePanel extends JPanel {

    private BufferedImage menuBackground;
    private final Launcher lf;

    public EndGamePanel(Launcher lf) {
        this.lf = lf;
       this.menuBackground = AssetManager.getSprite("menu");
        this.setBackground(Color.BLACK);
        this.setLayout(null);

        JButton start = new JButton("Restart Game");
        start.setFont(new Font("Courier New", Font.BOLD, 24));
        start.setBounds(150, 300, 250, 50);
        start.addActionListener(e -> {
            lf.setFrame("start");
        });

        JButton exit = new JButton("Exit");
        exit.setFont(new Font("Courier New", Font.BOLD, 24));
        exit.setBounds(150, 400, 250, 50);
        exit.addActionListener((actionEvent -> this.lf.closeGame()));

        this.add(start);
        this.add(exit);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (menuBackground != null) {
            g.drawImage(menuBackground, 0, 0, null);
        }
    }
}

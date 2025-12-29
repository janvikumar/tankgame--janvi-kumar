package tankrotationexample.menus;


import tankrotationexample.Launcher;
import tankrotationexample.util.AssetManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class StartMenuPanel extends JPanel {

    private BufferedImage menuBackground;
    private final Launcher lf;

    public StartMenuPanel(Launcher lf) {
        this.lf = lf;
        this.menuBackground = AssetManager.getSprite("menu");
        this.setBackground(Color.BLACK);
        this.setLayout(null);

        String[] levels = {"level1", "level2", "level3"};
        JComboBox<String> levelSelect = new JComboBox<>(levels);
        levelSelect.setFont(new Font("Courier New", Font.BOLD, 18));
        levelSelect.setBounds(150, 240, 150, 40);

        this.add(levelSelect);

        JButton start = new JButton("Start");
        start.setFont(new Font("Courier New", Font.BOLD, 24));
        start.setBounds(150, 300, 150, 50);

        start.addActionListener(e -> {
            String chosen = (String) levelSelect.getSelectedItem();
            System.out.println("Chosen level = " + chosen);
            lf.startGame(chosen);   // ✅ loads level + switches frame + starts thread safely
        });


        JButton exit = new JButton("Exit");
        exit.setSize(new Dimension(200, 100));
        exit.setFont(new Font("Courier New", Font.BOLD, 24));
        exit.setBounds(150, 400, 150, 50);
        exit.addActionListener((actionEvent -> this.lf.closeGame()));

        this.add(start);
        this.add(exit);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.menuBackground != null) {
            g.drawImage(this.menuBackground, 0, 0, null);
        }
        //Graphics2D g2 = (Graphics2D) g;
      //  g2.drawImage(this.menuBackground, 0, 0, null);
    }
}

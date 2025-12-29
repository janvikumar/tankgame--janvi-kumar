package tankrotationexample.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author anthony-pc
 */
public class TankControl implements KeyListener {
    private final Tank t1;
    private final int up;
    private final int down;
    private final int right;
    private final int left;
    private final int shoot;
    
    public TankControl(Tank t1, int up, int down, int left, int right, int shoot) {
        this.t1 = t1;
        this.up = up;
        this.down = down;
        this.right = right;
        this.left = left;
        this.shoot = shoot;
    }

    @Override
    public void keyTyped(KeyEvent ke) {

    }

    @Override
    public void keyPressed(KeyEvent ke) {
        int k = ke.getKeyCode();
        if (k != up && k != down && k != left && k != right && k != shoot) return;

        //System.out.println("CONTROL: key=" + k + " tankId=" + System.identityHashCode(this.t1));

        if (k == up) this.t1.toggleUpPressed();
        if (k == down) this.t1.toggleDownPressed();
        if (k == left) this.t1.toggleLeftPressed();
        if (k == right) this.t1.toggleRightPressed();
        if (k == shoot) this.t1.ToggleShootPressed();   // later if you implement


    }
        @Override
        public void keyReleased(KeyEvent ke) {
            int k = ke.getKeyCode();
            if (k != up && k != down && k != left && k != right && k != shoot) return;

            if (k == up) this.t1.unToggleUpPressed();
            if (k == down) this.t1.unToggleDownPressed();
            if (k == left) this.t1.unToggleLeftPressed();
            if (k == right) this.t1.unToggleRightPressed();
            if (k == shoot){this.t1.unToggleShootPressed();
            }
        }
}

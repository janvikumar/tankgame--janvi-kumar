package tankrotationexample.game;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {
    private Clip clip;
    int loopCount;

    public Sound(Clip clip){
        this.clip = clip;
        this.loopCount = 1;
    }
    public Sound (Clip clip,int loopCount) {
        this.clip = clip;
        this.loopCount = loopCount;
    }
    public Sound stop(){
        if(this.clip.isRunning()){
            this.clip.stop();
        }
        return this;
    }
    public Sound play(){
        if(this.clip.isRunning()){
            this.clip.stop();
        }
        this.clip.setFramePosition(0);
        this.clip.start();
        return this;

    }

    public Sound loopContinuously() { //background music
        this.clip.loop(Clip.LOOP_CONTINUOUSLY);
        return this;
    }

    public Sound setVolume(float level) {
        FloatControl volume = (FloatControl) this.clip.getControl(
            FloatControl.Type.MASTER_GAIN);
            volume.setValue(20.0f * (float)Math.log10(level));
            return this;
    }

}

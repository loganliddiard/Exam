import edu.usu.audio.Sound;
import edu.usu.audio.SoundManager;
import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;
import edu.usu.graphics.Texture;
import org.joml.Vector2f;

public class PlayerCar {

    private Texture car_image;
    private Texture particle_image;
    private Rectangle box;
    private Sound crash_effect;
    private float pos_x;
    private float pos_y;
    private boolean crashed;
    private float lengths;
    private float heights;
    private float speed;
    private ParticleSystem particleSystem;

    public PlayerCar(SoundManager audio) {

        car_image = new Texture("resources/Textures/player.png");
        particle_image = new Texture("resources/Textures/particle.png");

        crash_effect = audio.load("crash", "resources/Audio/retro-explode.ogg", false);
        lengths = 0.2f;
        heights = 0.25f;
        speed = 1.0f;
        pos_y = 0.25f;
        pos_x = 0 - (lengths / 2);

        crashed = false;
        particleSystem = new ParticleSystem();
    }

    public void moveLeft(double elapsedTime) {
        if (!crashed) {
            pos_x -= (float) (speed * elapsedTime);
        }
    }

    public void moveRight(double elapsedTime) {
        if (!crashed) {
            pos_x += (float) (speed * elapsedTime);
        }
    }

    public void render(Graphics2D graphics) {
        if (!crashed) {
            box = new Rectangle(pos_x, pos_y, lengths, heights);
            graphics.draw(car_image, box, Color.WHITE);
        }else{
            particleSystem.render(graphics);
        }

    }

    public void update(double elapsedTime) {
        if (crashed) {
            particleSystem.update((float) elapsedTime);
        }
    }

    public float getHeight() {
        return heights;
    }

    public float getLength() {
        return lengths;
    }

    public float getPos_x() {
        return pos_x;
    }

    public float getPos_y() {
        return pos_y;
    }

    public void crash() {
        if (!crashed) {
            crashed = true;
            crash_effect.play();

            // Generate particles at the center of the car
            // Generate more particles with greater speed and longer lifespan
            Vector2f carCenter = new Vector2f(pos_x + lengths / 2, pos_y + heights / 2);
            particleSystem.generateParticles(carCenter, 120, .25f, 1.5f);
        }
    }

    public boolean getCrashed() {
        return crashed;
    }
}
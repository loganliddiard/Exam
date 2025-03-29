import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;
import edu.usu.graphics.Texture;
import org.joml.Vector2f;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

class ParticleSystem {
    private ArrayList<Particle> particles;
    private Random random;
    private float sizeRange;
    public ParticleSystem() {
        particles = new ArrayList<>();
        random = new Random();
        sizeRange = .025f;
    }
    public void generateParticles(Vector2f position, int count, float speed, float lifetimeRange) {
        for (int i = 0; i < count; i++) {
            // Generate a random angle in 360 degrees (0 to 2π radians)
            float particleAngle = (float) (random.nextFloat() * Math.PI * 2);

            // Randomize speed between 50% and 150% of base speed
            float particleSpeed = speed * (random.nextFloat() * 1.0f + 0.5f);

            // Calculate velocity using angle
            Vector2f velocity = new Vector2f(
                    (float) Math.cos(particleAngle) * particleSpeed,
                    (float) Math.sin(particleAngle) * particleSpeed
            );

            // Randomize lifetime and size
            float lifetime = random.nextFloat() * lifetimeRange + 0.5f;
            float size = random.nextFloat() * sizeRange + 0.01f; // Ensure visible size

            particles.add(new Particle(position, velocity, lifetime, size));
        }
    }


    public void update(float deltaTime) {
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = iterator.next();
            particle.update(deltaTime);
            if (!particle.isAlive()) {
                iterator.remove();
            }
        }
    }

    public void render(Graphics2D graphics) {
        Texture particleImage = new Texture("resources/Textures/particle.png");
        for (Particle particle : particles) {
            Rectangle rec = new Rectangle(
                    particle.position.x - (particle.size / 2),
                    particle.position.y - (particle.size / 2),
                    particle.size,
                    particle.size
            );
            graphics.draw(particleImage, rec, Color.WHITE);
        }
    }
}
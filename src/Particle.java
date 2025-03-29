import edu.usu.graphics.Rectangle;
import org.joml.Vector2f;

class Particle {
    Vector2f position;
    Vector2f velocity;
    float lifetime;
    float size;
    float rotation;
    float rotationSpeed; // Spin speed in degrees per second

    public Particle(Vector2f position, Vector2f velocity, float lifetime, float size) {
        this.position = new Vector2f(position);
        this.velocity = new Vector2f(velocity);
        this.lifetime = lifetime;
        this.size = size;

        // Random rotation and spin speed (-180° to 180° per second)
        this.rotation = (float) (Math.random() * 360.0);
        this.rotationSpeed = (float) ((Math.random() - 0.5) * 360.0);
    }

    public void update(float deltaTime) {
        lifetime -= deltaTime;
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);

        // Update rotation
        rotation += rotationSpeed * deltaTime;
    }

    public boolean isAlive() {
        return lifetime > 0;
    }
}

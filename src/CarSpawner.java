import edu.usu.graphics.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class CarSpawner {
    private float despawn_y;
    private float top;
    private PlayerCar player;
    private ArrayList<ObstacleCar> cars;
    private Random random;
    private Font font;
    private Texture panel;
    private int dodged;

    public CarSpawner(PlayerCar player, float despawn_y, float top) {
        this.top = top;
        this.despawn_y = despawn_y;
        this.player = player;
        cars = new ArrayList<>();
        random = new Random();
        font = new Font("resources/fonts/Roboto-Regular.ttf",36,true);
        panel = new Texture("resources/Textures/panel.png");
        dodged = 0;
    }

    public void update(double elapsedTime) {

        if (cars.isEmpty()) {
            ArrayList<Integer> lanes = new ArrayList<>();
            for (int i = 0; i <= 4; i++) {
                lanes.add(i); // Lanes 0 to 4
            }

            // Randomly choose how many cars to spawn (1 to 3)
            int carCount = random.nextInt(3) + 1;

            // Shuffle lanes and pick the first 'carCount' lanes
            Collections.shuffle(lanes);
            for (int i = 0; i < carCount; i++) {
                int lane = lanes.remove(0); // Remove lane to prevent duplicates
                cars.add(new ObstacleCar(lane, top));
            }
        } else {
            ArrayList<ObstacleCar> remove = new ArrayList<>();
            for (ObstacleCar car : cars) {
                car.update(elapsedTime);

                if (car.getPos_y() > despawn_y) {
                    remove.add(car);
                }
            }
            if (!remove.isEmpty()) {
                dodged += remove.size();
                // Add points here
                cars.removeAll(remove);
            }
        }

        checkCollisions();
    }

    public void checkCollisions() {
        float playerX = player.getPos_x();
        float playerY = player.getPos_y();
        float playerWidth = player.getLength();
        float playerHeight = player.getHeight();

        for (ObstacleCar car : cars) {
            float carX = car.getPos_x();
            float carY = car.getPos_y();
            float carWidth = car.getLength();
            float carHeight = car.getHeight();

            // Perform AABB collision check
            boolean collision = (playerX < carX + carWidth &&
                    playerX + playerWidth > carX &&
                    playerY < carY + carHeight &&
                    playerY + playerHeight > carY);

            if (collision) {
                System.out.println("Collision Detected!");
                // Assuming you want to mark the car as crashed
                player.crash(); // Assuming PlayerCar has a method for handling collisions
            }
        }
    }

    public void render(Graphics2D graphics) {
        renderHUD(graphics);
        for (ObstacleCar car : cars) {
            car.render(graphics);
        }
    }
    public void renderHUD(Graphics2D graphics) {

        String score = "CARS DODGED: "+dodged;
        float height = .1f;
        float width = font.measureTextWidth(score, height);
        Rectangle box_panel = new Rectangle(-1.0f,top+height,width,height);

        graphics.draw(panel,box_panel,Color.BLACK);

        graphics.drawTextByHeight(font,"CARS DODGED: "+dodged,-1.0f,top+height,height, Color.YELLOW);
    }

    public int getDodged(){
        return dodged;
    }
}
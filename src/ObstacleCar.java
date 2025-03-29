import edu.usu.graphics.Color;
import edu.usu.graphics.Graphics2D;
import edu.usu.graphics.Rectangle;
import edu.usu.graphics.Texture;

public class ObstacleCar {


        private Texture car_image;
        private Rectangle box;
        private float pos_x;
        private float pos_y;
        private boolean crashed;
        private float length;
        private float height;
        private float speed;
        private float bottom_of_screen;
        private float leftMost;

        public ObstacleCar(int lane_num,float top) {

            bottom_of_screen = 0.6f;
            leftMost = -.75f;
            float lane_width = .325f;
            car_image = new Texture("resources/Textures/oncoming.png");


            length = .2f;
            height = .25f;

            pos_x = leftMost + (lane_width * lane_num);
            speed = .6f;
            pos_y = top-height;

        }

        public void update(double elapsedTime){

            pos_y += (float) (speed * elapsedTime);

        }


        public void render(Graphics2D graphics){


            if(!crashed){
                box = new Rectangle(pos_x, pos_y, length, height);

                // Rotate around the correct center
                graphics.draw(car_image,box, Color.WHITE);
            }


            // Render particles
            //particleSystem.render(graphics);

        }
    public float getHeight() {
        return height;
    }

    public float getLength() {
        return length;
    }

    public float getPos_x() {
        return pos_x;
    }

    public float getPos_y() {
        return pos_y;
    }

    }


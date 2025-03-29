import edu.usu.audio.Sound;
import edu.usu.audio.SoundManager;
import edu.usu.graphics.*;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

public class Game {

    private final Graphics2D graphics;

    private double gameStartTime = -1;
    private final double transitionDelay = 3.0;
    private enum gameStates{
        menu,
        game,
        scores,
        options,
        credits,
        transition,
        exit,
        pause,
    };

    private PlayerCar player;
    private CarSpawner spawner;
    private PlayerCar yeah;
    private Font font;
    private Font title_font;
    private Font sub_font;

    private KeyboardInput input;
    private KeyboardInput menu_input;
    private gameStates current_state;
    private Menu menu;

    private Serializer serializer;
    private boolean pause;

    //Draw boarder
    final float screen_size = 2.0f;
    final float screen_height = 1.2f;
    //final float FRAMETHICKNESS = 0.005f;
    private static final float LHS = -1.0f;
    private static final float TOP = -0.6f;
    private float scrollOffset; // Initial position, will move up over time
    private float scrollOffset_road1; // Initial position, will move up over time
    private float scrollOffset_road2;
    private float scrollOffset_road3;
    private float scrollSpeed; // Adjust speed for smooth scrolling
    private Texture background;

    private SoundManager audio;
    private Sound level_music;
    private Sound countdown_sound;
    private HighScores highScores;
    private int score;
    public Game(Graphics2D graphics) {
        this.graphics = graphics;
    }

    public void initialize() {

        scrollOffset_road1 = TOP; // Initial position, will move up over time
        scrollOffset_road2 = TOP - screen_height;
        scrollOffset_road3 = TOP - screen_height - screen_height;
        scrollSpeed = 0.9f; // Adjust speed for smooth scrolling



        background = new Texture("resources/Textures/road.png");

        highScores = new HighScores();
        serializer = new Serializer();
        serializer.loadHighScores(highScores);

        current_state = gameStates.menu;





        audio = new SoundManager();
        menu = new Menu(audio);
        input = new KeyboardInput();
        menu_input = new KeyboardInput();
        pause = false;

        font = new Font("Arial", java.awt.Font.PLAIN, 84, false);


        //background = new Texture("resources/images/galaxy.png");

        // Key size is 36
        // Best height is .05
        title_font = new Font("resources/fonts/Roboto-BoldItalic.ttf", 36, true);
        //title_font = new Font("resources/fonts/karmatic-arcade/ka1.ttf", 36, true);
        //max size is 60
        //needs to be size 64 with font height of .07
        //sub_font = new Font("resources/fonts/dedicool/Dedicool.ttf", 64, true);
        sub_font = new Font("resources/fonts/Roboto-Regular.ttf", 64, true);
        input.registerCommand(GLFW_KEY_LEFT,false,(double elapsedTime) -> {

            player.moveLeft(elapsedTime);

        });
        input.registerCommand(GLFW_KEY_RIGHT,false,(double elapsedTime) -> {

            player.moveRight(elapsedTime);

        });


        //different game states use different controls
        menu_input.registerCommand(GLFW_KEY_UP,true,(double elapsedTime) -> {

            if(current_state == gameStates.menu) menu.upOption();;
            if(current_state == gameStates.game && pause) menu.pauseOptionChange();

        });

        menu_input.registerCommand(GLFW_KEY_DOWN,true,(double elapsedTime) -> {
            if(current_state == gameStates.menu) menu.downOption();
            if(current_state == gameStates.game && pause) menu.pauseOptionChange();

        });


        menu_input.registerCommand(GLFW_KEY_ESCAPE,true,(double elapsedTime) -> {
            if (current_state == gameStates.menu){
                serializer.shutdown();
                glfwSetWindowShouldClose(graphics.getWindow(), true);
            } else if (current_state == gameStates.game){
                //pause = !pause;

            }
            else current_state = gameStates.menu;

        });

        menu_input.registerCommand(GLFW_KEY_ENTER,true,(double elapsedTime)->{
            if(current_state.equals(gameStates.menu)){
                String change_to = menu.getHovering();
                switch(change_to){
                    case ("Start Game"):
                        //


                        score = 0;
                        pause = false;
                        player = new PlayerCar(audio);
                        spawner = new CarSpawner(player, TOP + screen_height,TOP);
                        current_state = gameStates.game;

                        break;
                    case ("View High Scores"):

                        serializer.loadHighScores(highScores);
                        current_state = gameStates.scores;

                        break;
                    case ("Credits"):
                        scrollOffset = 0; // Initial position, will move up over time
                        current_state = gameStates.credits;

                        break;
                    case ("Customize Controls"):
                        current_state = gameStates.options;

                        break;
                    case ("Continue"):
                        pause = !pause;
                        break;
                    case ("Exit"):
                        current_state = gameStates.exit;

                        break;
                }
            }
        });

    }

    public void shutdown() {
    }

    public void run() {
        // Grab the first time
        double previousTime = glfwGetTime();

        while (!graphics.shouldClose()) {
            double currentTime = glfwGetTime();
            double elapsedTime = currentTime - previousTime;    // elapsed time is in seconds
            previousTime = currentTime;

            processInput(elapsedTime);
            update(elapsedTime);
            render(elapsedTime);
        }
    }

    private void processInput(double elapsedTime) {
        // Poll for window events: required in order for window, keyboard, etc events are captured.
        glfwPollEvents();

        if(current_state == gameStates.game && !pause) input.update(elapsedTime,graphics);

        if (current_state != gameStates.game && current_state != gameStates.transition){
            menu_input.update(elapsedTime,graphics);
        } else if(current_state == gameStates.game && pause){
            menu_input.update(elapsedTime,graphics);
        }

    }

    private void update(double elapsedTime) {

        if (gameStartTime != -1) { // Check if timer has started
            double currentTime = glfwGetTime();
            if (currentTime - gameStartTime >= transitionDelay) {

                switch (current_state){
                    case transition:


                        break;

                    case game:
                        current_state = gameStates.menu;
                        break;

                }

                gameStartTime = -1; // Reset timer
            }
        }

        switch (current_state) {
            case menu:
                // Code to execute if expression equals value1
                break;
            case game:
                // Code to execute if expression equals value2
                player.update(elapsedTime);
                if (player.getCrashed()){
                    if(gameStartTime == -1){
                        score = spawner.getDodged();

                        if(score > 0){
                            highScores.addScore(new Score(score));
                            serializer.saveHighScores(highScores);
                        }

                        gameStartTime = glfwGetTime();
                    }
                    break;

                }



                spawner.update(elapsedTime);
                //If paused hold on updating

                //ROAD SCROLL UPDATE

                scrollOffset_road1 += (float) (scrollSpeed * elapsedTime);
                scrollOffset_road2 +=   (scrollSpeed * elapsedTime);


                System.out.println("ROAD 1 = "+scrollOffset_road1);
                System.out.println("ROAD 2 = "+scrollOffset_road2);

                if(scrollOffset_road1 > screen_height + TOP){
                    scrollOffset_road1 = scrollOffset_road2 - (screen_height);
                }

                if(scrollOffset_road2 > screen_height + TOP){
                    scrollOffset_road2 = scrollOffset_road1 - (screen_height);
                }

                if(!pause){

                }

                break;
            case transition:
                if (gameStartTime == -1) { // Start timer only if it hasn't started
                    gameStartTime = glfwGetTime();

                }
                break;
            case exit:

                serializer.shutdown();
                glfwSetWindowShouldClose(graphics.getWindow(), true);
                // Code to execute if expression equals value3
                break;
            case scores:
                // Code to execute if expression equals value4
                break;
            // ... more cases
            case credits:

                break;
        }
    }

    private void render(double elapsedTime) {
        graphics.begin();


        //graphics.draw(background,box,Color.WHITE);


        //Draw boarder


        float title_position_y = -0.35f;
        float title_position_x = -0.5f;
        float title_textHeight = .05f;

        float position_x = -0.5f;
        float position_y = -0.15f;
        float textHeight = .045f;



        switch (current_state) {
            case menu:

                graphics.drawTextByHeight(title_font, "MIDTERM: DODGE", title_position_x, title_position_y, title_textHeight, Color.WHITE);


                for(String option: menu.getOptions()){
                    if(Objects.equals(option, menu.getHovering())){
                        graphics.drawTextByHeight(sub_font, option, position_x, position_y, textHeight, Color.YELLOW);

                    }
                    else{
                        graphics.drawTextByHeight(sub_font, option, position_x, position_y, textHeight, Color.WHITE);
                    }

                    position_y = position_y + .075f;
                }


                break;
            case game:
                Rectangle box1 = new Rectangle(LHS,scrollOffset_road1,screen_size,screen_height);
                Rectangle box2 = new Rectangle(LHS,scrollOffset_road2,screen_size,screen_height);
                graphics.draw(background,box1, Color.WHITE);
                graphics.draw(background,box2, Color.WHITE);


                player.render(graphics);
                spawner.render(graphics);



                if(player.getCrashed()){
                    float top = -0.25f;
                    String gameover = "GAME OVER";
                    String thanks = "Thank you for playing!";

                    String scored = "You scored "+spawner.getDodged()+" points!";
                    //graphics.drawTextByHeight(sub_font, "Starting in : "+String.format("%.2f",transitionDelay - (glfwGetTime() - gameStartTime)), title_position_x, title_position_y, title_textHeight, Color.WHITE);
                    float width = font.measureTextWidth(gameover, textHeight);
                    graphics.drawTextByHeight(font, gameover, 0.0f - width / 2, top, textHeight, Color.WHITE);

                    width = font.measureTextWidth(thanks, textHeight);
                    graphics.drawTextByHeight(font, thanks, 0.0f - width / 2, top+textHeight, textHeight, Color.WHITE);

                    width = font.measureTextWidth(scored, textHeight);
                    graphics.drawTextByHeight(font, scored, 0.0f - width / 2, top+textHeight+textHeight, textHeight, Color.WHITE);

                }




                break;
            case transition:
                graphics.drawTextByHeight(sub_font, "Starting in : "+String.format("%.2f",transitionDelay - (glfwGetTime() - gameStartTime)), title_position_x, title_position_y, title_textHeight, Color.WHITE);
                break;
            case options:
                graphics.drawTextByHeight(title_font, "OPTIONS", title_position_x, title_position_y, title_textHeight, Color.WHITE);
                graphics.drawTextByHeight(sub_font, "Not required anymore :)", position_x, position_y, textHeight, Color.WHITE);

                break;
            case scores:
                graphics.drawTextByHeight(title_font, "SCORES", title_position_x, title_position_y, title_textHeight, Color.WHITE);
                if(this.highScores != null && this.highScores.initialized){

                    int temp = 0;
                    for(Score score : this.highScores.getScores()){
                        graphics.drawTextByHeight(sub_font, "Rank "+(temp+1)+" - "+score.score, position_x, position_y+(textHeight*(temp+1)), title_textHeight, Color.WHITE);
                        temp +=1;
                    }

                } else graphics.drawTextByHeight(sub_font, "No scores recorded...", position_x, position_y+textHeight, title_textHeight, Color.WHITE);


                // Code to execute if expression equals value2
                break;
            // ... more cases
            case credits:

                // Starting Y position (beginning from the bottom of the screen)
                float startY = TOP + .2f; // Start slightly off-screen
                float yOffset = startY + scrollOffset; // Apply scroll effect
                textHeight = textHeight -.01f;
                // Draw title
                graphics.drawTextByHeight(title_font, "CREDITS", title_position_x, yOffset, textHeight, Color.WHITE);
                yOffset += textHeight * 2; // Extra space after title

                // Credits data
                String[][] credits = {
                        {"Dev", "Logan Liddiard"},
                        {"Background", "Dean Mathias"},
                        {"Sound Effect", "Pixabay.com"},
                        {"Sprite Work", "Dean Mathias"},
                        {"Code Contributors", "Dean Mathias & ChatGPT"},
                        {"Special Thanks", "Dean Mathias & ChatGPT"}
                };

                float sectionSpacing = textHeight * 1.5f; // Spacing between sections

                for (String[] credit : credits) {
                    // Only draw if text is inside the screen bounds
                    if (yOffset + textHeight > (-screen_height / 2)+textHeight*2 && yOffset < (screen_height / 2)-textHeight*2) {
                        graphics.drawTextByHeight(sub_font, credit[0], position_x, yOffset, textHeight, Color.WHITE);
                        yOffset += textHeight;
                        graphics.drawTextByHeight(sub_font, "\t" + credit[1], position_x, yOffset, textHeight, Color.WHITE);
                        yOffset += sectionSpacing;
                    } else {
                        yOffset += textHeight +sectionSpacing; // Still update position, even if not drawn
                    }
                }


                break;
        }



        graphics.end();
    }
}
import java.awt.Robot;
import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

public class LordGivt {
    private Robot robot;
    private ScheduledExecutorService executor;

    public LordGivt() {
        try {
            this.robot = new Robot();
            downloadImage("https://cdn.discordapp.com/attachments/1158731821836664969/1240342484140163133/image.png?ex=66463674&is=6644e4f4&hm=d406d021da7d22ec9e62b5a0f3b7379dc4e200603979aa0ec09f4463df826a54&", "image.jpg");
            this.executor = Executors.newSingleThreadScheduledExecutor();
            this.executor.scheduleAtFixedRate(this::autoAttack, 0, 100, TimeUnit.MILLISECONDS);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private void downloadImage(String url, String fileName) {
        try {
            BufferedImage image = ImageIO.read(new URL(url));
            ImageIO.write(image, "jpg", new File(fileName));
            System.out.println("[ + ] Image successfully downloaded: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[ + ] Image could not be retrieved");
        }
    }

    public float getAttackSpeed() {
        try {
            URL url = new URL("https://127.0.0.1:2999/liveclientdata/allgamedata");
            BufferedImage image = ImageIO.read(url);
            String jsonString = "";
            return Float.parseFloat(jsonString);
        } catch (IOException | NumberFormatException e) {
            return 0.0f;
        }
    }

    public List<int[]> getEnemyCoordinates() {
        int top = 0, left = 0, width = 1920, height = 1080;
        String enemyImagePath = "image.jpg";
        BufferedImage enemyImage;
        try {
            enemyImage = ImageIO.read(new File(enemyImagePath));
        } catch (IOException e) {
            return null;
        }
        BufferedImage screenImage = null;
        List<int[]> coordinates = new ArrayList<>();
        double threshold = 0.95;
        for (int i = 0; i < enemyImage.getWidth(); i++) {
            for (int j = 0; j < enemyImage.getHeight(); j++) {
                if (enemyImage.getRGB(i, j) >= threshold) {
                    coordinates.add(new int[]{i, j});
                }
            }
        }
        return coordinates;
    }

    private double[] kite() {
        double speed = getAttackSpeed();
        double attack;
        double move;
        if (speed <= 2.35) {
            attack = 0.74 / speed * 0.60;
            move = 1 / speed * 0.60;
        } else {
            attack = 0.73 / speed * 0.55;
            move = 1 / speed * 0.60;
        }
        return new double[]{attack, move};
    }

    private void autoAttack() {
        if (isKeyPressed(KeyEvent.VK_SPACE)) {
            moveMouse();
        }
    }

    private void moveMouse() {
        List<int[]> coordinates = getEnemyCoordinates();
        if (coordinates != null && !coordinates.isEmpty()) {
            int[] firstCoordinate = coordinates.get(0);
            int x = firstCoordinate[0];
            int y = firstCoordinate[1];
            robot.mouseMove(x, y);
            robot.mousePress(KeyEvent.BUTTON3_DOWN_MASK);
            robot.mouseRelease(KeyEvent.BUTTON3_DOWN_MASK);
            double[] kiteValues = kite();
            if (getAttackSpeed() >= 2.35) {
                try {
                    Thread.sleep(70);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime <= kiteValues[1]) {
                    robot.mousePress(KeyEvent.BUTTON3_DOWN_MASK);
                    robot.mouseRelease(KeyEvent.BUTTON3_DOWN_MASK);
                    if (System.currentTimeMillis() - startTime >= kiteValues[1]) {
                        robot.keyPress(KeyEvent.VK_SPACE);
                        robot.keyRelease(KeyEvent.VK_SPACE);
                    }
                }
            } else {
                try {
                    Thread.sleep((long) (kiteValues[0] / 2));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime <= kiteValues[1]) {
                    robot.mousePress(KeyEvent.BUTTON3_DOWN_MASK);
                    robot.mouseRelease(KeyEvent.BUTTON3_DOWN_MASK);
                    if (System.currentTimeMillis() - startTime >= kiteValues[1]) {
                        robot.keyPress(KeyEvent.VK_SPACE);
                        robot.keyRelease(KeyEvent.VK_SPACE);
                    }
                }
            }
        } else {
            robot.mousePress(KeyEvent.BUTTON3_DOWN_MASK);
            robot.mouseRelease(KeyEvent.BUTTON3_DOWN_MASK);
        }
    }

    private boolean isKeyPressed(int keyCode) {
        return robot.keyPress(keyCode);
    }

    public static void main(String[] args) {
        new LordGivt();
    }
}

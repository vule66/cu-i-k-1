import java.awt.*;

public class Gian {
    private Rectangle rectangle;

    public Gian(int x, int y) {
        this.rectangle = new Rectangle(x, y, 50, 100); // Gà có kích thước 50x50
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void move(int speed) {
        rectangle.y += speed;
    }

    public boolean isOutOfBounds(int screenHeight) {
        return rectangle.y > screenHeight;
    }

    public boolean contains(Point point) {
        return rectangle.contains(point);
    }
}

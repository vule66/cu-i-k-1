import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameDapGian extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
    private Timer timer;
    private ArrayList<Gian> gians;
    private GameManager gameManager;
    private Random random;

    private boolean isMousePressed = false;
    private Image backgroundImage; // Hình ảnh nền
    private Image cursorImage; // Hình ảnh con chuột
    private Image gianImage; // Hình ảnh gián
    private Image shootImage; // Hình ảnh đạn hoặc hình ảnh vùng va chạm

    public GameDapGian() {
        gians = new ArrayList<>();
        gameManager = new GameManager();
        random = new Random();

        // Tải hình ảnh nền
        backgroundImage = new ImageIcon(getClass().getResource("bgr.jpg")).getImage();

        // Tải hình ảnh gián
        gianImage = new ImageIcon(getClass().getResource("gias.gif")).getImage();

        // Tải hình ảnh con chuột
        cursorImage = new ImageIcon(getClass().getResource("vot.png")).getImage();

        // Tải hình ảnh đạn
        shootImage = new ImageIcon(getClass().getResource("vot.png")).getImage();

        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        timer = new Timer(20, this);
        timer.start();

        // Cập nhật con chuột mặc định
        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), "Custom Cursor"));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Vẽ nền
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // Vẽ gián
        for (Gian gian : gians) {
            Rectangle rect = gian.getRectangle();
            g.drawImage(gianImage, rect.x, rect.y, rect.width, rect.height, this);
        }

        // Vẽ vùng va chạm (đạn)
        if (isMousePressed) {
            // Vẽ hình ảnh đạn tại vị trí chuột
            g.drawImage(shootImage, getMousePosition().x - shootImage.getWidth(null) / 2, getMousePosition().y - shootImage.getHeight(null) / 2, this);
        }

        // Vẽ điểm số
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Điểm: " + gameManager.getScore(), 10, 20);
        g.drawString("Trượt:"+ gameManager.getMissedChickens(),10,40);

        // Game Over screen
        if (gameManager.isGameOver()) {
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.setColor(Color.RED);
            g.drawString("Game Over!", getWidth() / 2 - 100, getHeight() / 2 - 40);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.setColor(Color.WHITE);
            g.drawString("Click to Restart", getWidth() / 2 - 75, getHeight() / 2 + 40);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameManager.isGameOver()) return;

        // Di chuyển gián
        Iterator<Gian> gianIterator = gians.iterator();
        while (gianIterator.hasNext()) {
            Gian gian = gianIterator.next();
            gian.move(gameManager.getChickenSpeed());

            // Nếu gián ra ngoài màn hình, loại bỏ gián và kiểm tra thua
            if (gian.isOutOfBounds(getHeight())) {
                gianIterator.remove();
                gameManager.incrementMissedChickens();
                if (gameManager.getMissedChickens() >= 5) { // Nếu có 5 gián ra ngoài, kết thúc game
                    gameManager.endGame();
                }
            }
        }

        // Tạo gián mới với xác suất
        int spawnChance = 2 + (gameManager.getScore() / 50); // Tăng xác suất mỗi khi có 50 điểm

        // Kiểm tra và spawn gián không chồng lấn
        if (random.nextInt(100) < spawnChance) {
            int randomX;
            boolean positionOccupied;
            do {
                randomX = random.nextInt(getWidth() - 50); // Vị trí X ngẫu nhiên (gián có chiều rộng 50px)
                positionOccupied = false;

                // Kiểm tra xem gián có chồng lên nhau không
                for (Gian gian : gians) {
                    if (Math.abs(gian.getRectangle().x - randomX) < 50) { // Kiểm tra xem gián đã có chưa (gián có chiều rộng 50px)
                        positionOccupied = true;
                        break;
                    }
                }
            } while (positionOccupied); // Lặp lại cho đến khi tìm được vị trí không bị chồng lên

            gians.add(new Gian(randomX, 0)); // Tạo gián mới với vị trí X không chồng lên
        }

        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameManager.isGameOver()) {
            restartGame();
            return;
        }

        shootAt(e.getPoint());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        isMousePressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isMousePressed = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isMousePressed) {
            shootAt(e.getPoint());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private void shootAt(Point point) {
        boolean shotHit = false;

        // Lấy kích thước của shootImage (đạn) để tạo vùng va chạm
        int shootWidth = shootImage.getWidth(null);
        int shootHeight = shootImage.getHeight(null);

        // Tạo một vùng va chạm xung quanh hình ảnh bắn (đạn)
        Rectangle shootArea = new Rectangle(point.x - shootWidth / 2, point.y - shootHeight / 2, shootWidth, shootHeight);

        // Kiểm tra va chạm với gián trong vùng này
        Iterator<Gian> gianIterator = gians.iterator();
        while (gianIterator.hasNext()) {
            Gian gian = gianIterator.next();
            if (shootArea.intersects(gian.getRectangle())) {  // Kiểm tra xem vùng bắn có giao với gián hay không
                gianIterator.remove();  // Loại bỏ gián đã bị bắn
                gameManager.increaseScore(10);  // Tăng điểm số
                shotHit = true;
                break;
            }
        }

        repaint();  // Vẽ lại màn hình
    }

    private void restartGame() {
        gameManager.resetGame();
        gians.clear();
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Đập Gián");
        GameDapGian game = new GameDapGian();
        frame.add(game);
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

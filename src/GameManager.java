public class GameManager {
    private int score;
    private int chickenSpeed;
    private int missedChickens;
    private boolean isGameOver;

    public GameManager() {
        score = 0;
        chickenSpeed = 2;
        missedChickens = 0;
        isGameOver = false;
    }

    public void increaseScore(int points) {
        score += points;
        if (score % 50 == 0) {
            chickenSpeed++; // Tăng tốc độ gà mỗi 50 điểm
        }
    }

    public void incrementMissedChickens() {
        missedChickens++;
    }

    public void resetGame() {
        score = 0;
        chickenSpeed = 2;
        missedChickens = 0;
        isGameOver = false;
    }

    public void endGame() {
        isGameOver = true;
    }

    public int getScore() {
        return score;
    }

    public int getChickenSpeed() {
        return chickenSpeed;
    }

    public int getMissedChickens() {
        return missedChickens;
    }

    public boolean isGameOver() {
        return isGameOver;
    }
}
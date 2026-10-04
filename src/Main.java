//Platformer game with 3 levels

// Game state variables
int currentLevel = 1;
boolean gameWon = false;

        // Player variables
        float playerX, playerY;
final int playerWidth = 30;
final int playerHeight = 50;
float playerSpeed = 5;
float jumpForce = 12;
float yVelocity = 0;
float gravity = 0.6;
boolean isJumping = false;
boolean onGround = false;

// Game objects
ArrayList<Platform> platforms = new ArrayList<Platform>();
ArrayList<Enemy> enemies = new ArrayList<Enemy>();
ArrayList<Spike> spikes = new ArrayList<Spike>();
Goal goal;

void setup() {
    size(800, 600);
    noStroke();
    loadLevel(currentLevel);
}

void draw() {
    background(135, 206, 235); // Blue

    if (!gameWon) {
        handleInput();
        updatePhysics();
        drawGameObjects();
        checkCollisions();
        displayUI();
    } else {
        displayWinScreen();
    }
}

void loadLevel(int level) {
    // Clear existing objects
    platforms.clear();
    enemies.clear();
    spikes.clear();

    // Always add ground platform
    platforms.add(new Platform(0, height - 20, width, 20));

    // Level designs
    if (level == 1) {
        platforms.add(new Platform(200, 500, 150, 20));
        platforms.add(new Platform(400, 400, 150, 20));
        enemies.add(new Enemy(300, 480, 40, 40, 1.5, 250, 450));
        goal = new Goal(700, 350, 50, 50);
    }
    else if (level == 2) {
        platforms.add(new Platform(100, 450, 100, 20));
        platforms.add(new Platform(300, 350, 100, 20));
        platforms.add(new Platform(500, 250, 100, 20));
        spikes.add(new Spike(200, 430, 80, 20));
        spikes.add(new Spike(400, 330, 80, 20));
        enemies.add(new Enemy(400, 600, 40, 40, 1.5, 250, 300));
        enemies.add(new Enemy(150, 430, 30, 30, 2, 100, 300));
        goal = new Goal(650, 200, 50, 50);
    }
    else if (level == 3) {
        platforms.add(new Platform(150, 500, 80, 20));
        platforms.add(new Platform(300, 400, 80, 20));
        platforms.add(new Platform(450, 300, 80, 20));
        platforms.add(new Platform(600, 200, 80, 20));
        spikes.add(new Spike(230, 480, 40, 20));
        spikes.add(new Spike(380, 380, 40, 20));
        spikes.add(new Spike(530, 280, 40, 20));
        enemies.add(new Enemy(200, 480, 25, 25, 3, 150, 350));
        enemies.add(new Enemy(500, 280, 25, 25, 2, 450, 650));
        goal = new Goal(700, 150, 50, 50);
    }

    resetPlayer();
}

void resetPlayer() {
    playerX = 100;
    playerY = 300;
    yVelocity = 0;
}

void handleInput() {
    if (keyPressed) {
        if (keyCode == LEFT || key == 'a') playerX -= playerSpeed;
        if (keyCode == RIGHT || key == 'd') playerX += playerSpeed;
        if ((keyCode == UP || key == ' ' || key == 'w') && !isJumping && onGround) {
            yVelocity = -jumpForce;
            isJumping = true;
            onGround = false;
        }
    }

    // Keep player in bounds
    playerX = constrain(playerX, 0, width - playerWidth);
}

void updatePhysics() {
    // Apply gravity
    yVelocity += gravity;
    playerY += yVelocity;

    // Reset jumping flag when on ground
    onGround = false;

    // Platform collision
    for (Platform p : platforms) {
        if (playerX + playerWidth > p.x &&
                playerX < p.x + p.w &&
                playerY + playerHeight >= p.y &&
                playerY + playerHeight <= p.y + p.h) {
            playerY = p.y - playerHeight;
            yVelocity = 0;
            onGround = true;
            isJumping = false;
        }
    }

    // Check if player fell off screen
    if (playerY > height) {
        resetPlayer();
    }
}

void drawGameObjects() {
    // Draw platforms
    for (Platform p : platforms) {
        p.display();
    }

    // Draw enemies
    for (Enemy e : enemies) {
        e.update();
        e.display();
    }

    // Draw spikes
    for (Spike s : spikes) {
        s.display();
    }

    // Draw goal
    goal.display();

    // Draw player
    fill(255, 0, 0); // Red
    rect(playerX, playerY, playerWidth, playerHeight);
}
// Collision Check
void checkCollisions() {
    checkEnemyCollision();
    checkSpikeCollision();
    checkGoalCollision();
}

void checkEnemyCollision() {
    for (Enemy e : enemies) {
        if (playerX + playerWidth > e.x &&
                playerX < e.x + e.w &&
                playerY + playerHeight > e.y &&
                playerY < e.y + e.h) {
            resetPlayer();
        }
    }
}

void checkSpikeCollision() {
    for (Spike s : spikes) {
        if (playerX + playerWidth > s.x &&
                playerX < s.x + s.w &&
                playerY + playerHeight > s.y &&
                playerY < s.y + s.h) {
            resetPlayer();
        }
    }
}

void checkGoalCollision() {
    if (playerX + playerWidth > goal.x &&
            playerX < goal.x + goal.w &&
            playerY + playerHeight > goal.y &&
            playerY < goal.y + goal.h) {
        if (currentLevel < 3) {
            currentLevel++;
            loadLevel(currentLevel);
        } else {
            gameWon = true;
        }
    }
}

//UI Classes

void displayUI() {
    fill(0);
    text("Level: " + currentLevel, 20, 30);
    text("WASD/Arrows to move, Space to jump", 20, 60);
}

void displayWinScreen() {
    textSize(50);
    fill(0);
    text("YOU WIN!", width/2 - 120, height/2);
    textSize(12);
}

// Game Object Classes

class Platform {
    float x, y, w, h;

    Platform(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    void display() {
        fill(100, 200, 100); // Green
        rect(x, y, w, h);
    }
}

class Enemy {
    float x, y, w, h, speed, leftBound, rightBound;
    boolean movingRight = true;

    Enemy(float x, float y, float w, float h, float speed, float leftBound, float rightBound) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.speed = speed;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
    }

    void update() {
        if (movingRight) {
            x += speed;
            if (x > rightBound)
                movingRight = false;
        } else {
            x -= speed;
            if (x < leftBound)
                movingRight = true;
        }
    }

    void display() {
        fill(200, 100, 100); // Red
        rect(x, y, w, h);
    }
}

class Spike {
    float x, y, w, h;

    Spike(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    void display() {
        fill(150); // Gray base
        rect(x, y, w, h);

        // Draw spike triangles
        fill(180);
        int spikeCount = 5;
        float spikeWidth = w / spikeCount;
        for (int i = 0; i < spikeCount; i++) {
            triangle(x + i * spikeWidth, y + h, x + (i + 0.5) * spikeWidth, y, x + (i + 1) * spikeWidth, y + h);
        }
    }
}

class Goal {
    float x, y, w, h;

    Goal(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    void display() {
        fill(255, 255, 0); // Yellow
        rect(x, y, w, h);

        fill(0);
        textSize(20);
        text("GOAL", x + 5, y + h/2 + 5);
        textSize(12);
    }
}
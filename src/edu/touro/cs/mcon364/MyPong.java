package edu.touro.cs.mcon364;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
//path to properties file "C:\Users\Elyah Weiser\IdeaProjects\pong-one-player-weisere\highscores.properties"
public class MyPong extends JFrame {
    private static JButton ScoreButton;
    private static int LPScore = 0; //left paddle score
    private static boolean endgame = false;
    static Timer ballTimer;
    PriorityQueue<Score> scores = new PriorityQueue<>(new MyScoreComparator());
    boolean hitPaddle = true;
    private Point delta = new Point(+10,-10);
    Properties highScores = new Properties();
    private Point ball = new Point(100,200);

    public static void main(String[] args) {
	// write your code here
        new MyPong();
    }

    private JButton button;
    public MyPong()
    {
        //trying to test my properties thing seemingly not working
//        scores.add(new Score(50,"ezw"));
//        scores.add(new Score(40,"ezw"));
//        scores.add(new Score(2,"ezw"));
//        saveHighScores();
        setSize(800,800);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        add(new GamePanel(), BorderLayout.CENTER);
        ScoreButton = new JButton("TODO: Score");
        add(ScoreButton, BorderLayout.NORTH);
//        button.addActionListener( e -> {
//            startThreadThatCallsVerySlowMethod();
//        });
        add(new JLabel("TODO: status bar"), BorderLayout.SOUTH);
        setVisible(true);
        loadHighScoresFromProprietiesQueue();//loading scores from the properties class into the scores queue
    }

//    private void startThreadThatCallsVerySlowMethod() {
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //verySlowMethod();
//            }
//        });
//        t.start(); // not t.run
//    }

//    private void verySlowMethod() {
//        for (int i=0;i<1_000_000;i++)
//            System.out.print(i + " ");
//    }

    private class GamePanel extends JPanel{

        private Point paddle = new Point(20,300);


        GamePanel(){
            setSize(700,700);
            setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
            this.setBackground( Color.BLACK );
            ballTimer = new Timer(10,
                    e -> {
                        Rectangle currentBallLocation = new Rectangle(ball.x, ball.y, 40, 40);
                        Rectangle currentLPaddleLocation = new Rectangle(paddle.x, paddle.y, 10, 80);
                        hitPaddle = currentBallLocation.intersects(currentLPaddleLocation);
                        if (hitPaddle) {
                            LPScore++;
                            ScoreButton.setText("Hits: " + LPScore); // update hit counter on button
                            delta.x = -delta.x;//ball bounces away
                        }

                        // update location
                        ball.translate(delta.x,  delta.y);

                        if(ball.x <10 && !hitPaddle){//if the ball didnt hit the paddle and hit the left wall the game is over
                            ScoreButton.setText("game over");
                            ballTimer.stop();
                            GETUSERPANE User = new GETUSERPANE();//open up JOptionPane
                            User.getUserInput();//gets the initials
                        }

                        // bounds checking
                        if (ball.y < 10 || ball.y > 700) // top or bottom border
                        {
                            delta.y = -delta.y;
                        }
                        if (ball.x < 10 || ball.x > 700) // top or bottom border
                        {
                            delta.x = -delta.x;
                        }




                        repaint();
                    });
            ballTimer.start();

            addMouseWheelListener( e ->{
                if(paddle.y < 0){paddle.y = 0;}//sets upper limit on paddle
                if(paddle.y > 700){paddle.y = 700;}//sets lower limit on paddle
                paddle.y += -e.getPreciseWheelRotation() * 5;
                repaint();
            });
        }

        @Override
        public void paint(Graphics g){
            super.paint(g);

            g.setColor(Color.WHITE);
            g.fillOval(ball.x, ball.y, 40,40);

            g.fillRect(paddle.x,paddle.y, 10,80);
        }
    }

    private class Score /* Comparable<Score> */ {
        int score;
        String user;

        public Score(int score, String user){
            this.score = score;
            this.user = user;
        }


//        @Override
//        public int compareTo(Score other) {
//                // Sort by score in descending order
//                int scoreComp = Integer.compare(other.score, this.score);
//                if (scoreComp != 0) {
//                    return scoreComp;
//                }
//                // If scores are tied, sort by name in ascending order
//                return this.user.compareTo(other.user);
//            }

        public int getScore() {
            return score;
        }

        public String getUser() {
            return user;
        }
    }

    public class MyScoreComparator implements Comparator<Score> {
        @Override
        public int compare(Score s1, Score s2) {
            // compare o1 and o2, and return -1, 0, or 1
            // depending on their relative order
            // Sort by score in descending order
                int scoreComp = Integer.compare(s2.score, s1.score);
                if (scoreComp != 0) {
                    return scoreComp;
                }
                // If scores are tied, sort by name in ascending order
                return s1.user.compareTo(s2.user);
        }
    }



    private class GETUSERPANE extends JOptionPane{
        private String user;

        public void getUserInput() {

            user = showInputDialog(null, "GAME OVER! \nEnter three letter initials:");
            if(user != null) {//after user enters name and hits okay
                System.out.println("User entered: " + user);
                JButton startGameButton = new JButton("Start New Game");//this start new game button will appear
                startGameButton.addActionListener(e -> {
                    // code to restart the game and auto closes panel
                    LPScore = 0;
                    delta.x = -delta.x;
                    ball = new Point(100,200);
                    ballTimer.start();
                    hitPaddle = true;
                    Window window = SwingUtilities.getWindowAncestor(startGameButton);
                    window.dispose();

                });
                scores.add(new Score(LPScore, user));//adds user and score to list of scores (may need to move this)
                saveHighScoresFromQueueToProperties();//saves the top 3 from the queue to the properties
                loadHighScoresFromProprietiesQueue();//clears the queue and add the top 3 scores from properties
                String topScores = displayHighScoresFromProperties();//String builds the scores from the queue
                Object[] message = { "Welcome " + user + "!\n Your score is: " + LPScore + "\nTopScores are\n" + topScores, startGameButton };
                JOptionPane.showMessageDialog(null, message);
            } else {
                System.out.println("User cancelled the input dialog.");
            }

        }
    }


    //not sure what to do about the save and properties but will deal with that later
    private void loadHighScoresFromProprietiesQueue() {
        scores.clear();
        try {
            FileInputStream file = new FileInputStream("highscores.properties");
            highScores = new Properties();
            highScores.load(file);
            file.close();
            for (String user : highScores.stringPropertyNames()) {
                int score = Integer.parseInt(highScores.getProperty(user));
                scores.offer(new Score(score, user));
            }
        } catch (IOException e) {
            System.out.println("Unable to load high scores.");
            highScores = new Properties();
        }
        //highScores.clear();
    }

    private void saveHighScoresFromQueueToProperties() {
        try {
            FileOutputStream file = new FileOutputStream("highscores.properties");
            highScores.clear();
            //PriorityQueue<Score> copyScores = new PriorityQueue<>(scores); for some reason not saving all my scores not sure why???
            int count = 0;
            while ((!scores.isEmpty() && count < 3)) {
                Score score = scores.poll();
                highScores.setProperty(score.getUser(), String.valueOf(score.getScore()));
                count++;
            }
            highScores.store(file, null);
            file.close();
        } catch (IOException e) {
            System.out.println("Unable to save high scores.");
        }
    }

//    private void addHighScore(Score score) {
//        if (scores.size() < 3 || score.getScore() > scores.peek().getScore()) {
//            scores.offer(score);
//            saveHighScores();
//        }
//    }

    private String displayHighScoresFromProperties() {
        StringBuilder sb = new StringBuilder();
        for (String user : highScores.stringPropertyNames()) {
            int score = Integer.parseInt(highScores.getProperty(user));
            sb.append(user + ": " + score + "\n");
        }
        return sb.toString();
    }

    private String displayHighScoresFromQueue(){
        StringBuilder sb = new StringBuilder();
        for (Score s: scores){
            sb.append(s.getUser() + ": " + s.getScore()+ "\n");
        }
        return sb.toString();
    }



}


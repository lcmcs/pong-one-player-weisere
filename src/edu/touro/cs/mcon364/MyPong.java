package edu.touro.cs.mcon364;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Comparator;

public class MyPong extends JFrame {
    private static JButton ScoreButton;
    private static int LPScore = 0; //left paddle score

    public static void main(String[] args) {
	// write your code here
        new MyPong();
    }

    private JButton button;
    public MyPong()
    {
        setSize(700,700);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        add(new GamePanel(), BorderLayout.CENTER);
        ScoreButton = new JButton("TODO: Score");
        add(ScoreButton, BorderLayout.NORTH);
//        button.addActionListener( e -> {
//            startThreadThatCallsVerySlowMethod();
//        });
        add(new JLabel("TODO: status bar"), BorderLayout.SOUTH);
        setVisible(true);
    }

    private void startThreadThatCallsVerySlowMethod() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //verySlowMethod();
            }
        });
        t.start(); // not t.run
    }

//    private void verySlowMethod() {
//        for (int i=0;i<1_000_000;i++)
//            System.out.print(i + " ");
//    }

    private static class GamePanel extends JPanel{
        private Point ball = new Point(100,200);
        private Point delta = new Point(+1,-1);
        private Point paddle = new Point(20,300);


        GamePanel(){
            setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
            this.setBackground( Color.BLACK );
            Timer ballTimer = new Timer(10,
                    e -> {
                        // update location
                        ball.translate(delta.x,  delta.y);

                        // bounds checking
                        if (ball.y < 10 || ball.y > 500) // top or bottom border
                        {
                            delta.y = -delta.y;
                        }
                        if (ball.x < 10 || ball.x > 700) // top or bottom border
                        {
                            delta.x = -delta.x;
                        }

                        Rectangle currentBallLocation = new Rectangle(ball.x, ball.y, 40, 40);
                        Rectangle currentLPaddleLocation = new Rectangle(paddle.x, paddle.y, 10, 80);
                        if (currentBallLocation.intersects(currentLPaddleLocation)) {
                            LPScore++;
                            ScoreButton.setText("Hits: " + LPScore); // update hit counter on button
                            delta.x = -delta.x;//ball bounces away
                        }

                        repaint();
                    });
            ballTimer.start();

            addMouseWheelListener( e ->{
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

    private class Score implements Comparator<Score> {
        int score;
        String user;

        public Score(int score, String user){
            this.score = score;
            this.user = user;
        }

        @Override
        public int compare(Score o1, Score o2) {
            if (o1.score == o2.score){return 0;}
            else {return o1.score - o2.score;}
        }

    }
}


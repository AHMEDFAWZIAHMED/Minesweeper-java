import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class App {

    static JFrame frame;
    static JPanel contentPane;
    static JButton[] buttons;
    static int[] allIndex, fColIndex, lColIndex, hiddenBomb;
    static ArrayList<Integer> revealedSpot;

    public static void main(String[] args) throws Exception {
        
        hiddenBomb = new int[5];
        revealedSpot = new ArrayList<>();
        allIndex = IntStream.range(0, 25).toArray();
        fColIndex = IntStream.of(0, 5, 10, 15, 20).toArray();
        lColIndex = IntStream.of(4, 9, 14, 19, 24).toArray();

        frame = new JFrame();
        contentPane = new JPanel();
        buttons = new JButton[25];

        contentPane.setLayout(new GridLayout(5, 5));
        frame.add(contentPane);
        frame.setSize(500, 500);

        createBoard();

        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
    }

    static void createBoard() {
        for (int i=0; i<25; i++) {
            int j = i;
            buttons[i] = new JButton("0");
            buttons[i].setBackground(Color.BLUE);
            buttons[i].setForeground(Color.BLUE);
            buttons[i].setFocusPainted(false);
            buttons[i].setFont(new Font("Arial", Font.BOLD, 40));
            buttons[i].addActionListener(e -> checkSpot(j));
            contentPane.add(buttons[i]);
        }
    }

    static void checkSpot(int index) {

        if (revealedSpot.isEmpty()) {
            plantBombs(index);
        }
        int numOfBomb = 0;
        revealedSpot.add(index);
        ArrayList<Integer> neighbors = findNeighbors(index);

        if (isHolds(hiddenBomb, index)) {
            gameOver(0);
            return;
        }
        for (int neighbor: neighbors) {
            if (isHolds(hiddenBomb, neighbor)) {
                numOfBomb++;
            }
        }
        buttons[index].setText(String.valueOf(numOfBomb));
        buttons[index].setEnabled(false);
        buttons[index].setBackground(Color.WHITE);
        buttons[index].setForeground(Color.BLUE);
        contentPane.validate();
        contentPane.repaint();

        if (numOfBomb == 0) {
            buttons[index].setForeground(Color.LIGHT_GRAY);
            buttons[index].setBackground(Color.WHITE);
            buttons[index].setText("");
            for (int n: neighbors) {
                if (!revealedSpot.contains(n)) {
                    checkSpot(n);
                }

            }
        }
        if (revealedSpot.size() == 20) {
            gameOver(1);
        }
    }

    static void plantBombs(int indx) {
        List<Integer> cpuChoice = IntStream.range(0, 25).boxed().collect(Collectors.toList());
        Collections.shuffle(cpuChoice);
        cpuChoice.removeIf(n -> (n == indx));
        cpuChoice.removeIf(n -> (cpuChoice.indexOf(n)>4));
        for (int i=0; i<cpuChoice.size(); i++) {
            hiddenBomb[i] = cpuChoice.get(i);
        }
    }

    static ArrayList<Integer> findNeighbors(int number) {
        
        ArrayList<Integer> nearNeighbors = new ArrayList<>();

        int[] allindx = IntStream.of(-1, 1, -4, 4, -5, 5, -6, 6).map(n -> n+number).toArray();
        int[] firstCo = IntStream.of(1, -4, -5, 5, 6).map(n -> n+number).toArray();
        int[] lastCo = IntStream.of(-1, 4, -5, 5, -6).map(n -> n+number).toArray();

        if (isHolds(fColIndex, number)) {
            for (int f: firstCo) {
                if (isHolds(allIndex, f)) {
                    nearNeighbors.add(f);
                }
            }
        }
        else if (isHolds(lColIndex, number)) {
            for (int l: lastCo) {
                if (isHolds(allIndex, l)) {
                    nearNeighbors.add(l);
                }
            }
        }
        else {
            for (int a: allindx) {
                if (isHolds(allIndex, a)) {
                    nearNeighbors.add(a);
                }
            }
        }
        return nearNeighbors;
    }

    static void gameOver(int result) {
        String resultText = "You got it!";
        ImageIcon bomb = new ImageIcon("src/bomb.png");
        ImageIcon explosion = new ImageIcon("src/explosion.png");
        for (int i=0; i<buttons.length; i++) {
            if (isHolds(hiddenBomb, i)) { continue; }
            buttons[i].setEnabled(false);
            if (!revealedSpot.contains(i)) {
                buttons[i].setText("");
            }
        }
        for (int i=0; i<hiddenBomb.length; i++) {
            bomb.getImage().getScaledInstance(10, 10, Image.SCALE_SMOOTH);
            buttons[hiddenBomb[i]].setBackground(null);
            buttons[hiddenBomb[i]].setForeground(null);
            buttons[hiddenBomb[i]].setText(null);
            buttons[hiddenBomb[i]].setContentAreaFilled(false);
            buttons[hiddenBomb[i]].setBorderPainted(false);
            buttons[hiddenBomb[i]].setIcon(bomb);
            contentPane.validate();
            contentPane.repaint();
        }
        int delay = 1000;
        if (result == 0) {
            for (int i=0; i<hiddenBomb.length; i++) {
                int j = i;
                Timer timer1 = new Timer(500*(i+1), new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        explosion.getImage().getScaledInstance(10, 10, Image.SCALE_SMOOTH);
                        buttons[hiddenBomb[j]].setBackground(null);
                        buttons[hiddenBomb[j]].setForeground(null);
                        buttons[hiddenBomb[j]].setText(null);
                        buttons[hiddenBomb[j]].setContentAreaFilled(false);
                        buttons[hiddenBomb[j]].setBorderPainted(false);
                        buttons[hiddenBomb[j]].setIcon(explosion);
                        contentPane.validate();
                        contentPane.repaint();
                    }
                    
                });
                timer1.setRepeats(false);
                timer1.start();
            }
            resultText = "Good luck next time";
            delay = 3000;
        }
        String resText = resultText;
        Timer timer2 = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] options = {"Restart", "Exit"};
                int dialog = JOptionPane.showOptionDialog(
                    frame, "Play again?", resText, JOptionPane.NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                if (dialog == 0) {
                    restartGame();
                }
                else {
                    frame.dispose();
                }
            }
        });
        timer2.setRepeats(false);
        timer2.start();
    }

    static void restartGame() {
        for (JButton button: buttons) {
            button.setEnabled(true);
            button.setIcon(null);
            button.setText("0");
            button.setBackground(Color.BLUE);
            button.setForeground(Color.BLUE);
            button.setContentAreaFilled(true);
            button.setBorderPainted(true);
        }
        contentPane.validate();
        contentPane.repaint();
        revealedSpot.clear();
    }

    static boolean isHolds(int[] intArray, int numb) {
        for (int a: intArray) {
            if (a == numb) {
                return true;
            }
        }
        return false;
    }
}

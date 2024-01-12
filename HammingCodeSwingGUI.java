import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HammingCodeSwingGUI extends JFrame {

    private JTextField dataBitsField;
    private JTextArea resultArea;

    public HammingCodeSwingGUI() {
        setTitle("Hamming Code Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240)); // Set background color

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBackground(new Color(200, 220, 255)); // Set panel background color

        JLabel label = new JLabel("Enter number of bits of data word:");
        label.setFont(new Font("Arial", Font.BOLD, 14));
        dataBitsField = new JTextField(10);
        JButton generateButton = new JButton("Generate Hamming Code");
        generateButton.setBackground(new Color(100, 180, 100)); // Set button background color
        generateButton.setForeground(Color.WHITE); // Set button text color
        generateButton.setFont(new Font("Arial", Font.PLAIN, 12));

        JPanel resultPanel = new JPanel(new BorderLayout());
        resultArea = new JTextArea(15, 40);
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(255, 255, 200)); // Set text area background color
        JScrollPane scrollPane = new JScrollPane(resultArea);

        generateButton.addActionListener(e -> generateHammingCode());

        inputPanel.add(label);
        inputPanel.add(dataBitsField);
        inputPanel.add(generateButton);

        resultPanel.add(scrollPane, BorderLayout.CENTER);

        add(inputPanel, BorderLayout.NORTH);
        add(resultPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private static int calcNoOfRedBits(int d) {
        int r = 1;
        for (int i = 1; i <= d; i++) {
            if (Math.pow(2, i) >= (d + i + 1)) {
                r = i;
                break;
            }
        }
        return r;
    }

    private static void calcSenderCodeWord(int senderCodeWord[], int dataWord[], int r, int d) {
        int k = d - 1, ind = 1;
        boolean flag;
        for (int i = (d + r - 1); i >= 0; i--) {
            flag = false;
            for (int j = 0; j < ind; j++) {
                if ((int) Math.pow(2, j) == ind) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                senderCodeWord[i] = 0;
            } else {
                senderCodeWord[i] = dataWord[k];
                k--;
            }
            ind++;
        }
    }

    private static void codeWord(int senderCodeWord[], int d, int r) {
        System.out.println("\nRedundant Bits:");
        int ind = 1;
        for (int i = (d + r - 1); i >= 0; i--) {
            boolean flag = false;
            int xor = 0;
            for (int j = 0; j < ind; j++) {
                if ((int) Math.pow(2, j) == ind) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                int k = d + r - ind;
                int count = ind;
                while (k >= 0) {
                    xor ^= senderCodeWord[k];
                    k--;
                    count--;
                    if (count == 0) {
                        k = k - ind;
                        count = ind;
                    }
                }
                System.out.println("R" + (i - (d + r)) + " = " + xor);
                senderCodeWord[i] = xor;
            }
            ind++;
        }
        System.out.println();
    }

    private static boolean detectError(int recCodeWord[], int redBits[], int d, int r) {
        codeWord(recCodeWord, d, r);
        int ind = 1, rind = r - 1;
        for (int i = (d + r - 1); i >= 0; i--) {
            boolean flag = false;
            for (int j = 0; j < ind; j++) {
                if ((int) Math.pow(2, j) == ind) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                redBits[rind] = recCodeWord[i];
                rind--;
            }
            ind++;
        }
        for (int i = 0; i < r; i++) {
            if (redBits[i] == 1) {
                return false;
            }
        }
        return true;
    }

    private static void correctError(int recCodeWord[], int redBits[], int dataWord[], int d, int r) {
        String binary = "";
        for (int i = 0; i < r; i++) {
            binary = binary + redBits[i];
        }
        int decimal = Integer.parseInt(binary, 2);
        System.out.println("Error detected at bit position " + decimal);
        int index = d + r - decimal;
        if (recCodeWord[index] == 0) {
            recCodeWord[index] = 1;
        } else {
            recCodeWord[index] = 0;
        }
        int ind = 1, dind = d - 1;
        for (int i = (d + r - 1); i >= 0; i--) {
            boolean flag = false;
            for (int j = 0; j < ind; j++) {
                if ((int) Math.pow(2, j) == ind) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                dataWord[dind] = recCodeWord[i];
                dind--;
            }
            ind++;
        }
    }

    private void generateHammingCode() {
        try {
            int dataBits = Integer.parseInt(dataBitsField.getText());

            // Your Hamming Code logic
            int d = dataBits;
            int dataWord[] = new int[d];
            int r = calcNoOfRedBits(d);

            resultArea.append("Number of Redundant bits are: " + r + "\n");

            resultArea.append("Enter Data word:\n");
            for (int i = 0; i < d; i++)
                dataWord[i] = Integer.parseInt(JOptionPane.showInputDialog("Enter Data bit " + (i + 1) + ":"));

            int senderCodeWord[] = new int[d + r];
            calcSenderCodeWord(senderCodeWord, dataWord, r, d);
            resultArea.append("Sender Code Word:\n");
            codeWord(senderCodeWord, d, r);
            for (int i = 0; i < (d + r); i++)
                resultArea.append(senderCodeWord[i] + " ");
            resultArea.append("\n");

            int recCodeWord[] = new int[d + r];
            resultArea.append("For RECEIVER:\n");
            resultArea.append("Enter Received Code Word: \n");
            for (int i = 0; i < (d + r); i++)
                recCodeWord[i] = Integer.parseInt(JOptionPane.showInputDialog("Enter Received bit " + (i + 1) + ":"));

            int redBits[] = new int[r];
            boolean err = detectError(recCodeWord, redBits, d, r);
            if (err) {
                resultArea.append("\nNo error in transmission.\n");
                resultArea.append("\nExtracted Data word is:\n");
                for (int i = 0; i < d; i++)
                    resultArea.append(dataWord[i] + " ");
                resultArea.append("\n");
            } else {
                resultArea.append("\nError found in data transmission.\n----------------------------------\n\n");
                correctError(recCodeWord, redBits, dataWord, d, r);
                resultArea.append("Code Word after Error correction is:\n");
                for (int i = 0; i < (d + r); i++)
                    resultArea.append(recCodeWord[i] + " ");
                resultArea.append("\n");
                resultArea.append("\nExtracted Data word is:\n");
                for (int i = 0; i < d; i++)
                    resultArea.append(dataWord[i] + " ");
                resultArea.append("\n");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number of bits.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HammingCodeSwingGUI().setVisible(true));
    }
}

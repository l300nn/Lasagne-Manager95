package javaSpiel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameUI extends JFrame {
    private RaceLogik raceLogik;
    private BettingSystem bettingSystem;

    // UI Komponenten
    private JLabel balanceLabel;
    private JLabel breakingNewsLabel;
    private JPanel horsesPanel;
    private RaceTrackPanel raceTrackPanel;
    private JTextArea logArea;
    private JButton startButton;
    private JTextField betInputField;
    private JButton placeBetButton;

    private List<JPanel> horseRowPanels;
    private int selectedHorseIndex = -1;

    private Timer raceTimer;
    private double[] currentProgress;

    private Random random = new Random();
    private int commentCooldown = 0;

    // Farbkonzept - Modern Dark Theme
    private final Color COLOR_BG = new Color(18, 18, 18);
    private final Color COLOR_CARD_BG = new Color(30, 30, 30);
    private final Color COLOR_TEXT_PRIMARY = new Color(240, 240, 240);
    private final Color COLOR_TEXT_SECONDARY = new Color(160, 160, 160);
    private final Color COLOR_PRIMARY = new Color(0, 120, 215);
    private final Color COLOR_ACCENT = new Color(39, 174, 96);
    private final Color COLOR_DANGER = new Color(220, 53, 69);
    private final Color COLOR_HOVER = new Color(45, 45, 45);
    private final Color COLOR_SELECTED = new Color(60, 60, 80);

    public GameUI() {
        raceLogik = new RaceLogik();
        bettingSystem = new BettingSystem(1000.0);

        setTitle("Vinewood Inside Track - Modern UI");
        setSize(1250, 820);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout(15, 15));

        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initUI();
        prepareNewRace();
    }

    private void initUI() {
        // --- Top-Panel: Balance ---
        JPanel topPanel = createCardPanel("");
        topPanel.setLayout(new BorderLayout());

        balanceLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 0, 0, 70));
                int h = getHeight();
                int w = getWidth();
                for (int y = 0; y < h; y += 3) {
                    g2.drawLine(0, y, w, y);
                }
            }
        };
        balanceLabel.setOpaque(true);
        balanceLabel.setBackground(new Color(5, 15, 5));
        balanceLabel.setForeground(new Color(50, 255, 50));
        balanceLabel.setFont(new Font("Monospaced", Font.BOLD, 32));
        balanceLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 40, 40), 4),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));

        topPanel.add(balanceLabel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // --- Center-Panel ---
        JPanel mainCenterPanel = new JPanel(new BorderLayout(15, 15));
        mainCenterPanel.setBackground(COLOR_BG);

        // --- BREAKING NEWS BANNER ---
        JPanel newsPanel = new JPanel(new BorderLayout());
        newsPanel.setBackground(new Color(220, 10, 30));
        breakingNewsLabel = new JLabel("🎤 LIVE: WILLKOMMEN IN VINEWOOD!");
        breakingNewsLabel.setFont(new Font("Impact", Font.ITALIC, 24));
        breakingNewsLabel.setForeground(Color.WHITE);
        breakingNewsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        newsPanel.add(breakingNewsLabel, BorderLayout.CENTER);

        Border yellowMatte = BorderFactory.createMatteBorder(3, 0, 3, 0, new Color(255, 215, 0));
        Border emptyPadding = BorderFactory.createEmptyBorder(12, 15, 12, 15);
        newsPanel.setBorder(BorderFactory.createCompoundBorder(yellowMatte, emptyPadding));

        mainCenterPanel.add(newsPanel, BorderLayout.NORTH);

        // --- Split für Wettbüro und Strecke ---
        JPanel splitPanel = new JPanel(new BorderLayout(15, 0));
        splitPanel.setBackground(COLOR_BG);

        // Left: Wettbüro
        horsesPanel = createCardPanel("Wettbüro");
        horsesPanel.setPreferredSize(new Dimension(600, 0));

        // Right: Rennstrecke
        raceTrackPanel = new RaceTrackPanel();
        raceTrackPanel.setBackground(COLOR_CARD_BG);

        JPanel trackContainer = createCardPanel("Rennstrecke");
        trackContainer.add(raceTrackPanel, BorderLayout.CENTER);

        splitPanel.add(horsesPanel, BorderLayout.WEST);
        splitPanel.add(trackContainer, BorderLayout.CENTER);

        mainCenterPanel.add(splitPanel, BorderLayout.CENTER);
        add(mainCenterPanel, BorderLayout.CENTER);

        // --- Bottom-Panel: Log und Start ---
        JPanel bottomPanel = new JPanel(new BorderLayout(15, 15));
        bottomPanel.setBackground(COLOR_BG);

        JPanel logCard = createCardPanel("System-Terminal");

        // Custom JTextArea für CRT Scanlines-Effekt
        logArea = new JTextArea(5, 50) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                // Dezent transparente Scanlines über den Text zeichnen
                g2.setColor(new Color(0, 0, 0, 70));
                int h = getHeight();
                int w = getWidth();
                for (int y = 0; y < h; y += 3) {
                    g2.drawLine(0, y, w, y);
                }
            }
        };
        logArea.setEditable(false);
        // Typisches CRT Phosphor-Grün auf extrem dunklem Hintergrund
        Color crtGreen = new Color(50, 255, 50);
        logArea.setBackground(new Color(5, 15, 5));
        logArea.setForeground(crtGreen);
        logArea.setCaretColor(crtGreen);
        logArea.setFont(new Font("Monospaced", Font.BOLD, 15));
        logArea.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JScrollPane scrollPane = new JScrollPane(logArea);
        // Bildschirm-Gehäuse/Rand (Monitor-Bezel)
        Border bezelInner = BorderFactory.createLoweredBevelBorder();
        Border bezelOuter = BorderFactory.createLineBorder(new Color(40, 40, 40), 5);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(bezelOuter, bezelInner));

        logCard.add(scrollPane, BorderLayout.CENTER);

        startButton = new JButton("Rennen starten");
        styleModernButton(startButton, COLOR_DANGER, Color.WHITE);
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        startButton.setEnabled(false);
        startButton.setPreferredSize(new Dimension(250, 0));
        startButton.addActionListener(e -> startRace());

        bottomPanel.add(logCard, BorderLayout.CENTER);
        bottomPanel.add(startButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Timer
        raceTimer = new Timer(50, e -> updateRaceTick());
    }

    private JPanel createCardPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(COLOR_CARD_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        if (!title.isEmpty()) {
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titleLabel.setForeground(COLOR_TEXT_PRIMARY);
            panel.add(titleLabel, BorderLayout.NORTH);
        }

        return panel;
    }

    private void styleModernButton(JButton btn, Color bgColor, Color fgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) {
                    btn.setBackground(bgColor.brighter());
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) {
                    btn.setBackground(bgColor);
                }
            }
        });
    }

    private void prepareNewRace() {
        raceLogik.prepareNewRace();
        updateBalanceLabel();

        selectedHorseIndex = -1;
        horseRowPanels = new ArrayList<>();

        BorderLayout layout = (BorderLayout) horsesPanel.getLayout();
        Component centerComp = layout.getLayoutComponent(BorderLayout.CENTER);
        if (centerComp != null)
            horsesPanel.remove(centerComp);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(COLOR_CARD_BG);

        currentProgress = new double[raceLogik.getHorses().size()];
        raceTrackPanel.setHorses(raceLogik.getHorses(), currentProgress);

        List<Horse> horses = raceLogik.getHorses();
        List<Double> odds = raceLogik.getCurrentOdds();

        // --- Interaktive Klickbare Reihen ---
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        tablePanel.setBackground(COLOR_CARD_BG);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_CARD_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));

        JLabel h1 = new JLabel("Pferd");
        h1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        h1.setForeground(COLOR_TEXT_SECONDARY);
        headerPanel.add(h1, BorderLayout.WEST);

        JLabel h2 = new JLabel("Quote", SwingConstants.CENTER);
        h2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        h2.setForeground(COLOR_TEXT_SECONDARY);
        h2.setPreferredSize(new Dimension(80, 30));
        headerPanel.add(h2, BorderLayout.EAST);

        tablePanel.add(headerPanel);

        // Pferde-Zeilen (Klickbar)
        for (int i = 0; i < horses.size(); i++) {
            Horse h = horses.get(i);
            final int currentIndex = i;

            JPanel rowPanel = new JPanel(new BorderLayout());
            rowPanel.setBackground(COLOR_CARD_BG);
            rowPanel.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 15));
            rowPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Name + Pixel-Portrait
            JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            namePanel.setOpaque(false);

            JLabel portraitLabel = new JLabel(createHorsePortrait(h.getColor(), 24, 24));
            JLabel nameLabel = new JLabel((i + 1) + ". " + h.getName());
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            nameLabel.setForeground(COLOR_TEXT_PRIMARY);

            namePanel.add(portraitLabel);
            namePanel.add(nameLabel);
            rowPanel.add(namePanel, BorderLayout.WEST);

            // Quote
            JLabel oddsLabel = new JLabel(String.valueOf(odds.get(i)), SwingConstants.CENTER);
            oddsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            oddsLabel.setForeground(COLOR_PRIMARY);
            oddsLabel.setPreferredSize(new Dimension(80, 30));
            rowPanel.add(oddsLabel, BorderLayout.EAST);

            // Hover & Click Logic
            rowPanel.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    if (selectedHorseIndex != currentIndex && placeBetButton.isEnabled()) {
                        rowPanel.setBackground(COLOR_HOVER);
                    }
                }

                public void mouseExited(MouseEvent evt) {
                    if (selectedHorseIndex != currentIndex && placeBetButton.isEnabled()) {
                        rowPanel.setBackground(COLOR_CARD_BG);
                    }
                }

                public void mouseClicked(MouseEvent evt) {
                    if (placeBetButton.isEnabled()) {
                        selectedHorseIndex = currentIndex;
                        updateHorseSelection();
                    }
                }
            });

            horseRowPanels.add(rowPanel);
            tablePanel.add(rowPanel);
        }

        contentPanel.add(tablePanel);
        contentPanel.add(Box.createVerticalStrut(30));

        // Gesamter Einsatz-Bereich als ein CRT-Terminal
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)) {
            @Override
            protected void paintChildren(Graphics g) {
                super.paintChildren(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 0, 0, 70));
                int h = getHeight();
                int w = getWidth();
                for (int y = 0; y < h; y += 3) {
                    g2.drawLine(0, y, w, y);
                }
            }
        };
        inputPanel.setBackground(new Color(5, 15, 5));
        Border inputBezelInner = BorderFactory.createLoweredBevelBorder();
        Border inputBezelOuter = BorderFactory.createLineBorder(new Color(40, 40, 40), 4);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(inputBezelOuter, inputBezelInner));

        Color crtGreen = new Color(50, 255, 50);

        JLabel betLabel = new JLabel("EINSATZ: $");
        betLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        betLabel.setForeground(crtGreen);
        inputPanel.add(betLabel);

        betInputField = new JTextField("10", 8);
        betInputField.setBackground(new Color(5, 15, 5));
        betInputField.setForeground(crtGreen);
        betInputField.setFont(new Font("Monospaced", Font.BOLD, 18));
        betInputField.setHorizontalAlignment(JTextField.CENTER);
        betInputField.setCaretColor(crtGreen);
        betInputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(crtGreen, 1),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        inputPanel.add(betInputField);

        placeBetButton = new JButton("[ WETTE PLATZIEREN ]");
        placeBetButton.setBackground(new Color(5, 15, 5));
        placeBetButton.setForeground(crtGreen);
        placeBetButton.setFont(new Font("Monospaced", Font.BOLD, 16));
        placeBetButton.setFocusPainted(false);
        placeBetButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(crtGreen, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        placeBetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        placeBetButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (placeBetButton.isEnabled()) {
                    placeBetButton.setBackground(crtGreen);
                    placeBetButton.setForeground(new Color(5, 15, 5));
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (placeBetButton.isEnabled()) {
                    placeBetButton.setBackground(new Color(5, 15, 5));
                    placeBetButton.setForeground(crtGreen);
                }
            }
        });
        placeBetButton.addActionListener(e -> placeBet());
        inputPanel.add(placeBetButton);

        contentPanel.add(inputPanel);
        horsesPanel.add(contentPanel, BorderLayout.CENTER);

        horsesPanel.revalidate();
        horsesPanel.repaint();

        for (ActionListener al : startButton.getActionListeners()) {
            startButton.removeActionListener(al);
        }
        startButton.addActionListener(e -> startRace());

        startButton.setText("Rennen starten");
        startButton.setEnabled(true);
        startButton.setBackground(COLOR_DANGER);

        breakingNewsLabel.setText("🎤 WARTEN AUF DEN START DES NÄCHSTEN RENNENS...");
    }

    private void updateHorseSelection() {
        for (int i = 0; i < horseRowPanels.size(); i++) {
            JPanel p = horseRowPanels.get(i);
            if (i == selectedHorseIndex) {
                p.setBackground(COLOR_SELECTED);
                p.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_PRIMARY, 1),
                        BorderFactory.createEmptyBorder(7, 4, 7, 14)));
            } else {
                p.setBackground(COLOR_CARD_BG);
                p.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 15));
            }
        }
    }

    // Kleines Pixel-Portrait des Pferdes für das Wettbüro
    private Icon createHorsePortrait(Color color, int width, int height) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

                // Hintergrund-Kästchen
                g2.setColor(new Color(20, 20, 20));
                g2.fillRoundRect(x, y, width, height, 4, 4);

                // Pferdekopf (Profil)
                g2.setColor(color);
                g2.fillRect(x + 6, y + 6, 12, 14); // Hals/Kopf
                g2.fillRect(x + 14, y + 6, 6, 6); // Schnauze

                // Mähne
                g2.setColor(new Color(40, 40, 40));
                g2.fillRect(x + 4, y + 6, 3, 10);

                // Auge
                g2.setColor(Color.WHITE);
                g2.fillRect(x + 12, y + 8, 2, 2);
                g2.setColor(Color.BLACK);
                g2.fillRect(x + 13, y + 8, 1, 1);
            }

            @Override
            public int getIconWidth() {
                return width;
            }

            @Override
            public int getIconHeight() {
                return height;
            }
        };
    }

    private void placeBet() {
        if (selectedHorseIndex == -1) {
            JOptionPane.showMessageDialog(this, "Bitte klicke auf eine Reihe, um ein Pferd auszuwählen!");
            return;
        }

        try {
            double amount = Double.parseDouble(betInputField.getText());
            if (bettingSystem.placeBet(selectedHorseIndex, amount)) {
                updateBalanceLabel();
                placeBetButton.setEnabled(false);
                placeBetButton.setBackground(new Color(15, 35, 15));
                placeBetButton.setForeground(new Color(30, 100, 30));
                betInputField.setEnabled(false);

                // Die Auswahl fixieren (Hover-Effekte stoppen, Cursor zurücksetzen)
                for (JPanel p : horseRowPanels) {
                    p.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }

                String name = raceLogik.getHorses().get(selectedHorseIndex).getName();
                log(">>> Wette platziert: " + amount + " $ auf " + name);
                breakingNewsLabel.setText("💰 WETTE AUF " + name.toUpperCase() + " WURDE ANGENOMMEN!");
            } else {
                JOptionPane.showMessageDialog(this, "Ungültiger Einsatz oder nicht genug Guthaben!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Bitte eine gültige Zahl eingeben!");
        }
    }

    private void startRace() {
        startButton.setEnabled(false);
        startButton.setBackground(new Color(60, 60, 60));
        placeBetButton.setEnabled(false);
        placeBetButton.setBackground(new Color(15, 35, 15));
        placeBetButton.setForeground(new Color(30, 100, 30));
        betInputField.setEnabled(false);
        for (JPanel p : horseRowPanels)
            p.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        logArea.setText("");
        log("Rennen gestartet! Die Boxen öffnen sich...");
        breakingNewsLabel.setText("🚨 DAS RENNEN STARTET! DIE BOXEN SIND OFFEN!");
        commentCooldown = 30;
        raceTimer.start();
    }

    private void updateRaceTick() {
        List<Horse> horses = raceLogik.getHorses();
        boolean raceFinished = false;
        int winningIndex = -1;

        for (int i = 0; i < horses.size(); i++) {
            currentProgress[i] += raceLogik.calculateTickProgress(horses.get(i));

            if (currentProgress[i] >= 100.0) {
                currentProgress[i] = 100.0;
                raceFinished = true;
                if (winningIndex == -1) {
                    winningIndex = i;
                }
            }
        }

        raceTrackPanel.updateParticles(); // Staub updaten und erzeugen
        raceTrackPanel.repaint();

        if (!raceFinished) {
            if (commentCooldown > 0) {
                commentCooldown--;
            } else {
                if (random.nextDouble() < 0.03) {
                    generateCommentary(horses);
                    commentCooldown = 50;
                }
            }
        }

        if (raceFinished) {
            raceTimer.stop();
            finishRace(winningIndex);
        }
    }

    private void generateCommentary(List<Horse> horses) {
        Horse h = horses.get(random.nextInt(horses.size()));
        String name = h.getName();
        String comment = "";

        switch (name) {
            case "Tax Evasion":
                comment = name + " legt einen Zahn zu – das Finanzamt hat den Steuerbescheid geschickt!";
                break;
            case "OnlyFoals":
                comment = name + " verlangsamt das Tempo, um ein Selfie für die Subscriber zu machen.";
                break;
            case "Pony Soprano":
                comment = name + " macht der Konkurrenz ein Angebot, das sie nicht ablehnen kann (und zieht vorbei).";
                break;
            case "Glue Factory":
                comment = name + " rennt um sein Leben – der Fahrer vom Transporter hat gewinkt!";
                break;
            case "Divorce Settlement":
                comment = name + " nimmt die halbe Rennstrecke für sich in Anspruch!";
                break;
            case "Sugar Daddy":
                comment = name + " wird plötzlich von jüngeren Stuten umschwärmt und verliert den Fokus.";
                break;
            case "Bad Investment":
                comment = name + " stürzt gnadenlos ab, wie der Krypto-Markt!";
                break;
            case "Al Capony":
                comment = name + " hat angeblich die Jury geschmiert. Wir warten auf den VAR.";
                break;
            case "Hoof Hearted":
                comment = "Der Jockey von " + name + " hält sich verdächtig oft die Nase zu.";
                break;
            case "Bitcoin Crash":
                comment = name + " war kurz vorne und ist jetzt komplett im Keller.";
                break;
            default:
                String[] general = {
                        name + " erleidet einen plötzlichen Motivationsverlust.",
                        name + " scheint vergessen zu haben, dass es sich um ein Rennen handelt.",
                        name + " wird vom Jockey mit der Peitsche an den Ernst der Lage erinnert.",
                        name + " stolpert über die eigenen Hufe. Peinlich.",
                        "Jemand im Publikum hat 'Leckerli' gerufen – " + name + " ist sichtlich abgelenkt.",
                        name + " überlegt kurz, ob ein Leben als Lasagne nicht doch stressfreier wäre.",
                        name + " trabt wie bei einem Sonntagsausflug.",
                        name + " zieht plötzlich gnadenlos an. Wurde da gedopt?"
                };
                comment = general[random.nextInt(general.length)];
                break;
        }

        log("🎤 [Kommentator]: \"" + comment + "\"");
        breakingNewsLabel.setText("🚨 " + comment.toUpperCase());
    }

    private void finishRace(int winningIndex) {
        Horse winner = raceLogik.getHorses().get(winningIndex);
        log("\n🏁 Das Rennen ist vorbei! Gewinner: " + winner.getName());

        breakingNewsLabel.setText("🏆 GEWINNER: " + winner.getName().toUpperCase() + " HAT DAS RENNEN GEWONNEN!");

        if (bettingSystem.getSelectedHorseIndex() != -1) {
            double odds = raceLogik.getCurrentOdds().get(winningIndex);
            double winAmount = bettingSystem.evaluateRace(winningIndex, odds);

            if (winAmount > 0) {
                log("$$$ GEWONNEN! Du erhältst " + String.format("%.2f", winAmount) + " $!");
                JOptionPane.showMessageDialog(this, "Glückwunsch! " + winner.getName() + " hat gewonnen!\nDu gewinnst "
                        + String.format("%.2f", winAmount) + " $!");
            } else {
                log("--- Verloren. Dein Geld ist nun im Casino-Tresor.");
                JOptionPane.showMessageDialog(this,
                        winner.getName() + " gewinnt.\nDeine Wette war leider ein Schuss in den Ofen.");
            }
        } else {
            log("Keine Wette platziert.");
        }

        updateBalanceLabel();

        startButton.setText("Neues Rennen");
        startButton.setEnabled(true);
        startButton.setBackground(COLOR_PRIMARY);
        for (ActionListener al : startButton.getActionListeners()) {
            startButton.removeActionListener(al);
        }
        startButton.addActionListener(e -> prepareNewRace());
    }

    private void updateBalanceLabel() {
        balanceLabel.setText("Guthaben: " + String.format("%.2f", bettingSystem.getBankroll()) + " $");
    }

    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}

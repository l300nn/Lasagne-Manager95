package javaSpiel;

public class BettingSystem {
    private double bankroll;
    private int selectedHorseIndex = -1;
    private double currentBetAmount = 0.0;

    public BettingSystem(double initialBankroll) {
        this.bankroll = initialBankroll;
    }

    public double getBankroll() {
        return bankroll;
    }

    public boolean placeBet(int horseIndex, double amount) {
        if (amount > 0 && amount <= bankroll) {
            this.selectedHorseIndex = horseIndex;
            this.currentBetAmount = amount;
            this.bankroll -= amount; // Einsatz sofort vom Guthaben abziehen
            return true;
        }
        return false;
    }

    public double evaluateRace(int winningHorseIndex, double odds) {
        if (selectedHorseIndex == winningHorseIndex) {
            // ==========================================
            // HIER ANPASSEN: Gewinnausschüttung
            // ==========================================
            // Gewinn = Einsatz * Quote
            double winAmount = currentBetAmount * odds;

            // Gewinn dem Guthaben hinzufügen (Einsatz ist ja schon weg, Gewinn beinhaltet
            // Einsatz)
            bankroll += winAmount;
            resetBet();
            return winAmount;
        }
        resetBet();
        return 0.0; // Verloren
    }

    private void resetBet() {
        this.selectedHorseIndex = -1;
        this.currentBetAmount = 0.0;
    }

    public int getSelectedHorseIndex() {
        return selectedHorseIndex;
    }

    public double getCurrentBetAmount() {
        return currentBetAmount;
    }
}

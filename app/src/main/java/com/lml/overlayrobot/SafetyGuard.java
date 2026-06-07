package com.lml.overlayrobot;

public class SafetyGuard {

    private static final String[] SENSITIVE = {
        "acheter", "achat", "buy", "vendre", "vente", "sell",
        "dépôt", "depot", "deposit", "retrait", "withdraw",
        "payer", "paiement", "payment", "confirmer", "confirmation",
        "valider", "envoyer", "send", "supprimer", "delete",
        "mot de passe", "password", "carte bancaire", "credit card",
        "banque", "bank", "transfer", "virement", "trade", "order"
    };

    public static boolean isSensitive(String text, String description) {
        if (text == null && description == null) return false;
        String combined = ((text != null ? text : "") + " " + (description != null ? description : "")).toLowerCase();
        for (String kw : SENSITIVE) {
            if (combined.contains(kw)) return true;
        }
        return false;
    }

    public static boolean canAutoClick(String text, String description, RobotMode mode) {
        if (mode == RobotMode.POINTAGE || mode == RobotMode.SIMULATION) return false;
        if (isSensitive(text, description)) {
            return mode != RobotMode.AUTO_SAFE;
        }
        return true;
    }
}
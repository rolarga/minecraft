package ch.bukkit.playground.instant.model;

public enum BattleType {
    COOP("Coop"),
    PVP("PvP"),
    GROUPPVP("Group Pvp");

    private String displayName;

    private BattleType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

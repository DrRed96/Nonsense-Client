package wtf.bhopper.nonsense.network;

public record Account(int id, String username, Rank rank) {

    public static final Account DEFAULT_ACCOUNT = new Account(0, "User", Rank.USER);
    public static final Account DEFAULT_MODERATOR = new Account(0, "Moderator", Rank.MODERATOR);
    public static final Account DEFAULT_ADMIN = new Account(0, "Admin", Rank.ADMIN);

    public enum Rank {
        USER("User", "\247f"),
        MODERATOR("Moderator", "\2479\u271A "),
        ADMIN("Admin", "\247c\u269D ");

        public final String name;
        public final String prefix;

        Rank(String name, String prefix) {
            this.name = name;
           this.prefix = prefix;
        }
    }

    public String getDisplayName() {
        return this.rank.prefix + this.username;
    }

}

package wtf.bhopper.nonsense.universe;

import com.google.common.base.Objects;

public record Account(int id, String username, Rank rank) {

    public static final Account DEFAULT_ACCOUNT = new Account(0, "User", Rank.USER);

    public enum Rank {
        USER("User", "\247f"),
        MODERATOR("Moderator", "\2479✭ "),
        ADMIN("Admin", "\247c❂ "),
        BANNED("Banned", "\2470\247m");

        public final String name;
        public final String prefix;

        Rank(String name, String prefix) {
            this.name = name;
            this.prefix = prefix;
        }
    }

    public String getDisplayName() {
        return this.rank.prefix + "\247r" + this.username;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", this.id)
                .add("username", this.username)
                .add("rank", this.rank)
                .toString();
    }
}

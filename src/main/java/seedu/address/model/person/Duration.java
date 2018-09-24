package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Workout's Duration in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidDuration(String)}
 */
public class Duration {

    public static final String MESSAGE_DURATION_CONSTRAINTS =
            "Duration should only contain alphanumeric characters and spaces, and it should not be blank";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String DURATION_VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} ]*";

    public final String fullDuration;

    /**
     * Constructs a {@code Duration}.
     *
     * @param duration A valid duration.
     */
    public Duration(String duration) {
        requireNonNull(duration);
        checkArgument(isValidDuration(duration), MESSAGE_DURATION_CONSTRAINTS);
        fullDuration = duration;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidDuration(String test) {
        return test.matches(DURATION_VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return fullDuration;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Duration // instanceof handles nulls
                && fullDuration.equals(((Duration) other).fullDuration)); // state check
    }

    @Override
    public int hashCode() {
        return fullDuration.hashCode();
    }

}
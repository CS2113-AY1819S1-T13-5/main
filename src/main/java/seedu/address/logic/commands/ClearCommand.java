package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.CommandHistory;
import seedu.address.model.WorkoutBook;
import seedu.address.model.Model;

/**
 * Clears the workout book.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "Workout book has been cleared!";


    @Override
    public CommandResult execute(Model model, CommandHistory history) {
        requireNonNull(model);
        model.resetData(new WorkoutBook());
        model.commitWorkoutBook();
        return new CommandResult(MESSAGE_SUCCESS);
    }
}

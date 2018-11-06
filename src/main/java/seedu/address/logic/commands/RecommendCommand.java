package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CALORIES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DIFFICULTY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DURATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_OPTIONAL_CALORIES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_OPTIONAL_DIFFICULTY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_OPTIONAL_DURATION;

import java.util.List;
import java.util.Random;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.events.ui.JumpToListRequestEvent;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.RecommendArguments;
import seedu.address.model.workout.Mode;
import seedu.address.model.workout.Workout;
import seedu.address.model.workout.WorkoutsPredicate;

/**
 * Recommends an existing workout from the workout book.
 */
public class RecommendCommand extends Command {

    public static final String COMMAND_WORD = "recommend";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Recommends a workout from the workout book "
            + "identified by a combination of DURATION,DIFFICULTY,CALORIES "
            + "or by a combination of optionals DURATION,DIFFICULTY,CALORIES\n"
            + "Two ways: Non-optional and optional\n"
            + "Non-optional Parameters: "
            + "[" + PREFIX_DURATION + "DURATION] "
            + "[" + PREFIX_DIFFICULTY + "DIFFICULTY] "
            + "[" + PREFIX_CALORIES + "CALORIES]\n"
            + "Non-optional Example: " + COMMAND_WORD + " "
            + PREFIX_DURATION + "20m\n"
            + "Optional Parameters: "
            + PREFIX_OPTIONAL_CALORIES + "CALORIES" + " or " + PREFIX_CALORIES + "CALORIES "
            + PREFIX_OPTIONAL_DIFFICULTY + "DIFFICULTY" + " or " + PREFIX_DIFFICULTY + "DIFFICULTY "
            + PREFIX_OPTIONAL_DURATION + "DURATION" + " or " + PREFIX_DURATION + "DURATION\n"
            + "Optional Example: " + COMMAND_WORD + " "
            + PREFIX_OPTIONAL_CALORIES + "150 "
            + PREFIX_DIFFICULTY + "beginner "
            + PREFIX_DURATION + "20m";

    public static final String MESSAGE_SUCCESS = "Workout recommended!";
    public static final String MESSAGE_NO_SUCH_WORKOUT = "There is no such workout in the workout book.";
    public static final String MESSAGE_OPTIONALS = "You need to supply all three prefixes as inputs,"
            + " be it optional or non-optional!";
    public static final String MESSAGE_INVALID_WORKOUTS_RECOMMENDED_SIZE = "The recommended workout size "
            + "provided has exceeded the current number of workouts recommended.\n"
            + "Current number of workouts recommended: %1$s";

    private final RecommendArguments recommendArguments;

    public RecommendCommand(RecommendArguments recommendArguments) {
        requireAllNonNull(recommendArguments);
        this.recommendArguments = recommendArguments;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        List<Workout> filteredWorkoutList = model.getFilteredWorkoutList();
        List<Workout> filteredInternalList;

        filteredInternalList = model.getFinalFilteredInternalList(recommendArguments);
        if (filteredInternalList.isEmpty()) {
            throw new CommandException(MESSAGE_NO_SUCH_WORKOUT);
        }

        if (recommendArguments.isModeNull() || (!recommendArguments.isModeNull() && recommendArguments.getMode()
                .toString().equals("single"))) {
            int targetIndex = getTargetIndex(filteredWorkoutList, filteredInternalList);
            EventsCenter.getInstance().post(new JumpToListRequestEvent(targetIndex));
        } else {
            Mode mode = recommendArguments.getMode();
            if (mode.toString().equals("all")) {
                WorkoutsPredicate workoutsPredicate = new WorkoutsPredicate(filteredInternalList);
                model.updateFilteredWorkoutList(workoutsPredicate);
            } else {
                int multipleInteger = mode.getMultipleInteger(mode.toString());
                if (multipleInteger > filteredInternalList.size()) {
                    throw new CommandException(String.format(MESSAGE_INVALID_WORKOUTS_RECOMMENDED_SIZE,
                            filteredInternalList.size()));
                }
                List<Workout> subFilteredInternalList = filteredInternalList.subList(0, multipleInteger);
                WorkoutsPredicate workoutsPredicate = new WorkoutsPredicate(subFilteredInternalList);
                model.updateFilteredWorkoutList(workoutsPredicate);
            }
        }
        return new CommandResult(MESSAGE_SUCCESS);
    }

    private int getTargetIndex(List<Workout> filteredWorkoutList, List<Workout> filteredInternalList) {
        Random rand = new Random();
        int randomIndex = rand.nextInt(filteredInternalList.size());
        Workout randomWorkout = filteredInternalList.get(randomIndex);
        return filteredWorkoutList.indexOf(randomWorkout);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof RecommendCommand // instanceof handles nulls
                && recommendArguments.equals(((RecommendCommand) other).recommendArguments));
    }

}

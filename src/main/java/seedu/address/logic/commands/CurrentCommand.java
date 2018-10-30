package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.ParserUtil.parseTag;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_WORKOUTS;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.jsoup.nodes.Element;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ProfileWindowManager;
import seedu.address.model.tag.Tag;
import seedu.address.model.workout.Calories;
import seedu.address.model.workout.Difficulty;
import seedu.address.model.workout.Duration;
import seedu.address.model.workout.Equipment;
import seedu.address.model.workout.Instruction;
import seedu.address.model.workout.Muscle;
import seedu.address.model.workout.Name;
import seedu.address.model.workout.Type;
import seedu.address.model.workout.Workout;

/**
 * Changes a workout to be the current workout in the workout book.
 */
public class CurrentCommand extends Command {

    public static final String COMMAND_WORD = "current";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Sets a workout to be the current workout identified "
            + "by the index number used in the displayed workout list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1 ";

    public static final String MESSAGE_CURRENT_WORKOUT_SUCCESS = "Current Workout: %1$s";
    public static final String MESSAGE_CURRENT_WORKOUT_FAILURE = "Fail to make the workout current.";
    public static final String MESSAGE_DUPLICATE_CURRENT_WORKOUT = "This workout is already current.";
    public static final String MESSAGE_MULTIPLE_CURRENT_WORKOUT = "There is already a current workout. Complete that "
            + "before trying again.";
    public static final String MESSAGE_MORE_DIFFICULT = "This workout is more difficult than your indicated workout "
            + "difficulty.\n";
    public static final String MESSAGE_HIGHER_CALORIES = "This workout requires more calories to be burnt than your "
            + "preferred calories.\n";
    public static final String MESSAGE_HIGHER_DURATION = "This workout will take longer than your preferred duration."
            + "\n";
    public static final String MESSAGE_CONTINUE = "Do you still want to make this workout current?";

    private static boolean currentWorkout;

    private boolean success = true;
    private final Index targetIndex;

    /**
     * @param targetIndex of the person in the filtered workout list to edit the state tag
     */
    public CurrentCommand(Index targetIndex) {
        requireNonNull(targetIndex);

        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        List<Workout> filteredWorkoutList = model.getFilteredWorkoutList();
        try {
            Workout workoutToEdit = filteredWorkoutList.get(targetIndex.getZeroBased());
            Workout editedWorkout = createEditedWorkout(workoutToEdit);
            if (success) {
                model.updateWorkout(workoutToEdit, editedWorkout);
                model.updateFilteredWorkoutList(PREDICATE_SHOW_ALL_WORKOUTS);
                model.commitWorkoutBook();
                this.currentWorkout = true;
                return new CommandResult(String.format(MESSAGE_CURRENT_WORKOUT_SUCCESS, editedWorkout));
            } else {
                return new CommandResult(MESSAGE_CURRENT_WORKOUT_FAILURE);
            }
        } catch (IndexOutOfBoundsException | ParseException e) {
            throw new CommandException(Messages.MESSAGE_INVALID_WORKOUT_DISPLAYED_INDEX);
        }
    }

    /**
     * Creates and returns a {@code Workout} with the details of {@code workoutToEdit}
     * edited with {@code editWorkoutDescriptor}.
     */
    private Workout createEditedWorkout(Workout workoutToEdit) throws CommandException, ParseException {
        assert workoutToEdit != null;
        Name updatedName = workoutToEdit.getName();
        Type updatedType = workoutToEdit.getType();
        Duration updatedDuration = workoutToEdit.getDuration();
        Difficulty updatedDifficulty = workoutToEdit.getDifficulty();
        Equipment updatedEquipment = workoutToEdit.getEquipment();
        Muscle updatedMuscle = workoutToEdit.getMuscle();
        Calories updatedCalories = workoutToEdit.getCalories();
        Instruction updatedInstruction = workoutToEdit.getInstruction();
        Set<Tag> originalTags = workoutToEdit.getTags();
        Set<Tag> updatedTags = new HashSet<>();
        for (Tag entry: originalTags) {
            updatedTags.add(entry);
        }

        int userCalories;
        int userDuration;

        ProfileWindowManager profileWindowManager;
        try {
            profileWindowManager = ProfileWindowManager.getInstance();
            Element divCalories = profileWindowManager.getCalories();
            Element divDuration = profileWindowManager.getDuration();
            Element divDifficulty = profileWindowManager.getDifficulty();
            String userDifficulty = profileWindowManager.trimmedDifficulty(divDifficulty.ownText());
            String calories = profileWindowManager.trimmedCalories(divCalories.ownText());
            String duration = profileWindowManager.trimmedDuration(divDuration.ownText());
            userCalories = profileWindowManager.convertStringIntoInt(calories);
            userDuration = profileWindowManager.convertStringIntoInt(duration);
            if (profileWindowManager.isMoreDifficult(userDifficulty, updatedDifficulty.toString())) {
                int reply = JOptionPane.showConfirmDialog(null, MESSAGE_MORE_DIFFICULT
                        + MESSAGE_CONTINUE, "Making this workout current", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.NO_OPTION) {
                    success = false;
                    return new Workout(updatedName, updatedType, updatedDuration, updatedDifficulty, updatedEquipment,
                            updatedMuscle, updatedCalories, updatedInstruction, originalTags, null);
                }
            }
            if (profileWindowManager.isHigherCalories(profileWindowManager.convertStringIntoInt(profileWindowManager
                    .trimmedCalories(updatedCalories.toString())), userCalories)) {
                int reply = JOptionPane.showConfirmDialog(null, MESSAGE_HIGHER_CALORIES
                        + MESSAGE_CONTINUE, "Making this workout current", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.NO_OPTION) {
                    success = false;
                    return new Workout(updatedName, updatedType, updatedDuration, updatedDifficulty, updatedEquipment,
                            updatedMuscle, updatedCalories, updatedInstruction, originalTags, null);
                }
            }
            if (profileWindowManager.isHigherDuration(profileWindowManager.convertStringIntoInt(profileWindowManager
                    .trimmedDuration(updatedDuration.toString())), userDuration)) {
                int reply = JOptionPane.showConfirmDialog(null, MESSAGE_HIGHER_DURATION
                        + MESSAGE_CONTINUE, "Making this workout current", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.NO_OPTION) {
                    success = false;
                    return new Workout(updatedName, updatedType, updatedDuration, updatedDifficulty, updatedEquipment,
                            updatedMuscle, updatedCalories, updatedInstruction, originalTags, null);
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        Tag future = parseTag("future");
        Tag current = parseTag("current");
        Tag completed = parseTag("completed");

        if (currentWorkout) {
            throw new CommandException(MESSAGE_MULTIPLE_CURRENT_WORKOUT);
        }
        if (originalTags.contains(current)) {
            throw new CommandException(MESSAGE_DUPLICATE_CURRENT_WORKOUT);
        }

        if (originalTags.contains(future)) {
            updatedTags.remove(future);
        }
        if (originalTags.contains(completed)) {
            updatedTags.remove(completed);
        }
        updatedTags.add(current);

        return new Workout(updatedName, updatedType, updatedDuration, updatedDifficulty, updatedEquipment,
                updatedMuscle, updatedCalories, updatedInstruction, updatedTags, null);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CurrentCommand)) {
            return false;
        }

        // state check
        CurrentCommand e = (CurrentCommand) other;
        return targetIndex.equals(e.targetIndex);
    }

    public static void setCurrentWorkout(boolean b) {
        currentWorkout = b;
    }
}

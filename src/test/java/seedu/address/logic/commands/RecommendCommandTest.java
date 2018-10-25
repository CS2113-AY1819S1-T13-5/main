package seedu.address.logic.commands;

import static org.junit.Assert.assertEquals;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyWorkoutBook;
import seedu.address.model.RecommendArguments;
import seedu.address.model.workout.Calories;
import seedu.address.model.workout.Difficulty;
import seedu.address.model.workout.Duration;
import seedu.address.model.workout.Workout;
import seedu.address.testutil.WorkoutBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class RecommendCommandTest {

    private static final CommandHistory EMPTY_COMMAND_HISTORY = new CommandHistory();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private CommandHistory commandHistory = new CommandHistory();

    private ModelStub modelStub = new ModelStub();

    @Before
    public void setUpModelStub() {
        Workout validWorkout = new WorkoutBuilder().build();
        modelStub.addWorkout(validWorkout);
    }

    @Test
    public void constructor_nullRecommendArguments_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        new RecommendCommand(null);
    }

    @Test
    public void execute_singleFieldRecommend_successful() throws Exception {
        // Calories
        Optional<Calories> calories = Optional.of(new Calories("150"));
        RecommendArguments recommendArguments = new RecommendArguments.Builder().withCalories(calories).build();
        CommandResult commandResult = new RecommendCommand(recommendArguments).execute(modelStub, commandHistory);

        assertEquals(RecommendCommand.MESSAGE_SUCCESS, commandResult.feedbackToUser);
        assertEquals(EMPTY_COMMAND_HISTORY, commandHistory);

        // Difficulty
        Optional<Difficulty> difficulty = Optional.of(new Difficulty("advanced"));
        recommendArguments = new RecommendArguments.Builder().withDifficulty(difficulty).build();
        commandResult = new RecommendCommand(recommendArguments).execute(modelStub, commandHistory);

        assertEquals(RecommendCommand.MESSAGE_SUCCESS, commandResult.feedbackToUser);
        assertEquals(EMPTY_COMMAND_HISTORY, commandHistory);

        // Duration
        Optional<Duration> duration = Optional.of(new Duration("20m"));
        recommendArguments = new RecommendArguments.Builder().withDuration(duration).build();
        commandResult = new RecommendCommand(recommendArguments).execute(modelStub, commandHistory);

        assertEquals(RecommendCommand.MESSAGE_SUCCESS, commandResult.feedbackToUser);
        assertEquals(EMPTY_COMMAND_HISTORY, commandHistory);
    }

    @Test
    public void execute_workoutNotFound_throwsCommandException() throws Exception {
        // Calories
        Optional<Calories> calories = Optional.of(new Calories("200"));
        RecommendArguments recommendArguments = new RecommendArguments.Builder().withCalories(calories).build();
        RecommendCommand recommendCommand = new RecommendCommand(recommendArguments);

        thrown.expect(CommandException.class);
        thrown.expectMessage(recommendCommand.MESSAGE_NO_SUCH_WORKOUT);
        recommendCommand.execute(modelStub, commandHistory);

        // Difficulty
        Optional<Difficulty> difficulty = Optional.of(new Difficulty("beginner"));
        recommendArguments = new RecommendArguments.Builder().withDifficulty(difficulty).build();
        recommendCommand = new RecommendCommand(recommendArguments);

        thrown.expect(CommandException.class);
        thrown.expectMessage(recommendCommand.MESSAGE_NO_SUCH_WORKOUT);
        recommendCommand.execute(modelStub, commandHistory);

        // Duration
        Optional<Duration> duration = Optional.of(new Duration("10m"));
        recommendArguments = new RecommendArguments.Builder().withDuration(duration).build();
        recommendCommand = new RecommendCommand(recommendArguments);

        thrown.expect(CommandException.class);
        thrown.expectMessage(recommendCommand.MESSAGE_NO_SUCH_WORKOUT);
        recommendCommand.execute(modelStub, commandHistory);
    }

    /**
     * A model stub.
     */
    private class ModelStub implements Model {
        final ArrayList<Workout> workoutsAdded = new ArrayList<>();

        @Override
        public void addWorkout(Workout workout) {
            requireNonNull(workout);
            workoutsAdded.add(workout);
        }

        @Override
        public void resetData(ReadOnlyWorkoutBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyWorkoutBook getWorkoutBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasWorkout(Workout workout) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteWorkout(Workout target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateWorkout(Workout target, Workout editedWorkout) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Workout> getFilteredWorkoutList() {
            return FXCollections.observableArrayList(workoutsAdded);
        }

        @Override
        public List<Workout> getFilteredInternalList(RecommendArguments recommendArguments) {
            return workoutsAdded.stream()
                    .filter(!recommendArguments.isCaloriesNull() ? w -> w.getCalories().fullCalories
                            .equals(recommendArguments.getCalories().toString()) : w -> w != null)
                    .filter(!recommendArguments.isDifficultyNull() ? w -> w.getDifficulty().fullDifficulty
                            .equals(recommendArguments.getDifficulty().toString()) : w -> w != null)
                    .filter(!recommendArguments.isDurationNull() ? w -> w.getDuration().fullDuration
                            .equals(recommendArguments.getDuration().toString()) : w -> w != null)
                    .collect(Collectors.toList());
        }

        @Override
        public void updateFilteredWorkoutList(Predicate<Workout> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canUndoWorkoutBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canRedoWorkoutBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void undoWorkoutBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void redoWorkoutBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void commitWorkoutBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void sortFilteredWorkoutList() {
            throw new AssertionError("This method should not be called.");
        }
    }

}
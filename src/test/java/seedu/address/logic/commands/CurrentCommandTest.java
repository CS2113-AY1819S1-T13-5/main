package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showWorkoutAtIndex;
import static seedu.address.logic.commands.CurrentCommand.MESSAGE_MULTIPLE_CURRENT_WORKOUT;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_WORKOUT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_WORKOUT;
import static seedu.address.testutil.TypicalIndexes.INDEX_TENTH_WORKOUT;
import static seedu.address.testutil.TypicalParameters.getTypicalTrackedDataList;
import static seedu.address.testutil.TypicalWorkouts.getTypicalWorkoutBook;

import org.junit.Before;
import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.TrackedData;
import seedu.address.model.UserPrefs;
import seedu.address.model.WorkoutBook;
import seedu.address.model.workout.Workout;

/**
 * Contains integration tests (interaction with the Model, UndoCommand and RedoCommand) and unit tests for
 * {@code CurrentCommand}.
 */
public class CurrentCommandTest {

    private Model model = new ModelManager(getTypicalWorkoutBook(), getTypicalTrackedDataList(), new TrackedData(),
            new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();

    @Before
    public void setUp() {
        CurrentCommand.setCurrentWorkout(false);
    }

    @Test
    public void execute_validIndexUnfilteredList_success() throws CommandException {

        Workout currentWorkout = model.getFilteredWorkoutList().get(INDEX_TENTH_WORKOUT.getZeroBased());
        Workout editedWorkout = CurrentCommand.createEditedWorkout(currentWorkout);
        CurrentCommand currentCommand = new CurrentCommand(INDEX_TENTH_WORKOUT);

        String expectedMessage = String.format(CurrentCommand.MESSAGE_CURRENT_WORKOUT_SUCCESS, editedWorkout);

        Model expectedModel = new ModelManager(model.getWorkoutBook(), model.getTrackedDataList(),
                model.getTrackedData(), new UserPrefs());
        expectedModel.updateWorkout(model.getFilteredWorkoutList().get(9), editedWorkout);
        expectedModel.commitModel();

        assertCommandSuccess(currentCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_preexistingCurrentWorkoutUnfilteredList_failure() {
        CurrentCommand.setCurrentWorkout(true);
        CurrentCommand currentCommand = new CurrentCommand(INDEX_TENTH_WORKOUT);

        assertCommandFailure(currentCommand, model, commandHistory, MESSAGE_MULTIPLE_CURRENT_WORKOUT);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredWorkoutList().size() + 1);
        CurrentCommand currentCommand = new CurrentCommand(outOfBoundIndex);

        assertCommandFailure(currentCommand, model, commandHistory, Messages.MESSAGE_INVALID_WORKOUT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() throws CommandException {
        showWorkoutAtIndex(model, INDEX_TENTH_WORKOUT);

        Workout editedWorkout = CurrentCommand
                .createEditedWorkout(model.getFilteredWorkoutList().get(INDEX_FIRST_WORKOUT.getZeroBased()));
        CurrentCommand currentCommand = new CurrentCommand(INDEX_FIRST_WORKOUT);

        String expectedMessage = String.format(CurrentCommand.MESSAGE_CURRENT_WORKOUT_SUCCESS, editedWorkout);

        Model expectedModel = new ModelManager(new WorkoutBook(model.getWorkoutBook()), model.getTrackedDataList(),
                model.getTrackedData(), new UserPrefs());
        expectedModel.updateWorkout(model.getFilteredWorkoutList().get(0), editedWorkout);
        expectedModel.commitModel();

        assertCommandSuccess(currentCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_preexistingCurrentWorkoutFilteredList_failure() {
        CurrentCommand.setCurrentWorkout(true);
        showWorkoutAtIndex(model, INDEX_TENTH_WORKOUT);

        CurrentCommand currentCommand = new CurrentCommand(INDEX_FIRST_WORKOUT);

        assertCommandFailure(currentCommand, model, commandHistory, MESSAGE_MULTIPLE_CURRENT_WORKOUT);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showWorkoutAtIndex(model, INDEX_FIRST_WORKOUT);

        Index outOfBoundIndex = INDEX_SECOND_WORKOUT;
        // ensures that outOfBoundIndex is still in bounds of workout book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getWorkoutBook().getWorkoutList().size());

        CurrentCommand currentCommand = new CurrentCommand(outOfBoundIndex);

        assertCommandFailure(currentCommand, model, commandHistory, Messages.MESSAGE_INVALID_WORKOUT_DISPLAYED_INDEX);
    }

    @Test
    public void executeUndoRedo_validIndexUnfilteredList_success() throws Exception {
        Workout currentWorkout = model.getFilteredWorkoutList().get(INDEX_FIRST_WORKOUT.getZeroBased());
        CurrentCommand currentCommand = new CurrentCommand(INDEX_FIRST_WORKOUT);
        Model expectedModel = new ModelManager(new WorkoutBook(model.getWorkoutBook()), model.getTrackedDataList(),
                model.getTrackedData(), new UserPrefs());
        Workout editedWorkout = CurrentCommand.createEditedWorkout(currentWorkout);
        expectedModel.updateWorkout(currentWorkout, editedWorkout);
        expectedModel.commitModel();

        // current -> first workout set to current
        currentCommand.execute(model, commandHistory);

        // undo -> reverts workout book back to previous state and filtered workout list to show all workouts
        expectedModel.undoModel();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // redo -> same first workout deleted again
        expectedModel.redoModel();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void executeUndoRedo_invalidIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredWorkoutList().size() + 1);
        CurrentCommand currentCommand = new CurrentCommand(outOfBoundIndex);

        // execution failed -> workout book state not added into model
        assertCommandFailure(currentCommand, model, commandHistory, Messages.MESSAGE_INVALID_WORKOUT_DISPLAYED_INDEX);

        // single workout book state in model -> undoCommand and redoCommand fail
        assertCommandFailure(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_FAILURE);
        assertCommandFailure(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_FAILURE);
    }

    @Test
    public void equals() {
        CurrentCommand currentFirstCommand = new CurrentCommand(INDEX_FIRST_WORKOUT);
        CurrentCommand currentSecondCommand = new CurrentCommand(INDEX_SECOND_WORKOUT);

        // same object -> returns true
        assertTrue(currentFirstCommand.equals(currentFirstCommand));

        // same values -> returns true
        CurrentCommand deleteFirstCommandCopy = new CurrentCommand(INDEX_FIRST_WORKOUT);
        assertTrue(currentFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(currentFirstCommand.equals(1));

        // null -> returns false
        assertFalse(currentFirstCommand.equals(null));

        // different workout -> returns false
        assertFalse(currentFirstCommand.equals(currentSecondCommand));
    }
}

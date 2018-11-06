package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CALORIES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DIFFICULTY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DURATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_OPTIONAL_CALORIES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_OPTIONAL_DIFFICULTY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_OPTIONAL_DURATION;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import seedu.address.logic.commands.RecommendCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ProfileWindowManager;
import seedu.address.model.RecommendArguments;
import seedu.address.model.workout.Calories;
import seedu.address.model.workout.Difficulty;
import seedu.address.model.workout.Duration;
import seedu.address.model.workout.Mode;

/**
 * Parses input arguments and creates a new {@code RecommendCommand} object
 */
public class RecommendCommandParser implements Parser<RecommendCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the {@code RecommendCommand}
     * and returns a {@code RecommendCommand} object for execution.
     */
    public RecommendCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_MODE, PREFIX_DURATION,
                PREFIX_DIFFICULTY, PREFIX_CALORIES, PREFIX_OPTIONAL_CALORIES, PREFIX_OPTIONAL_DIFFICULTY,
                PREFIX_OPTIONAL_DURATION);

        RecommendArguments recommendArguments;
        if (isPrefixPresent(argMultimap, PREFIX_MODE) && isPrefixPresent(argMultimap, PREFIX_DURATION,
                PREFIX_DIFFICULTY, PREFIX_CALORIES, PREFIX_OPTIONAL_CALORIES, PREFIX_OPTIONAL_DIFFICULTY,
                PREFIX_OPTIONAL_DURATION)) {
            if (isPrefixPresent(argMultimap, PREFIX_OPTIONAL_CALORIES, PREFIX_OPTIONAL_DIFFICULTY,
                    PREFIX_OPTIONAL_DURATION) && !isOptionalValid(argMultimap)) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        RecommendCommand.MESSAGE_OPTIONALS));
            } else {
                recommendArguments = getRecommendArguments(argMultimap);
            }
        } else if (argMultimap.getPreamble().isEmpty() && argMultimap.getTheOnlyPrefix() == null) {
            ProfileWindowManager profileWindowManager;
            try {
                profileWindowManager = ProfileWindowManager.getInstance();
                recommendArguments = new RecommendArguments.Builder().withCalories(profileWindowManager
                        .extractCalories(), Optional.of(false))
                        .withDifficulty(profileWindowManager.extractDifficulty(), Optional.of(false))
                        .withDuration(profileWindowManager.extractDuration(), Optional.of(false)).build();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                recommendArguments = new RecommendArguments.Builder().build();
            }
        } else {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RecommendCommand.MESSAGE_USAGE));
        }

        return new RecommendCommand(recommendArguments);
    }

    private RecommendArguments getRecommendArguments(ArgumentMultimap argMultimap) throws ParseException {

        RecommendArguments.Builder recommendArgumentsBuilder = new RecommendArguments.Builder();

        if (!argMultimap.getAllValues(PREFIX_DURATION).isEmpty()) {
            Optional<Duration> duration = Optional.of(ParserUtil.parseDuration(argMultimap.getValue(PREFIX_DURATION)
                    .get()));
            recommendArgumentsBuilder.withDuration(duration, Optional.of(false));
        } else if (!argMultimap.getAllValues(PREFIX_OPTIONAL_DURATION).isEmpty()) {
            Optional<Duration> duration = Optional.of(ParserUtil.parseDuration(argMultimap
                    .getValue(PREFIX_OPTIONAL_DURATION).get()));
            recommendArgumentsBuilder.withDuration(duration, Optional.of(true));
        }
        if (!argMultimap.getAllValues(PREFIX_DIFFICULTY).isEmpty()) {
            Optional<Difficulty> difficulty = Optional.of(ParserUtil.parseDifficulty(argMultimap
                    .getValue(PREFIX_DIFFICULTY).get()));
            recommendArgumentsBuilder.withDifficulty(difficulty, Optional.of(false));
        } else if (!argMultimap.getAllValues(PREFIX_OPTIONAL_DIFFICULTY).isEmpty()) {
            Optional<Difficulty> difficulty = Optional.of(ParserUtil.parseDifficulty(argMultimap
                    .getValue(PREFIX_OPTIONAL_DIFFICULTY).get()));
            recommendArgumentsBuilder.withDifficulty(difficulty, Optional.of(true));
        }
        if (!argMultimap.getAllValues(PREFIX_CALORIES).isEmpty()) {
            Optional<Calories> calories = Optional.of(ParserUtil.parseCalories(argMultimap.getValue(PREFIX_CALORIES)
                    .get()));
            recommendArgumentsBuilder.withCalories(calories, Optional.of(false));
        } else if (!argMultimap.getAllValues(PREFIX_OPTIONAL_CALORIES).isEmpty()) {
            Optional<Calories> calories = Optional.of(ParserUtil.parseCalories(argMultimap
                    .getValue(PREFIX_OPTIONAL_CALORIES).get()));
            recommendArgumentsBuilder.withCalories(calories, Optional.of(true));
        }

        Optional<Mode> mode = Optional.of(ParserUtil.parseMode(argMultimap.getValue(PREFIX_MODE).get()));
        recommendArgumentsBuilder.withMode(mode);

        return recommendArgumentsBuilder.build();
    }

    /**
     * Returns true if at least one of the prefixes contains non-empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean isPrefixPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).anyMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    /**
     * Returns true if the Optional inputs are valid
     */
    private static boolean isOptionalValid(ArgumentMultimap argumentMultimap) {
        return (isPrefixPresent(argumentMultimap, PREFIX_CALORIES, PREFIX_OPTIONAL_CALORIES)
                && isPrefixPresent(argumentMultimap, PREFIX_DIFFICULTY, PREFIX_OPTIONAL_DIFFICULTY)
                && isPrefixPresent(argumentMultimap, PREFIX_DURATION, PREFIX_OPTIONAL_DURATION));
    }

}

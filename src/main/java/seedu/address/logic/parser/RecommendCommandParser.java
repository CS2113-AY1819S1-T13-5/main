package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CALORIES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DIFFICULTY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DURATION;

import java.util.stream.Stream;

import seedu.address.logic.commands.RecommendCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.RecommendArguments;
import seedu.address.model.workout.Calories;
import seedu.address.model.workout.Difficulty;
import seedu.address.model.workout.Duration;

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
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_DURATION,
                PREFIX_DIFFICULTY, PREFIX_CALORIES);

        if (!isPrefixPresent(argMultimap, PREFIX_DURATION, PREFIX_DIFFICULTY, PREFIX_CALORIES) || !argMultimap
                .getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RecommendCommand.MESSAGE_USAGE));
        }

        RecommendArguments recommendArguments = getRecommendArguments(argMultimap);

        return new RecommendCommand(recommendArguments);
    }

    private RecommendArguments getRecommendArguments(ArgumentMultimap argMultimap) throws ParseException {
        Duration duration = null;
        Difficulty difficulty = null;
        Calories calories = null;

        if (!argMultimap.getAllValues(PREFIX_DURATION).isEmpty()) {
            duration = ParserUtil.parseDuration(argMultimap.getValue(PREFIX_DURATION).get());
        }
        if (!argMultimap.getAllValues(PREFIX_DIFFICULTY).isEmpty()) {
            difficulty = ParserUtil.parseDifficulty(argMultimap.getValue(PREFIX_DIFFICULTY).get());
        }
        if (!argMultimap.getAllValues(PREFIX_CALORIES).isEmpty()) {
            calories = ParserUtil.parseCalories(argMultimap.getValue(PREFIX_CALORIES).get());
        }

        return new RecommendArguments(calories, difficulty, duration);
    }

    /**
     * Returns true if at least one of the prefixes contains non-empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean isPrefixPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).anyMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
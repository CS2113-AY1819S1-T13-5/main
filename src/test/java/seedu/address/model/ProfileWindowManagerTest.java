package seedu.address.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

public class ProfileWindowManagerTest {

    @Test
    public void isValidGenderTest() throws IOException {
        ProfileWindowManager profileWindowManager;
        profileWindowManager = ProfileWindowManager.getInstance();
        String validGender = "male";
        String invalidGender = "males";

        assertTrue(profileWindowManager.isValidGender(validGender));
        assertFalse(profileWindowManager.isValidGender(invalidGender));
    }

    @Test
    public void isValidWeightTest() throws IOException {
        ProfileWindowManager profileWindowManager;
        profileWindowManager = ProfileWindowManager.getInstance();
        String validWeight = "66.2";
        String invalidWeight = "94.88";
        String invalidWeight2 = "94.8kg";

        assertTrue(profileWindowManager.isValidWeight(validWeight));
        //Weight should be in the format xx.xx where x is any integer.
        assertFalse(profileWindowManager.isValidWeight(invalidWeight));
        assertFalse(profileWindowManager.isValidWeight(invalidWeight2));
    }

    @Test
    public void isValidHeightTest() throws IOException {
        ProfileWindowManager profileWindowManager;
        profileWindowManager = ProfileWindowManager.getInstance();
        String validHeight = "1.77";
        String invalidHeight = "1.734";
        String invalidHeight2 = "177";

        assertTrue(profileWindowManager.isValidHeight(validHeight));
        //Height should be in the format x.xx where x is any integer.
        assertFalse(profileWindowManager.isValidHeight(invalidHeight));
        assertFalse(profileWindowManager.isValidHeight(invalidHeight2));
    }

    @Test
    public void isValidUsernameTest() throws IOException {
        ProfileWindowManager profileWindowManager;
        profileWindowManager = ProfileWindowManager.getInstance();
        String validUsername = "John Doe";
        String invalidUsername = "John@Doe";

        assertTrue(profileWindowManager.isValidUsername(validUsername));
        //Name should only consists of alphanumeric characters
        assertFalse(profileWindowManager.isValidUsername(invalidUsername));
    }

    @Test
    public void convertStringToIntegerTest() throws IOException {
        ProfileWindowManager profileWindowManager;
        profileWindowManager = ProfileWindowManager.getInstance();
        String validStringInteger = "123";
        String invalidStringInteger = "1234";

        assertEquals(profileWindowManager.convertStringIntoInt(validStringInteger), 123);
        assertNotEquals(profileWindowManager.convertStringIntoInt(invalidStringInteger), 1235);
    }

    @Test
    public void isHigherCaloriesTest() throws IOException {
        ProfileWindowManager profileWindowManager;
        profileWindowManager = ProfileWindowManager.getInstance();
        int lowerCalories = 150;
        int higherCalories = 160;
        int sameCalories = 150;
        assertTrue(profileWindowManager.isHigherCalories(higherCalories, lowerCalories));

        //Should return false since lowerCalories is not higher than higherCalories
        assertFalse(profileWindowManager.isHigherCalories(lowerCalories, higherCalories));
        //Should return false since lowerCalories is not higher than sameCalories
        assertFalse(profileWindowManager.isHigherCalories(lowerCalories, sameCalories));
    }

    @Test
    public void isHigherDurationTest() throws IOException {
        ProfileWindowManager profileWindowManager;
        profileWindowManager = ProfileWindowManager.getInstance();
        int lowerDuration = 20;
        int higherDuration = 21;
        int sameDuration = 20;
        assertTrue(profileWindowManager.isHigherDuration(higherDuration, lowerDuration));

        //Should return false since lowerDuration is not higher than higherDuration
        assertFalse(profileWindowManager.isHigherDuration(lowerDuration, higherDuration));
        //Should return false since lowerDuration is not higher than sameDuration
        assertFalse(profileWindowManager.isHigherDuration(lowerDuration, sameDuration));
    }

    @Test
    public void isMoreDifficultTest() throws IOException {
        ProfileWindowManager profileWindowManager;
        profileWindowManager = ProfileWindowManager.getInstance();
        String lowerDifficulty = "beginner";
        String higherDifficulty = "intermediate";
        String sameDifficulty = "beginner";
        assertTrue(profileWindowManager.isMoreDifficult(higherDifficulty, lowerDifficulty));

        //Should return false since lowerDifficult is not higher than higherDifficult
        assertFalse(profileWindowManager.isMoreDifficult(lowerDifficulty, higherDifficulty));
        //Should return false since lowerDifficult is not higher than sameDifficult
        assertFalse(profileWindowManager.isMoreDifficult(lowerDifficulty, sameDifficulty));
    }

    @Test
    public void calculateBmiTest() throws IOException {
        ProfileWindowManager profileWindowManager;
        profileWindowManager = ProfileWindowManager.getInstance();
        String validHeight = "1.83";
        String validWeight = "88.1";
        String validWeight2 = "78.1";
        double actualBmi = profileWindowManager.calculateBmi(validHeight, validWeight);
        double actualBmi2 = profileWindowManager.calculateBmi(validHeight, validWeight2);
        double expectedBmi = 26.3;

        assertEquals(expectedBmi, actualBmi, 0.1);

        //Wrong expectedBMI should be not equal
        assertNotEquals(expectedBmi, actualBmi2);

    }
}

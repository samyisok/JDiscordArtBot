package ru.utils.name;

import java.util.List;
import java.util.Random;

public class RandomNameService {
    private final Random random = new Random();
    private final List<String> firstNames = List.of(
            "James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael", "Linda",
            "William", "Elizabeth", "David", "Barbara", "Richard", "Susan", "Joseph", "Jessica",
            "Thomas", "Sarah", "Charles", "Karen", "Christopher", "Lisa", "Daniel", "Nancy",
            "Matthew", "Betty", "Anthony", "Sandra", "Mark", "Margaret");

    private final List<String> lastNames = List.of(
            "Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson",
            "Moore", "Taylor", "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin",
            "Thompson", "Garcia", "Martinez", "Robinson", "Clark", "Rodriguez", "Lewis", "Lee",
            "Walker", "Hall", "Allen", "Young", "King", "Wright");

    /**
     * Generate a random full name
     * 
     * @return a randomly generated full name
     */
    public String generateRandomName() {
        String firstName = firstNames.get(random.nextInt(firstNames.size()));
        String lastName = lastNames.get(random.nextInt(lastNames.size()));
        return firstName + " " + lastName;
    }
}

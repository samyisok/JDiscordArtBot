package ru.utils.name;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RandomNameService {
    private static final String FOLDER = "names";
    private final Map<String, Map<String, Set<String>>> dataNames;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private static final Set<String> REVERSE_NAMES = Set.of("chinese", "japanese");
    private static final Set<String> AVAILABLE_NAMES =
            Set.of("chinese", "japanese", "english", "french", "german", "jewish");
    private static final Set<String> CATEGORIES = Set.of("firstname", "lastname");
    private static final String FILE_SUFFIX = "female.json";

    public RandomNameService() {
        dataNames = loadNameData();
    }

    public String generateRandomName() {
        String language = dataNames.keySet().stream()
                .skip(random.nextInt(dataNames.keySet().size()))
                .findFirst()
                .get();

        Set<String> firstNameSet = dataNames.get(language).get("firstname");
        Set<String> lastNameSet = dataNames.get(language).get("lastname");

        String firstName =
                firstNameSet.stream().skip(random.nextInt(firstNameSet.size()))
                        .findFirst().orElseThrow(
                                () -> new RuntimeException("Cant get random element"));
        String lastName = lastNameSet.stream().skip(random.nextInt(firstNameSet.size()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cant get random element"));

        return REVERSE_NAMES.contains(language) ? lastName + " " + firstName
                : firstName + " " + lastName;
    }

    public static Map<String, Map<String, Set<String>>> loadNameData() {
        Map<String, Map<String, Set<String>>> result = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        AVAILABLE_NAMES.forEach(
                group -> {
                    CATEGORIES.forEach(category -> {
                        String filename = group + '_' + category + '_' + FILE_SUFFIX;
                        try (InputStream is = RandomNameService.class.getClassLoader()
                                .getResourceAsStream(FOLDER + "/" + filename)) {

                            String[] names = mapper.readValue(is, String[].class);

                            result
                                    .computeIfAbsent(group, g -> new HashMap<>())
                                    .computeIfAbsent(category, c -> new HashSet<>())
                                    .addAll(List.of(names));
                        } catch (IOException e) {
                            System.out.println("namefile empty");
                            e.printStackTrace();
                        }

                    });
                });

        return result;
    }
}

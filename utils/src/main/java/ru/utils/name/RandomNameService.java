package ru.utils.name;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RandomNameService {
    private static final String FOLDER = "names";
    private final Map<String, Map<String, Set<String>>> dataNames;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final Set<String> REVERSE_NAMES = Set.of("chinese", "japanese");

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

        String firstName = firstNameSet.stream().skip(random.nextInt(firstNameSet.size()))
                .findFirst().orElseThrow(() -> new RuntimeException("Cant get random element"));
        String lastName = lastNameSet.stream().skip(random.nextInt(firstNameSet.size()))
                .findFirst().orElseThrow(() -> new RuntimeException("Cant get random element"));

        return REVERSE_NAMES.contains(language) ? lastName + " " + firstName : firstName + " " + lastName;
    }

    public static Map<String, Map<String, Set<String>>> loadNameData() {
        Map<String, Map<String, Set<String>>> result = new HashMap<>();

        try {
            // Load all files from the folder inside resources
            Enumeration<URL> resources = RandomNameService.class.getClassLoader().getResources(FOLDER);

            List<String> filenames = new ArrayList<>();
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                switch (url.getProtocol()) {
                    case "file" -> filenames.addAll(listFilesFromDirectory(url));
                    case "jar" -> filenames.addAll(listFilesFromJar(url, FOLDER));
                }
            }

            for (String filename : filenames) {
                if (!filename.endsWith(".json")) {
                    continue;
                }

                String[] parts = filename.replace(".json", "").split("_"); // e.g., "japanese_lastname_female"

                if (parts.length < 2) {
                    continue;
                }

                String language = parts[0];
                String category = parts[1]; // "firstname" or "lastname"

                try (InputStream is = RandomNameService.class.getClassLoader()
                        .getResourceAsStream(FOLDER + "/" + filename)) {

                    if (is == null) {
                        continue;
                    }

                    ObjectMapper mapper = new ObjectMapper();
                    String[] names = mapper.readValue(is, String[].class);

                    result
                            .computeIfAbsent(language, l -> new HashMap<>())
                            .computeIfAbsent(category, c -> new HashSet<>())
                            .addAll(List.of(names));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load name files", e);
        }

        return result;
    }

    private static List<String> listFilesFromDirectory(URL folderUrl) throws Exception {
        File folder = new File(folderUrl.toURI());
        String[] files = folder.list((dir, name) -> name.endsWith(".json"));
        return files != null ? List.of(files) : List.of();
    }

    private static List<String> listFilesFromJar(URL jarUrl, String folderName) throws Exception {
        String jarPath = jarUrl.getPath().substring(5, jarUrl.getPath().indexOf("!"));
        List<String> result = new ArrayList<>();

        try (JarFile jar = new JarFile(jarPath)) {
            jar.stream()
                    .map(JarEntry::getName)
                    .filter(name -> name.startsWith(folderName + "/") && name.endsWith(".json"))
                    .forEach(name -> result.add(name.substring(folderName.length() + 1)));
        }

        return result;
    }

}

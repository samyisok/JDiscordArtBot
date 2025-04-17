package ru.sarahbot.sarah.file.service;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.utils.name.RandomNameService;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaifuCreatorService {
    private final RandomNameService randomNameService;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    record AgeGroup(int from, int to) {
    };

    private final Set<AgeGroup> AGE_GROUPS = Set.of(
            new AgeGroup(16, 18),
            new AgeGroup(16, 21),
            new AgeGroup(18, 23),
            new AgeGroup(16, 33),
            new AgeGroup(16, 45),
            new AgeGroup(16, 2000),
            new AgeGroup(2000, 99999));

    private static final Set<String> VIRTUES = Set.of(
            "Master of Cooking",
            "Alchemist",
            "Intellegent",
            "Physically fit",
            "Artist",
            "Cosplayer",
            "Virgin",
            "Romantic",
            "Submissive",
            "Idol",
            "Rich",
            "Zoologist",
            "Game-Developer",
            "Shapeshifter",
            "Blue Blood",
            "Optimistic",
            "Loves to roleplay",
            "Wizzard skills",
            "Shy",
            "Big Breasts",
            "Super Overhemly Big Breasts",
            "Big Butt",
            "Nudist",
            "Maid",
            "Witch",
            "Esper",
            "Videoblogger",
            "Karate Black Belt",
            "Ninja",
            "Programmer",
            "Office Worker",
            "Armor",
            "Horny",
            "Can see through clothes",
            "Can detect contents by licking",
            "Socialist",
            "Has a Donut",
            "Super-Strength",
            "Super-Eating",
            "Can manipulate Qi",
            "Brain Calculator",
            "Timetraveler");

    private static final Set<String> FLAWS = Set.of(
            "Smokes",
            "Drinks",
            "Drugs",
            "A lot of phobias",
            "Orphan",
            "Con Artist",
            "Disabled",
            "Jealous",
            "Tax Evader",
            "Awful character",
            "Criminal",
            "PTSD",
            "Army Veteran",
            "Mutant",
            "Permanently Unemployed",
            "Hairy",
            "Masochistic",
            "Sadistic",
            "Tsundere",
            "Antisocial",
            "Yandere",
            "Attentionwhore",
            "Your BOSS",
            "Depressive",
            "Yakuza",
            "Vampire",
            "Baka",
            "Incureable Disease",
            "Skeleton",
            "CEO",
            "Communist",
            "From Space",
            "Can eat only rocks");

    static final Set<String> QUIRKS = Stream.concat(FLAWS.stream(), VIRTUES.stream()).collect(Collectors.toSet());

    static final Set<String> RACES = Set.of(
            "Caucasian-Human",
            "Asian-Human",
            "Afro-Human",
            "Elf",
            "Dark-Elf",
            "High-Elf",
            "half-Orc",
            "Oni",
            "Half-Dworf",
            "Demon",
            "Nekogirl",
            "Cowgirl",
            "Bunnygirl",
            "Dragongirl",
            "Wolfgirl",
            "Shmokagirl",
            "Slimegirl",
            "Cybergirl",
            "Horsegirl",
            "Mushroomgirl",
            "Octopusgirl",
            "Куколка_Муровья");

    public String generate() {
        int totalRaces = random.nextInt(1, 3);
        int age = getAge();
        int totalQuirks = random.nextInt(3, 5);
        String name = randomNameService.generateRandomName();
        int height = getHeight();
        String quirks = getQuirks(totalQuirks);
        String races = getRaces(totalRaces);

        String waifu = MessageFormat.format("""
                name: {0}
                race: {1}
                age: {2}
                height: {3}
                quirks: {4}
                """,
                name,
                races,
                age,
                height,
                quirks);

        return waifu;
    }

    int getAge() {
        AgeGroup ageGroup = getAgeGroup();
        int age = random.nextInt(ageGroup.from(), ageGroup.to());
        return age;
    }

    int getHeight() {
        return random.nextInt(130, 210);
    }

    AgeGroup getAgeGroup() {
        return AGE_GROUPS.stream().skip(random.nextInt(AGE_GROUPS.size())).findFirst()
                .orElse(new AgeGroup(42, 42));
    }

    String getQuirks(int totalQuirks) {
        Set<String> quirks = new HashSet<>();

        while (quirks.size() < totalQuirks) {
            String quirk = QUIRKS.stream().skip(random.nextInt(QUIRKS.size())).findFirst().get();
            if (quirks.contains(quirk)) {
                continue;
            }
            quirks.add(quirk);
        }
        return String.join(", ", quirks);
    }

    String getRaces(int totalRaces) {
        Set<String> races = new HashSet<>();
        while (races.size() < totalRaces) {
            String race = RACES.stream().skip(random.nextInt(RACES.size())).findFirst().get();
            if (races.contains(race)) {
                continue;
            }
            races.add(race);
        }
        return String.join(", ", races);
    }
}

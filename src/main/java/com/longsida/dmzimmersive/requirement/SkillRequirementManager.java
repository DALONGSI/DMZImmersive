package com.longsida.dmzimmersive.requirement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SkillRequirementManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get()
            .resolve("dmzimmersive")
            .resolve("skill_requirements.json");

    private static Map<String, SkillRequirement> requirements = new HashMap<>();

    public static void load() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                Files.createDirectories(CONFIG_PATH.getParent());
                requirements = createDefaults();
                save();
            } else {
                try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                    requirements = GSON.fromJson(reader,
                            new TypeToken<Map<String, SkillRequirement>>() {}.getType());
                    if (requirements == null) requirements = new HashMap<>();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            requirements = new HashMap<>();
        }

        System.out.println("[DMZImmersive] SkillRequirementManager loaded, " + requirements.size() + " skills");
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(requirements, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SkillRequirement get(String skillName) {
        if (skillName == null) return null;
        return requirements.get(skillName.toLowerCase());
    }

    private static Map<String, SkillRequirement> createDefaults() {
        Map<String, SkillRequirement> map = new HashMap<>();
        String[] skills = {
                "jump", "sprint", "fly", "meditation", "kisense",
                "potentialunlock", "kicontrol", "kiboost", "kimanipulation",
                "instant_transmission", "defense_penetration", "healing_reduction",
                "ki_infusion", "kiprotection", "fusion",
                "ki_barrage", "masenko", "kamehameha", "galick_gun", "taiyoken",
                "death_beam", "fake_moon", "kienzan", "makkanko", "burning_attack",
                "big_bang", "sokidan", "kienzan_doble", "emperor_death_beam",
                "final_flash", "spiritbomb", "supernova", "supernova_cooler",
                "final_explosion", "soul_punisher",
                "meteor", "wolf_fang", "kaioken_attack", "dragon_fist",
                "deadly_dance", "super_god_fist", "deadly_dance_vegetto", "oozaru_fist",
                "superforms", "legendaryforms", "godforms", "androidforms",
                "kaioken", "ultimate"
        };
        for (String skill : skills) {
            SkillRequirement req = new SkillRequirement();
            for (String stat : new String[]{"STR", "SKP", "PWR", "RES", "VIT", "ENE"}) {
                req.stats.put(stat, new SkillRequirement.StatReq(0, 0, Integer.MAX_VALUE));
            }
            map.put(skill, req);
        }
        return map;
    }
}
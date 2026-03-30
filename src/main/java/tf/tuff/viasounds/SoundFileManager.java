package tf.tuff.viasounds;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SoundFileManager {
    private final Map<String, List<String>> soundEventToFiles = new HashMap<>();
    private final List<String> palette = new ArrayList<>();
    private final Map<String, Integer> pathToIndex = new HashMap<>();

    public SoundFileManager() {
        loadSoundsFromJSON();
    }

    private void loadSoundsFromJSON() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("sound_mappings.json")) {
            if (is == null) return;

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);

            Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String eventName = entry.getKey();
                String minecraftEvent = "minecraft:" + eventName;

                JsonNode soundEntry = entry.getValue();
                JsonNode soundsArray = soundEntry.get("sounds");

                if (soundsArray != null && soundsArray.isArray()) {
                    List<String> paths = new ArrayList<>();
                    for (JsonNode pathNode : soundsArray) {
                        String fullPath = pathNode.asText();
                        String cleanPath = fullPath.replace("tuff:", "");
                        paths.add(cleanPath);
                    }

                    soundEventToFiles.put(minecraftEvent, paths);

                    String tuffEventName = "tuff:" + eventName;
                    if (!pathToIndex.containsKey(tuffEventName)) {
                        pathToIndex.put(tuffEventName, palette.size());
                        palette.add(tuffEventName);
                    }
                }
            }

        } catch (Exception e) { ViaSoundsPlugin.instance.debug("Error loading sounds: "+e.getMessage()); }
    }

    public String getRandomFilePath(String soundEvent) {
        List<String> files = soundEventToFiles.get(soundEvent);
        if (files == null || files.isEmpty()) {
            return null;
        }
        return "tuff:" + soundEvent.replace("minecraft:", "");
    }

    public boolean isModernSound(String soundEvent) {
        return soundEventToFiles.containsKey(soundEvent);
    }

    public List<String> getAllSoundPaths() {
        return palette;
    }

    public int getPathIndex(String filePath) {
        return pathToIndex.getOrDefault(filePath, -1);
    }
}
